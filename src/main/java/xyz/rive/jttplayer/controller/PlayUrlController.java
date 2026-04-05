package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import xyz.rive.jttplayer.menu.action.OpenFilesAction;
import xyz.rive.jttplayer.util.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayUrlController extends CommonController {

    @FXML
    private BorderPane play_url_view;
    @FXML
    private TextArea url_text;
    @FXML
    private TextField title_text;
    @FXML
    private TextArea cover_url_text;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, play_url_view);
    }

    public void openFile(MouseEvent event) {
        OpenFilesAction action = new OpenFilesAction(false);
        action.handle(event);
        if(!action.isCanceled()) {
            closeView();
        }
    }

    public void playUrl(MouseEvent event) {
        consumeEvent(event);
        String url = url_text.getText();
        if(StringUtils.isEmpty(url)) {
            return ;
        }
        String title = title_text.getText();
        String cover = cover_url_text.getText();

        closeView();
        getPlayerManager().playUrl(url, title, cover);
    }

    @Override
    public void beforeCloseView() {
        url_text.clear();
        title_text.clear();
        cover_url_text.clear();
    }
}
