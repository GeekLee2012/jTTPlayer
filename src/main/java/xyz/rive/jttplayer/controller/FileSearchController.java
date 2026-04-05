package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import xyz.rive.jttplayer.common.FileSearchOptions;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.trim;

public class FileSearchController extends CommonController {
    @FXML
    private BorderPane file_search_view;
    @FXML
    private TextField file_title;
    @FXML
    private TextField artist;
    @FXML
    private TextField album;
    @FXML
    private CheckBox match_case;
    @FXML
    private CheckBox match_whole_words;
    @FXML
    private CheckBox search_reversed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, file_search_view);
    }

    public void searchNext(MouseEvent event) {
        consumeEvent(event);
        search(true);
    }

    public void search(boolean next) {
        String title = trim(file_title.getText());
        String artistName = trim(artist.getText());
        String albumName = trim(album.getText());
        boolean ignoreCase = !match_case.isSelected();
        boolean wholeWords = match_whole_words.isSelected();
        boolean reversed = search_reversed.isSelected();
        getControllerManager().searchPlaybackQueue(new FileSearchOptions(
                title, artistName, albumName,
                ignoreCase, wholeWords, reversed
        ), next);
    }

    public void markupAll(MouseEvent event) {
        consumeEvent(event);
        String title = trim(file_title.getText());
        String artistName = trim(artist.getText());
        String albumName = trim(album.getText());
        boolean ignoreCase = !match_case.isSelected();
        boolean wholeWords = match_whole_words.isSelected();
        boolean reversed = search_reversed.isSelected();
        getControllerManager().markupPlaybackQueue(new FileSearchOptions(
                title, artistName, albumName,
                ignoreCase, wholeWords, reversed)
        );
    }

}
