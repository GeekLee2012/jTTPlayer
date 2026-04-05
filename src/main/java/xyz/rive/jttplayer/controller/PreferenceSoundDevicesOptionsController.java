package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PreferenceSoundDevicesOptionsController extends CommonController {

    @FXML
    private VBox sound_devices_options;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, sound_devices_options);
    }
}
