package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConfirmController extends CommonController {

    @FXML
    private BorderPane confirm_view;

    @FXML
    private Label center;

    private Runnable okAction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, confirm_view);
    }

    public void applyClose(MouseEvent event) {
        event.consume();
        closeView();
        Optional.ofNullable(okAction).ifPresent(__ -> okAction.run());
    }

    public void setupConfirm(String text, Runnable okAction) {
        center.setText(text);
        this.okAction = okAction;
    }
}
