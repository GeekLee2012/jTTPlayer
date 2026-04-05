package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import static xyz.rive.jttplayer.util.FileUtils.readLines;
import static xyz.rive.jttplayer.util.FxUtils.getResourceAsStream;

public class SearchComputerController extends CommonController {

    @FXML
    private BorderPane search_computer_view;
    @FXML
    private ComboBox<String> search_location;
    @FXML
    private ListView<HBox> search_types;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, search_computer_view);
        setupSearchLocation();
        setupSearchTypes();
    }

    private void setupSearchLocation() {
        Arrays.asList("此电脑", "音乐", "公用文档", "公用音乐", "自定义")
              .forEach(name -> {
                  search_location.getItems().addAll(name);
              });
    }

    private void setupSearchTypes() {
        Optional.ofNullable(readLines(getResourceAsStream("audio-types.txt")))
                .ifPresent(lines -> {
                    lines.forEach(line -> {
                        CheckBox cb = new CheckBox(line);
                        cb.setMaxWidth(Double.MAX_VALUE);
                        HBox item = new HBox(cb);
                        item.setMaxWidth(Double.MAX_VALUE);
                        HBox.setHgrow(cb, Priority.ALWAYS);
                        search_types.getItems().add(item);
                    });
                });

    }

    private void selectAllTypes(boolean selected) {
        search_types.getItems().forEach(item -> {
            Node node = item.lookup("CheckBox");
            if(node instanceof CheckBox) {
                CheckBox cb = (CheckBox)node;
                cb.setSelected(selected);
            }
        });
    }

    public void selectAllTypes(MouseEvent event) {
        consumeEvent(event);
        selectAllTypes(true);
    }

    public void deselectAllTypes(MouseEvent event) {
        consumeEvent(event);
        selectAllTypes(false);
    }

}
