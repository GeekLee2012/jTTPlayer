package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import xyz.rive.jttplayer.skin.SkinXml;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static xyz.rive.jttplayer.skin.Constants.DEFAULT_SKIN_NAME;
import static xyz.rive.jttplayer.util.FileUtils.guessSimpleName;
import static xyz.rive.jttplayer.util.FileUtils.transformPath;
import static xyz.rive.jttplayer.util.FxUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.contentEquals;

public class PreferenceSkinOptionsController extends CommonController {

    @FXML
    private VBox skin_options;
    @FXML
    private TextField skin_root;
    @FXML
    private ListView<Label> skin_list;
    @FXML
    private Label skin_name;
    @FXML
    private StackPane skin_preview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, skin_options);
        loadContent();
    }

    public void loadContent() {
        skin_root.setText(transformPath(getPlayerOptions().getSkinRoot()));
        setupSkinList();
        //Region previewNode = loadResource("main-view-preview.fxml");
        skin_preview.getChildren().setAll(new Label("预览暂未开发"));
    }

    public void setupSkinList() {
        skin_list.getItems().clear();
        getSkinManager().getSkins().forEach((key, value) -> {
            String name = guessSimpleName(key);
            Label item = new Label(name);
            item.prefWidthProperty().bind(skin_list.widthProperty().add(-26));
            item.setUserData(key);
            item.setOnMouseClicked(event -> {
                if(event.getClickCount() > 1
                        && event.getButton() == MouseButton.PRIMARY) {
                    getPlayerManager().setActiveSkin(key);
                } else {
                    previewSkin(key);
                }
            });
            if (contentEquals(DEFAULT_SKIN_NAME, key)) {
                item.setText("<默认皮肤>");
                skin_list.getItems().add(0, item);
            } else {
                skin_list.getItems().add(item);
            }
        });
        skin_list.getSelectionModel().selectFirst();
    }

    private void previewSkin(String key) {
        try {
            SkinXml skin = getSkinManager().getSkinXml(key);
            skin_name.setText(String.format("<%s>", skin.name));
//            Region previewNode = (Region) skin_preview.getChildren().get(0);
//            previewNode.getStylesheets().setAll(
//                    context.getTssManager().boostrapPlayerWindow(skin, "player-window-preview")
//            );
//            getControllerManager().setupPreviewSkin(skin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectSkinRoot(MouseEvent event) {
        consumeEvent(event);
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择皮肤根目录");
        File selection = chooser.showDialog(getStageManager().getPreferenceStage());
        if(selection == null) {
            return ;
        }
        skin_root.setText(transformPath(selection.getAbsolutePath()));
        getPlayerManager().setSkinRoot(skin_root.getText());
        setupSkinList();
    }

    public void applySKin(MouseEvent event) {
        consumeEvent(event);
        Label selection = skin_list.getSelectionModel().getSelectedItem();
        getPlayerManager().setActiveSkin(getUserData(selection, String.class));
    }

    public void removeSkin(MouseEvent event) {
        consumeEvent(event);
        Label selection = skin_list.getSelectionModel().getSelectedItem();
        String key = getUserData(selection, String.class);
        if (contentEquals(key, DEFAULT_SKIN_NAME)) {
            return ;
        }
        getStageManager().showConfirm(
                String.format("皮肤将从硬盘上直接删除，确定要继续吗？\n文件名称为：%s", key),
                () -> {
                    skin_list.getItems().remove(selection);
                    getSkinManager().remove(key);
        });
    }

    public void refreshSkins(MouseEvent event) {
        consumeEvent(event);
        getSkinManager().setupSkins();
        setupSkinList();
    }

}
