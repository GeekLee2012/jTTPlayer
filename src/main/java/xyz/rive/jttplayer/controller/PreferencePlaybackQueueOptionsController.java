package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import xyz.rive.jttplayer.common.PlaybackQueueOptions;
import xyz.rive.jttplayer.control.FontButton;

import java.net.URL;
import java.util.ResourceBundle;

import static xyz.rive.jttplayer.common.Constants.TAG_MEMO;
import static xyz.rive.jttplayer.util.StringUtils.trim;

public class PreferencePlaybackQueueOptionsController extends CommonController {
    @FXML
    private VBox playback_queue_options;
    @FXML
    private CheckBox allow_dnd;
    @FXML
    private CheckBox ban_disk_delete;
    @FXML
    private CheckBox show_track_tip;
    @FXML
    private FontButton font_btn;
    @FXML
    private CheckBox show_seq_no;
    @FXML
    private CheckBox use_custom_title_format;
    @FXML
    private TextField custom_title_format;
    @FXML
    private CheckBox use_default_title_format;
    @FXML
    private TextField default_title_format;
    @FXML
    private Label tag_memo;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, playback_queue_options);
        loadContent();
    }

    public void loadContent() {
        tag_memo.setText(TAG_MEMO);

        PlaybackQueueOptions options = getConfiguration().getPlaybackQueueOptions();
        //监听变更
        allow_dnd.selectedProperty().addListener((o, ov, nv) -> {
            options.setAllowDnd(nv);
        });
        show_seq_no.selectedProperty().addListener((o, ov, nv) -> {
            options.setShowSeqNo(nv);
        });
        show_track_tip.selectedProperty().addListener((o, ov, nv) -> {
            options.setShowTrackTip(nv);
        });
        use_custom_title_format.selectedProperty().addListener((o, ov, nv) -> {
            options.setUseCustomTitleFormat(nv);
        });
        custom_title_format.textProperty().addListener((o, ov, nv) -> {
            options.setCustomTitleFormat(trim(nv));
        });
        use_default_title_format.selectedProperty().addListener((o, ov, nv) -> {
            options.setUseDefaultTitleFormat(nv);
        });
        default_title_format.textProperty().addListener((o, ov, nv) -> {
            options.setDefaultTitleFormat(trim(nv));
        });
        font_btn.setOkAction(option -> {
            getPlayerManager().setPlaybackQueueFontSize(option.getSize());
        });
    }

    private void setupData() {
        PlaybackQueueOptions options = getConfiguration().getPlaybackQueueOptions();
        //加载数据
        allow_dnd.setSelected(options.isAllowDnd());
        ban_disk_delete.setSelected(options.isBanDiskDelete());
        show_track_tip.setSelected(options.isShowTrackTip());

        show_seq_no.setSelected(options.isShowSeqNo());
        use_custom_title_format.setSelected(options.isUseCustomTitleFormat());
        custom_title_format.setText(options.getCustomTitleFormat());
        use_default_title_format.setSelected(options.isUseDefaultTitleFormat());
        default_title_format.setText(options.getDefaultTitleFormat());
        font_btn.setFontSize(getPlayerManager().getPlaybackQueueFontSize(), 12);
    }

    @Override
    public void afterShowView() {
        setupData();
    }
}
