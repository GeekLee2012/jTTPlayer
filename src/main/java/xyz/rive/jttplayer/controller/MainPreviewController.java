package xyz.rive.jttplayer.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import xyz.rive.jttplayer.common.GeneralOptions;
import xyz.rive.jttplayer.common.PlayState;
import xyz.rive.jttplayer.common.Track;
import xyz.rive.jttplayer.control.AnimatedTrackMetadata;
import xyz.rive.jttplayer.control.PlayTime;
import xyz.rive.jttplayer.control.ProgressBarHorizontal;
import xyz.rive.jttplayer.menu.action.OpenFilesAction;
import xyz.rive.jttplayer.menu.action.ShowStageAction;
import xyz.rive.jttplayer.menu.strategy.SharedStrategies;
import xyz.rive.jttplayer.menu.strategy.ShowUnderItemStrategy;
import xyz.rive.jttplayer.skin.SkinXml;
import xyz.rive.jttplayer.skin.SkinXmlWindowItem;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.common.Constants.IMAGE_SUFFIXES;
import static xyz.rive.jttplayer.common.Constants.MAIN;
import static xyz.rive.jttplayer.skin.SkinXmlWindowItem.*;
import static xyz.rive.jttplayer.util.FileUtils.exists;
import static xyz.rive.jttplayer.util.FileUtils.toExternalForm;
import static xyz.rive.jttplayer.util.FxUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class MainPreviewController extends CommonController {

    @FXML
    private AnchorPane app_main;
    @FXML
    private Region top;
    @FXML
    private Region app_logo;
    @FXML
    private Region set_btn;
    @FXML
    private Region minimode_btn;
    @FXML
    private Region mini_btn;
    @FXML
    private Region close_btn;
    @FXML
    private AnimatedTrackMetadata info;
    @FXML
    private ProgressBarHorizontal play_progress;
    @FXML
    private PlayTime play_time;
    //@FXML
    //private VolumeBarVertical volume_bar;
    @FXML
    private Label stereo;
    @FXML
    private Label status;
    @FXML
    private Region stop_btn;
    @FXML
    private Region open_btn;
    @FXML
    private Region prev_btn;
    @FXML
    private Region play_btn;
    @FXML
    private Region pause_btn;
    @FXML
    private Region next_btn;
    @FXML
    private Region mode_single_btn;
    @FXML
    private Region mode_loop_btn;
    @FXML
    private Region mode_slider_btn;
    @FXML
    private Region mode_circle_btn;
    @FXML
    private Region mode_random_btn;
    @FXML
    private Region playback_queue_btn;
    @FXML
    private Region equalizer_btn;
    @FXML
    private Region lyric_btn;
    @FXML
    private Region browser_btn;
    private String lastChannelStateText = "声道";
    private String lastPlayStateText;
    @FXML
    private Region mute;
    @FXML
    private ProgressBarHorizontal volume_fill;
    @FXML
    private  HBox visual_box;
    @FXML
    private ImageView track_cover;
    private SkinXml skin;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupController(this, app_main);
    }

    @Override
    public void setupSkin() {
        super.setupSkin();
        setItemsHidden(stereo, status, stop_btn, open_btn);

        if (skin == null) {
            return ;
        }

        SkinXmlWindowItem winItem = skin.getPlayerWindow();
        winItem.items.forEach(item -> {
            if (item.isIconItem()) {
                setAnchorAuto(app_logo, skin, item, winItem);
            } //
            else if (item.isSetItem()) {
                setAnchorAuto(set_btn, skin, item, winItem);
            } else if (item.isMinimizeItem()) {
                setAnchorAuto(mini_btn, skin, item, winItem);
            } else if (item.isMiniModeItem()) {
                setAnchorAuto(minimode_btn, skin, item, winItem);
            } else if (item.isExitItem()) {
                setAnchorAuto(close_btn, skin, item, winItem);
            } //
            else if (item.isVisualItem()) {
                setPrefSize(visual_box, item.size());
                setAnchorAuto(visual_box, skin, item, winItem);
                setFitSize(track_cover, item.height(), item.height());
            } else if (item.isInfoItem()) {
                setAnchorAuto(info, skin, item, winItem);
                setPrefSize(info, item.size());
                double width = item.size().width() - 5;
                info.setMinWidth(width);
                info.setMaxWidth(width);
            } else if (item.isProgressItem()) {
                setAnchorAuto(play_progress, skin, item, winItem);
                setPrefSize(play_progress, item.size());
                play_progress.getStylesheets().setAll(
                        getTssManager().boostrapProgressBar(skin, item, "progress-bar-preview")
                );
            } else if (item.isLedItem()) {
                //setPrefSize(play_time, item.size());
                setAnchorAuto(play_time, skin, item, winItem);
                play_time.getStylesheets().setAll(
                        getTssManager().boostrapPlayTime(skin, item, "play-time-preview")
                );
            } //
            else if (item.isPrevItem()) {
                setAnchorAuto(prev_btn, skin, item, winItem);
            } else if (item.isPlayItem()) {
                setAnchorAuto(play_btn, skin, item, winItem);
            } else if (item.isPauseItem()) {
                setAnchorAuto(pause_btn, skin, item, winItem);
            } else if (item.isNextItem()) {
                setAnchorAuto(next_btn, skin, item, winItem);
            } //
            else if (item.isModeSingleItem()) {
                setAnchorAuto(mode_single_btn, skin, item, winItem);
            } else if (item.isModeLoopItem()) {
                setAnchorAuto(mode_loop_btn, skin, item, winItem);
            } else if (item.isModeSliderItem()) {
                setAnchorAuto(mode_slider_btn, skin, item, winItem);
            } else if (item.isModeCircleItem()) {
                setAnchorAuto(mode_circle_btn, skin, item, winItem);
            } else if (item.isModeRandomItem()) {
                setAnchorAuto(mode_random_btn, skin, item, winItem);
            } //
            else if (item.isMuteItem()) {
                setPrefSize(mute, item.size());
                setAnchorAuto(mute, skin, item, winItem);
            } else if (item.isVolumeItem()) {
                setAnchorAuto(volume_fill, skin, item, winItem);
                setPrefSize(volume_fill, item.size());
                volume_fill.getStylesheets().setAll(
                        getTssManager().boostrapProgressBar(skin, item, "volume-bar-preview")
                );
            } //
            else if(item.isPlaylistItem()) {
                setAnchorAuto(playback_queue_btn, skin, item, winItem);
            } else if(item.isEqualizerItem()) {
                setAnchorAuto(equalizer_btn, skin, item, winItem);
            } else if(item.isLyricItem()) {
                setAnchorAuto(lyric_btn, skin, item, winItem);
            } else if(item.isBrowserItem()) {
                setAnchorAuto(browser_btn, skin, item, winItem);
            } //
            else if (item.isStopItem()) {
                setItemsVisible(stop_btn);
                setAnchorAuto(stop_btn, skin, item, winItem);
            } else if (item.isOpenItem()) {
                setItemsVisible(open_btn);
                setAnchorAuto(open_btn, skin, item, winItem);
            } else if (item.isStereoItem()) {
                setItemsVisible(stereo);
                setAnchorAuto(stereo, skin, item, winItem);
            }  else if (item.isStatusItem()) {
                setItemsVisible(status);
                setAnchorAuto(status, skin, item, winItem);
            }
        });
    }

    public void setupPreviewSkin(SkinXml skin) {
        this.skin = skin;
        setupSkin();
    }
}