package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.trim;

public class RenamePlaybackQueueController extends CommonController {

    @FXML
    private BorderPane rename_playback_queue_view;

    @FXML
    private TextField name_text;

    private Consumer<String> okAction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, rename_playback_queue_view);
    }

    public void applyClose(MouseEvent event) {
        consumeEvent(event);
        String name = trim(name_text.getText());
        if(isEmpty(name)) {
            return ;
        }
        closeView();
        Optional.ofNullable(okAction).ifPresent(
                action -> action.accept(name));
    }

    public void setupData(String name, Consumer<String> action) {
        name_text.setText(trim(name));
        name_text.selectAll();
        okAction = action;
    }

    @Override
    public void beforeCloseView() {
        name_text.clear();
    }
}
