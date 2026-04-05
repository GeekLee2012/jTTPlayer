package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import xyz.rive.jttplayer.control.ColorButton;
import xyz.rive.jttplayer.control.FontButton;
import xyz.rive.jttplayer.control.TabsView;
import xyz.rive.jttplayer.skin.StandaloneXml;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class PreferenceLyricOptionsController extends CommonController {

    @FXML
    private VBox lyric_options;
    @FXML
    private TabsView lyric_mode_content;
    @FXML
    private ComboBox<String> win_mode_scroll_mode;
    @FXML
    private ComboBox<String> win_mode_alignment;
    @FXML
    private ComboBox<String> win_mode_fade_in_fade_out;
    @FXML
    private Spinner<Integer> win_mode_line_spacing;
    @FXML
    private ColorButton win_mode_color_normal;
    @FXML
    private ColorButton win_mode_color_hilight;
    @FXML
    private ColorButton win_mode_background;
    @FXML
    private FontButton win_mode_font;
    @FXML
    private FontButton win_mode_hilight_font;
    @FXML
    private FontButton desk_mode_font;
    @FXML
    private CheckBox desk_mode_font_shadow;
    @FXML
    private CheckBox desk_mode_auto_unlock;
    private boolean winModeLoaded = false;
    private boolean deskModeLoaded = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, lyric_options);
        lyric_mode_content.onTabChanged(this::setActiveTab);
        onActiveTabChanged((o, ov,nv) ->  loadContent());
    }

    private void loadContent() {
        lyric_mode_content.setActiveIndex(activeTabIndex.get());

        if (activeTabIndex.get() == 1 && !deskModeLoaded) {
            deskModeLoaded = true;

            desk_mode_font.setOkAction(option -> {
                getPlayerManager().setLyricDesktopFontSize(option.getSize());
            });

            desk_mode_font_shadow.selectedProperty().addListener((o, ov, nv) -> {
                getPlayerManager().setLyricDesktopFontShadow(nv);
            });

            desk_mode_auto_unlock.selectedProperty().addListener((o, ov, nv) -> {
                getPlayerManager().setLyricDesktopAutoUnlock(nv);
            });
        } else if(activeTabIndex.get() < 1 && !winModeLoaded){
            winModeLoaded = true;

            win_mode_scroll_mode.getItems().addAll("垂直滚动", "水平滚动");
            win_mode_alignment.getItems().addAll(
                    "左对齐(上对齐)",
                    "居中",
                    "右对齐(下对齐)"
            );
            win_mode_fade_in_fade_out.getItems().addAll(
                    "不使用",
                    "1/3高度(宽度)",
                    "1/4高度(宽度)",
                    "1/5高度(宽度)",
                    "1/6高度(宽度)",
                    "1/8高度(宽度)",
                    "1/10高度(宽度)",
                    "1/12高度(宽度)",
                    "1/16高度(宽度)"
            );

            //监听
            win_mode_alignment.valueProperty().addListener((o, ov, nv) -> {
                int index = win_mode_alignment.getItems().indexOf(nv);
                getPlayerManager().setLyricAlignment(index);
            });
            win_mode_line_spacing.valueProperty().addListener((o, ov, nv) -> {
                getPlayerManager().setLyricLineSpacing(nv);
            });
            win_mode_color_normal.valueProperty().addListener((o, ov, nv) -> {
                getPlayerManager().setLyricColorNormal(nv);
            });
            win_mode_color_hilight.valueProperty().addListener((o, ov, nv) -> {
                getPlayerManager().setLyricColorHilight(nv);
            });
            win_mode_background.valueProperty().addListener((o, ov, nv) -> {
                getPlayerManager().setLyricBackgroundColor(nv);
            });
            win_mode_font.setOkAction(option -> {
                getPlayerManager().setLyricFontSize(option.getSize());
            });
            win_mode_hilight_font.setOkAction(option -> {
                getPlayerManager().setLyricHilightFontSize(option.getSize());
            });
        }

        setupData();
    }

    private void setupData() {
        if (activeTabIndex.get() == 1) {
            desk_mode_font.setFontSize(getPlayerManager().getLyricDesktopFontSize());
            desk_mode_font_shadow.setSelected(getPlayerManager().isLyricDesktopFontShadow());
            desk_mode_auto_unlock.setSelected(getPlayerManager().isLyricDesktopAutoUnlock());
        } else {
            win_mode_background.setValue(getPlayerManager().getLyricBackgroundColor());
            win_mode_font.setFontSize(getPlayerManager().getLyricFontSize());
            win_mode_hilight_font.setFontSize(getPlayerManager().getLyricHilightFontSize());
            win_mode_alignment.setValue(win_mode_alignment.getItems()
                    .get(getPlayerManager().getLyricAlignment()));
            win_mode_line_spacing.getEditor().setText(
                    String.valueOf(getPlayerManager().getLyricLineSpacing()));
            win_mode_color_normal.setValue(getPlayerManager().getLyricColorNormal());
            win_mode_color_hilight.setValue(getPlayerManager().getLyricColorHilight());

            setupColorBtnsCustomColors();
        }
    }

    private void setupColorBtnsCustomColors() {
        StandaloneXml xml = context.getActiveLyricXml();
        Arrays.asList(win_mode_background, win_mode_color_normal, win_mode_color_hilight)
                .forEach(btn -> {
                    //TODO 用户自定义的颜色也被误清啦
                    btn.clearCustomColors();
                    btn.addCustomColors(xml.textColor, xml.hilightColor, xml.bkgndColor);
                });
    }

    @Override
    public void afterShowView() {
        loadContent();
    }

    public void goToLyricSearch(MouseEvent event) {
        consumeEvent(event);
        getControllerManager().setPreferenceActiveNavItem("歌词搜索");
    }
}
