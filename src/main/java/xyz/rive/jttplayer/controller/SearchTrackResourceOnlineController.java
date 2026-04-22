package xyz.rive.jttplayer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import xyz.rive.jttplayer.common.*;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static xyz.rive.jttplayer.util.FileUtils.*;
import static xyz.rive.jttplayer.util.FxUtils.getUserData;
import static xyz.rive.jttplayer.util.HttpUtils.*;
import static xyz.rive.jttplayer.util.JsonUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class SearchTrackResourceOnlineController extends CommonController {

    @FXML
    private BorderPane search_track_resource_online_view;
    @FXML
    private ComboBox<String> server_list;
    @FXML
    private TextField track_title;
    @FXML
    private TextField track_artist;
    @FXML
    private ListView<HBox> lyric_list;
    @FXML
    private TextField save_filename;
    @FXML
    private Label download_cover_btn;
    @FXML
    private Label download_lrc_btn;
    private String lastTrackTitle;
    private String lastTrackArtist;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupController(this, search_track_resource_online_view);
        loadContent();
    }

    public void loadContent() {
        loadServerList();
    }

    private void loadServerList() {
        LyricSearchOptions options = getConfiguration().getLyricSearchOptions();
        server_list.getItems().clear();
        options.getServers().forEach(server -> {
            server_list.getItems().add(server.getName());
        });
        if (isEmpty(options.getSelectedServer())) {
            server_list.getSelectionModel().selectFirst();
        } else {
            server_list.getSelectionModel().select(options.getSelectedServer());
        }
    }

    public void searchSongs(MouseEvent event) {
        consumeEvent(event);
        String title = trim(track_title.getText());
        String artist = trim(track_artist.getText());
        if (isEmpty(title) && isEmpty(artist)) {
            return ;
        }
        LyricSearchOptions options = getConfiguration().getLyricSearchOptions();
        String serverName = server_list.getSelectionModel().getSelectedItem();
        Server server = options.getServer(serverName);
        if (server == null) {
            return ;
        }
        String url = String.format("%s/songs?title=%s&artist=%s",
                server.getApiUrl(), title, artist);
        ReplyMsg<List<TrackResource>> replyMsg = parseResources(request(url));
        if (replyMsg == null || !replyMsg.isOk() || !replyMsg.hasData()) {
            return ;
        }
        lyric_list.getItems().clear();
        List<?> data = replyMsg.getData();
        for (int i = 0; i < data.size(); i++) {
            Object o = data.get(i);
            if (o instanceof TrackResource) {
                TrackResource lrc = (TrackResource) o;
                lyric_list.getItems().add(createResourceItem(lrc, i));
            }
        }
    }

    private ReplyMsg<List<TrackResource>> parseResources(String content) {
        JsonNode root = json(content);
        if (root == null) {
            return null;
        }
        ReplyMsg<List<TrackResource>> replyMsg = new ReplyMsg<>();
        replyMsg.setCode(root.path("code").asInt());
        replyMsg.setMsg(root.path("msg").asText());
        JsonNode dataNode = root.path("data");
        if (dataNode == null || !dataNode.isArray()) {
            return null;
        }
        List<TrackResource> lrcs = new ArrayList<>();
        for (Iterator<JsonNode> iter = dataNode.elements(); iter.hasNext();) {
            lrcs.add(parseJson(iter.next(), TrackResource.class));
        }
        replyMsg.setData(lrcs);
        return replyMsg;
    }


    public TrackResource fetchLrcResource(TrackResource resource) {
        LyricSearchOptions options = getConfiguration().getLyricSearchOptions();
        String serverName = server_list.getSelectionModel().getSelectedItem();
        Server server = options.getServer(serverName);
        if (server == null) {
            return resource;
        }
        String url = String.format("%s/lrc", server.getApiUrl());
        Map<String, Object> params = new HashMap<>();
        params.put("id", resource.getId());
        params.put("title", resource.getTitle());
        params.put("artist", resource.getArtist());
        params.put("album", resource.getAlbum());
        params.put("duration", (int)(resource.getDuration() * 60D));
        //附加参数，格式为json字符串
        params.put("extras", stringify(resource.getExtras()));
        ReplyMsg<TrackResource> replyMsg = parseLrcResource(post(url, params));
        if (replyMsg == null || !replyMsg.isOk() || !replyMsg.hasData()) {
            return resource;
        }
        return replyMsg.getData();
    }

    private ReplyMsg<TrackResource> parseLrcResource(String content) {
        JsonNode root = json(content);
        if (root == null) {
            return null;
        }
        ReplyMsg<TrackResource> replyMsg = new ReplyMsg<>();
        replyMsg.setCode(root.path("code").asInt());
        replyMsg.setMsg(root.path("msg").asText());
        JsonNode dataNode = root.path("data");
        if (dataNode == null) {
            return null;
        }
        replyMsg.setData(parseJson(dataNode, TrackResource.class));
        return replyMsg;
    }


    private HBox createResourceItem(TrackResource resource, int index) {
        Label sqno = new Label((index + 1) + "");
        Label title = new Label(resource.getTitle());
        Label artist = new Label(resource.getArtist());
        Label album = new Label(resource.getAlbum());
        Label duration = new Label(toMMss(resource.getDuration()));
        sqno.getStyleClass().add("sqno");
        title.getStyleClass().add("title");
        artist.getStyleClass().add("artist");
        album.getStyleClass().add("album");
        duration.getStyleClass().add("duration");

        HBox item = new HBox(6, sqno, title, artist, album, duration);
        HBox.setHgrow(title, Priority.ALWAYS);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setUserData(resource);
        item.setOnMouseClicked(event -> {
            consumeEvent(event);
            context.runTask(() -> {
                TrackResource uResource = getUserData(item, TrackResource.class);
                if (isEmpty(uResource.getLrc())) {
                    TrackResource lrcResource = fetchLrcResource(uResource);
                    uResource.setLrc(lrcResource.getLrc());
                    uResource.setTrc(lrcResource.getTrc());
                    item.setUserData(uResource);
                }
            });
        });
        return item;
    }

    @Override
    public void afterShowView() {
        super.afterShowView();
        loadContent();
        //歌曲信息
        Track track = getCurrentTrack();
        if (track == null) {
            return ;
        }
        track_title.setText(trim(track.getTitle()));
        track_artist.setText(trim(track.getArtist()));
        save_filename.setText(String.format("%s", trim(track.basicMetadata())));
        if (!contentEqualsIgnoreCase(track_title.getText(), lastTrackTitle)
            || !contentEqualsIgnoreCase(track_artist.getText(), lastTrackArtist)) {
            lyric_list.getItems().clear();
        }
    }

    @Override
    public void afterCloseView() {
        super.afterCloseView();
        lastTrackTitle = trim(track_title.getText());
        lastTrackArtist = trim(track_artist.getText());
        setupBtnState(download_cover_btn, "下载封面", false);
        setupBtnState(download_lrc_btn, "下载歌词", false);
    }

    public void switchSaveFilename(MouseEvent event) {
        consumeEvent(event);
        String filename = trim(save_filename.getText());
        if (isEmpty(filename)) {
            return ;
        }
        int index = filename.lastIndexOf(".lrc");
        if (index > -1) {
            filename = filename.substring(0, index);
        }
        index = filename.lastIndexOf("-");
        if (index < 0) {
            return ;
        }
        String part0 = trim(filename.substring(0, index));
        String part1 = trim(filename.substring(index + 1));
        save_filename.setText(String.format("%s - %s", part1, part0));
    }

    private void setupBtnState(Label btn, String text, boolean disabled) {
        runFx(() -> {
            btn.setText(text);
            btn.setDisable(disabled);
        });
    }

    private void setupBtnDownloadTip(Label btn, String text, String tipText) {
        setupBtnState(btn, tipText, true);
        runDelay(() -> setupBtnState(btn, text, false), 1000);
    }

    public void downloadLrc(MouseEvent event) {
        consumeEvent(event);
        HBox selection = lyric_list.getSelectionModel().getSelectedItem();
        if (selection == null) {
            return;
        }
        TrackResource resource = getUserData(selection, TrackResource.class);
        if (resource == null || isEmpty(resource.getLrc())) {
            getStageManager().showAlert("下载失败！当前歌曲没有找到相关的歌词内容！");
            return ;
        }
        String filename = save_filename.getText();
        if (isEmpty(filename)) {
            return ;
        }
        String savePath = null;
        LyricSearchOptions options = getConfiguration().getLyricSearchOptions();
        Track track = getCurrentTrack();
        if (options.isSaveToTrackPath() && track != null) {
            savePath = track.getParentUrl();
        }
        if (savePath == null || !options.isSaveToTrackPath()) {
            String downloadPath = transformPath(options.getDownloadPath());
            if (!isEmpty(downloadPath)) {
                try {
                    if (!exists(downloadPath)) {
                        Files.createDirectories(Paths.get(downloadPath));
                    }
                    savePath = downloadPath;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (isEmpty(savePath) || !exists(savePath)) {
            getStageManager().showAlert("没有指定保存文件夹，请前往设置\"千千选项 - 歌曲搜索 - 保存到歌曲所在文件夹/保存到其他文件夹\"");
            return ;
        }
        setupBtnState(download_lrc_btn, "歌词下载中", true);
        String lrcFile = String.format("%s/%s.lrc", savePath, filename);
        String trcFile = String.format("%s/%s [Trans].lrc", savePath, filename);
        writeText(lrcFile, resource.getLrc());
        writeText(trcFile, resource.getTrc());

        setupBtnDownloadTip(download_lrc_btn, "下载歌词", "歌词已下载");
    }

    public void downloadCover(MouseEvent event) {
        consumeEvent(event);
        HBox selection = lyric_list.getSelectionModel().getSelectedItem();
        if (selection == null) {
            return;
        }
        TrackResource resource = getUserData(selection, TrackResource.class);
        if (resource == null || isEmpty(resource.getCover())
                || !trim(resource.getCover()).startsWith("http")) {
            getStageManager().showAlert("下载失败！当前歌曲没有找到相关的封面图片！");
            return ;
        }
        String filename = save_filename.getText();
        if (isEmpty(filename)) {
            return ;
        }
        String savePath = null;
        Track track = getCurrentTrack();
        if (track != null) {
            savePath = track.getParentUrl();
        }
        if (isEmpty(savePath) || !exists(savePath)) {
            return ;
        }
        String coverFile = String.format("%s/%s.png", savePath, filename);
        setupBtnState(download_cover_btn, "封面下载中", true);
        context.runTask(() -> {
            writeFile(coverFile, requestBytes(resource.getCover()));
            setupBtnDownloadTip(download_cover_btn, "下载封面","封面已下载");
            getControllerManager().updateTrackMetadata();
        });
    }
}
