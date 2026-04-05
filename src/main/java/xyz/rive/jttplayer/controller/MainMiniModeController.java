package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import xyz.rive.jttplayer.anim.SpectrumAnimation;
import xyz.rive.jttplayer.common.GeneralOptions;
import xyz.rive.jttplayer.common.PlayState;
import xyz.rive.jttplayer.common.Track;
import xyz.rive.jttplayer.control.AnimatedTrackMetadata;
import xyz.rive.jttplayer.control.PlayTime;
import xyz.rive.jttplayer.control.ProgressBarHorizontal;
import xyz.rive.jttplayer.control.ProgressBarVertical;
import xyz.rive.jttplayer.menu.action.OpenFilesAction;
import xyz.rive.jttplayer.menu.strategy.SharedStrategies;
import xyz.rive.jttplayer.skin.SkinXml;
import xyz.rive.jttplayer.skin.SkinXmlItem;
import xyz.rive.jttplayer.skin.SkinXmlWindowItem;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.common.Constants.IMAGE_SUFFIXES;
import static xyz.rive.jttplayer.common.Constants.MAIN_MINI;
import static xyz.rive.jttplayer.util.FileUtils.exists;
import static xyz.rive.jttplayer.util.FxUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class MainMiniModeController extends CommonController {

    @FXML
    private AnchorPane app_main_mini_mode;
    @FXML
    private Region app_logo;
    @FXML
    private AnimatedTrackMetadata info;
    @FXML
    private Region stop_btn;
    @FXML
    private Region open_btn;
    @FXML
    private Region lyric_btn;
    @FXML
    private Region prev_btn;
    @FXML
    private Region play_btn;
    @FXML
    private Region pause_btn;
    @FXML
    private Region next_btn;
    @FXML
    private Region mini_btn;
    @FXML
    private Region minimode_btn;
    @FXML
    private Region close_btn;
    @FXML
    private ProgressBarHorizontal play_progress;
    @FXML
    private PlayTime play_time;
    @FXML
    private Region mute;
    @FXML
    private ProgressBarHorizontal volume_fill;
    @FXML
    private ProgressBarVertical volume_fill_v;
    @FXML
    private HBox visual_box;
    @FXML
    private ImageView track_cover;
    @FXML
    private Canvas spectrum_canvas;
    private SpectrumAnimation spectrumAnimation;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupController(this, app_main_mini_mode, MAIN_MINI);
        setupListeners();
        showVisualEffect();
    }

    private void setupListeners() {
        onPlayState((o, ov, nv) -> updateActionsStates());
        onTrackChanged((o, ov, nv) -> updateTrackMetadata());
        onTimePosition((o, ov, nv) -> {
            if(play_progress.isSliding()) {
                return ;
            }
            runFx(() -> {
                Double value = (Double) nv;
                updateTrackProgress(value == null? -1 : value.intValue());
            });
        });

        onVolumeMute(mute -> {
            runFx(this::updateVolumeState);
        });

        onLyricShow(show -> switchLyricStageByMode());
        onLyricDesktopShow(show -> switchLyricStageByMode());

        info.resetMetadataPosition();
        info.setOnMouseClicked(event -> {
            consumeEvent(event);
            if(event.getButton() == MouseButton.SECONDARY) {
                //showAppMenu(event);
                return ;
            }
            if (event.getClickCount() < 2) {
                info.nextMetadata(event);
                hideAllMenus(event);
                return ;
            }

            //双击打开文件属性
            context.setFileAttributesTrack(getCurrentTrack());
            getStageManger().getFileAttributesStage().show();
        });

        play_progress.onMouseClicked(event -> {
            hideAllMenus(event);
            Optional.ofNullable(getCurrentTrack())
                    .ifPresent(track -> {
                        double duration = track.getTrackLength();
                        int seconds = (int) (duration * play_progress.getPercentValue());
                        getPlayerManager().seekPlay(seconds);
                    });
        }).onSliding(event ->  {
            hideAllMenus(event);
            Optional.ofNullable(getCurrentTrack())
                    .ifPresent(track -> {
                        double seconds = track.getTrackLength() * play_progress.getPercentValue();
                        updateTrackProgress((int)seconds);
                    });
        });

        play_time.setOnMouseClicked(event -> {
            getPlayerManager().togglePlayTimeCountDownMode();
            play_time.setCountDownMode(getPlayerManager().isPlayTimeCountDownMode());
        });

        volume_fill.setEnabled(true);
        volume_fill.setValue(getVolume());
        onVolumeChanged((o, ov, nv) -> {
            runFx(() -> {
                volume_fill.setValue((Double) nv);
                updateVolumeState();
            });
        });

        volume_fill_v.setEnabled(true);
        volume_fill_v.setValue(getVolume());
        onVolumeChanged((o, ov, nv) -> {
            runFx(() -> {
                volume_fill_v.setValue((Double) nv);
                updateVolumeState();
            });
        });

        volume_fill.onMouseClicked(event -> {
            hideAllMenus(event);
            setVolume(volume_fill.getValue());
        }).onSliding(event -> {
            hideAllMenus(event);
            setVolume(volume_fill.getValue());
            //updateVolumeState();
        });

        volume_fill_v.onMouseClicked(event -> {
            hideAllMenus(event);
            setVolume(volume_fill_v.getValue());
        }).onSliding(event -> {
            hideAllMenus(null);
            setVolume(volume_fill_v.getValue());
            //updateVolumeState();
        });
    }

    private void updateTrackProgress(int seconds) {
        Optional.ofNullable(getCurrentTrack())
                .ifPresent(track -> {
                    if(getCurrentTrackDuration() <= 0) {
                        return ;
                    }
                    play_progress.setValue(seconds * 100D / getCurrentTrackDuration());
                    play_time.update(seconds, getCurrentTrackDuration());
                });
    }

    private void updateVolumeState() {
        mute.getStyleClass().clear();
        boolean isMute = (getPlayer().isMute()
                || getPlayer().getVolume() <= 0);
        toggleBtnHighlight(mute, isMute);
        //stereo.setText(isMute ? "静音" : lastStereoText);
    }

    private void switchLyricStageByMode() {
        showStage(getLyricStage(),
                !isMiniMode() && !isLyricDesktopMode() && isLyricShow(),
                LyricController.class);
        showStage(getLyricMiniModeStage(),
                isMiniMode() && !isLyricDesktopMode() && isLyricShow(),
                LyricMiniModeController.class);
        showStage(getLyricDesktopStage(),
                isLyricDesktopMode() && isLyricShow(),
                LyricDesktopController.class);
        if(!isLyricDesktopMode()) {
            getMainStage().setIconified(false);
        }
    }

    private void setTrackCover(Image image) {
        //默认封面
        image = (image != null && !image.isError()) ? image
                : getImage("album_cover.bmp");
        track_cover.setImage(image);
    }

    private void updateCover() {
        if (!visual_box.isVisible()) {
            return ;
        }
        Track track = getCurrentTrack();
        if(track == null) {
            setTrackCover(null);
            return;
        }
        String cover = trim(track.getCover());
        if(!isEmpty(cover)) {
            setTrackCover(new Image(cover, true));
            return ;
        }

        if(trimLowerCase(cover).startsWith("http")) {
            setTrackCover(new Image(cover, true));
            return ;
        }

        String url = track.getUrl();
        if(trimLowerCase(url).startsWith("http") && isEmpty(cover)) {
            setTrackCover(null);
            return ;
        }

        byte[] coverBytes = context.getMetadataService().readCover(url);
        Image nativeCover = null;
        if(coverBytes != null) {
            //内嵌封面
            nativeCover = new Image(new BufferedInputStream(new ByteArrayInputStream(coverBytes)));
        } else if(!url.startsWith("http")) {
            int index = url.lastIndexOf(".");
            if(index > -1) {
                String coverUrl;
                //同名文件
                for (int i = 0; i < IMAGE_SUFFIXES.size(); i++) {
                    coverUrl = url.substring(0, index).concat(IMAGE_SUFFIXES.get(i));
                    if(exists(coverUrl)) {
                        nativeCover = createImage(coverUrl);
                        break;
                    }
                }
                //名称为Cover的文件
                if (nativeCover == null) {
                    String[] coverNames = { "Cover", "cover" };
                    for (int i = 0; i < IMAGE_SUFFIXES.size(); i++) {
                        for (int j = 0; j < coverNames.length; j++) {
                            coverUrl = track.getParentUrl().concat(coverNames[j]).concat(IMAGE_SUFFIXES.get(i));
                            if(exists(coverUrl)) {
                                nativeCover = createImage(coverUrl);
                                break;
                            }
                        }
                    }
                }
            }
        }
        setTrackCover(nativeCover);
    }

    public void updateTrackMetadata() {
        GeneralOptions options = getConfiguration().getGeneralOptions();
        int interval = options.isAllowMetadataAutoSwitch() ?
                options.getMetadataAutoSwitchInterval() : Integer.MAX_VALUE;
        info.setInterval(interval);
        //track_meta.resetMetadataPosition();
        runFx(() -> {
            info.updateMetadata(
                    getCurrentTrackIndex(),
                    getCurrentTrack()
            );
            updateCover();
        });
    }

    public void updateActionsStates() {
        int state = getPlayerManager().getPlayState();
        boolean playing = isPlaying();
        boolean playable = state == PlayState.PLAYING.getValue()
                || state == PlayState.PAUSED.getValue();
        boolean seekable = playable
                && getPlayerManager().isCurrentTrackSeekable();

        play_btn.setManaged(!playing);
        play_btn.setVisible(!playing);
        pause_btn.setManaged(playing);
        pause_btn.setVisible(playing);
        stop_btn.setDisable(!playable);
        play_progress.setEnabled(seekable);

        runFx(() ->  {
            PlayState playState = PlayState.of(state);
            if(playState == PlayState.STOPPING
                    || playState == PlayState.STOPPED) {
                resetTrackProgress();
            }
        });

        showVisualEffect();
    }

    private void resetTrackProgress() {
        play_progress.setValue(0);
        play_time.update(1, 1);
    }

    public void refresh() {
        updateTrackMetadata();
        updateActionsStates();
        updateTrackProgress((int) getPlayerManager().getTimePosition());
    }

    public void minimized(MouseEvent event) {
        consumeEvent(event);
        getStageManger().minimized();
    }

    public void toggleMiniMode(MouseEvent event) {
        consumeEvent(event);
        getPlayerManager().toggleMiniMode();
    }

    public void openFiles(MouseEvent event) {
        consumeContextMenuEvent(event);
        new OpenFilesAction().handle(event);
    }

    private void showStage(Stage stage, boolean show, Class<? extends CommonController> controllerClass) {
        Optional.ofNullable(stage).ifPresent(__ -> {
            Consumer<? super CommonController> callback = CommonController::onShown;
            if (show) {
                if (stage == getLyricDesktopStage()) {
                    setBelowStageCenterAlign(stage, getMainStage(), 60);
                }
                stage.show();
            } else {
                stage.hide();
                callback = CommonController::onClosed;
            }
            getControllerManager().setupCallback(controllerClass, callback);
            setupExtraBtnAutoHighlight();
        });
    }

    private void setupExtraBtnAutoHighlight() {
        toggleBtnHighlight(lyric_btn, isLyricShow());
    }

    private SpectrumAnimation getSpectrumAnimation() {
        if (spectrumAnimation == null) {
            spectrumAnimation = new SpectrumAnimation(context, spectrum_canvas);
        }
        return spectrumAnimation;
    }

    private void startSpectrum() {
        getSpectrumAnimation().start();
    }

    private void stopSpectrum(boolean reset) {
        if (spectrumAnimation != null) {
            spectrumAnimation.stop(reset);
        }
    }

    private void stopSpectrum() {
        stopSpectrum(false);
    }

    public void showVisualEffect(boolean reset) {
        if (!visual_box.isVisible()) {
            return ;
        }
        if (getPlayerManager().isVisualCover()) {
            setItemsVisible(track_cover);
            setItemsHidden(spectrum_canvas);
            stopSpectrum();
        } else {
            setItemsHidden(track_cover);
            setItemsVisible(spectrum_canvas);
        }

        if (isPlaying()) {
            startSpectrum();
        } else  {
            stopSpectrum(reset);
        }
    }

    public void showVisualEffect() {
        showVisualEffect(false);
    }

    private void doSwitchVisualEffect() {
        int index = getPlayerOptions().getActiveVisualIndex();
        getPlayerManager().setActiveVisualIndex((index + 1) % 8);
        showVisualEffect();
    }

    public void switchVisualEffect(MouseEvent event) {
        consumeEvent(event);
        if (event.getButton() == MouseButton.PRIMARY) {
            hideAllMenus(event);
            doSwitchVisualEffect();
        } else if (event.getButton() == MouseButton.SECONDARY) {
            getMenuManager().getVisualContextMenu().show(event);
        }
    }

    public void toggleAppMainMenu2(MouseEvent event) {
        consumeEvent(event);
        if (getMenuManager().isAppMainMenuShowing()) {
            getMenuManager().hideAppMainMenu();
        } else {
            showAppMenu(event, SharedStrategies.getSharedUnder());
        }
    }

    @Override
    public void setupSkin() {
        super.setupSkin();
        setItemsHidden(
                app_logo, info, play_progress,
                lyric_btn, stop_btn, open_btn,
                mute, volume_fill, volume_fill_v,
                play_time, visual_box, track_cover,
                spectrum_canvas
        );

        SkinXml skin = getActiveSkinXml();
        SkinXmlWindowItem winItem = skin.getMiniWindow() ;
        if (winItem == null) {
            skin = getSkinManager().getDefaultSkinXml();
            winItem = skin.getMiniWindow();
        }

        for (SkinXmlItem item : winItem.items) {
            if (item.isIconItem()) {
                setItemsVisible(app_logo);
                setAnchorAuto(app_logo, skin, item, winItem);
            } else if (item.isInfoItem()) {
                setItemsVisible(info);
                setAnchorAuto(info, skin, item, winItem);
                setPrefSize(info, item.size());
                double width = item.size().width() - 5;
                info.setMinWidth(width);
                info.setMaxWidth(width);
                info.setupStyle(item.color, item.font, item.fontSize);
                info.resetMetadataPosition();
            } else if (item.isProgressItem()) {
                setItemsVisible(play_progress);
                setAnchorAuto(play_progress, skin, item, winItem);
                setPrefSize(play_progress, item.size());
                play_progress.getStylesheets().setAll(
                        getTssManager().boostrapProgressBar(skin, item, "progress-bar-mini")
                );
            } else if (item.isVisualItem()) {
                setItemsVisible(visual_box);
                setPrefSize(visual_box, item.size());
                setAnchorAuto(visual_box, skin, item, winItem);
                setFitSize(track_cover, item.height(), item.height());
                setPrefSize(spectrum_canvas, item.size());
                showVisualEffect(true);
            } else if (item.isMuteItem()) {
                setItemsVisible(mute);
                setPrefSize(mute, item.size());
                setAnchorAuto(mute, skin, item, winItem);
                setBackgroundImage(mute, skin, item.image);
            } else if (item.isVolumeItem()) {
                volume_fill.setEnabled(!item.vertical);
                Region volumeFill = item.vertical ? volume_fill_v : volume_fill;
                String cssFilename = item.vertical ? "volume-bar-vertical-mini" : "volume-bar-mini";
                setItemsVisible(volumeFill);
                setAnchorAuto(volumeFill, skin, item, winItem);
                setPrefSize(volumeFill, item.size());
                volumeFill.getStylesheets().setAll(
                        getTssManager().boostrapProgressBar(skin, item, item.vertical, cssFilename)
                );
            } else if (item.isLedItem()) {
                //setPrefSize(play_time, item.size());
                setItemsVisible(play_time);
                setAnchorAuto(play_time, skin, item, winItem);
                play_time.getStylesheets().setAll(
                        getTssManager().boostrapPlayTime(skin, item, "play-time-mini")
                );
            } else if (item.isStopItem()) {
                setItemsVisible(stop_btn);
                setAnchorAuto(stop_btn, skin, item, winItem);
            } else if (item.isOpenItem()) {
                setItemsVisible(open_btn);
                setAnchorAuto(open_btn, skin, item, winItem);
            } else if(item.isLyricItem()) {
                setItemsVisible(lyric_btn);
                setAnchorAuto(lyric_btn, skin, item, winItem);
            } else if (item.isPrevItem()) {
                setAnchorAuto(prev_btn, skin, item, winItem);
            } else if (item.isPlayItem()) {
                setAnchorAuto(play_btn, skin, item, winItem);
            } else if (item.isPauseItem()) {
                setAnchorAuto(pause_btn, skin, item, winItem);
            } else if (item.isNextItem()) {
                setAnchorAuto(next_btn, skin, item, winItem);
            } else if (item.isMinimizeItem()) {
                setAnchorAuto(mini_btn, skin, item, winItem);
            } else if (item.isMiniModeItem()) {
                setAnchorAuto(minimode_btn, skin, item, winItem);
            } else if (item.isCloseItem() || item.isExitItem()) {
                setAnchorAuto(close_btn, skin, item, winItem);
            }
        }
        
    }

}
