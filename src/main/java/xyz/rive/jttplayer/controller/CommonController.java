package xyz.rive.jttplayer.controller;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.*;
import xyz.rive.jttplayer.control.DndAction;
import xyz.rive.jttplayer.manager.*;
import xyz.rive.jttplayer.menu.*;
import xyz.rive.jttplayer.menu.strategy.ShowStrategy;
import xyz.rive.jttplayer.player.Player;
import xyz.rive.jttplayer.service.AsyncService;
import xyz.rive.jttplayer.service.TrackService;
import xyz.rive.jttplayer.skin.PositionBasedItem;
import xyz.rive.jttplayer.skin.SkinXml;
import xyz.rive.jttplayer.skin.SkinXmlItem;
import xyz.rive.jttplayer.skin.SkinXmlWindowItem;
import xyz.rive.jttplayer.util.FxUtils;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.common.Constants.*;
import static xyz.rive.jttplayer.util.FxUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.trimLowerCase;

public abstract class CommonController implements Initializable {
    protected String name = "common";
    protected final ApplicationContext context = ApplicationContext.getInstance();
    protected Node rootNode;
    protected IntegerProperty activeTabIndex = new SimpleIntegerProperty(0);

    protected Stage getStage() {
        if(rootNode == null || rootNode.getScene() == null) {
            return null;
        }
        return (Stage) rootNode.getScene().getWindow();
    }

    protected void setupController(CommonController controller, Node root) {
        setupController(controller, root, null);
        /*
        //jnativehook - Fix Bugs
        Arrays.asList("TextField", "TextArea", "ComboBox")
                .forEach(selector -> {
                    registerIgnoreGlobalKeys(root.lookupAll(selector));
                });
       */
    }

    public StageManager getStageManager() {
        return context.getStageManager();
    }

    public ControllerManager getControllerManager() {
        return context.getControllerManager();
    }

    public PlayerManager getPlayerManager() {
        return context.getPlayerManager();
    }

    public SkinManager getSkinManager() {
        return context.getSkinManager();
    }

    public TssManager getTssManager() {
        return context.getTssManager();
    }

    public SkinXml getActiveSkinXml() {
        return context.getActiveSkinXml();
    }

    protected Stage getMainStage() {
        return getStageManager().getMainStage();
    }

    protected Stage getEqualizerStage() {
        return getStageManager().getEqualizerStage();
    }

    protected Stage getPlaybackQueueStage() {
        return getStageManager().getPlaybackQueueStage();
    }

    protected Stage getLyricStage() {
        return getStageManager().getLyricStage();
    }

    protected Stage getLyricDesktopStage() {
        return getStageManager().getLyricDesktopStage();
    }

    protected Stage getLyricMiniModeStage() {
        return getStageManager().getLyricMiniModeStage();
    }

    protected void setupController(CommonController controller, Node root, String name) {
        context.getControllerManager().registerController(controller);
        rootNode = root;
        this.name = name;
    }

    public void setupSkin() {

    }

    public void closeView() {
        Optional.ofNullable(getStage()).ifPresent(stage -> {
            beforeCloseView();

            boolean isMainStage = MAIN.equalsIgnoreCase(name)
                    || MAIN_MINI.equalsIgnoreCase(name);
            /*
            if(isMainStage) {
                getPlayerManager().setExitReady();
            }
             */
            stage.hide();
            if(isMainStage) {
                getPlayerManager().exit();
            } else if(EQUALIZER.equalsIgnoreCase(name)) {
                getStageManager().setEqualizerShow(false);
            } else if(PLAYBACK_QUEUE.equalsIgnoreCase(name)) {
                getStageManager().setPlaybackQueueShow(false);
            } else if(LYRIC.equalsIgnoreCase(name)) {
                getStageManager().setLyricShow(false);
            }

            onClosed();
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        runFx(this::setupSkin);
    }

    public void registerIgnoreGlobalKeys(Node... nodes) {
        Optional.ofNullable(nodes)
                .ifPresent(__ -> registerIgnoreGlobalKeys(Arrays.asList(nodes)));
    }

    public void registerIgnoreGlobalKeys(Collection<Node> nodes) {
        Optional.ofNullable(nodes)
                .ifPresent(__ -> {
                    nodes.forEach(node -> {
                        if(node instanceof TextField
                                || node instanceof TextArea
                                || node instanceof ComboBox) {
                            node.focusedProperty().addListener((o, ov, nv) -> {
                                getPlayerManager().setIgnoreGlobalKeys(nv);
                            });
                        }
                    });
                });
    }

    //Never Override
    public void onClosed() {
        getPlayerManager().setIgnoreGlobalKeys(false);
        afterCloseView();
    }

    //Never Override
    public void onShown() {
        afterShowView();
    }

    public void afterShowView() {
        //Never Override
    }

    public void beforeCloseView() {
        //Override
    }

    public void afterCloseView() {
        //Override
    }

    protected void consumeEvent(Event event) {
        Optional.ofNullable(event).ifPresent(__ -> event.consume());
    }

    protected void consumeAllEvent(Event event) {
        Optional.ofNullable(event).ifPresent(__ -> event.consume());
        consumeContextMenuEvent(event);
    }

    protected void consumeContextMenuEvent(Event event) {
        if (event == null) {
            return ;
        }
        if(event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            if (MouseButton.SECONDARY == mouseEvent.getButton()) {
                mouseEvent.consume();
                hideAllMenus(mouseEvent);
            }
        }
    }

    public Configuration getConfiguration() {
        return context.getConfiguration();
    }

    public PlayerOptions getPlayerOptions() {
        return getConfiguration().getPlayerOptions();
    }

    public MenuTemplates getMenuTemplates() {
        return getMenuManager().getMenuTemplates();
    }

    public Player getPlayer() {
        return getPlayerManager().getActivePlayer();
    }

    public Track getCurrentTrack() {
        return getPlayer().getCurrentTrack();
    }

    public int getCurrentTrackIndex() {
        return getPlayerManager().getCurrentTrackIndex();
    }

    public double getVolume() {
        return getPlayer().getVolume();
    }

    public void setVolume(double volume) {
        getPlayer().setVolume(volume);
    }

    public boolean isPlaying() {
        return getPlayerManager().isPlaying();
    }

    public void togglePlay(MouseEvent event) {
        consumeContextMenuEvent(event);
        getPlayerManager().togglePlay();
    }

    public void playPrevious(MouseEvent event) {
        consumeContextMenuEvent(event);
        getPlayerManager().playPrevious();
    }

    public void playNext(MouseEvent event) {
        consumeContextMenuEvent(event);
        getPlayerManager().playNext();
    }

    public void stopPlay(MouseEvent event) {
        consumeContextMenuEvent(event);
        getPlayerManager().stopPlay();
    }

    public void toggleMute(MouseEvent event) {
        consumeContextMenuEvent(event);
        getPlayerManager().toggleVolumeMute();
    }

    public void togglePlaybackQueue(MouseEvent event) {
        consumeContextMenuEvent(event);
        getStageManager().togglePlaybackQueueShow();
    }

    public void toggleEqualizer(MouseEvent event) {
        consumeContextMenuEvent(event);
        getStageManager().toggleEqualizerShow();
    }

    public void toggleLyric(MouseEvent event) {
        consumeContextMenuEvent(event);
        getStageManager().toggleLyricShow();
    }

    public boolean isPlaybackQueueShow() {
        return getStageManager().isPlaybackQueueShow();
    }

    public boolean isEqualizerShow() {
        return getStageManager().isEqualizerShow();
    }

    public boolean isLyricShow() {
        return getStageManager().isLyricShow();
    }

    public TrackService getTrackService() {
        return context.getTrackService();
    }

    public AsyncService getAsyncService() {
        return context.getAsyncService();
    }

    public PlaybackQueue getCurrentPlaybackQueue() {
        return getPlayerManager().getCurrentPlaybackQueue();
    }

    public void setCurrentPlaybackQueueIndex(int index) {
        getPlayerManager().setCurrentPlaybackQueueIndex(index);
    }

    public int getCurrentPlaybackQueueIndex() {
        return getPlayerManager().getCurrentPlaybackQueueIndex();
    }

    public PlaybackQueue getActivePlaybackQueue() {
        return getPlayerManager().getActivePlaybackQueue();
    }

    public void setActivePlaybackQueueIndex(int index) {
        getPlayerManager().setActivePlaybackQueueIndex(index);
    }

    public int getActivePlaybackQueueIndex() {
        return getPlayerManager().getActivePlaybackQueueIndex();
    }

    public void appendToPlaybackQueue(List<File> files) {
        getTrackService().appendToPlaybackQueue(files);
    }

    public void appendToPlaybackQueue(File file) {
        getTrackService().appendToPlaybackQueue(file);
    }

    protected void handleDndAction(DndAction.DndContext ctx) {
        List<File> files = ctx.getFiles();
        if(files == null || files.isEmpty()) {
            return ;
        }
        if(files.size() == 1) {
            File file = files.get(0);
            if (file.isDirectory()) {
                appendToPlaybackQueue(file);
                return ;
            } else if (trimLowerCase(file.getName()).endsWith(".m3u")
                    || trimLowerCase(file.getName()).endsWith(".m3u8")
                    || trimLowerCase(file.getName()).endsWith(".jttpl") ) {
                restorePlaybackQueue(file);
                return ;
            }
        }

        appendToPlaybackQueue(files);
    }


    public void restorePlaybackQueue(File file) {
        getPlayerManager().addPlaybackQueue(file);
    }

    public void setCurrentIndex(int queueIndex, int trackIndex) {
        getPlayerManager().setCurrentIndex(queueIndex, trackIndex);
    }

    public List<PlaybackQueue> getPlaybackQueues() {
        return getPlayerManager().getPlaybackQueues();
    }

    public PlayerManager onVolumeMute(Consumer<Boolean> listener) {
        return getPlayerManager().onVolumeMute(listener);
    }

    public PlayerManager onPlayState(ChangeListener<? super Number> listener) {
        return getPlayerManager().onPlayState(listener);
    }

    public PlayerManager onPlayMode(ChangeListener<? super Number> listener) {
        return getPlayerManager().onPlayMode(listener);
    }

    public StageManager onPlaybackQueueShow(Consumer<Boolean> listener) {
        return getStageManager().onPlaybackQueueShow(listener);
    }

    public StageManager onEqualizerShow(Consumer<Boolean> listener) {
        return getStageManager().onEqualizerShow(listener);
    }

    public StageManager onLyricShow(Consumer<Boolean> listener) {
        return getStageManager().onLyricShow(listener);
    }

    public StageManager onLyricDesktopShow(Consumer<Boolean> listener) {
        return getStageManager().onLyricDesktopShow(listener);
    }

    public StageManager onLyricDesktopLocked(Consumer<Boolean> listener) {
        return getStageManager().onLyricDesktopLocked(listener);
    }

    public PlayerManager onPlaybackQueuesSize(ChangeListener<? super Number> listener) {
        return getPlayerManager().onPlaybackQueuesSize(listener);
    }

    public PlayerManager onTrackChanged(ChangeListener<? super String> listener) {
        return getPlayerManager().onTrackChanged(listener);
    }

    public PlayerManager onTimePosition(ChangeListener<? super Number> listener) {
        return getPlayerManager().onTimePosition(listener);
    }

    public PlayerManager onVolumeChanged(ChangeListener<? super Number> listener) {
        return getPlayerManager().onVolumeChanged(listener);
    }

    public StageManager onPlaybackQueueStageResized(Consumer<Size[]> listener) {
        return getStageManager().onPlaybackQueueStageResized(listener);
    }

    public StageManager onPlaybackQueueNamesCollapsed(ChangeListener<? super Boolean> listener) {
        return getStageManager().onPlaybackQueueNamesCollapsed(listener);
    }

    public Consumer<Size[]> getPlaybackQueueStageResizedListener() {
        return getStageManager().getPlaybackQueueStageResizedListener();
    }

    public void runFx(Runnable runnable) {
        FxUtils.runFx(runnable);
    }

    public boolean isPlaybackQueueNamesCollapsed() {
        return getStageManager().isPlaybackQueueNamesCollapsed();
    }

    public void setPlaybackQueueNamesCollapsed(boolean collapsed) {
        getStageManager().setPlaybackQueueNamesCollapsedProperty(collapsed);
    }

    public void showAppMenu(InputEvent event) {
        showAppMenu(event, null);
    }

    public void showAppMenu(InputEvent event, ShowStrategy strategy) {
        getAppMainContextMenu()
                .setShowStrategy(strategy)
                .setEvent(event)
                .show();
    }

    public void toggleAppMainMenu(MouseEvent event) {
        consumeEvent(event);
        if(event.getButton() == MouseButton.PRIMARY) {
            hideAllMenus(event);
        } else if(event.getButton() == MouseButton.SECONDARY) {
            showAppMenu(event);
        }
    }

    public MenuManager getMenuManager() {
        return context.getMenuManager();
    }

    public PopMenu getAppMainContextMenu() {
        return getMenuManager().getAppMainContextMenu();
    }

    public PopMenu getMenuBarPopMenu() {
        return getMenuManager().getMenuBarPopMenu();
    }

    public PlaybackQueueContextMenu getPlaybackQueueContextMenu() {
        return getMenuManager().getPlaybackQueueContextMenu();
    }

    public PopMenu getPlaybackQueueNavigationContextMenu() {
        return getMenuManager().getPlaybackQueueNavigationContextMenu();
    }

    public PopMenu getEqualizerNavigationContextMenu() {
        return getMenuManager().getEqualizerNavigationContextMenu();
    }

    public LyricContextMenu getLyricContextMenu() {
        return getMenuManager().getLyricContextMenu();
    }

    public LyricDesktopContextMenu getLyricDesktopContextMenu() {
        return getMenuManager().getLyricDesktopContextMenu();
    }

    public TrackContextMenu getTrackContextMenu() {
        return getMenuManager().getTrackContextMenu();
    }

    public void hideMenu(MouseEvent event, PopMenu menu) {
        consumeEvent(event);
        if(menu != null && menu.isShowing()) {
            if (event == null
                    || event.getSource() != context.getContextMenuTrigger()
                    || event.getButton() == MouseButton.PRIMARY ) {
                menu.hide();
            }
        }
    }

    public void hideAllMenus(MouseEvent event) {
        getMenuManager().getAllPopups().forEach(menu -> hideMenu(event, menu));
    }

    public void setPlaybackMode(int mode) {
        getPlayerManager().setPlaybackMode(mode);
    }

    public void toggleEqualizerEnabled() {
        getPlayerManager().toggleEqualizerEnabled();
    }

    public PlayerManager onEqualizerEnabled(Consumer<Boolean> listener) {
        return getPlayerManager().onEqualizerEnabled(listener);
    }

    public PlayerManager onEqualizerIndexChanged(Consumer<Integer> listener) {
        return getPlayerManager().onEqualizerIndexChanged(listener);
    }

    public EqualizerContextMenu getEqualizerContextMenu() {
        return getMenuManager().getEqualizerContextMenu();
    }

    public PlayModeContextMenu getPlayModeContextMenu() {
        return getMenuManager().getPlayModeContextMenu();
    }

    public double getCurrentTrackDuration() {
        Track track = getCurrentTrack();
        if(track == null) {
            return 0;
        }
        double duration = track.getTrackLength();
        if(duration <= 0) {
            duration = getPlayer().getDuration();
            if(duration > 0) {
                track.setTrackLength(duration);
            }
        }
        return Math.max(duration, 0);
    }

    //同步更新歌曲信息到相关界面
    public void refreshStagesTrackMetadata(Track track) {
        context.getControllerManager().refreshStagesTrackMetadata(track);
    }

    protected void toggleBtnHighlight(Region btn, boolean active) {
        if(btn == null) {
            return ;
        }
        String activeClass = "active";
        btn.getStyleClass().removeAll(activeClass);
        if(active) {
            btn.getStyleClass().add(activeClass);
        }
    }

    public boolean isMiniMode() {
        return getPlayerManager().isMiniMode();
    }

    public boolean isLyricDesktopMode() {
        return getStageManager().isLyricDesktopMode();
    }

    public ScheduledFuture<?> runDelay(Runnable task, long millis) {
        return context.runDelay(task, millis);
    }

    public void setActiveTab(int index) {
        if(index >= 0) {
            activeTabIndex.set(index);
        }
    }

    public void onActiveTabChanged(ChangeListener<? super Number> listener) {
        activeTabIndex.addListener(listener);
    }


    public Size getSkinItemSize(SkinXml skin, PositionBasedItem item) {
        return getSkinManager().getItemSize(skin, item);
    }

    public byte[] getSkinEntry(SkinXml skin, String entryName) {
        return getSkinManager().getSknEntry(skin.filename, entryName);
    }

    public String getSkinEntryPath(SkinXml skin, String entryName) {
        return getSkinManager().getSknEntryUrl(skin, entryName);
    }

    public Size getSkinImageSize(SkinXml skin, String imageName) {
        return getSkinManager().getImageSize(skin, imageName);
    }

    public void setAnchorAuto(Region region, SkinXml skin, SkinXmlItem item, SkinXmlWindowItem winItem) {
        double winWidth = winItem.width();
        double winHeight = winItem.height();
        Size size = getSkinImageSize(skin, winItem.image);
        if (winWidth <= 0) {
            winWidth = size.width();
        }
        if (winHeight <= 0) {
            winHeight = size.height();
        }
        if (item.isAlignRight() || item.isAlignTopRight()) {
            setAnchorAlignRight(region,
                    (winWidth - item.x2),
                    item.y1
            );
        } else if (item.isAlignBottomLeft()) {
            setAnchorAlignBottomLeft(region,
                    item.x1,
                    (winHeight - item.y2)
            );
        } else if (item.isAlignBottomRight()) {
            setAnchorAlignBottomRight(region,
                    (winWidth - item.x2),
                    (winHeight - item.y2)
            );
        } else if (item.vertical){
            setAnchorAlignBottomLeft(region,
                    item.x1,
                    (winHeight - item.y2)
            );
        } else {
            setAnchorDefault(region, item.x1, item.y1);
        }
    }

    protected void setItemsHidden(Node... items) {
        for (Node item : items) {
            item.setManaged(false);
            item.setVisible(false);
        }
    }

    protected void setItemsVisible(Node... items) {
        for (Node item : items) {
            item.setManaged(true);
            item.setVisible(true);
        }
    }

    protected void setItemsAutoVisible(boolean visible, Node... items) {
        if (visible) {
            setItemsVisible(items);
        } else {
            setItemsHidden(items);
        }
    }

}
