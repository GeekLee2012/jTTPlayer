package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

public class FileQuickPositionController extends CommonController {

    @FXML
    private BorderPane file_quick_position_view;
    @FXML
    private ComboBox<String> keyword;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, file_quick_position_view);

        keyword.setOnKeyPressed(event -> {
            consumeEvent(event);
        });
    }

    public void markupAll(MouseEvent event) {
        consumeEvent(event);
        keyword.setValue(keyword.getEditor().getText());
        String text = keyword.getValue();
        if(isEmpty(text)) {
            return ;
        }
        getControllerManager().markupAllPlaybackQueue(text);
        closeView();
    }

    @Override
    public void afterCloseView() {
        keyword.getEditor().clear();
        keyword.setValue("");
    }
}
