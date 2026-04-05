package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LyricOffsetController extends CommonController {

    @FXML
    private BorderPane lyric_offset_view;

    @FXML
    private Spinner<Integer> offset;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, lyric_offset_view);
    }

    public void applyClose(MouseEvent event) {
        consumeEvent(event);
        int editValue = offset.getValue();
        try {
            //JDK bug：编辑后需要按Enter键，提交更新；否则获取到的是旧值
            editValue = Integer.parseInt(offset.getEditor().getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeView();
        getControllerManager().setLyricOffset(editValue);
    }

}
