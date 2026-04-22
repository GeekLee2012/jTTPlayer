package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import xyz.rive.jttplayer.common.LyricSearchOptions;
import xyz.rive.jttplayer.common.Server;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static xyz.rive.jttplayer.util.FxUtils.getUserData;
import static xyz.rive.jttplayer.util.StringUtils.contentEquals;

public class LyricServerManageController extends CommonController {

    @FXML
    private BorderPane lyric_server_manage_view;
    @FXML
    private ListView<Label> server_list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, lyric_server_manage_view);
    }

    public void loadContent() {
        server_list.getItems().clear();
        LyricSearchOptions options = getConfiguration().getLyricSearchOptions();
        Optional.ofNullable(options.getServers())
                .ifPresent(list -> {
                    list.forEach(item -> {
                        Label label = new Label(item.getName());
                        label.setUserData(item);
                        server_list.getItems().add(label);
                    });
                });
    }

    @Override
    public void afterShowView() {
        super.afterShowView();
        loadContent();
    }

    public void addServer(MouseEvent event) {
        consumeEvent(event);
        getStageManager().getLyricServerEditStage().show();
        getControllerManager().createLyricServer(server -> {
                    Label item = new Label(server.getName());
                    item.setUserData(server);
                    server_list.getItems().add(item);

                    LyricSearchOptions options = getConfiguration().getLyricSearchOptions();
                    options.getServers().add(server);
                });
    }

    public void updateServer(MouseEvent event) {
        consumeEvent(event);
        Label item = server_list.getSelectionModel().getSelectedItem();
        if (item == null) {
            return ;
        }
        getStageManager().getLyricServerEditStage().show();
        getControllerManager().updateLyricServer(true,
                getUserData(item, Server.class),
                server -> {
                    LyricSearchOptions options = getConfiguration().getLyricSearchOptions();
                    options.updateServer(server);

                    loadContent();
                });
    }

    public void removeServer(MouseEvent event) {
        consumeEvent(event);
        Label item = server_list.getSelectionModel().getSelectedItem();
        if (item == null) {
            return ;
        }
        Server selection = getUserData(item, Server.class);
        getStageManager().showConfirm(
                String.format("确定要删除当前服务器吗？\n" +
                        "名称：%s\nURL：%s",
                        selection.getName(),
                        selection.getApiUrl()),
                () -> {
                    LyricSearchOptions options = getConfiguration().getLyricSearchOptions();
                    options.getServers().removeIf(server ->
                            contentEquals(selection.getId(), server.getId())
                    );

                    loadContent();
                });

    }

}
