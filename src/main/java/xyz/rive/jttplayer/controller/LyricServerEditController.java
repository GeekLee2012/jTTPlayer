package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import xyz.rive.jttplayer.common.Pair;
import xyz.rive.jttplayer.common.Server;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.trim;

public class LyricServerEditController extends CommonController {

    @FXML
    private BorderPane lyric_server_edit_view;
    @FXML
    private Label title;
    @FXML
    private TextField server_name;
    @FXML
    private TextArea api_url;
    @FXML
    private Label error_msg;
    private Server server;
    private Consumer<Server> okAction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupController(this, lyric_server_edit_view);
    }

    public void loadContent(Server server) {
        this.server = server;
        if (server != null) {
            title.setText("修改歌词服务器");
            server_name.setText(server.getName());
            api_url.setText(server.getApiUrl());
        }
    }

    public void setOkAction(Consumer<Server> action) {
        this.okAction = action;
    }

    @Override
    public void afterShowView() {
        super.afterShowView();
    }

    public void applyClose(MouseEvent event) {
        consumeEvent(event);
        String name = server_name.getText();
        String apiUrl = api_url.getText();

        if (isEmpty(name) || isEmpty(apiUrl)) {
            return ;
        }

        boolean createMode = (server == null);

        //不允许重名
        if (createMode && getConfiguration().getLyricSearchOptions()
                .existsServer(name)) {
            error_msg.setText("错误：已存在同名的服务器！");
            setItemsVisible(error_msg);
            return ;
        }

        if (createMode) {
            server = new Server();
        }
        server.setName(trim(name));
        server.setApiUrl(trim(apiUrl));

        Optional.ofNullable(okAction).ifPresent(__ -> {
            okAction.accept(server);
        });

        closeView();
    }

    @Override
    public void beforeCloseView() {
        server_name.setText("");
        api_url.setText("");
        error_msg.setText("");
        setItemsHidden(error_msg);
        server = null;
        title.setText("添加歌词服务器");
    }
}
