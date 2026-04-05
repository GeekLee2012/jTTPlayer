package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PreferenceKeysOptionsController extends CommonController {

    @FXML
    private VBox keys_options;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, keys_options);
    }
}
