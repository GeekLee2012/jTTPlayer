package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AlertController extends CommonController {

    @FXML
    private BorderPane alert_view;

    @FXML
    private Label center;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, alert_view);
    }

    public void setContentText(String text) {
        center.setText(text);
    }

}
