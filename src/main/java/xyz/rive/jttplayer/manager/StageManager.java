package xyz.rive.jttplayer.manager;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.*;
import xyz.rive.jttplayer.control.StageDnmAction;
import xyz.rive.jttplayer.control.StageResizeAction;
import xyz.rive.jttplayer.controller.LyricServerEditController;
import xyz.rive.jttplayer.controller.LyricServerManageController;
import xyz.rive.jttplayer.controller.PlaybackQueueSelectionController;
import xyz.rive.jttplayer.skin.SkinXml;
import xyz.rive.jttplayer.skin.SkinXmlWindowItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static javafx.stage.Modality.WINDOW_MODAL;
import static xyz.rive.jttplayer.util.FxUtils.*;

public class StageManager extends AbstractManager {

    private Stage mainStage;
    private Scene mainScene;
    private Scene mainMiniScene;
    private Stage playbackQueueStage;
    private Stage equalizerStage;
    private Stage lyricStage;
    private Stage lyricDesktopStage;
    private Stage lyricMiniModeStage;
    private Stage preferenceStage;
    private Stage playUrlStage;
    private Stage alertStage;
    private Stage confirmStage;
    private Stage fileNameFormatStage;
    private Stage fileAttributesStage;
    private Stage fileTagEditStage;
    private Stage renamePlaybackQueueStage;
    private Stage fileQuickPositionStage;
    private Stage fileSearchStage;
    private Stage lyricOffsetStage;
    private Stage fontSelectionStage;
    private Stage playbackQueueSelectionStage;
    private Stage searchComputerStage;
    private Stage lyricServerManageStage;
    private Stage lyricServerEditStage;
    private Stage searchTrackResourceOnlineStage;

    private Consumer<Boolean> equalizerShowListener;
    private Consumer<Boolean> playbackQueueShowListener;
    private List<Consumer<Boolean>> lyricShowListeners;
    private List<Consumer<Boolean>> lyricDesktopShowListeners;
    private List<Consumer<Boolean>> lyricDesktopLockedListeners;
    private Consumer<Size[]> playbackQueueStageResizedListener;

    private Consumer<WindowEvent> fileAttributesShowHandler;
    private boolean fileAttributesShowHandlerChanged = false;

    private final BooleanProperty playbackQueueNamesCollapsedProperty = new SimpleBooleanProperty(false);
    private StageResizeAction playbackQueueResizeAction;
    private StageResizeAction lyricResizeAction;


    public StageManager(ApplicationContext context) {
        super(context);
    }

    private Bound getStageBound(Stage stage) {
        return new Bound(
                stage.getX(),
                stage.getY(),
                stage.getWidth(),
                stage.getHeight()
        );
    }

    public void hideAllPopups() {
        getContext().getMenuManager().hideAllPopups();
    }

    public StageManager setMainStage(Stage stage) {
        mainStage = stage;
        setupStageCommon(mainStage, null,
                "jTTPlayer", "TTPlayer.png", null);
        return this;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public SkinManager getSkinManager() {
        return getContext().getSkinManager();
    }


    private void setupMainScene() {
        boolean isMiniMode = getPlayerOptions().isMiniMode();
        Stage stage = getMainStage();
        if(isMiniMode) {
            if (mainMiniScene == null) {
                mainMiniScene = stage.getScene();
            }
        } else if (mainScene == null) {
            mainScene = stage.getScene();
        }
    }

    public void setupStagesByMode(boolean restoring) {
        Stage stage = getMainStage();
        boolean isMiniMode = getPlayerOptions().isMiniMode();

        SkinXml skin = getContext().getActiveSkinXml();
        Size size = getSkinManager().getItemSize(skin,
                isMiniMode ? skin.getMiniWindow()
                        : skin.getPlayerWindow());
        String layout = isMiniMode ?
                "main-view-mini-mode.fxml"
                : "main-view.fxml";
        Scene scene = isMiniMode ? mainMiniScene : mainScene;

        if (isMiniMode && !restoring) {
            saveStagesBounds();
        }

        setupStageCommon(stage, scene, layout, size.width(), size.height())
                .show();
        setupMainScene();

        stage.setMaxWidth(size.width());
        stage.setMaxHeight(size.height());
        stage.getScene().getStylesheets().setAll(
                isMiniMode ? getContext().getTssManager().boostrapPlayerMiniWindow(skin)
                        : getContext().getTssManager().boostrapPlayerWindow(skin)
        );

        setupStageAutoAttached(stage);
        toggleStagesByMode();
    }

    public Stage getPlaybackQueueStage() {
        if(playbackQueueStage == null) {
            SkinXml skin = getContext().getActiveSkinXml();
            Size size = getSkinManager().getItemSize(skin, skin.getPlaylistWindow());
            playbackQueueStage = createStage("playback-queue-view.fxml",
                    getMainStage(), "播放列表", size.width(), size.height());
            playbackQueueStage.getScene().getStylesheets().setAll(
                    getContext().getTssManager().boostrapPlaylistWindow(skin)
            );
            setupStageAutoAttached(playbackQueueStage);
            playbackQueueResizeAction = setResizable(playbackQueueStage)
                    .setMinSize(size.width(), size.height())
                    .onResized(getPlaybackQueueStageResizedListener());
            //setBelowStage(playbackQueueStage, getMainStage());
            resetStagePosition(playbackQueueStage, skin.getPlaylistWindow());
        }
        return playbackQueueStage;
    }

    public Stage getEqualizerStage() {
        if(equalizerStage == null) {
            SkinXml skin = getContext().getActiveSkinXml();
            SkinXmlWindowItem winItem = skin.getEqualizerWindow();
            if (winItem == null) {
                return equalizerStage;
            }
            Size size = getSkinManager().getItemSize(skin, winItem);
            equalizerStage = createStage("equalizer-view.fxml",
                    getMainStage(), "均衡器", size.width(), size.height());
            equalizerStage.getScene().getStylesheets().setAll(
                    getContext().getTssManager().boostrapEqualizerWindow(skin)
            );
            equalizerStage.setMaxWidth(size.width());
            equalizerStage.setMaxHeight(size.height());
            equalizerStage.setResizable(false);
            setupStageAutoAttached(equalizerStage);
            //setBelowStage(equalizerStage, getMainStage());
            resetStagePosition(equalizerStage, skin.getEqualizerWindow());
        }
        return equalizerStage;
    }

    public Stage getLyricStage() {
        if(lyricStage == null) {
            SkinXml skin = getContext().getActiveSkinXml();
            Size size = getSkinManager().getItemSize(skin, skin.getLyricWindow());
            lyricStage = createStage("lyric-view.fxml",
                    getMainStage(), "歌词秀", size.width(), size.height());
            lyricStage.getScene().getStylesheets().setAll(
                    getContext().getTssManager().boostrapLyricWindow(skin)
            );
            setupStageAutoAttached(lyricStage);
            lyricResizeAction = setResizable(lyricStage)
                    .setMinSize(size.width(), size.height());
            //setBelowStage(lyricStage, getMainStage());
            resetStagePosition(lyricStage, skin.getLyricWindow());
        }
        return lyricStage;
    }

    public Stage getLyricDesktopStage() {
        if(lyricDesktopStage == null) {
            double width = 520;
            double height = 99;
            SkinXml skin = getContext().getActiveSkinXml();
            lyricDesktopStage = createStage("lyric-desktop-view.fxml",
                    null, "歌词秀", width, height);
            lyricDesktopStage.getScene().getStylesheets().setAll(
                    getContext().getTssManager().boostrapDesklrcBar(skin)
            );
            Optional.ofNullable(getUserData(lyricDesktopStage, StageDnmAction.class))
                            .ifPresent(action -> {
                                action.onMoving((e, p) -> {
                                            hideAllPopups();
                                            getContext().getControllerManager()
                                                    .setLyricDesktopToolbarVisible(true);
                                        }).
                                        onMoveFinished((e, p) -> hideAllPopups());
                                action.setPropagation(true);
                            });
            setResizable(lyricDesktopStage).setMinSize(width, height);
        }
        return lyricDesktopStage;
    }

    public Stage getPreferenceStage() {
        if(preferenceStage == null) {
            preferenceStage = createModalityStage("preference-view.fxml", getMainStage(),
                    "千千静听 - 选项", 609, 539, WINDOW_MODAL);
        }
        return preferenceStage;
    }

    public Stage getPlayUrlStage() {
        if(playUrlStage == null) {
            playUrlStage = createModalityStage("play-url-view.fxml",
                    getMainStage(), 396, 365, WINDOW_MODAL);
        }
        return playUrlStage;
    }

    public Stage getAlertStage() {
        if(alertStage == null) {
            alertStage = createModalityStage("alert-view.fxml",
                    getMainStage(), 396, 156, WINDOW_MODAL);
        }
        return alertStage;
    }

    public Stage getConfirmStage() {
        if(confirmStage == null) {
            confirmStage = createModalityStage("confirm-view.fxml",
                    getMainStage(), 396, 163, WINDOW_MODAL);
        }
        return confirmStage;
    }

    public Stage getFileNameFormatStage() {
        if(fileNameFormatStage == null) {
            fileNameFormatStage = createModalityStage("file-name-format-view.fxml",
                    getMainStage(), 396, 275, WINDOW_MODAL);
        }
        return fileNameFormatStage;
    }

    public Stage getFileQuickPositionStage() {
        if(fileQuickPositionStage == null) {
            fileQuickPositionStage = createModalityStage("file-quick-position-view.fxml",
                    getMainStage(), 396, 105, WINDOW_MODAL);
        }
        return fileQuickPositionStage;
    }

    public Stage getFileSearchStage() {
        if(fileSearchStage == null) {
            fileSearchStage = createModalityStage("file-search-view.fxml",
                    getMainStage(), 396, 163, WINDOW_MODAL);
        }
        return fileSearchStage;
    }

    public Stage getLyricOffsetStage() {
        if(lyricOffsetStage == null) {
            lyricOffsetStage = createModalityStage("lyric-offset-view.fxml",
                    getMainStage(), 366, 139, WINDOW_MODAL);
        }
        return lyricOffsetStage;
    }

    public Stage getLyricMiniModeStage() {
        if(lyricMiniModeStage == null) {
            double width = 296;
            SkinXml skin = getContext().getActiveSkinXml();
            Size size = getSkinManager().getItemSize(skin, skin.getMiniWindow());
            lyricMiniModeStage = createStage("lyric-view-mini-mode.fxml",
                    getMainStage(), "歌词秀迷你模式", width, size.height());
            lyricMiniModeStage.getScene().getStylesheets().setAll(
                    getContext().getTssManager().boostrapLyricMiniWindow(skin)
            );

            Optional.ofNullable(getUserData(lyricMiniModeStage, StageDnmAction.class))
                            .ifPresent(action -> action.setEnable(false));

            lyricMiniModeStage.setOnShown(__ -> runFx(this::setLyricMiniAttachToMainStage));
        }
        return lyricMiniModeStage;
    }

    private void setLyricMiniAttachToMainStage() {
        if (lyricMiniModeStage == null) {
            return ;
        }
        boolean toRight = true;
        //正右方
        if(toRight) {
            lyricMiniModeStage.setX(getMainStage().getX() + getMainStage().getWidth());
            lyricMiniModeStage.setY(getMainStage().getY());
        } else {
            //正下方
            lyricMiniModeStage.setX(getMainStage().getX());
            lyricMiniModeStage.setY(getMainStage().getY() + getMainStage().getHeight());
            //lyricMiniModeStage.setY(getMainStage().getY() + 30);
        }
    }

    public Stage getFontSelectionStage() {
        if(fontSelectionStage == null) {
            fontSelectionStage = createModalityStage("font-selection-view.fxml",
                    getMainStage(), 606, 531, WINDOW_MODAL);
        }
        return fontSelectionStage;
    }

    public Stage getPlaybackQueueSelectionStage() {
        if(playbackQueueSelectionStage == null) {
            playbackQueueSelectionStage = createModalityStage("playback-queue-selection-view.fxml",
                    getMainStage(), 321, 404, WINDOW_MODAL);
            playbackQueueSelectionStage.setOnShown(event -> {
                hideAllPopups();
                getContext().getControllerManager()
                        .onStageShown(PlaybackQueueSelectionController.class);
            });
        }
        return playbackQueueSelectionStage;
    }

    public Stage getSearchComputerStage() {
        if(searchComputerStage == null) {
            double width = 725;
            double height = 569;
            searchComputerStage = createStage("search-computer-view.fxml",
                    getMainStage(), width, height);
            setResizable(searchComputerStage).setMinSize(width, height);
            searchComputerStage.setOnShown(event -> hideAllPopups());
        }
        return searchComputerStage;
    }

    public Stage getRenamePlaybackQueueStage() {
        if(renamePlaybackQueueStage == null) {
            renamePlaybackQueueStage = createModalityStage("rename-playback-queue-view.fxml",
                    getMainStage(), 396, 150, WINDOW_MODAL);
        }
        return renamePlaybackQueueStage;
    }

    public Stage getFileAttributesStage() {
        try {
            if (fileAttributesStage == null) {
                fileAttributesStage = createModalityStage("file-attributes-view.fxml",
                        getMainStage(), 606, 509, WINDOW_MODAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(fileAttributesShowHandler != null
                && fileAttributesShowHandlerChanged
                && fileAttributesStage != null) {
            fileAttributesStage.setOnShown(event -> fileAttributesShowHandler.accept(event));
            fileAttributesShowHandlerChanged = false;
        }
        return fileAttributesStage;
    }

    public Stage getFileTagEditStage() {
        if(fileTagEditStage == null) {
            fileTagEditStage = createModalityStage("file-tag-edit-view.fxml",
                    getMainStage(), 431, 325, WINDOW_MODAL);
        }
        return fileTagEditStage;
    }

    public void showAlert(String text) {
        getAlertStage().show();
        getContext().getControllerManager().setupAlert(text);
    }

    public void showConfirm(String text, Runnable okAction) {
        getConfirmStage().show();
        getContext().getControllerManager()
                .setupConfirm(text, okAction);
    }

    public void setupStageAutoAttached(Stage stage) {
        Optional.ofNullable(getUserData(stage, StageDnmAction.class))
                .ifPresent(action -> {
                    if (isMacOS() || isWindows()) {
                        action.onMoving((e, p) -> doStageAutoAttached(stage, e, p));
                    } else {
                        action.onMoveFinished((e, p) -> doStageAutoAttached(stage, e, new Position[] {
                                p, new Position(stage.getX(), stage.getY())
                        }));
                    }
                });
    }

    private void onMainStageMoved(MouseEvent event, Position[] positions) {
        Position fromPos = positions[0];
        Position toPos = positions[1];
        double offsetX = toPos.x() - fromPos.x();
        double offsetY = toPos.y() - fromPos.y();

        boolean isMiniMode = getContext().getPlayerManager().isMiniMode();
        setupStagePositionByOffset(playbackQueueStage, isPlaybackQueueShow(), offsetX, offsetY);
        setupStagePositionByOffset(lyricStage, isLyricShow() && !isMiniMode, offsetX, offsetY);
        setupStagePositionByOffset(equalizerStage, isEqualizerShow(), offsetX, offsetY);
        setupStagePositionByOffset(lyricMiniModeStage, isLyricShow() && isMiniMode, offsetX, offsetY);
    }

    private void doStageAutoAttached(Stage currentStage, MouseEvent event, Position[] positions) {
        hideAllPopups();

        if(currentStage == mainStage && !isMacOS()) {
            onMainStageMoved(event, positions);
            return ;
        }
        GeneralOptions options = getContext().getConfiguration().getGeneralOptions();
        double limit = options.getWinAutoAttachLimit();
        if (!options.isAllowWinAutoAttach() || limit <= 0) {
            return ;
        }

        Stage[] stages = { mainStage, playbackQueueStage, lyricStage, equalizerStage };
        for (Stage stage : stages) {
            if (stage == null || stage == currentStage) {
                continue;
            }
            double x = currentStage.getX() + currentStage.getWidth();
            double y = currentStage.getY() + currentStage.getHeight();

            double x0 = stage.getX() + stage.getWidth();
            double y0 = stage.getY() + stage.getHeight();

            //正下方
            if(currentStage.getY() - y0 >= 0
                    && currentStage.getY() - y0 <= limit) {
                currentStage.setY(y0);
                if(Math.abs(currentStage.getX() - stage.getX()) <= limit) {
                    currentStage.setX(stage.getX());
                }
                break;
            }
            //正右方
            if(currentStage.getX() - x0 >= 0
                    && currentStage.getX() - x0 <= limit) {
                currentStage.setX(x0);
                if(Math.abs(currentStage.getY() - stage.getY()) <= limit) {
                    currentStage.setY(stage.getY());
                }
                break;
            }
            //正左方
            if(stage.getX() - x >= 0
                    && stage.getX() - x <= limit) {
                currentStage.setX(stage.getX() - currentStage.getWidth());
                if(Math.abs(currentStage.getY() - stage.getY()) <= limit) {
                    currentStage.setY(stage.getY());
                }
                break;
            }
            //正上方
            if(stage.getY() - y >= 0
                    && stage.getY() - y <= limit) {
                currentStage.setY(stage.getY() - currentStage.getHeight());
                if(Math.abs(currentStage.getX() - stage.getX()) <= limit) {
                    currentStage.setX(stage.getX());
                }
                break;
            }
        }
    }

    public Consumer<Size[]> getPlaybackQueueStageResizedListener() {
        return playbackQueueStageResizedListener;
    }


    public StageManager onPlaybackQueueStageResized(Consumer<Size[]> listener) {
        this.playbackQueueStageResizedListener = listener;
        return this;
    }

    public StageManager onPlaybackQueueShow(Consumer<Boolean> listener) {
        this.playbackQueueShowListener = listener;
        return this;
    }

    public StageManager onEqualizerShow(Consumer<Boolean> listener) {
        this.equalizerShowListener = listener;
        return this;
    }

    public StageManager onLyricShow(Consumer<Boolean> listener) {
        if (lyricShowListeners == null) {
            lyricShowListeners = new ArrayList<>(6);
        }
        if(!lyricShowListeners.contains(listener)) {
            lyricShowListeners.add(listener);
        }
        return this;
    }

    public StageManager onLyricDesktopShow(Consumer<Boolean> listener) {
        if (lyricDesktopShowListeners == null) {
            lyricDesktopShowListeners = new ArrayList<>(6);
        }
        if(!lyricDesktopShowListeners.contains(listener)) {
            lyricDesktopShowListeners.add(listener);
        }
        return this;
    }

    public StageManager onLyricDesktopLocked(Consumer<Boolean> listener) {
        if (lyricDesktopLockedListeners == null) {
            lyricDesktopLockedListeners = new ArrayList<>(6);
        }
        if(!lyricDesktopLockedListeners.contains(listener)) {
            lyricDesktopLockedListeners.add(listener);
        }
        return this;
    }

    public void minimized() {
        getMainStage().setIconified(true);
    }

    private void restoreStageBound(Stage stage, Bound bound) {
        if(stage == null || bound == null) {
            return ;
        }
        double x = getMainStage().getX();
        double y = getMainStage().getY();

        stage.setWidth(bound.getWidth());
        stage.setHeight(bound.getHeight());
        stage.setX(x + bound.getX());
        stage.setY(y + bound.getY());
    }

    public PlayerOptions getPlayerOptions() {
        return getContext().getConfiguration().getPlayerOptions();
    }


    public Bound getStageRelativeBound(Stage stage, boolean showing) {
        if(stage == null || !showing) {
            return null;
        }
        double x = getMainStage().getX();
        double y = getMainStage().getY();
        return new Bound(stage.getX() - x, stage.getY() - y,
                stage.getWidth(), stage.getHeight());
    }

    public void saveStagesBounds() {
        PlayerOptions options = getPlayerOptions();
        options.setPlaybackQueueViewBound(
                getStageRelativeBound(getPlaybackQueueStage(), options.isPlaybackQueueShow()));
        options.setEqualizerViewBound(
                getStageRelativeBound(getEqualizerStage(), options.isEqualizerShow()));
        options.setLyricViewBound(
                getStageRelativeBound(getLyricStage(), options.isLyricShow()));
    }

    public void toggleStagesByMode() {
        PlayerOptions options = getPlayerOptions();
        setLyricShow(options.isLyricShow());
        setLyricDesktopMode(options.isLyricDesktopMode());
        if(options.isMiniMode()) {
            Optional.ofNullable(getPlaybackQueueStage())
                    .ifPresent(Stage::hide);
            Optional.ofNullable(getEqualizerStage())
                    .ifPresent(Stage::hide);
        } else {
            setLyricViewAlwaysOnTop(options.isLyricViewAlwaysOnTop());

            setEqualizerShow(options.isEqualizerShow());
            setPlaybackQueueShow(options.isPlaybackQueueShow());

            restoreStageBound(getEqualizerStage(),
                    options.getEqualizerViewBound());
            restoreStageBound(getPlaybackQueueStage(),
                    options.getPlaybackQueueViewBound());
            restoreStageBound(getLyricStage(),
                    options.getLyricViewBound());
        }
    }

    public void toggleLyricAlwaysTop() {
        boolean alwaysOnTop = !getPlayerOptions().isLyricViewAlwaysOnTop();
        setLyricViewAlwaysOnTop(alwaysOnTop);
        getContext().getControllerManager().updateLyricOntopState(isLyricDesktopMode()) ;
    }

    public void setLyricViewAlwaysOnTop(boolean alwaysOnTop) {
        getPlayerOptions().setLyricViewAlwaysOnTop(alwaysOnTop);
        getLyricStage().setAlwaysOnTop(alwaysOnTop);
        getContext().getMenuManager().getLyricContextMenu()
                .setAlwaysOnTop(alwaysOnTop);
    }

    public void setPlaybackQueueShow(boolean show) {
        getPlayerOptions().setPlaybackQueueShow(show);
        Optional.ofNullable(playbackQueueShowListener).ifPresent(
                listener -> listener.accept(show));
    }

    public void setLyricShow(boolean show) {
        getPlayerOptions().setLyricShow(show);
        Optional.ofNullable(lyricShowListeners).ifPresent(
                listeners -> listeners.forEach(item -> item.accept(show)));
    }

    public void setLyricDesktopMode(boolean show) {
        getPlayerOptions().setLyricDesktopMode(show);
        Optional.ofNullable(lyricDesktopShowListeners).ifPresent(
                listeners -> listeners.forEach(item -> item.accept(show)));
        getContext().getMenuManager().getAppMainContextMenu().refresh();
    }


    public void setEqualizerShow(boolean show) {
        getPlayerOptions().setEqualizerShow(show);
        Optional.ofNullable(equalizerShowListener).ifPresent(
                listener -> listener.accept(show));
    }

    public void togglePlaybackQueueShow() {
        setPlaybackQueueShow(!isPlaybackQueueShow());
    }

    public boolean isPlaybackQueueShow() {
        return getPlayerOptions().isPlaybackQueueShow();
    }

    public void toggleEqualizerShow() {
        setEqualizerShow(!isEqualizerShow());
    }

    public boolean isEqualizerShow() {
        return getPlayerOptions().isEqualizerShow();
    }

    public void toggleLyricShow() {
        setLyricShow(!isLyricShow());
    }

    public void toggleLyricDesktopShow() {
        setLyricDesktopMode(!isLyricDesktopMode());
    }

    public void setLyricDesktopLocked(boolean locked) {
        getPlayerOptions().setLyricDesktopLocked(locked);
        Optional.ofNullable(lyricDesktopLockedListeners).ifPresent(
                listeners -> listeners.forEach(item -> item.accept(locked)));
        getContext().getMenuManager().getAppMainContextMenu().refresh();
    }

    public boolean isLyricShow() {
        return getPlayerOptions().isLyricShow();
    }

    public boolean isLyricDesktopMode() {
        return getPlayerOptions().isLyricDesktopMode();
    }

    public boolean isPlaybackQueueNamesCollapsed() {
        return playbackQueueNamesCollapsedProperty.get();
    }

    public void setPlaybackQueueNamesCollapsedProperty(boolean collapsed) {
        playbackQueueNamesCollapsedProperty.set(collapsed);
    }

    public StageManager onPlaybackQueueNamesCollapsed(ChangeListener<? super Boolean> listener) {
        onProperty(playbackQueueNamesCollapsedProperty, listener);
        return this;
    }

    public void onFileAttributesShow(Consumer<WindowEvent> handler) {
        fileAttributesShowHandler = handler;
        fileAttributesShowHandlerChanged = true;
    }

    public double getStagesOpacity() {
        return getPlayerOptions().getOpacity();
    }

    public void setStagesOpacity(double value) {
        getPlayerOptions().setOpacity(value);
        getMainStage().setOpacity(value);
        getEqualizerStage().setOpacity(value);
        getPlaybackQueueStage().setOpacity(value);
        getLyricStage().setOpacity(value);
    }

    public boolean isStagesIgnoreOpacityOnActive() {
        return getPlayerOptions().isIgnoreOpacityOnActive();
    }

    public void toggleStagesActiveOpacityOption() {
        getPlayerOptions().setIgnoreOpacityOnActive(
                !isStagesIgnoreOpacityOnActive());
    }

    private List<Stage> getExtraStages() {
        return Arrays.asList(
                getEqualizerStage(),
                getPlaybackQueueStage(),
                getLyricStage());
    }

    public List<Integer> getAttachedStageIndexes(Bound mainBound) {
        List<Stage> stages = getExtraStages();

        List<Integer> showingIndexes = new ArrayList<>(stages.size());
        for (int i = 0; i < stages.size(); i++) {
            if(stages.get(i).isShowing()) {
                showingIndexes.add(i);
            }
        }

        List<Integer> attachedIndexes = new ArrayList<>(stages.size());
        for (int index : showingIndexes) {
            if(isAttached(mainBound, stages.get(index))) {
                attachedIndexes.add(index);
            }
        }

        if(attachedIndexes.isEmpty()) {
            return null;
        }

        showingIndexes.removeIf(attachedIndexes::contains);
        List<Integer> indirectAttachedIndexes = new ArrayList<>(showingIndexes.size());
        for (int si : showingIndexes) {
            for (int ai : attachedIndexes) {
                if(isAttached(stages.get(ai), stages.get(si))) {
                    indirectAttachedIndexes.add(si);
                }
            }
        }

        if(!indirectAttachedIndexes.isEmpty()) {
            attachedIndexes.addAll(indirectAttachedIndexes);
        }

        return attachedIndexes;
    }

    public void refreshSkin() {
        boolean isMiniMode = getPlayerOptions().isMiniMode();

        SkinXml skin = getContext().getActiveSkinXml();
        if (skin == null) {
            return ;
        }
        Size size = getSkinManager().getItemSize(skin,
                isMiniMode ? skin.getMiniWindow()
                        : skin.getPlayerWindow());
        mainStage.setWidth(size.width());
        mainStage.setHeight(size.height());
        mainStage.setMinWidth(size.width());
        mainStage.setMinHeight(size.height());
        mainStage.setMaxWidth(size.width());
        mainStage.setMaxHeight(size.height());
        mainStage.getScene().getStylesheets().setAll(
                isMiniMode ? getContext().getTssManager().boostrapPlayerMiniWindow(skin)
                        : getContext().getTssManager().boostrapPlayerWindow(skin)
        );

        SkinXmlWindowItem playlistWin = skin.getPlaylistWindow();
        SkinXmlWindowItem lyricWin = skin.getLyricWindow();
        SkinXmlWindowItem eqWin = skin.getEqualizerWindow();

        setPlaybackQueueShow(playlistWin != null);
        setLyricShow(lyricWin != null);
        setEqualizerShow(eqWin != null);

        if (playbackQueueStage != null && playlistWin != null) {
            size = getSkinManager().getItemSize(skin, playlistWin);
            playbackQueueStage.setWidth(size.width());
            playbackQueueStage.setHeight(size.height());
            playbackQueueStage.setMinWidth(size.width());
            playbackQueueStage.setMinHeight(size.height());
            playbackQueueResizeAction.setMinSize(size.width(), size.height());
            playbackQueueStage.getScene().getStylesheets().setAll(
                    getContext().getTssManager().boostrapPlaylistWindow(skin)
            );
        }

        if (lyricStage != null && lyricWin != null) {
            size = getSkinManager().getItemSize(skin, lyricWin);
            lyricStage.setWidth(size.width());
            lyricStage.setHeight(size.height());
            lyricStage.setMinWidth(size.width());
            lyricStage.setMinHeight(size.height());
            lyricResizeAction.setMinSize(size.width(), size.height());
            lyricStage.getScene().getStylesheets().setAll(
                    getContext().getTssManager().boostrapLyricWindow(skin)
            );
        }

        if (lyricDesktopStage != null) {
            //lyricDesktopStage.setWidth(520);
            //lyricDesktopStage.setHeight(99);
            lyricDesktopStage.getScene().getStylesheets().setAll(
                    getContext().getTssManager().boostrapDesklrcBar(skin)
            );
        }

        if (lyricMiniModeStage != null) {
            lyricMiniModeStage.getScene().getStylesheets().setAll(
                    getContext().getTssManager().boostrapLyricMiniWindow(skin)
            );
            size = getSkinManager().getItemSize(skin, skin.getMiniWindow());
            lyricMiniModeStage.setHeight(size.height());
            setLyricMiniAttachToMainStage();
        }

        if (equalizerStage != null && eqWin != null) {
            size = getSkinManager().getItemSize(skin, eqWin);
            equalizerStage.setWidth(size.width());
            equalizerStage.setHeight(size.height());
            equalizerStage.setMinWidth(size.width());
            equalizerStage.setMinHeight(size.height());
            equalizerStage.setMaxWidth(size.width());
            equalizerStage.setMaxHeight(size.height());
            equalizerStage.setResizable(false);
            equalizerStage.getScene().getStylesheets().setAll(
                    getContext().getTssManager().boostrapEqualizerWindow(skin)
            );
        }
        setupStagesAutoLayout();
    }

    public void setupStagesAutoLayout0() {
        Stage upStage = mainStage;
        if(isPlaybackQueueShow()) {
            playbackQueueStage.setWidth(playbackQueueStage.getMinWidth());
            playbackQueueStage.setHeight(playbackQueueStage.getMinHeight());
            setBelowStage(playbackQueueStage, mainStage);
            upStage = playbackQueueStage;
        }
        if(isLyricShow()) {
            setBelowStage(lyricStage, upStage);
            upStage = lyricStage;
            lyricStage.setWidth(lyricStage.getMinWidth());
            getLyricStage().setHeight(lyricStage.getMinHeight());
            getContext().getControllerManager().refreshLyricView();
        }
        if(isEqualizerShow()) {
            setBelowStage(equalizerStage, upStage);
            //upStage = getEqualizerStage();
        }
    }

    public void resetStagePosition(Stage stage, SkinXmlWindowItem winItem) {
        if (stage != null) {
            double x = mainStage.getX();
            double y = mainStage.getY();
            stage.setX(x + winItem.x1);
            stage.setY(y + winItem.y1);
        }
    }

    public void setupStagesAutoLayout() {
        SkinXml skinXml = getContext().getActiveSkinXml();
        if(isPlaybackQueueShow()) {
            getPlaybackQueueStage().show();
            playbackQueueStage.setWidth(playbackQueueStage.getMinWidth());
            playbackQueueStage.setHeight(playbackQueueStage.getMinHeight());
            resetStagePosition(playbackQueueStage, skinXml.getPlaylistWindow());
        }
        if(isLyricShow()) {
            getLyricStage().show();
            lyricStage.setWidth(lyricStage.getMinWidth());
            lyricStage.setHeight(lyricStage.getMinHeight());
            resetStagePosition(lyricStage, skinXml.getLyricWindow());
            getContext().getControllerManager().refreshLyricView();
        }
        if(isEqualizerShow()) {
            getEqualizerStage().show();
            equalizerStage.setWidth(equalizerStage.getMinWidth());
            equalizerStage.setHeight(equalizerStage.getMinHeight());
            resetStagePosition(equalizerStage, skinXml.getEqualizerWindow());
        }
    }

    public Stage getLyricServerManageStage() {
        if(lyricServerManageStage == null) {
            lyricServerManageStage = createModalityStage("lyric-server-manage-view.fxml",
                    getMainStage(), 321, 404, WINDOW_MODAL);
            lyricServerManageStage.setOnShown(event -> {
                hideAllPopups();
                getContext().getControllerManager()
                        .onStageShown(LyricServerManageController.class);
            });
        }
        return lyricServerManageStage;
    }

    public Stage getLyricServerEditStage() {
        if(lyricServerEditStage == null) {
            lyricServerEditStage = createModalityStage("lyric-server-edit-view.fxml",
                    getMainStage(), 431, 325, WINDOW_MODAL);
            lyricServerEditStage.setOnShown(event -> {
                hideAllPopups();
                getContext().getControllerManager()
                        .onStageShown(LyricServerEditController.class);
            });
        }
        return lyricServerEditStage;
    }

    public Stage getSearchTrackResourceOnlineStage() {
        if(searchTrackResourceOnlineStage == null) {
            searchTrackResourceOnlineStage = createModalityStage("search-track-resource-online-view.fxml",
                    getMainStage(), 618, 455, WINDOW_MODAL);
            searchTrackResourceOnlineStage.setOnShown(event -> hideAllPopups());
        }
        return searchTrackResourceOnlineStage;
    }

}
