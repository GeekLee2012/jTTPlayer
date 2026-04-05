package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import xyz.rive.jttplayer.control.TabsView;

import java.net.URL;
import java.util.ResourceBundle;

public class PreferenceNetworkOptionsController extends CommonController {

    @FXML
    private VBox network_options;

    @FXML
    private TabsView network_content;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, network_options);
        loadContent();
    }

    private void loadContent() {
        network_content.setActiveIndex(0);
    }

}
