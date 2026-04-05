package xyz.rive.jttplayer.controller;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import xyz.rive.jttplayer.common.*;
import xyz.rive.jttplayer.control.SimpleTableView;
import xyz.rive.jttplayer.control.TabsView;
import xyz.rive.jttplayer.menu.MenuMeta;
import xyz.rive.jttplayer.menu.PopMenu;
import xyz.rive.jttplayer.menu.strategy.SharedStrategies;
import xyz.rive.jttplayer.menu.strategy.ShowUnderItemStrategy;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static xyz.rive.jttplayer.common.Constants.*;
import static xyz.rive.jttplayer.util.FileUtils.*;
import static xyz.rive.jttplayer.util.FxUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class FileAttributesController extends CommonController {

    @FXML
    private BorderPane file_attributes_view;
    @FXML
    private Label title;
    @FXML
    private TabsView center;
    @FXML
    private TextField filename;
    @FXML
    private TextField file_source;
    @FXML
    private TextField file_title;
    @FXML
    private TextField artist;
    @FXML
    private TextField album;
    @FXML
    private TextField track_number;
    @FXML
    private ComboBox<String> genre;
    @FXML
    private TextField publish_date;
    @FXML
    private TextArea comment;
    @FXML
    private TextField encoding_type;
    @FXML
    private TextField channels;
    @FXML
    private TextField sample_rate;
    @FXML
    private TextField bits;
    @FXML
    private TextField bit_rate;
    @FXML
    private TextField track_length;
    @FXML
    private TextField track_gain;
    @FXML
    private VBox file_cover_box;
    @FXML
    private ImageView file_cover;
    @FXML
    private Label none_cover;
    @FXML
    private Label image_source;
    @FXML
    private SimpleTableView advance_mode_table;
    @FXML
    private Region advance_btn;
    @FXML
    private Label traditional_btn;
    @FXML
    private Region add_btn;
    @FXML
    private Region remove_btn;
    @FXML
    private Region edit_btn;
    @FXML
    private Region reset_btn;

    private File imageFile;
    private String coverUrl;
    private boolean coverRemoved;
    private PopMenu traditionalZhMenu;
    private Tooltip fileNameTip;
    private Track workingTrack;

    ChangeListener<String> listener;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, file_attributes_view);

        center.onTabChanged(index -> {
            setupTitle();
            if (index == 1) {
                loadAlbumCover();
            } else {
                loadAttributes();
            }
        });
        center.setActiveIndex(0);

        Optional.ofNullable(readLines(getResourceAsStream("genre.txt")))
                .ifPresent(genre.getItems()::addAll);

        getPlayerManager().onFileAttributeTagsAdvanceMode((o, ov, nv) -> {
            setupCommonTags(getWorkingTrack());
        });
        getStageManger().onFileAttributesShow(__ -> center.setActiveIndex(0));

        advance_mode_table.onDoubleClick(this::editTagMetadata);
        advance_mode_table.setOnMouseExited(this::updateMetadataFromTable);

        setupDndAction(ctx -> setupImageFile(ctx.getFile()), file_cover_box);
    }

    private void setWorkingTrack(Track track) {
        workingTrack = track;
    }

    private void setCoverUrl(String url) {
        coverUrl = url;
    }

    private Track getWorkingTrack() {
        if(workingTrack == null) {
            Track track = context.getFileAttributesTrack();
            if(track != null) {
                //setWorkingTrack(track.clone());
                setWorkingTrack(track);
            }
        }
        return workingTrack;
    }

    private void setupTitle() {
        title.requestFocus();
        Track track = getWorkingTrack();
        if(track == null) {
            return;
        }
        //标题
        if(track.equals(getCurrentTrack())) {
            title.setText("文件属性（正在播放）");
        } else {
            int size = getCurrentPlaybackQueue().size();
            int index = getCurrentPlaybackQueue().indexOf(track);
            title.setText(String.format("文件属性（%s/%s）", index + 1, size));
        }
    }

    private void loadAttributes() {
        Track track = getWorkingTrack();
        if(track == null) {
            return;
        }
        //基本信息
        filename.setText(track.getUrl());
        boolean isRemoteTrack = startsWithIgnoreCase(track.getUrl(), "http");
        filename.setEditable(isRemoteTrack);
        if(listener == null) {
            listener = (o, nv, ov) -> getWorkingTrack().setUrl(trim(filename.getText()));
        }
        if (isRemoteTrack) {
            filename.textProperty().addListener(listener);
        } else {
            filename.textProperty().removeListener(listener);
        }
        file_source.setText(isRemoteTrack ? "网络歌曲" : "本地歌曲");

        //标签
        setupCommonTags(track);
        //格式
        encoding_type.setText(track.getTransformedEncodingType());
        channels.setText(track.getChannels());
        sample_rate.setText(track.getSampleRate() > 0 ?
                String.format("%s Hz", track.getSampleRate()) : null
        );
        bits.setText(track.getTransformedBits());
        bit_rate.setText(track.getTransformedBitRate2());
        track_length.setText(String.format("%s (约%s秒)",
                toMMss((long)(track.getTrackLength())),
                Math.round(track.getTrackLength())));
        track_gain.setText("");


        if(fileNameTip == null) {
            fileNameTip = setupTip(filename, trim(track.getUrl()));
        } else {
            fileNameTip.setText(trim(track.getUrl()));
        }
    }

    private void setupCommonTags(Track track) {
        boolean advanceMode = getPlayerManager().isFileAttributeTagsAdvanceModeEnabled();
        setupAdvanceMode(advanceMode);
        if(advanceMode) {
            loadAdvanceModeData(track);
        } else {
            setupNormalModeTags(track);
        }
    }

    private void setupNormalModeTags(Track track) {
        if(track == null) {
            return;
        }

        file_title.setText(trim(track.getTitle()));
        artist.setText(trim(track.getArtist()));
        album.setText(trim(track.getAlbum()));
        publish_date.setText(trim(track.getDate()));
        track_number.setText(trim(track.getTrackNumber()));
        genre.setValue(trim(track.getGenre()));
        comment.setText(trim(track.getComment()));

        file_title.setOnMouseExited(event -> {
            track.setTitle(trim(file_title.getText()));
        });

        artist.setOnMouseExited(event -> {
            track.setArtist(trim(artist.getText()));
        });

        album.setOnMouseExited(event -> {
            track.setAlbum(trim(album.getText()));
        });

        publish_date.setOnMouseExited(event -> {
            track.setDate(trim(publish_date.getText()));
        });

        track_number.setOnMouseExited(event -> {
            track.setTrackNumber(trim(track_number.getText()));
        });

        genre.setOnMouseExited(event -> {
            track.setGenre(trim(genre.getEditor().getText()));
        });

        comment.setOnMouseExited(event -> {
            track.setComment(trim(comment.getText()));
        });
    }

    private void loadAlbumCover() {
        //重置
        none_cover.setManaged(true);
        none_cover.setVisible(true);
        file_cover.setManaged(false);
        file_cover.setVisible(false);
        file_cover.setImage(null);
        setCoverRemoved(false);
        image_source.setText("封面来源：未知");
        coverUrl = null;

        Track track = getWorkingTrack();
        if(track == null) {
            return;
        }
        String cover = trim(track.getCover());
        if(!isEmpty(cover)) {
            image_source.setText("封面来源：".concat(
                    trimLowerCase(cover).startsWith("http") ? "网络" : "本地")
            );

            showCoverImage(new Image(cover, true));
            return ;
        }

        if(trimLowerCase(cover).startsWith("http")) {
            image_source.setText("封面来源：网络");
            showCoverImage(new Image(cover, true));
            return ;
        }

        String url = track.getUrl();
        if(trimLowerCase(url).startsWith("http")) {
            return ;
        }

        byte[] coverBytes = context.getMetadataService().readCover(url);
        Image nativeCover = null;
        if(coverBytes != null) {
            //内嵌封面
            nativeCover = new Image(new BufferedInputStream(new ByteArrayInputStream(coverBytes)));
            if(nativeCover != null) {
                image_source.setText("封面来源：内嵌");
            }
        } else {
            int index = url.lastIndexOf(".");
            if(index > -1) {
                //同名文件
                for (int i = 0; i < IMAGE_SUFFIXES.size(); i++) {
                    setCoverUrl(url.substring(0, index).concat(IMAGE_SUFFIXES.get(i)));
                    if(exists(coverUrl)) {
                        nativeCover = new Image(toExternalForm(coverUrl));
                        break;
                    }
                }
                //名称为Cover的文件
                if (nativeCover == null) {
                    String[] coverNames = { "Cover", "cover" };
                    for (int i = 0; i < IMAGE_SUFFIXES.size(); i++) {
                        for (int j = 0; j < coverNames.length; j++) {
                            setCoverUrl(track.getParentUrl().concat(coverNames[j]).concat(IMAGE_SUFFIXES.get(i)));
                            if(exists(coverUrl)) {
                                nativeCover = new Image(toExternalForm(coverUrl));
                                break;
                            }
                        }
                    }
                }
            }
            if(nativeCover != null) {
                image_source.setText("封面来源：本地");
            }
        }
        showCoverImage(nativeCover);
    }

    private void showCoverImage(Image image) {
        boolean none = (image == null || image.isError());
        none_cover.setManaged(none);
        none_cover.setVisible(none);

        file_cover.setManaged(!none);
        file_cover.setVisible(!none);
        file_cover.setImage(image);

        if(none) {
            setCoverUrl(null);
            image_source.setText("封面来源：未知");
        }
    }

    private void setupImageFile(File file) {
        if(file == null) {
            return ;
        }
        setImageFile(file);
        Optional.ofNullable(toExternalForm(file))
                .ifPresent(url -> {
                    showCoverImage(new Image(url));
                    setWorkingTrackCover(url);
                });
    }

    public void importImage(MouseEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择图片文件");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("图片文件",
                        IMAGE_SUFFIXES.stream().map("*"::concat)
                                .collect(Collectors.toCollection(ArrayList::new))
                )
        );
        File selection = chooser.showOpenDialog(getStageManger().getMainStage());
        setupImageFile(selection);
    }

    private void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    private void setCoverRemoved(boolean removed) {
        this.coverRemoved = removed;
    }

    public void removeImage(MouseEvent event) {
        setWorkingTrackCover(null);
        showCoverImage(null);
        setImageFile(null);
        setCoverRemoved(true);
    }

    private void setWorkingTrackCover(String url) {
        Track track = getWorkingTrack();
        if(track == null) {
            return;
        }
        track.setCover(url);
    }

    public void exportImage(MouseEvent event) {
        Track track = getWorkingTrack();
        if(track == null) {
            return;
        }
        String cover = trim(track.getCover());
        if(trimLowerCase(cover).startsWith("http")) {
            return ;
        }
        byte[] coverBytes = context.getMetadataService().readCover(track.getUrl());
        if(coverBytes == null || coverBytes.length < 128) {
            return ;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("导出图片");
        chooser.setInitialFileName(track.getTitle());
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "图片文件(.png, .jpg)",
                        "*.png", "*.jpg")
        );
        File selection = chooser.showSaveDialog(getMainStage());
        if(selection == null) {
            return ;
        }
        writeFile(selection.getAbsolutePath(), coverBytes);
    }

    public void embedImage(MouseEvent event) {
        Track track = getWorkingTrack();
        if(track == null) {
            return;
        }
        //当原封面来源网络时，不进行覆盖
        String cover = trim(track.getCover());
        if(trimLowerCase(cover).startsWith("http")) {
            return ;
        }
        if(coverUrl != null && exists(coverUrl)) {
            context.getMetadataService().writeCover(track.getUrl(), new File(coverUrl));
        }
    }

    public void readMetadataFromFile(MouseEvent event) {
        //consumeEvent(event);
        Track track = getWorkingTrack();
        if(track == null) {
            return;
        }
        try {
            File file = new File(track.getUrl());
            if(!file.exists() || !file.isFile()) {
                return ;
            }
            track.setLyricEmbed(null);
            Metadata metadata = context.getMetadataService().read(file);
            context.getTrackService().syncMetadataToTrack(metadata, track);
        } catch (Exception e) {
            e.printStackTrace();
        }
        genre.setValue(trim(track.getGenre()));
        center.loadTabContent();
    }

    @Override
    public void beforeCloseView() {
        setImageFile(null);

        Optional.ofNullable(getWorkingTrack())
                .ifPresent(this::refreshStagesTrackMetadata);

        hideMenus(null);
    }

    public void saveToFile(MouseEvent event) {
        //consumeEvent(event);
        Track track = getWorkingTrack();
        if(track == null) {
            return;
        }
        //Tag
        context.getMetadataService().writeFields(track);
        //Cover
        if(imageFile != null || coverRemoved) {
            context.getMetadataService().writeCover(track.getUrl(), imageFile);
        }
    }

    public void showFileNameFormatView(MouseEvent event) {
        //consumeEvent(event);
        getStageManger().getFileNameFormatStage().show();
        getControllerManager().updateFilename(filename.getText(), this::updateMetadataFromFileName);
    }

    public void updateMetadataFromFileName(String format) {
        format = trim(format);
        if(format.isEmpty()) {
            return ;
        }
        Track track = getWorkingTrack();
        if(track == null) {
            return ;
        }

        Path path = Paths.get(filename.getText());
        String simpleName = guessSimpleName(String.valueOf(path.getFileName()));
        //简单起见，不做解析格式
        if("%(Artist) - %(Title)".equals(format)) {
            int index = simpleName.indexOf("-");
            if(index > -1) {
                track.setArtist(simpleName.substring(0, index).trim());
                track.setTitle(simpleName.substring(index + 1));
            }
        } else if("%(Title) - %(Artist)".equals(format)) {
            int index = simpleName.lastIndexOf("-");
            if(index > -1) {
                track.setTitle(simpleName.substring(0, index).trim());
                track.setArtist(simpleName.substring(index + 1).trim());
            }
        } else if("%(Artist) - %(TrackNumber).%(Title)".equals(format)) {
            int index1 = simpleName.lastIndexOf("-");
            int index2 = simpleName.indexOf(".");
            if(index1 > -1 && index2 > -1) {
                track.setArtist(simpleName.substring(0, index1).trim());
                track.setTrackNumber(simpleName.substring(index1 + 1, index2).trim());
                track.setTitle(simpleName.substring(index2 + 1).trim());
            }
        } else if("%(TrackNumber).%(Artist) - %(Title)".equals(format)) {
            int index1 = simpleName.indexOf("-");
            int index2 = simpleName.indexOf(".");
            if(index1 > -1 && index2 > -1) {
                track.setTrackNumber(simpleName.substring(0, index1).trim());
                track.setArtist(simpleName.substring(index1 + 1, index2).trim());
                track.setTitle(simpleName.substring(index2 + 1).trim());
            }
        } else if("%(Artist)\\%(Title)".equals(format)) {
            track.setTitle(simpleName);
            track.setArtist(String.valueOf(path.getParent().getFileName()));
        } else if("%(Album)\\%(TrackNumber).%(Title)".equals(format)) {
            int index = simpleName.indexOf(".");
            if(index > -1) {
                track.setTrackNumber(simpleName.substring(0, index).trim());
                track.setTitle(simpleName.substring(index + 1).trim());
                track.setAlbum(String.valueOf(path.getParent().getFileName()));
            }
        } else if("%(Artist)\\%(Album)\\%(Title)".equals(format)) {
            track.setTitle(simpleName);
            track.setAlbum(String.valueOf(path.getParent().getFileName()));
            track.setArtist(String.valueOf(path.getParent()
                    .getParent().getFileName()));
        } else if("%(Genre)\\%(Album)\\%(Title)".equals(format)) {
            track.setTitle(simpleName);
            track.setAlbum(String.valueOf(path.getParent().getFileName()));
            track.setGenre(String.valueOf(path.getParent()
                    .getParent().getFileName()));
        }
        setupCommonTags(track);
    }

    public void toggleAdvanceMode(MouseEvent event) {
        //consumeEvent(event);
        getPlayerManager().toggleFileAttributeTagsAdvanceMode();
    }

    public void setupAdvanceMode(boolean advanceMode) {
        Set<Node> nonAdvanceNodes = file_attributes_view.lookupAll(".non_advance");
        nonAdvanceNodes.forEach(node -> {
            node.setManaged(!advanceMode);
            node.setVisible(!advanceMode);
        });

        advance_mode_table.setManaged(advanceMode);
        advance_mode_table.setVisible(advanceMode);

        advance_btn.getStyleClass().removeAll("active");
        if(advanceMode) {
            advance_btn.getStyleClass().add("active");
        }
        advance_btn.requestFocus();

        add_btn.setManaged(advanceMode);
        add_btn.setVisible(advanceMode);

        remove_btn.setManaged(advanceMode);
        remove_btn.setVisible(advanceMode);

        edit_btn.setManaged(advanceMode);
        edit_btn.setVisible(advanceMode);

        reset_btn.setManaged(!advanceMode);
        reset_btn.setVisible(!advanceMode);
    }

    private void addTableRow(String key, String value) {
        if(isEmpty(key) || isEmpty(value)) {
            return ;
        }
        advance_mode_table.addRow(
                new Label(trim(key)),
                new TextField(trim(value))
        );
    }

    private void loadAdvanceModeData(Track track, String... ignoreKeys) {
        Optional.ofNullable(track)
                .ifPresent(__ -> {
                    advance_mode_table.clear();
                    track.setGenre(trim(genre.getEditor().getText()));

                    addTableRow("TITLE", track.getTitle());
                    addTableRow("ARTIST", track.getArtist());
                    addTableRow("ALBUM", track.getAlbum());
                    addTableRow("DATE", track.getDate());
                    addTableRow("GENRE", track.getGenre());
                    addTableRow("TRACKNUMBER", track.getTrackNumber());
                    addTableRow("RATING", track.getRating());
                    addTableRow("COMMENT", track.getComment());
                    if((ignoreKeys == null || !Arrays.asList(ignoreKeys).contains("LYRICS"))
                            && isEmpty(track.getLyricEmbedText())) {
                        String text = context.getMetadataService().readLyric(track.getUrl());
                        track.setLyricEmbed(Lyric.parseFromText(text));
                    }
                    addTableRow("LYRICS", track.getLyricEmbedText());
        });
    }

    public void updateMetadataFromTable(MouseEvent event) {
        //consumeEvent(event);
        Track track = getWorkingTrack();
        if(track == null) {
            return ;
        }
        advance_mode_table.getData().forEach(cells -> {
            String key = cells[0];
            String value = cells[1];
            setTrackValue(track, key, value, true);
        });
    }

    public Pair getActiveTagMetadata() {
        HBox activeItem = (HBox) advance_mode_table.lookup(".active");
        if(activeItem == null) {
            return null;
        }

        Label keyLabel = (Label) activeItem.getChildren().get(0);
        TextField valueField = (TextField) activeItem.getChildren().get(1);
        String key = trim(keyLabel.getText());
        String value = trim(valueField.getText());
        //恢复TextField/TextArea吞掉的换行符
        if("LYRICS".equalsIgnoreCase(key)) {
            value = trim(value.replaceAll("\\[", "\n["));
        }
        return new Pair(key, value);
    }

    private void setTrackValue(Track track, String key, String value, boolean edit) {
        if(track == null || key == null) {
            return ;
        }
        if("TITLE".equalsIgnoreCase(key)) {
            if (!edit && !isEmpty(track.getTitle())) {
               return ;
            }
            track.setTitle(value);
        } else if("ARTIST".equalsIgnoreCase(key)) {
            if (!edit && !isEmpty(track.getArtist())) {
                return ;
            }
            track.setArtist(value);
        } else if("ALBUM".equalsIgnoreCase(key)) {
            if (!edit && !isEmpty(track.getAlbum())) {
                return ;
            }
            track.setAlbum(value);
        } else if("DATE".equalsIgnoreCase(key)) {
            if (!edit && !isEmpty(track.getDate())) {
                return ;
            }
            track.setDate(value);
        } else if("GENRE".equalsIgnoreCase(key)) {
            if (!edit && !isEmpty(track.getGenre())) {
                return ;
            }
            track.setGenre(value);
        } else if("TRACKNUMBER".equalsIgnoreCase(key)) {
            if (!edit && !isEmpty(track.getTrackNumber())) {
                return ;
            }
            track.setTrackNumber(value);
        } else if("RATING".equalsIgnoreCase(key)) {
            if (!edit && !isEmpty(track.getRating())) {
                return ;
            }
            track.setRating(value);
        } else if("COMMENT".equalsIgnoreCase(key)) {
            if (!edit && !isEmpty(track.getComment())) {
                return ;
            }
            track.setComment(value);
        } else if("LYRICS".equalsIgnoreCase(key)) {
            if (!edit && !isEmpty(track.getLyricEmbedText())) {
                return ;
            }
            Lyric lyric = null;
            if(!isEmpty(value)) {
                //恢复TextField/TextArea吞掉的换行符
                value = trim(value).replaceAll("\\[", "\n[");
                lyric = Lyric.parseFromText(value);
            }
            track.setLyricEmbed(lyric);
        }
    }

    public void removeTagMetadata(MouseEvent event) {
        //consumeEvent(event);
        Track track = getWorkingTrack();
        if(track == null) {
            return ;
        }

        Label label = (Label) advance_mode_table.lookup(".active Label");
        if(label == null) {
            return ;
        }

        String key = label.getText();
        setTrackValue(track, key, null, true);
        loadAdvanceModeData(track, key);
    }

    private void doUpdateTagMetadata(Track track, Pair pair, boolean edit) {
        if(track == null || pair == null) {
            return ;
        }
        String key = pair.key();
        String value = (String) pair.value();
        setTrackValue(track, key, value, edit);
        loadAdvanceModeData(track);
    }

    private void doEditTagMetadata(boolean edit, Pair data) {
        Track track = getWorkingTrack();
        if(track == null) {
            return ;
        }
        getStageManger().getFileTagEditStage().show();
        getControllerManager().updateTagMetadata(track, edit, data, pair -> {
            doUpdateTagMetadata(track, pair, edit);
        });
    }

    public void addTagMetadata(MouseEvent event) {
        //consumeEvent(event);
        doEditTagMetadata(false, null);
    }

    public void editTagMetadata(MouseEvent event) {
        //consumeEvent(event);
        Pair data = getActiveTagMetadata();
        if(data == null) {
            return ;
        }
        doEditTagMetadata(true, data);
    }

    public void showPrevious(MouseEvent event) {
        //consumeEvent(event);
        showTrackAttributes(-1);
    }

    public void showNext(MouseEvent event) {
        //consumeEvent(event);
        showTrackAttributes(1);
    }

    private void showTrackAttributes(int offset) {
        Track track = getWorkingTrack();
        if(track == null) {
            return ;
        }
        PlaybackQueue queue = getPlayerManager().getPlaybackQueue(track.getQueueId());
        if(queue == null || queue.size() <= 1) {
            return ;
        }
        setWorkingTrack(null);
        int index = queue.indexOf(track);
        Optional.ofNullable(queue.getTrack(index + offset))
                .ifPresent(target -> {
                    context.setFileAttributesTrack(target);
                    //center.setActiveIndex(center.getActiveIndex());
                    center.loadTabContent();
                });
    }

    private PopMenu getTraditionalZhMenu() {
        if (traditionalZhMenu == null) {
            traditionalZhMenu = new PopMenu(context.getMainStage())
                    .setShowStrategy(SharedStrategies.getSharedUnder())
                    .setMenuList(getChineseMenu());
            traditionalZhMenu.setOnShown(event -> {
                traditional_btn.getStyleClass().add("active");
            });
            traditionalZhMenu.setOnHidden(event -> {
                traditional_btn.getStyleClass().remove("active");
            });
        }
        return traditionalZhMenu;
    }

    private List<MenuMeta> getChineseMenu() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("简体 -> 繁体", event -> switchTraditionalZh(true), 139));
        menuMetas.add(new MenuMeta("繁体 -> 简体", event -> switchTraditionalZh(false)));
        return menuMetas;
    }

    private void switchTraditionalZh(boolean traditional) {
        Track track = getWorkingTrack();
        if (track == null) {
            return ;
        }
        if(traditional) {
            track.setTitle(ZhConverterUtil.toTraditional(track.getTitle()));
            track.setArtist(ZhConverterUtil.toTraditional(track.getArtist()));
            track.setAlbum(ZhConverterUtil.toTraditional(track.getAlbum()));
            track.setComment(ZhConverterUtil.toTraditional(track.getComment()));
        } else {
            track.setTitle(ZhConverterUtil.toSimple(track.getTitle()));
            track.setArtist(ZhConverterUtil.toSimple(track.getArtist()));
            track.setAlbum(ZhConverterUtil.toSimple(track.getAlbum()));
            track.setComment(ZhConverterUtil.toSimple(track.getComment()));
        }
        track.setGenre(trim(genre.getEditor().getText()));
        //center.setActiveIndex(center.getActiveIndex());
        center.loadTabContent();
    }

    public void toggleTraditionalMenu(MouseEvent event) {
        getTraditionalZhMenu().toggle(event);
    }

    public void hideMenus(MouseEvent event) {
        hideMenu(event, getTraditionalZhMenu());
    }

    @Override
    public void afterCloseView() {
        super.afterCloseView();
        setWorkingTrack(null);
        setCoverUrl(null);
    }

}
