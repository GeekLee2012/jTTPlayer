package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.common.Constants.FILENAME_FORMATS;

public class FileNameFormatController extends CommonController {

    @FXML
    private BorderPane file_name_format_view;
    @FXML
    private TextArea filename;
    @FXML
    private ComboBox<String> format_style;

    private Consumer<String> onSelectedHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, file_name_format_view);
        format_style.getItems().addAll(FILENAME_FORMATS);
    }

    public void applyClose(MouseEvent event) {
        consumeEvent(event);
        String selection = (String) format_style.getValue();
        if(selection == null) {
            return ;
        }
        closeView();
        Optional.ofNullable(onSelectedHandler)
                .ifPresent(__ -> onSelectedHandler.accept(selection));
    }

    public void onSelected(Consumer<String> handler) {
        this.onSelectedHandler = handler;
    }

    public void updateFilename(String value) {
        filename.setText(value);
    }

}
