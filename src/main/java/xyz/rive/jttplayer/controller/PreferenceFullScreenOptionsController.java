package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PreferenceFullScreenOptionsController extends CommonController {

    @FXML
    private VBox full_screen_options;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, full_screen_options);
    }

}
