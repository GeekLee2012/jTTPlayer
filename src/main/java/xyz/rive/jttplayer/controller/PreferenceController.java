package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import xyz.rive.jttplayer.common.PreferenceContentMeta;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.*;

import static xyz.rive.jttplayer.common.Constants.PREFERENCE;
import static xyz.rive.jttplayer.util.FxUtils.getUserData;
import static xyz.rive.jttplayer.util.FxUtils.loadResource;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

public class PreferenceController extends CommonController {

    @FXML
    private BorderPane preference_view;
    @FXML
    private VBox navbar;
    @FXML
    private Label content_title;
    @FXML
    private BorderPane center;
    private final Map<String, Node> CONTENTS_MAP = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, preference_view, PREFERENCE);
        setupNavigation();
    }

    private void setupNavigation() {
        List<PreferenceContentMeta> list = Arrays.asList(
                new PreferenceContentMeta("关于", "\"千千静听\"的简明介绍", "preference-view-about.fxml", PreferenceAboutController.class),
                new PreferenceContentMeta("常规", "常规操作的参数设置", "preference-view-general-options.fxml", PreferenceGeneralOptionsController.class),
                new PreferenceContentMeta("播放", "播放相关的参数设置", "preference-view-play-options.fxml", PreferencePlayOptionsController.class),
                new PreferenceContentMeta("快捷键", "自定义播放器的快捷键", "preference-view-keys-options.fxml", PreferenceKeysOptionsController.class),
                new PreferenceContentMeta("视觉效果", "视觉效果相关的参数设置", "preference-view-visual-options.fxml", PreferenceVisualOptionsController.class),
                new PreferenceContentMeta("播放列表", "播放列表相关的参数设置", "preference-view-playback-queue-options.fxml", PreferencePlaybackQueueOptionsController.class),
                new PreferenceContentMeta("媒体库", "媒体库", "preference-view-media-library-options.fxml", PreferenceMediaLibraryOptionsController.class),
                new PreferenceContentMeta("歌词秀", "歌词秀相关的参数设置", "preference-view-lyric-options.fxml", PreferenceLyricOptionsController.class),
                new PreferenceContentMeta("歌词搜索", "歌词的本地以及在线搜索", "preference-view-lyric-search-options.fxml", PreferenceLyricSearchOptionsController.class),
                new PreferenceContentMeta("网络连接", "网络连接相关的参数设置", "preference-view-network-options.fxml", PreferenceNetworkOptionsController.class),
                new PreferenceContentMeta("音效插件", "激活及配置音效插件", "preference-view-sound-effect-options.fxml", PreferenceSoundEffectOptionsController.class),
                new PreferenceContentMeta("音频设备", "选择及配置音频输出设备", "preference-view-sound-devices-options.fxml", PreferenceSoundDevicesOptionsController.class),
                new PreferenceContentMeta("皮肤", "选择播放器的皮肤", "preference-view-skin-options.fxml", PreferenceSkinOptionsController.class),
                new PreferenceContentMeta("全屏显示", "全屏显示相关的参数设置", "preference-view-full-screen-options.fxml", PreferenceFullScreenOptionsController.class),
                new PreferenceContentMeta("系统关联", "关联音频文件以及创建快捷方式", "preference-view-system-association-options.fxml", PreferenceSystemAssociationOptionsController.class)
        );
        list.forEach(meta -> {
            Label item = new Label(meta.getNavTitle());
            Region graphic = new Region();
            graphic.getStyleClass().add("flag");
            item.setGraphic(graphic);
            item.setUserData(meta);
            item.setOnMouseClicked(event -> {
                setActiveNavItem(item, -1);
            });
            navbar.getChildren().add(item);
        });
        setFirstNavItemActive();
    }

    private void setFirstNavItemActive() {
        setActiveNavItem((Label) navbar.getChildren().get(0), -1);
    }

    public void setActiveNavItem(String name) {
        setActiveNavItem(name, -1);
    }

    public void setActiveNavItem(String name, int tabIndex) {
        for (Node child : navbar.getChildren()) {
            Label item = (Label) child;
            if(item.getText().equalsIgnoreCase(name)) {
                setActiveNavItem(item, tabIndex);
                break;
            }
        }
    }

    private void setActiveNavItem(Label item, int tabIndex) {
        navbar.getChildren().forEach(nItem -> {
            nItem.getStyleClass().removeAll("active");
        });
        item.getStyleClass().add("active");

        loadContent(item, tabIndex);
    }

    private void loadContent(Label item, int tabIndex) {
        center.setCenter(new Region());
        center.getCenter().getStyleClass().add("content");

        Optional.ofNullable((PreferenceContentMeta) item.getUserData())
                .ifPresent(meta -> {
                    content_title.setText(meta.getContentTitle());
                    try {
                        if(!isEmpty(meta.getResource())) {
                            center.setCenter(getCenterContent(meta));
                            center.getCenter().getStyleClass().add("content");
                            getControllerManager().onStageShown(meta.getControllerClass());
                            getControllerManager().setActiveTab(meta.getControllerClass(), tabIndex);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
        });
    }

    private Node getCenterContent(PreferenceContentMeta meta) {
        String key = meta.getResource();
        if(CONTENTS_MAP.containsKey(key)) {
            return CONTENTS_MAP.get(key);
        }
        Node content = loadResource(key);
        CONTENTS_MAP.put(key, content);
        return content;
    }

    @Override
    public void afterCloseView() {
        super.afterCloseView();
        setFirstNavItemActive();
    }

    public void applyClose(MouseEvent event) {
        consumeEvent(event);
        closeView();
        getConfiguration().save();
    }
}
