package xyz.rive.jttplayer.manager;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.input.KeyCombination;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.*;
import xyz.rive.jttplayer.control.GlobalShortcutKeys;
import xyz.rive.jttplayer.controller.MainController;
import xyz.rive.jttplayer.controller.MainMiniModeController;
import xyz.rive.jttplayer.player.Player;
import xyz.rive.jttplayer.player.bass.BassPlayer;
import xyz.rive.jttplayer.player.mpv.MpvPlayer;
import xyz.rive.jttplayer.service.AsyncService;
import xyz.rive.jttplayer.service.PlaybackQueueService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.common.Constants.*;
import static xyz.rive.jttplayer.common.EqualizerOptions.CUSTOM_EQ_INDEX;
import static xyz.rive.jttplayer.service.PlaybackQueueService.BY_INDEX_SUPPLIER;
import static xyz.rive.jttplayer.util.FileUtils.*;
import static xyz.rive.jttplayer.util.FileUtils.transformPath;
import static xyz.rive.jttplayer.util.FxUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class PlayerManager extends AbstractManager {
    private Player lastActivePlayer;
    private Player activePlayer;
    private Map<Integer, Player> players;
    private boolean playCoreChanged;

    private final IntegerProperty playStateProperty = new SimpleIntegerProperty(0);
    private final IntegerProperty playModeProperty = new SimpleIntegerProperty(0);
    private final DoubleProperty volumeProperty = new SimpleDoubleProperty(100);

    private final StringProperty currentIndexProperty = new SimpleStringProperty("");
    private final DoubleProperty timePositionProperty = new SimpleDoubleProperty(-1);
    //private Consumer<Double> volumeChangedListener;
    private Consumer<Boolean> equalizerEnabledListener;
    private Consumer<Integer> equalizerIndexChangedListener;
    private Consumer<Boolean> volumeMuteListener;
    private List<TrackSelection> trackSelections;
    private List<TrackSelection> playLaterSelections;

    private final BooleanProperty fileAttributeTagsAdvanceModeProperty = new SimpleBooleanProperty(false);
    private boolean exitReady = false;
    private final BooleanProperty lyricOffsetSetByMouseWheelModeProperty = new SimpleBooleanProperty(false);
    private boolean seekOnPaused = false;
    private final BooleanProperty playbackQueueSortByName = new SimpleBooleanProperty(false);
    private boolean ignoreCurrentIndexMode = false;
    private boolean ignoreGlobalKeys = false;
    private boolean restoring = false;

    public PlayerManager(ApplicationContext context) {
        super(context);
        registerPlayers();
        setupListeners();
    }

    private Map<Integer, Player> getPlayers() {
        if(players == null) {
            players = new HashMap<>();
        }
        return players;
    }

    public void registerPlayer(int type, Player player) {
        if (type < 0 || player == null) {
            return ;
        }
        getPlayers().putIfAbsent(type, player);
    }

    private void registerPlayers() {
        registerPlayer(0, new BassPlayer());
        registerPlayer(1, new MpvPlayer());
    }



    private void setupListeners() {
        currentIndexProperty.addListener(
                (o, ov, nv) -> {
                    Track track = getCurrentPlaybackQueue()
                            .getTrack(getCurrentTrackIndex());
                    if(track == null || track.equals(getCurrentTrack())) {
                        return ;
                    }
                    stopOldPlayer();
                    getActivePlayer().play(track);
                    if(ignoreCurrentIndexMode) {
                        getActivePlayer().pause();
                        setPlayStateProperty(PlayState.PAUSED.getValue());
                        setIgnoreCurrentIndexMode(false);
                    }

                });
        getActivePlayer().onStarted(started -> {
            if(seekOnPaused) {
                setSeekOnPaused(false);
                return ;
            }
            if(isRestoring()) {
                return ;
            }
            setPlayStateProperty(PlayState.PLAYING.getValue());
        }).onPaused(paused -> {
            Optional.ofNullable(getActivePlayer().getCurrentTrack()).ifPresent(track -> {
                setPlayStateProperty(paused ?
                        PlayState.PAUSED.getValue()
                        : PlayState.PLAYING.getValue());
            });
        }).onStopped(stopped -> {
            if(getPlayState() != PlayState.STOPPING.getValue()) {
                setPlayStateProperty(PlayState.STOPPING.getValue());
                playNext();
            }
        }).onTimePosition(data -> {
            Double timePos = (Double) data;
            setTimePosition(timePos == null ? -1 : timePos);
        }).onStateChanged(data -> {
            Pair changed = (Pair) data;
            if(contentEquals("volume", changed.key())) {
                volumeProperty.set((Double) changed.value());
//                Optional.ofNullable(volumeChangedListener).ifPresent(
//                        listener -> listener.accept((Double) changed.value()));
            } else if(contentEquals("mute", changed.key())) {
                Optional.ofNullable(volumeMuteListener).ifPresent(
                        listener -> listener.accept((boolean) changed.value()));
            }
        });
    }

    private Player doGetPlayer() {
        return getPlayers().getOrDefault(
                getPlayOptions().getPlayCoreType(),
                getPlayers().get(1));
    }

    public void stopOldPlayer() {
        if(playCoreChanged) {
            Player active = doGetPlayer();
            if(lastActivePlayer != null) {
                active.onStarted(lastActivePlayer.getStartedListener());
                active.onPaused(lastActivePlayer.getPausedListener());
                active.onStopped(lastActivePlayer.getStoppedListener());
                active.onTimePosition(lastActivePlayer.getTimePositionListener());
                active.onStateChanged(lastActivePlayer.getStateListener());
                lastActivePlayer.setCurrentTrack(null);
                lastActivePlayer.stop();
            }
            setPlayCoreChanged(false);
        }
    }

    public void doQuitPlayer(Player... players) {
        if(players != null) {
            for (Player player: players) {
                if (player != null) {
                    player.quit();
                }
            }
        }
    }

    public void doQuitPlayer(Collection<Player> players) {
        if(players != null) {
            for (Player player: players) {
                if (player != null) {
                    player.quit();
                }
            }
        }
    }

    public void quitAllPlayers() {
        doQuitPlayer(getPlayers().values());
    }

    public Player getActivePlayer() {
        if(activePlayer == null || activePlayer != doGetPlayer()) {
            activePlayer = doGetPlayer();
            activePlayer.setPlayCorePath(getPlayOptions().getPlayCorePath());
        }
        if(isEqualizerEnabled()) {
            activePlayer.setEqualizer(getEqualizerValues());
        } else {
            activePlayer.removeEqualizer();
        }
        return activePlayer;
    }

    public void restore() {
        //Options
        PlayerOptions options = getPlayerOptions();
        getActivePlayer().setVolume(options.getVolume());
        if(options.isVolumeMute()) {
            getActivePlayer().mute();
        }
        //Queue
        restorePlaybackQueues();
        //Current Track
        restoreCurrentTrack();
    }

    public void setExitReady() {
        if(exitReady) {
            return ;
        }

        quitAllPlayers();
        savePlaybackQueues();
        savePlayerOptions();
        getConfiguration().save();
        cleanWorkPath();
        exitReady = true;
    }

    public void exit() {
        runFx(() -> {
            setExitReady();
            System.exit(0);
        });
    }

    public PlayerManager setupExitHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::setExitReady));
        //响应系统任务栏菜单
        getMainStage().setOnCloseRequest(event -> exit());
        return this;
    }

    public Stage getMainStage() {
        return getContext().getMainStage();
    }

    public Configuration getConfiguration() {
        return getContext().getConfiguration();
    }

    public StageManager getStageManager() {
        return getContext().getStageManager();
    }

    public ControllerManager getControllerManager() {
        return getContext().getControllerManager();
    }

    public PlaybackQueueService getPlaybackQueueService() {
        return getContext().getPlaybackQueueService();
    }

    public AsyncService getAsyncService() {
        return getContext().getAsyncService();
    }


    private PlayerOptions getPlayerOptions() {
        return getConfiguration().getPlayerOptions();
    }

    private LyricOptions getLyricOptions() {
        return getConfiguration().getLyricOptions();
    }

    public void togglePlayTimeCountDownMode() {
        getPlayerOptions().setPlayTimeCountDownMode(!isPlayTimeCountDownMode());
    }

    public boolean isPlayTimeCountDownMode() {
        return getPlayerOptions().isPlayTimeCountDownMode();
    }

    private void savePlayerOptions() {
        PlayerOptions options = getPlayerOptions();
        options.setVolume(getActivePlayer().getVolume());
        options.setVolumeMute(getActivePlayer().isMute());
        options.setCurrentTime(getTimePosition());
        options.setMainViewBound(getStageManager().getStageRelativeBound(getMainStage(), true));

        if(!options.isMiniMode()) {
            getStageManager().saveStagesBounds();
        }
    }


    private void savePlaybackQueues() {
        //清理旧文件
        File appData = new File(getAppDataPath());
        if(!appData.exists() || !appData.isDirectory()) {
            return ;
        }
        File[] files = appData.listFiles((file, name) ->
                trim(name).endsWith(PLAYBACK_QUEUE_SUFFIX));
        if(files != null && files.length > 0) {
            Arrays.asList(files).forEach(file -> {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        //保存
        getPlaybackQueueService().saveAll(
                new File(getAppDataPath()),
                BY_INDEX_SUPPLIER
        );
    }

    public void restorePlaybackQueues() {
        File appData = new File(getAppDataPath());
        if(!appData.exists() || !appData.isDirectory()) {
            return ;
        }
        File[] files = appData.listFiles((file, name) ->
                trim(name).endsWith(PLAYBACK_QUEUE_SUFFIX));
        if(files == null || files.length < 1) {
            return ;
        }
        Arrays.asList(files).forEach(this::addPlaybackQueue);
        doSortPlaybackQueues();
        getControllerManager().refreshPlaybackQueue();
    }

    public void restoreCurrentTrack() {
        PlaybackQueue queue = getCurrentPlaybackQueue();
        if (queue == null) {
            return ;
        }
        setRestoring(true);
        setIgnoreCurrentIndexMode(true);
        setCurrentIndex(getCurrentPlaybackQueueIndex(),
                getCurrentTrackIndex());
        int seconds = (int) getPlayerOptions().getCurrentTime();
        seekPause(seconds);
        runDelay(() -> {
            setRestoring(false);
            setIgnoreCurrentIndexMode(false);
            setSeekOnPaused(false);
        }, 2233);
    }

    public boolean isMiniMode() {
        return getPlayerOptions().isMiniMode();
    }

    public void toggleMiniMode() {
        PlayerOptions options = getPlayerOptions();
        boolean isMiniMode = !options.isMiniMode();
        options.setMiniMode(isMiniMode);
        getStageManager().setupStagesByMode(false);
        getControllerManager().refreshOnMiniMode(isMiniMode);
    }

    public PlaybackQueue getCurrentPlaybackQueue() {
        List<PlaybackQueue> queues = getPlaybackQueues();
        if(queues.isEmpty()) {
            getPlaybackQueue("默认", true);
            setCurrentPlaybackQueueIndex(0);
            setCurrentTrackIndex(-1);
        }
        int index = getCurrentPlaybackQueueIndex();
        if(index >= queues.size() || index < 0) {
            index = 0;
            setCurrentPlaybackQueueIndex(index);
            setCurrentTrackIndex(-1);
        }
        return queues.get(index);
    }

    public PlaybackQueue getActivePlaybackQueue() {
        List<PlaybackQueue> queues = getPlaybackQueues();
        if(queues.isEmpty()) {
            getPlaybackQueue("默认", true);
            setActivePlaybackQueueIndex(0);
            setCurrentTrackIndex(-1);
        }
        int index = getActivePlaybackQueueIndex();
        if(index >= queues.size() || index < 0) {
            index = 0;
            setActivePlaybackQueueIndex(0);
            setCurrentTrackIndex(-1);
        }
        return queues.get(index);
    }

    public PlaybackQueue getPlaybackQueueByName(String name) {
        return getPlaybackQueueService().byName(name);
    }

    public PlaybackQueue getPlaybackQueue(String id) {
        return getPlaybackQueueService().get(id);
    }

    public PlaybackQueue getPlaybackQueue(int index) {
        return getPlaybackQueueService().get(index);
    }

    public PlaybackQueue getPlaybackQueue(String name, boolean createIfNotExists) {
        PlaybackQueue queue = getPlaybackQueueByName(name);
        if(queue != null) {
            return queue;
        }
        return createIfNotExists ? createPlaybackQueue(name) : null;
    }

    public void setCurrentPlaybackQueueIndex(int index) {
        getPlayerOptions().setCurrentPlaybackQueueIndex(index);
    }

    public int getCurrentPlaybackQueueIndex() {
        return getPlayerOptions().getCurrentPlaybackQueueIndex();
    }

    public void setActivePlaybackQueueIndex(int index) {
        getPlayerOptions().setActivePlaybackQueueIndex(index);
    }

    public int getActivePlaybackQueueIndex() {
        return getPlayerOptions().getActivePlaybackQueueIndex();
    }

    public boolean isCurrentPlaybackQueue(PlaybackQueue queue) {
        return getCurrentPlaybackQueue() == queue;
    }

    public boolean isActivePlaybackQueue(PlaybackQueue queue) {
        return getActivePlaybackQueue() == queue;
    }

    public void setCurrentTrackIndex(int index) {
        getPlayerOptions().setCurrentTrackIndex(index);
    }

    public void setCurrentIndex(int queueIndex, int trackIndex) {
        setCurrentPlaybackQueueIndex(queueIndex);
        setCurrentTrackIndex(trackIndex);
        currentIndexProperty.set(String.format("%s-%s", queueIndex, trackIndex));
    }

    public void setIgnoreCurrentIndexMode(boolean value) {
        ignoreCurrentIndexMode = value;
    }

    public boolean isRestoring() {
        return restoring;
    }

    public void setRestoring(boolean restoring) {
        this.restoring = restoring;
    }

    public int getCurrentTrackIndex() {
        return getPlayerOptions().getCurrentTrackIndex();
    }

    public int getPlaybackQueueIndex(String queueId) {
        return getPlaybackQueueService().indexOf(queueId);
    }

    public void toggleVolumeMute() {
        getActivePlayer().toggleMute();
    }

    public void setPlayStateProperty(int state) {
        playStateProperty.set(state);
    }

    public void togglePlay() {
        Track track = getActivePlayer().getCurrentTrack();
        if(track != null) {
            if(isPlaying()) {
                getActivePlayer().pause();
                setPlayStateProperty(PlayState.PAUSED.getValue());
            } else if(getPlayState() < PlayState.STOPPING.getValue()){
                getActivePlayer().play();
                setPlayStateProperty(PlayState.PLAYING.getValue());
            } else {
                getActivePlayer().play(track);
                setPlayStateProperty(PlayState.PLAYING.getValue());
            }
        } else {
            setCurrentPlaybackQueueIndex(getActivePlaybackQueueIndex());
            Optional.ofNullable(getCurrentPlaybackQueue())
                    .ifPresent(queue -> {
                        playNext();
                    });
        }
    }

    public boolean isPlaying() {
        return getPlayState() == PlayState.PLAYING.getValue();
    }

    public void stopPlay() {
        try {
            getActivePlayer().stop();
            setPlayStateProperty(PlayState.STOPPING.getValue());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void playPrevious() {
        int index = Math.max(getCurrentTrackIndex(), -1);
        PlaybackQueue queue = getCurrentPlaybackQueue();
        if(queue == null) {
            return ;
        }
        int size = queue.size();
        if(size < 1) {
            return ;
        }
        int queueIndex = getCurrentPlaybackQueueIndex();
        switch (PlaybackMode.of(getPlaybackMode())) {
            case OnlyOne:
                getActivePlayer().stop();
                break;
            case RepeatOne:
                setCurrentIndex(queueIndex, -1);
                setCurrentIndex(queueIndex,index);
                break;
            case Sequence:
                //setCurrentIndex(queueIndex,Math.max(--index, 0));
                --index;
                if(index < 0) {
                    if(isAutoSwitchPlaybackQueue()) {
                        int queuesSize = getPlaybackQueues().size();
                        if(queuesSize > 1) {
                            int prevQueueIndex = queueIndex;
                            queueIndex = Math.max(--queueIndex, 0);
                            if(queueIndex < prevQueueIndex) {
                                setCurrentIndex(queueIndex, 0);
                            }
                        }
                    }
                } else {
                    setCurrentIndex(queueIndex, Math.max(index, 0));
                }
                break;
            case RepeatAll:
                if(--index < 0) {
                    index = size - 1;
                }
                setCurrentIndex(queueIndex, index);
                break;
            case Random:
                //setCurrentIndex.set(queueIndex, nextInt(size));
                break;
        }
    }

    public void playNext() {
        if(!getPlayLaterSelections().isEmpty()) {
            TrackSelection selection = getPlayLaterSelections().remove(0);
            if (!selection.getTrack().equals(getCurrentTrack())) {
                setCurrentIndex(selection.getQueueIndex(), selection.getIndex());
                return ;
            }
        }
        PlaybackQueue queue = getCurrentPlaybackQueue();
        if(queue == null) {
            return ;
        }
        int size = queue.size();
        if(size < 1) {
            return ;
        }
        int queueIndex = getCurrentPlaybackQueueIndex();
        int index = Math.max(getCurrentTrackIndex(), -1);
        switch (PlaybackMode.of(getPlaybackMode())) {
            case OnlyOne:
                getActivePlayer().stop();
                break;
            case RepeatOne:
                setCurrentIndex(queueIndex, -1);
                setCurrentIndex(queueIndex, index);
                break;
            case Sequence:
                //setCurrentIndex(queueIndex, Math.min(++index, size - 1););
                ++index;
                if(index >= size) {
                    if(isAutoSwitchPlaybackQueue()) {
                        int queuesSize = getPlaybackQueues().size();
                        if(queuesSize > 1) {
                            int prevQueueIndex = queueIndex;
                            queueIndex = Math.min(++queueIndex, queuesSize - 1);
                            if(queueIndex > prevQueueIndex) {
                                setCurrentIndex(queueIndex, 0);
                            }
                        }
                    }
                } else {
                    setCurrentIndex(queueIndex, Math.min(index, size - 1));
                }

                break;
            case RepeatAll:
                setCurrentIndex(queueIndex,++index % size);
                break;
            case Random:
                setCurrentIndex(queueIndex, nextInt(size));
                break;
        }

    }

    private void setSeekOnPaused(boolean value) {
        seekOnPaused = value;
    }

    public void seekPlay(int seconds) {
        seek(seconds, isPlaying());
    }

    public void seekPause(int seconds) {
        seek(seconds, false);
    }

    private void seek(int seconds, boolean playing) {
        getActivePlayer().seek(seconds);
        //Seek-On-Paused
        if(!playing) {
            getActivePlayer().pause();
            setSeekOnPaused(true);
            setPlayStateProperty(PlayState.PAUSED.getValue());
            if(getStageManager().isLyricShow()) {
                getControllerManager().setLyricSeekOnPause(seconds);
            }
        }
    }

    public void seekPlayRelative(int seconds) {
        seekPlay((int)(getTimePosition() + seconds));
    }

    public void addVolume(int offset) {
        double volume = getActivePlayer().getVolume();
        setVolume(volume + offset);
    }

    public void setVolume(double value) {
        getActivePlayer().setVolume(value);
    }

    public void toggleMute() {
        getActivePlayer().toggleMute();
    }

    public boolean isMusicOnlineShow() {
        return getPlayerOptions().isMusicOnlineShow();
    }

    public PlayerManager onVolumeMute(Consumer<Boolean> listener) {
        this.volumeMuteListener = listener;
        return this;
    }

    public PlayerManager onPlayState(ChangeListener<? super Number> listener) {
        onProperty(playStateProperty, listener);
        return this;
    }

    public PlayerManager onPlayMode(ChangeListener<? super Number> listener) {
        onProperty(playModeProperty, listener);
        return this;
    }

    public List<PlaybackQueue> getPlaybackQueues() {
        return getPlaybackQueueService().listAll();
    }

    public PlayerManager onPlaybackQueuesSize(ChangeListener<? super Number> listener) {
        onProperty(getPlaybackQueueService().sizeProperty(), listener);
        return this;
    }

    public PlayerManager onTrackChanged(ChangeListener<? super String> listener) {
        onProperty(currentIndexProperty, listener);
        return this;
    }

    public PlayerManager onTimePosition(ChangeListener<? super Number> listener) {
        onProperty(timePositionProperty, listener);
        return this;
    }

    public PlayerManager onVolumeChanged(ChangeListener<? super Number> listener) {
        //this.volumeChangedListener = listener;
        volumeProperty.addListener(listener);
        return this;
    }

    public void setMainStage(Stage stage) {
        getStageManager().setMainStage(stage);
        Runtime.getRuntime().addShutdownHook(new Thread(this::setExitReady));
        //响应系统任务栏菜单
        getMainStage().setOnCloseRequest(event -> {
            exit();
        });
    }

    public double getTimePosition() {
        return timePositionProperty.get();
    }

    public void setTimePosition(double value) {
        timePositionProperty.set(value);
    }

    public int getPlayState() {
        return playStateProperty.get();
    }

    public void setPlaybackMode(int mode) {
        getPlayerOptions().setPlaybackMode(mode);
        playModeProperty.set(mode);
    }

    public int getPlaybackMode() {
        return getPlayerOptions().getPlaybackMode();
    }

    public boolean isPlaybackMode(PlaybackMode mode) {
        return getPlaybackMode() == mode.getValue();
    }

    public PlayerManager onEqualizerEnabled(Consumer<Boolean> listener) {
        this.equalizerEnabledListener = listener;
        return this;
    }

    public PlayerManager onEqualizerIndexChanged(Consumer<Integer> listener) {
        this.equalizerIndexChangedListener = listener;
        return this;
    }

    private EqualizerOptions getEqualizerOptions() {
        return getConfiguration().getEqualizerOptions();
    }

    public boolean isEqualizerEnabled() {
        return getEqualizerOptions().isEnabled();
    }

    private void setEqualizerEnabled(boolean enabled) {
        getEqualizerOptions().setEnabled(enabled);
        Optional.ofNullable(equalizerEnabledListener).ifPresent(
                handler -> handler.accept(enabled));
    }

    public void toggleEqualizerEnabled() {
        setEqualizerEnabled(!isEqualizerEnabled());
    }

    public void resetEqualizer() {
        getEqualizerOptions().resetCustomEq();
        Optional.ofNullable(equalizerIndexChangedListener).ifPresent(
                handler -> handler.accept(1)
        );
    }

    public void setCurrentEqualizerIndex(int index) {
        setEqualizerEnabled(true);
        getEqualizerOptions().setCurrentEqIndex(index);
        getActivePlayer().setEqualizer(getEqualizerValues());
        Optional.ofNullable(equalizerIndexChangedListener).ifPresent(
                handler -> handler.accept(index)
        );
    }

    public int[] getEqualizerValues() {
        return getEqualizerOptions().getCurrentEqValues();
    }

    public int getCurrentEqualizerIndex() {
        return getEqualizerOptions().getCurrentEqIndex();
    }

    public boolean isCustomEqualizer() {
        return getCurrentEqualizerIndex() == CUSTOM_EQ_INDEX;
    }

    public void setCustomEqualizer(int[] values) {
        if(isEqualizerEnabled()) {
            getEqualizerOptions().setCustomEqValues(values);
            setCurrentEqualizerIndex(CUSTOM_EQ_INDEX);
        }
    }

    public boolean isCurrentEqualizerIndex(int index) {
        return getEqualizerOptions().isEnabled()
                && getCurrentEqualizerIndex() == index;
    }

    private PlaybackQueueOptions getPlaybackQueueOptions() {
        return getConfiguration().getPlaybackQueueOptions();
    }

    private PlayOptions getPlayOptions() {
        return getConfiguration().getPlayOptions();
    }

    public void playUrl(String url, String title, String cover) {
        Track track = new Track();
        track.setUrl(url);
        track.setTitle(isEmpty(title) ? url : title);
        track.setCover(cover);
        getActivePlaybackQueue().addAll(track);
        int index = getActivePlaybackQueue().size() - 1;
        setCurrentTrackIndex(index);
        refreshActivePlaybackQueue();
    }

    public boolean isCurrentTrackSeekable() {
        Track track = getActivePlayer().getCurrentTrack();
        return track != null && track.getTrackLength() > 0;
    }

    public Track getCurrentTrack() {
        return getActivePlayer().getCurrentTrack();
    }

    public List<TrackSelection> getTrackSelections() {
        if(trackSelections == null) {
            trackSelections = new CopyOnWriteArrayList<>();
        }
        return trackSelections;
    }

    public List<TrackSelection> getPlayLaterSelections() {
        if(playLaterSelections == null) {
            playLaterSelections = new CopyOnWriteArrayList<>();
        }
        return playLaterSelections;
    }

    public int toggleSelection(List<TrackSelection> list, Node target, int queueIndex, int index, boolean toggleMode) {
        if(list == null || target == null || queueIndex < 0 || index < 0) {
            return 0;
        }
        TrackSelection selection = new TrackSelection((Track) target.getUserData(), queueIndex, index);
        int result = 0;
        if(toggleMode) {
            if(list.contains(selection)) {
                list.remove(selection);
                --result;
            } else {
                list.add(selection);
                ++result;
            }
        } else {
            if(!list.contains(selection)) {
                list.add(selection);
                ++result;
            }

        }
        return result;
    }

    public int toggleTrackSelection(Node target, int queueIndex, int index, boolean toggleMode) {
        return toggleSelection(getTrackSelections(), target, queueIndex, index, toggleMode);
    }

    public int togglePlayLaterSelection(Node target, int queueIndex, int index, boolean toggleMode) {
        return toggleSelection(getPlayLaterSelections(), target, queueIndex, index, toggleMode);
    }

    public List<TrackSelection> removeInvalidPlayLaterSelections() {
        //同一个播放列表只能存在一个
        List<TrackSelection> list = new ArrayList<>();
        Map<Integer, TrackSelection> statMap = new HashMap<>();
        for (TrackSelection selection : getPlayLaterSelections()) {
            int queueIndex = selection.getQueueIndex();
            TrackSelection oldSelection = statMap.putIfAbsent(queueIndex, selection);
            if(oldSelection != null) {
                list.add(oldSelection);
                getPlayLaterSelections().remove(oldSelection);
            }
        }
        return list;
    }

    public PlaybackQueue createPlaybackQueue(String name) {
        return getPlaybackQueueService().create(name);
    }

    public boolean removePlaybackQueue(PlaybackQueue queue) {
        boolean isActive = isActivePlaybackQueue(queue);
        boolean isCurrent = isCurrentPlaybackQueue(queue);
        boolean success = getPlaybackQueueService().remove(queue);
        if (isActive) {
            setActivePlaybackQueueIndex(-1);
            getControllerManager().refreshPlaybackQueue();
        }
        if(isCurrent) {
            setCurrentPlaybackQueueIndex(-1);
        }
        return success;
    }

    public void savePlaybackQueue(PlaybackQueue queue) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("保存列表文件");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        PLAYBACK_QUEUE_SUFFIX_DESC,
                        PLAYBACK_QUEUE_SUFFIX_PATTERN)
        );
        File selection = chooser.showSaveDialog(getMainStage());
        if(selection == null) {
            return ;
        }
        String fileName = selection.getAbsolutePath();
        if(exists(fileName)
                || !getPlaybackQueueService().save(selection, queue, guessSimpleName(fileName))) {
            getStageManager().showAlert(String.format("保存播放列表 %1$s 失败！",
                    transformPath(fileName)));
        }
    }

    public void addPlaybackQueue(File file) {
        Optional.ofNullable(getPlaybackQueueService().restore(file))
                .ifPresent(getPlaybackQueueService()::add);
    }

    public void saveAllPlaybackQueues() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择文件夹");
        File selection = chooser.showDialog(getMainStage());
        if(selection == null) {
            return ;
        }
        getStageManager().showConfirm(String.format("文件夹 %1$s 下的已经存在的同名播放列表将被覆盖，要继续吗？",
                        transformPath(selection.getAbsolutePath())),
                () -> getPlaybackQueueService().saveAll(selection)
        );
    }


    public boolean isAlwaysOnTop() {
        return getPlayerOptions().isAlwaysOnTop();
    }

    public void setAlwaysOnTop(boolean value) {
        getPlayerOptions().setAlwaysOnTop(value);
    }

    public void toggleAlwaysOnTop() {
        setAlwaysOnTop(!isAlwaysOnTop());
    }

    public boolean isSortBy(SortBy sortBy) {
        return getTrackSortBy() == sortBy;
    }

    public SortBy getTrackSortBy() {
        return getPlayerOptions().getSortBy();
    }

    public void setTrackSortBy(SortBy sortBy) {
        getPlayerOptions().setSortBy(sortBy);
        sortActivePlaybackQueue();
    }

    public void sortActivePlaybackQueue() {
        PlaybackQueue queue = getCurrentPlaybackQueue();
        if(!queue.isEmpty()) {
            queue.sort(getTrackSortBy());
            Optional.ofNullable(getCurrentTrack()).ifPresent(track -> {
                int index = queue.indexOf(track);
                if (index > -1) {
                    setCurrentTrackIndex(index);
                }
            });
        }
        if(getStageManager().isPlaybackQueueShow()) {
            refreshActivePlaybackQueue();
        }
    }

    public void refreshActivePlaybackQueue() {
        runFx(() -> getControllerManager().refreshPlaybackQueue());
    }

    public void appendToPlaybackQueue(PlaybackQueue queue) {
        Optional.ofNullable(queue).ifPresent(__ -> {
            if (!queue.isEmpty()) {
                appendToPlaybackQueue(queue.getData());
            }
        });
    }

    public void appendToPlaybackQueue(List<Track> tracks) {
        getActivePlaybackQueue().addAll(tracks);
        refreshActivePlaybackQueue();
    }

    public void removeDuplicatedTracks() {
        getAsyncService().submit(() -> {
            getPlaybackQueueService().removeDuplicatedTracks(getActivePlaybackQueue());
            refreshActivePlaybackQueue();
        });
    }

    public void removeInvalidTracks() {
        getAsyncService().submit(() -> {
            getPlaybackQueueService().removeInvalidTracks(getActivePlaybackQueue());
            refreshActivePlaybackQueue();
        });
    }

    public boolean isFileAttributeTagsAdvanceModeEnabled() {
        return fileAttributeTagsAdvanceModeProperty.get();
    }

    public void toggleFileAttributeTagsAdvanceMode() {
        fileAttributeTagsAdvanceModeProperty.set(!isFileAttributeTagsAdvanceModeEnabled());
    }

    public PlayerManager onFileAttributeTagsAdvanceMode(ChangeListener<Boolean> listener) {
        onProperty(fileAttributeTagsAdvanceModeProperty, listener);
        return this;
    }

    public void playLast() {
        Optional.ofNullable(getCurrentPlaybackQueue())
                .ifPresent(queue -> {
                    if(queue.isEmpty()) {
                        return;
                    }
                    setCurrentTrackIndex(queue.size() - 1);
                });
    }

    public ScheduledFuture<?> runDelay(Runnable task, long millis) {
        return getAsyncService().scheduleDelayMillis(task, millis);
    }

    public ScheduledFuture<?> runEachDelay(Runnable task, long delay) {
        return getAsyncService().scheduleWithFixedDelay(task, 0, delay);
    }

    public Future<?> runTask(Runnable task) {
        return getAsyncService().submit(task);
    }

    public boolean isLyricOffsetSetByMouseWheelMode() {
        return lyricOffsetSetByMouseWheelModeProperty.get();
    }

    public void toggleLyricOffsetSetByMouseWheelMode() {
        lyricOffsetSetByMouseWheelModeProperty.set(!isLyricOffsetSetByMouseWheelMode());
    }




    public void setLyricAlignment(int alignment) {
        getLyricOptions().setWinModeAlignment(alignment);
        getControllerManager().adjustLyricStyle();
    }

    public int getLyricAlignment() {
        return getLyricOptions().getWinModeAlignment();
    }

    public void setLyricLineSpacing(int spacing) {
        getLyricOptions().setWinModeLineSpacing(spacing);
        getControllerManager().adjustLyricStyle();
    }

    public int getLyricLineSpacing() {
        return getLyricOptions().getWinModeLineSpacing();
    }

    public String getLyricColorNormal() {
        return getLyricOptions().getWinModeNormalColor();
    }

    public PlayerManager setLyricColorNormal(String color) {
        getLyricOptions().setWinModeNormalColor(color);
        getControllerManager().adjustLyricStyle();
        return this;
    }

    public String getLyricColorHilight() {
        return getLyricOptions().getWinModeHilightColor();
    }

    public PlayerManager setLyricColorHilight(String color) {
        getLyricOptions().setWinModeHilightColor(color);
        getControllerManager().adjustLyricStyle();
        return this;
    }

    public String getLyricBackgroundColor() {
        return getLyricOptions().getWinModeBackgroundColor();
    }

    public PlayerManager setLyricBackgroundColor(String color) {
        getLyricOptions().setWinModeBackgroundColor(color);
        getControllerManager().adjustLyricStyle();
        return this;
    }

    public int getLyricFontSize() {
        return getLyricOptions().getWinModeFontSize();
    }

    public int getLyricHilightFontSize() {
        return getLyricOptions().getWinModeHilightFontSize();
    }

    public void setLyricFontSize(int size) {
        getLyricOptions().setWinModeFontSize(size);
        getControllerManager().adjustLyricStyle();
    }

    public void setLyricHilightFontSize(int size) {
        getLyricOptions().setWinModeHilightFontSize(size);
        getControllerManager().adjustLyricStyle();
    }

    public int getLyricDesktopFontSize() {
        return getLyricOptions().getDeskModeFontSize();
    }

    public void setLyricDesktopFontSize(int size) {
        getLyricOptions().setDeskModeFontSize(size);
        getControllerManager().adjustLyricDesktopStyle();
    }

    public boolean isLyricDesktopFontShadow() {
        return getLyricOptions().isDeskModeFontShadow();
    }

    public void setLyricDesktopFontShadow(boolean enable) {
        getLyricOptions().setDeskModeFontShadow(enable);
        getControllerManager().adjustLyricDesktopStyle();
    }

    public boolean isLyricDesktopAutoUnlock() {
        return getLyricOptions().isDeskModeAutoUnlock();
    }

    public void setLyricDesktopAutoUnlock(boolean enable) {
        getLyricOptions().setDeskModeAutoUnlock(enable);
    }

    public void setLyricDesktopTextGradient(String style) {
        getLyricOptions().setDeskModeTextGradientStyle(style);
    }

    public String getLyricDesktopTextGradient() {
        return getLyricOptions().getDeskModeTextGradientStyle();
    }

    public boolean isActiveLyricDesktopTextGradient(String style) {
        return contentEqualsIgnoreCase(getLyricDesktopTextGradient(), style);
    }

    public boolean isPlaybackQueueSortByName() {
        return playbackQueueSortByName.get();
    }

    public void setPlaybackQueueSortByName(boolean value) {
        playbackQueueSortByName.set(value);
    }

    public void togglePlaybackQueueSortByName() {
        setPlaybackQueueSortByName(!isPlaybackQueueSortByName());
    }

    private void doSortPlaybackQueues() {
        String defaultName = "默认";
        //Comparator<PlaybackQueue> comparator = Comparator.comparing(q -> q.getCreated());
        Comparator<PlaybackQueue> comparator = (q1, q2) -> {
            String name1 = trim(q1.getName());
            String name2 = trim(q2.getName());
            if(name1.equals(defaultName)) {
                return -1;
            } else if(name2.equals(defaultName)) {
                return 1;
            }
            long offset = q1.getCreated() - q2.getCreated();
            return offset > 0 ? 1 : (offset < 0 ? -1 : 0);
        };
        if(isPlaybackQueueSortByName()) {
            comparator = (q1, q2) -> {
                String name1 = trim(q1.getName());
                String name2 = trim(q2.getName());
                if(name1.equals(defaultName)) {
                    return -1;
                } else if(name2.equals(defaultName)) {
                    return 1;
                }
                return name1.compareToIgnoreCase(name2);
            };
        }
        getPlaybackQueues().sort(comparator);
    }

    public void sortPlaybackQueues() {
        PlaybackQueue activeQueue = getActivePlaybackQueue();
        PlaybackQueue currentQueue = getCurrentPlaybackQueue();

        doSortPlaybackQueues();

        if(activeQueue != null) {
            int index = getPlaybackQueues().indexOf(activeQueue);
            setActivePlaybackQueueIndex(index);
        }
        if(currentQueue != null) {
            int index = getPlaybackQueues().indexOf(currentQueue);
            setCurrentPlaybackQueueIndex(index);
        }
        getControllerManager().updatePlaybackQueueNames();
    }

    public boolean isAutoSwitchPlaybackQueue() {
        return getPlayerOptions().isAutoSwitchPlaybackQueue();
    }

    public void setAutoSwitchPlaybackQueue(boolean value) {
        getPlayerOptions().setAutoSwitchPlaybackQueue(value);
    }

    public void toggleAutoSwitchPlaybackQueue() {
        setAutoSwitchPlaybackQueue(!isAutoSwitchPlaybackQueue());
    }

    public boolean isPlayFollowCursorMode() {
        return getPlayerOptions().isPlayFollowCursorMode();
    }

    public void setPlayFollowCursorMode(boolean value) {
        getPlayerOptions().setPlayFollowCursorMode(value);
    }

    public void togglePlayFollowCursorMode() {
        setPlayFollowCursorMode(!isPlayFollowCursorMode());
    }

    public void setIgnoreGlobalKeys(boolean value) {
        this.ignoreGlobalKeys = value;
    }

    public boolean isIgnoreGlobalKeys() {
        return ignoreGlobalKeys;
    }

    public int getPlaybackQueueFontSize() {
        return getPlaybackQueueOptions().getFontSize();
    }

    public void setPlaybackQueueFontSize(int size) {
        getPlaybackQueueOptions().setFontSize(size);
        refreshActivePlaybackQueue();
    }

    public void setPlayCoreChanged(boolean changed) {
        playCoreChanged = changed;
    }

    public void setPlayCoreType(int index) {
        int lastCoreType = getPlayOptions().getPlayCoreType();
        if(lastCoreType != index) {
            lastActivePlayer = getPlayers().get(lastCoreType);
            getPlayOptions().setPlayCoreType(index);
            setPlayCoreChanged(true);
        }
    }

    public void registerStageKeys(Stage stage) {
        Optional.ofNullable(stage)
                .ifPresent(__ -> {
                    stage.getScene().getAccelerators()
                            .put(KeyCombination.valueOf("space"), () -> {
                                if (isIgnoreGlobalKeys()) {
                                    return ;
                                }
                                togglePlay();
                            });
                });
    }

    public void setupShortcutKeys() {
        //getMainStage().getScene().getAccelerators().put(KeyCombination.valueOf("space"), this::togglePlay);

        //Maven仓库版本不是最新，经常导致应用崩溃
        //API设计有点繁琐，不好用

        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new GlobalShortcutKeys(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSkinRoot() {
        return getPlayerOptions().getSkinRoot();
    }

    public void setSkinRoot(String skinRoot) {
        getPlayerOptions().setSkinRoot(skinRoot);
        getContext().getSkinManager().updateSkinRoot();
    }

    public void setActiveSkin(String skinFilename) {
        getPlayerOptions().setActiveSkinName(skinFilename);
        getContext().getSkinManager().prepareSkin(skinFilename);
        getContext().getStageManager().refreshSkin();
        getContext().getControllerManager().refreshSkin();
    }

    public boolean isActiveSkin(String skinFilename) {
        return contentEquals(getPlayerOptions().getActiveSkinName(), skinFilename);
    }

    public void cleanWorkPath() {
        deleteRecursively(Paths.get(getWorkPath()), true);
    }

    public void setActiveVisualIndex(int index) {
        getPlayerOptions().setActiveVisualIndex(index);
        Optional.ofNullable(getControllerManager().getController(MainController.class))
                .ifPresent(MainController::showVisualEffect);
        Optional.ofNullable(getControllerManager().getController(MainMiniModeController.class))
                .ifPresent(MainMiniModeController::showVisualEffect);
    }

    public int getActiveVisualIndex() {
        return getPlayerOptions().getActiveVisualIndex();
    }

    public boolean isActiveVisualIndex(int index) {
        return getActiveVisualIndex() == index;
    }

    public boolean isVisualCover() {
        return getActiveVisualIndex() == 0;
    }

    public boolean isVisualSpectrum() {
        return getActiveVisualIndex() > 0;
    }



}
