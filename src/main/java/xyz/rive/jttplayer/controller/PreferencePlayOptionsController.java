package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static xyz.rive.jttplayer.util.FileUtils.exists;
import static xyz.rive.jttplayer.util.FileUtils.transformPath;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.trim;

public class PreferencePlayOptionsController extends CommonController {

    @FXML
    private VBox play_options;
    @FXML
    private ComboBox<String> play_core_option;
    @FXML
    private TextField play_core_path;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, play_options);
        loadContent();
    }

    public void loadContent() {
        play_core_option.getItems().clear();
        play_core_option.getItems().addAll("Bass", "mpv");

        play_core_option.valueProperty().addListener((o, ov, nv) -> {
            int index = play_core_option.getItems().indexOf(nv);
            getPlayerManager().setPlayCoreType(index);
            restoreLastPlayCorePath(index);
        });
        play_core_path.textProperty().addListener((o, ov, nv) -> {
            //TODO
            String path = trim(play_core_path.getText());
            getConfiguration()
                    .getPlayOptions()
                    .setPlayCorePath(path);
            getPlayer().setPlayCorePath(path);
            updateLastPlayCorePath(path);
        });
    }

    private void restoreLastPlayCorePath(int index) {
        String paths = getConfiguration().getPlayOptions().getLastPlayCorePaths();
        String[] pathArr = trim(paths).split(";");
        play_core_path.setText(trim(pathArr[index]));
    }

    private void updateLastPlayCorePath(String path) {
        String paths = getConfiguration().getPlayOptions().getLastPlayCorePaths();
        String[] oPathArr = trim(paths).split(";");
        String path1 = null;
        String path2 = null;
        if (oPathArr.length > 0) {
            path1 = oPathArr[0];
            path2 = oPathArr.length > 1 ? oPathArr[1] : null;
        }
        int index = getConfiguration().getPlayOptions().getPlayCoreType();
        if (index == 0) {
            path1 = path;
        } else if (index == 1){
            path2 = path;
        }
        getConfiguration().getPlayOptions()
                .setLastPlayCorePaths(
                        String.format("%s;%s", trim(path1), trim(path2))
                );
    }

    public void selectPlayCoreFile(MouseEvent event) {
        consumeEvent(event);

        String path = play_core_path.getText();
        File selection;
        if(getConfiguration().getPlayOptions().getPlayCoreType() < 1) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("选择播放器内核目录");
            if (exists(path)) {
                chooser.setInitialDirectory(new File(path));
            }
            selection = chooser.showDialog(getStageManger().getPreferenceStage());
        } else {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("选择播放器内核文件");
            if (exists(path)) {
                chooser.setInitialDirectory(new File(path).getParentFile());
            }
            selection = chooser.showOpenDialog(getStageManger().getPreferenceStage());
        }
        if(selection == null) {
            return ;
        }
        play_core_path.setText(transformPath(selection.getAbsolutePath()));
    }

    private void setupData() {
        play_core_option.setValue(play_core_option.getItems()
                .get(getConfiguration().getPlayOptions()
                        .getPlayCoreType()));

        play_core_path.setText(getConfiguration().getPlayOptions().getPlayCorePath());
    }

    @Override
    public void afterShowView() {
        setupData();
    }
}
