package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PreferenceMediaLibraryOptionsController extends CommonController {

    @FXML
    private VBox media_library_options;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, media_library_options);
    }
}
