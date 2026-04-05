package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PreferenceSystemAssociationOptionsController extends CommonController {

    @FXML
    private HBox system_association_options;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, system_association_options);
    }

}
