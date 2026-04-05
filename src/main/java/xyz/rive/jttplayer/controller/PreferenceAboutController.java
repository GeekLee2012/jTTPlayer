package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import static xyz.rive.jttplayer.common.Constants.ABOUT;

public class PreferenceAboutController extends CommonController {

    @FXML
    private VBox about_view;

    @FXML
    private TextArea about_text;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, about_view);

        about_text.setText(ABOUT);
    }

}
