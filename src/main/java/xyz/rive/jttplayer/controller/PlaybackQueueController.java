package xyz.rive.jttplayer.controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import xyz.rive.jttplayer.common.*;
import xyz.rive.jttplayer.menu.action.*;
import xyz.rive.jttplayer.menu.MenuMeta;
import xyz.rive.jttplayer.menu.PopMenu;
import xyz.rive.jttplayer.skin.SkinXml;
import xyz.rive.jttplayer.skin.SkinXmlWindowItem;
import xyz.rive.jttplayer.skin.StandaloneXml;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static xyz.rive.jttplayer.common.Constants.PLAYBACK_QUEUE;
import static xyz.rive.jttplayer.common.SortBy.*;
import static xyz.rive.jttplayer.util.FxUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class PlaybackQueueController extends CommonController {

    @FXML
    private AnchorPane playback_queue_view;
    @FXML
    private Region view_bg;
    @FXML
    private Region view_title;
    @FXML
    private Region close_btn;
    @FXML
    private AnchorPane playlist_box;
    @FXML
    private HBox toolbar;
    @FXML
    private ListView<HBox> name_list;
    @FXML
    private Region collapse_btn;
    @FXML
    private ListView<HBox> data_list;
    @FXML
    private VBox splitter;

    private double markNameListWidth = 0;
    private int lastSelectedIndex = -1;
    private FileSearchOptions lastSearchOptions;
    private int lastSearchIndex = -1;

    private Node dragStartItem;
    private Node dragOverItem;
    //private boolean ignoreTrackIndexChanged = false;
    private boolean copying = false;
    private boolean moving = false;
    private final DoubleProperty titleWidthOffsetProperty = new SimpleDoubleProperty(-78);
    private ChangeListener<? super Number> toolbarPosListener;
    private ChangeListener<? super Number> titlePosListener;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupController(this, playback_queue_view, PLAYBACK_QUEUE);
        setupDndAction(this::handleDndAction, playback_queue_view);

        initToolbar();
        //double scrollbarWidth = 23;
        setResizable(name_list, splitter)
                .addExcludeRegion(collapse_btn)
                .setResizableCheck(size -> {
                    double width = size.width();
                    double maxWidth = getStage().getWidth() * 0.382;
                    return width < maxWidth;
                }).onResized(sizes -> {
                    hideAllMenus(null);
                    setupListPosition(name_list.getWidth());
                    refreshAllList();
                });

        double initialWidth = 75;
        name_list.setPrefWidth(initialWidth);
        name_list.setMinWidth(initialWidth);
        setupListPosition(initialWidth);

        onPlaybackQueuesSize((o, ov, nv) ->
                runFx(() -> {
                    getPlayerManager().sortPlaybackQueues();
                    updatePlaybackQueueNames();
                })
        );

        onTrackChanged((o, ov, nv) ->
                runFx(() -> {
                    if(getCurrentPlaybackQueueIndex()
                            != getActivePlaybackQueueIndex()) {
                        return ;
                    }

                    setupDataListHighlight();
                    scrollToCurrentTrack();
                })
        );

        onPlaybackQueueNamesCollapsed((o, ov, nv)
                -> runFx(this::setupSplitterStyle));

    }

    private void initToolbar() {
        AtomicInteger count = new AtomicInteger(0);
        toolbar.getChildren().forEach(item -> {
            final int index = count.getAndIncrement();
            item.setOnMouseClicked(event ->
                    buildMenuBarPopMenu(index)
                            .toggle(event));

            item.setOnMouseEntered(event -> {
                if(getMenuBarPopMenu().isShowing()) {
                    buildMenuBarPopMenu(index).show(event);
                }
            });
        });
    }

    private String getFontStyle() {
        return String.format("-fx-font-size: %spx;",
                getPlayerManager().getPlaybackQueueFontSize());
    }

    public void updatePlaybackQueueNames() {
        name_list.getItems().clear();
        AtomicInteger count = new AtomicInteger();
        getPlaybackQueues().forEach(queue -> {
            int index = count.getAndIncrement();
            Label label = new Label(queue.getName());
            label.setStyle(getFontStyle());
            HBox item = new HBox(label);
            item.prefWidthProperty().bind(name_list.widthProperty().add(-2));
            item.maxWidthProperty().bind(name_list.widthProperty().add(-2));
            item.getStyleClass().add("name_wrap");
            item.setUserData(queue);
            item.setOnMouseClicked(event -> {
                lastSearchIndex = -1;
                if(event.getButton() == MouseButton.SECONDARY) {
                    context.setupContextMenuTrigger(item);
                    getPlaybackQueueContextMenu()
                            .setEvent(event).show();
                    return ;
                }

                hideAllMenus(event);
                switchToPlaybackQueue(index, queue);
            });
            name_list.getItems().add(item);
        });
        setupNameListHighlight();
    }

    private void setDragStartItem(Node item) {
        dragStartItem = item;
    }

    private void setDragOverItem(Node item) {
        dragOverItem = item;
    }

    /*
    private void setIgnoreTrackIndexChanged(boolean value) {
        ignoreTrackIndexChanged = value;
    }
    */

    public void switchToPlaybackQueue(int index, PlaybackQueue queue) {
        if(index < 0 || queue == null) {
            return ;
        }
        boolean scrollTop = (getPlayerManager().getActivePlaybackQueueIndex() != index);
        setActivePlaybackQueueIndex(index);
        loadPlaybackQueue(queue, scrollTop);
        setupNameListHighlight();

        if(!copying && !moving) {
            deselectAllTracks();
        } else {
            setupTrackSelectionsHighlight();
        }
    }

    @Override
    public void afterCloseView() {
        deselectAllTracks();
    }

    public void setCopying(boolean value) {
        this.copying = value;
        this.moving = false;
    }

    public void setMoving(boolean value) {
        this.moving = value;
        this.copying = false;
    }

    private void resetDrag() {
        setDragStartItem(null);
        setDragOverItem(null);
    }

    private void moveTrack() {
        int fromIndex = data_list.getItems().indexOf(dragStartItem);
        int toIndex = data_list.getItems().indexOf(dragOverItem);
        //int index = context.getCurrentTrackIndex();
        //boolean ignore = ((fromIndex - index) * (toIndex - index) <= 0);
        //setIgnoreTrackIndexChanged(ignore);
        getCurrentPlaybackQueue().move(fromIndex, toIndex);
        getPlayerManager().setTrackSortBy(None);
        runFx(this::refresh);
    }

    private void addTracks(Dragboard dragboard) {
        if(dragboard == null) {
            return ;
        }
        List<File> files = dragboard.getFiles();
        if(files == null || files.isEmpty()) {
            return ;
        }
        if(files.size() == 1) {
            File file = files.get(0);
            if (file.isDirectory()) {
                appendToPlaybackQueue(file);
                return ;
            } else if (trimLowerCase(file.getName()).endsWith(".m3u")
                    || trimLowerCase(file.getName()).endsWith(".m3u8")
                    || trimLowerCase(file.getName()).endsWith(".jttpl") ) {
                restorePlaybackQueue(file);
                return ;
            }
        }

        int toIndex = data_list.getItems().indexOf(dragOverItem);
        //int index = context.getCurrentTrackIndex();
        //setIgnoreTrackIndexChanged(toIndex <= index);
        getPlayerManager().setTrackSortBy(None);

        boolean needRefresh = false;
        for(File file : files) {
            Track track = context.getTrackService().parseTrack(file);
            if(track == null) {
                continue;
            }

            getCurrentPlaybackQueue().add(toIndex, track);
            needRefresh = true;
        }

        if(needRefresh) {
            runFx(this::refresh);
        }
    }

    private void loadPlaybackQueue(PlaybackQueue queue) {
        loadPlaybackQueue(queue, false);
    }

    private void loadPlaybackQueue(PlaybackQueue queue, boolean scrollTop) {
        data_list.getItems().clear();
        if(queue == null) {
            return ;
        }
        if(getPlayerManager().getTrackSortBy() != SortBy.Random) {
            queue.sort(getPlayerManager().getTrackSortBy());
        }

        List<Track> tracks = queue.getData();
        if(tracks == null || tracks.isEmpty()) {
            return ;
        }

        PlaybackQueueOptions options = getConfiguration().getPlaybackQueueOptions();
        AtomicInteger count = new AtomicInteger(0);
        StandaloneXml playlistXml = context.getActivePlaylistXml();
        tracks.forEach(track -> {
            track.setQueueId(queue.getId());
            int index = count.incrementAndGet();
            HBox item = createQueueItem(track, index, options);
            data_list.getItems().add(item);
            if(options.isShowTrackTip()) {
                setupTip(item, track.simplifyMetadata());
            }
            String bkgndColor = (index % 2 == 0) ? playlistXml.bkgndColor2 : playlistXml.bkgndColor;
            item.setStyle(String.format("-fx-background-color: %s;", bkgndColor));
        });

        if(scrollTop) {
            data_list.scrollTo(0);
        }

        if(getCurrentPlaybackQueueIndex() == getActivePlaybackQueueIndex()) {
            setupDataListHighlight();
        }
    }

    private HBox createQueueItem(Track track, int sqno, PlaybackQueueOptions options) {
        boolean showSqNo = options.isShowSeqNo();
        String titleFormat = options.getTitleFormat();
        String prefix = showSqNo ? String.valueOf(sqno).concat(".") : "";
        Region flag = new Region();
        String formattedTitle = track.getFormattedTitle(titleFormat);
        Label title = new Label(prefix.concat(formattedTitle));
        Label duration = new Label(toMMss(track.getDuration()));

        flag.getStyleClass().add("flag");
        title.getStyleClass().add("title");
        duration.getStyleClass().add("duration");
        /*
        title.maxWidthProperty().bind(
                getStage().widthProperty().add(
                        name_list.widthProperty()
                                .add(titleWidthOffsetProperty)
                                .multiply(-1)
                ));
         */
        title.maxWidthProperty().bind(data_list.widthProperty().add(titleWidthOffsetProperty));

        Arrays.asList(flag, title, duration)
                .forEach(label -> label.setStyle(getFontStyle()));

        HBox item = new HBox(6, flag, title, duration);
        HBox.setHgrow(title, Priority.ALWAYS);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setUserData(track);
        // setOnContextMenuRequested
        item.setOnMouseClicked(event -> {
            handleTrackItemClick(event, item, sqno - 1, track);
        });

        item.setOnDragDetected(event -> {
            consumeEvent(event);
            if (!options.isAllowDnd()) {
                return ;
            }
            removeListStyleClass(data_list, "selected", "current", "drag_over");
            //item.getStyleClass().removeAll("drag_over");

            StandaloneXml xml = context.getActivePlaylistXml();
            Color lineBkgnd = Color.valueOf(
                    sqno % 2 == 0 ? xml.bkgndColor
                            : isEmpty(xml.bkgndColor2) ? xml.bkgndColor : xml.bkgndColor2
            );

            Dragboard dragboard = item.startDragAndDrop(TransferMode.ANY);

            WritableImage image = new WritableImage((int) item.getWidth(), (int) item.getHeight());
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(lineBkgnd);
            item.snapshot(params, image);

            ClipboardContent content = new ClipboardContent();
            content.putString("MOVE_DRAG");
            dragboard.setContent(content);
            dragboard.setDragView(image);

            setDragStartItem(item);
        });

        item.setOnDragEntered(event -> {
            consumeEvent(event);
            if (!options.isAllowDnd()) {
                return ;
            }
            setDragOverItem(item);
        });

        item.setOnDragOver(event -> {
            consumeEvent(event);
            if (!options.isAllowDnd()) {
                return ;
            }
            if(dragOverItem != null) {
                event.acceptTransferModes(TransferMode.ANY);
                setupListHighlight(data_list, dragOverItem, "drag_over");
            }
        });

        item.setOnDragDropped(event -> {
            consumeEvent(event);
            if (!options.isAllowDnd()) {
                return ;
            }
            if(dragOverItem == null) {
                return ;
            }
            dragOverItem.getStyleClass().removeAll("drag_over");

            if(dragStartItem == null) {
                addTracks(event.getDragboard());
            } else if(dragStartItem != item) {
                moveTrack();
            }
            resetDrag();
        });
        return item;
    }

    private void handleTrackItemClick(MouseEvent event, HBox target, int index, Track track) {
        MouseButton btn = event.getButton();
        //重置
        hideAllMenus(event);
        setupListHighlight(data_list, index, "selected", "current");
        //多选
        if (event.isShiftDown()) {
            deselectAllTracks();

            int min = Math.min(index, lastSelectedIndex);
            int max = Math.max(index, lastSelectedIndex);
            AtomicInteger count = new AtomicInteger(0);
            for (HBox item : data_list.getItems()) {
                int idx = count.getAndIncrement();
                if(idx >= min && idx <= max) {
                    getPlayerManager().getTrackSelections().add(new TrackSelection(
                            track,
                            getActivePlaybackQueueIndex(),
                            idx)
                    );
                }
            };
        } else {
            if(!event.isControlDown()) {
                if(!isInTrackSelections(target) || MouseButton.SECONDARY != btn) {
                    deselectAllTracks();
                }
            }
            //单选
            int count = getPlayerManager().toggleTrackSelection(
                    target,
                    getActivePlaybackQueueIndex(),
                    index,
                    MouseButton.SECONDARY != btn
            );

            if(count < 0) {
                data_list.getItems().get(index).getStyleClass()
                        .removeAll("selected");
            }

            //播放跟随光标
            if(getPlayerManager().isPlayFollowCursorMode()) {
                count = getPlayerManager().togglePlayLaterSelection(target,
                        getActivePlaybackQueueIndex(),
                        index,
                        MouseButton.SECONDARY != btn);

                if(count < 0) {
                    data_list.getItems().get(index).getStyleClass()
                            .removeAll("selected", "current");
                } else if (count > 0) {
                    List<TrackSelection> invalidList = getPlayerManager().removeInvalidPlayLaterSelections();
                    invalidList.forEach(selection -> {
                        data_list.getItems().get(selection.getIndex()).getStyleClass()
                                .removeAll("selected", "current");
                    });
                }
            }

            if(MouseButton.PRIMARY == btn) {
                lastSelectedIndex = index;
            }

        }
        //高亮已选
        setupTrackSelectionsHighlight();
        //高亮播放跟随光标
        setupPlayLaterSelectionsHighlight();

        if(MouseButton.SECONDARY == btn) {
            context.setupContextMenuTrigger(target);
            getTrackContextMenu().show(event);
        }

        //双击播放
        if(event.getClickCount() < 2) {
            hideAllMenus(event);
            return ;
        }

        setCurrentIndex(getActivePlaybackQueueIndex(), index);
    }

    private boolean isInTrackSelections(Node node) {
        return isInTrackSelections(node, getPlayerManager().getTrackSelections());
    }

    private boolean isInTrackSelections(Node node, List<TrackSelection> selections) {
        if(selections == null || selections.isEmpty()) {
            return false;
        }
        Object data = node.getUserData();
        if(data == null) {
            return false;
        }
        for (TrackSelection selection : selections) {
            if(selection.getTrack().equals(data)) {
                return true;
            }
        }
        return false;
    }

    public void selectAllTracks() {
        removeListStyleClass(data_list, "selected");
        if(data_list.getItems().isEmpty()) {
            return ;
        }
        List<TrackSelection> selections = getPlayerManager().getTrackSelections();
        AtomicInteger index = new AtomicInteger(0);
        data_list.getItems().forEach(item -> {
            selections.add(new TrackSelection((Track) item.getUserData(),
                    getActivePlaybackQueueIndex(),
                    index.getAndIncrement())
            );
        });
        //高亮已选
        setupTrackSelectionsHighlight();
    }

    public void deselectAllTracks() {
        setCopying(false);
        setMoving(false);
        List<TrackSelection> selections = getPlayerManager().getTrackSelections();
        if(selections.isEmpty()) {
            return ;
        }
        selections.clear();
        removeListStyleClass(data_list, "selected");
    }

    private void doSelectionsHighlight(List<TrackSelection> list, String... highlightClass) {
        List<HBox> items = data_list.getItems();
        if(items == null || items.isEmpty()) {
            return ;
        }
        if(list == null || list.isEmpty()) {
            return ;
        }
        list.forEach(selection -> {
            int index = selection.getIndex();
            if(index >= items.size()) {
                return ;
            }
            Node item = items.get(index);
            if(item == null) {
                return ;
            }
            if(selection.getTrack().equals(item.getUserData())) {
                item.getStyleClass().addAll(highlightClass);
            }
        });
    }

    private void setupTrackSelectionsHighlight() {
        doSelectionsHighlight(getPlayerManager().getTrackSelections(), "selected");
    }

    private void setupPlayLaterSelectionsHighlight() {
        if (getPlayerManager().isPlayFollowCursorMode()) {
            doSelectionsHighlight(getPlayerManager().getPlayLaterSelections(), "selected", "current");
        }
    }

    public void selectInvertedTracks() {
        removeListStyleClass(data_list, "selected");
        if(data_list.getItems().isEmpty()) {
            return ;
        }
        List<TrackSelection> selections = getPlayerManager().getTrackSelections();
        List<TrackSelection> copiedSelections = new ArrayList<>(selections);
        selections.clear();

        AtomicInteger index = new AtomicInteger(0);
        data_list.getItems().forEach(item -> {
            if(isInTrackSelections(item, copiedSelections)) {
                return ;
            }
            selections.add(new TrackSelection((Track) item.getUserData(),
                    getActivePlaybackQueueIndex(),
                    index.getAndIncrement())
            );
        });
        copiedSelections.clear();
        //高亮已选
        setupTrackSelectionsHighlight();
    }

    public void pasteTrackSelections() {
        if(copying || moving) {
            copyTrackSelections(getActivePlaybackQueue(), moving);
            setMoving(false);
        }
    }

    public void copyTrackSelections(PlaybackQueue destQueue, boolean moving) {
        List<TrackSelection> selections = getPlayerManager().getTrackSelections();
        if(selections.isEmpty()) {
            return ;
        }

        if(destQueue == null) {
            return;
        }

        for (TrackSelection selection : selections) {
            Track track = selection.getTrack();
            boolean removed = true;
            if(moving) {
                PlaybackQueue fromQueue = getPlayerManager().getPlaybackQueue(track.getQueueId());
                if(fromQueue != null && !fromQueue.isEmpty()) {
                    removed = fromQueue.remove(track);
                }
            }
            if(removed) {
                destQueue.addAll(track);
            }
        }
        refresh();
    }

    public void removeSelectedTracks() {
        List<TrackSelection> selections = getPlayerManager().getTrackSelections();
        if(selections.isEmpty()) {
            return ;
        }

        for (TrackSelection selection : selections) {
            Track track = selection.getTrack();
            PlaybackQueue queue = getPlayerManager().getPlaybackQueue(track.getQueueId());
            if(queue != null && !queue.isEmpty()) {
                queue.remove(track);
            }
        }
        refresh();
    }

    public void refresh() {
        loadPlaybackQueue(getActivePlaybackQueue());
        updatePlaybackQueueNames();
    }

    private void removeListStyleClass(ListView<? extends Node> list, String... styleClass) {
        if(list == null || list.getItems().isEmpty()) {
            return ;
        }
        if(styleClass == null || styleClass.length < 1) {
            return ;
        }
        list.getItems().forEach(item -> {
            item.getStyleClass().removeAll(styleClass);
        });
    }

    private void setupListHighlight(ListView<? extends Node> list, int index, String... hilightClasses) {
        if(list == null) {
            return ;
        }
        int size = list.getItems().size();
        if(index < 0 || index >= size) {
            return ;
        }
        Node item = list.getItems().get(index);
        setupListHighlight(list, item, hilightClasses);
    }

    private void setupListHighlight(ListView<? extends Node> list, Node node, String... hilightClasses) {
        removeListStyleClass(list, hilightClasses);
        if(node != null) {
            node.getStyleClass().addAll(hilightClasses);
        }
    }

    private void setupDataListHighlight() {
        setupListHighlight(data_list, getCurrentTrackIndex(), "active");
    }

    private void scrollToCurrentTrack() {
        scrollToTrack(getCurrentTrackIndex());
    }

    private void scrollToTrack(int index) {
        Optional.ofNullable(data_list).ifPresent(__ -> {
            data_list.scrollTo(Math.max(index, 0));
        });
    }

    public void locateCurrentTrack() {
        runFx(() -> {
            setActivePlaybackQueueIndex(getCurrentPlaybackQueueIndex());
            refresh();
            scrollToCurrentTrack();
        });
    }

    private void setupNameListHighlight() {
        setupListHighlight(name_list, getActivePlaybackQueueIndex() ,"active");
    }

    public void clearPlaybackQueue(MouseEvent event) {
        data_list.getItems().clear();
        PlaybackQueue queue = getActivePlaybackQueue();
        if(queue == null) {
            return;
        }
        queue.clear();
        setActivePlaybackQueueIndex(-1);
        deselectAllTracks();
    }

    public void toggleCollapse(MouseEvent event) {
        consumeEvent(event);
        boolean collapsed = isPlaybackQueueNamesCollapsed();
        double width = markNameListWidth;
        if(!collapsed) {
            markNameListWidth = name_list.getWidth();
            width = 0;
        }
        name_list.setPrefWidth(width);
        name_list.setMinWidth(width);
        setItemsAutoVisible(width >= 0, name_list);
        setPlaybackQueueNamesCollapsed(!collapsed);
        setupListPosition(width);
        refreshAllList();
    }

    public void setupListPosition(double width) {
        double leftPadding = 0;
        double splitterWidth = 5;
        AnchorPane.setLeftAnchor(splitter, width + leftPadding);
        AnchorPane.setLeftAnchor(data_list, width + leftPadding + splitterWidth);
    }

    public void refreshAllList() {
        name_list.refresh();
        data_list.refresh();
    }

    private void setupSplitterStyle() {
        splitter.getStyleClass().clear();
        if(isPlaybackQueueNamesCollapsed()) {
            splitter.getStyleClass().add("collapsed");
        }
    }

    public List<MenuMeta> getAddMenu() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("文件",
                new OpenFilesAction()));
        menuMetas.add(new MenuMeta("文件夹",
                new OpenFolderAction()));
        menuMetas.add(new MenuMeta("本地搜索",
                new ShowStageAction(getStageManager().getSearchComputerStage()),
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("网上搜索",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("网络URL",
                new ShowStageAction(getStageManager().getPlayUrlStage())));
        return menuMetas;
    }

    public List<MenuMeta> getRemoveMenu() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("选中的文件",
                __ -> removeSelectedTracks()));
        menuMetas.add(new MenuMeta("重复的文件",
                __ -> getPlayerManager().removeDuplicatedTracks()));
        menuMetas.add(new MenuMeta("错误的文件",
                __ -> getPlayerManager().removeInvalidTracks()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("全部删除",
                this::clearPlaybackQueue));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("物理删除", null, null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)));
        return menuMetas;
    }


    public List<MenuMeta> getListMenu() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("新建列表", new CreatePlaybackQueueAction()));
        menuMetas.add(new MenuMeta("添加列表", new AddPlaybackQueueAction()));
        menuMetas.add(new MenuMeta("打开列表", new OpenPlaybackQueueAction()));
        menuMetas.add(new MenuMeta("保存列表", new SavePlaybackQueueAction()));
        menuMetas.add(new MenuMeta("删除列表", new RemovePlaybackQueueAction()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("保存所有列表",
                __ -> getPlayerManager().saveAllPlaybackQueues()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("媒体库模式",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        return menuMetas;
    }

    public List<MenuMeta> getSortMenu() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("按显示标题",
                new SortTracksAction(Title),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isSortBy(Title)))
        );
        menuMetas.add(new MenuMeta("按文件名",
                new SortTracksAction(FileName),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isSortBy(FileName)))
        );
        menuMetas.add(new MenuMeta("按路径名",
                new SortTracksAction(Url),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isSortBy(Url)))
        );
        menuMetas.add(new MenuMeta("按专辑名",
                new SortTracksAction(AlbumName),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isSortBy(AlbumName)))
        );
        menuMetas.add(new MenuMeta("按星级",
                new SortTracksAction(Rating),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isSortBy(Rating)))
        );
        menuMetas.add(new MenuMeta("按文件时间",
                new SortTracksAction(PublishDate),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isSortBy(PublishDate)))
        );
        menuMetas.add(new MenuMeta("按音轨序号",
                new SortTracksAction(TrackNumber),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isSortBy(TrackNumber)))
        );
        menuMetas.add(new MenuMeta("按播放长度",
                new SortTracksAction(Duration),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isSortBy(Duration)))
        );
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("随机乱序",
                new SortTracksAction(SortBy.Random),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isSortBy(SortBy.Random)))
        );
        return menuMetas;
    }

    public List<MenuMeta> getSearchMenu() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("当前播放",
                getIconStyle("common2.png", 3),
                new ShowCurrentTrackPositionAction()));
        menuMetas.add(new MenuMeta("快速定位",
                new ShowStageAction(getStageManager().getFileQuickPositionStage())));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("查找歌曲",
                getIconStyle("common2.png", 15),
                (EventHandler<? super MouseEvent>) __ -> getStageManager().getFileSearchStage().show()));
        menuMetas.add(new MenuMeta("查找上一个",
                new FileSearchAction(false)));
        menuMetas.add(new MenuMeta("查找下一个",
                new FileSearchAction()));
        return menuMetas;
    }

    public List<MenuMeta> getEditMenu() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("剪切",
                getIconStyle("common2.png", "-159 0"),
                new PlaybackQueueSetCopyingAction(true)));
        menuMetas.add(new MenuMeta("复制",
                getIconStyle("common2.png", 11),
                new PlaybackQueueSetCopyingAction()));
        menuMetas.add(new MenuMeta("粘贴",
                getIconStyle("common2.png", 12),
                new PlaybackQueuePasteAction()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("移动到列表",
                new CopyToPlaybackQueueAction(true)));
        menuMetas.add(new MenuMeta("复制到列表",
                new CopyToPlaybackQueueAction()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("全部选中",
                __ -> selectAllTracks()));
        menuMetas.add(new MenuMeta("全部不选",
                __ -> deselectAllTracks()));
        menuMetas.add(new MenuMeta("反向选中",
                __ -> selectInvertedTracks()));
        return menuMetas;
    }

    public List<MenuMeta> getPlayModeMenu() {
       return getMenuTemplates().getPlayModeMenuList();
    }

    public PopMenu buildMenuBarPopMenu(int index) {
        List<MenuMeta> list = null;
        if(index == 0) {
            list = getAddMenu();
        } else if(index == 1) {
            list = getRemoveMenu();
        } else if(index == 2) {
            list = getListMenu();
        } else if(index == 3) {
            list = getSortMenu();
        } else if(index == 4) {
            list = getSearchMenu();
        } else if(index == 5) {
            list = getEditMenu();
        } else if(index == 6) {
            list = getPlayModeMenu();
        }
        return getMenuBarPopMenu()
                .setMenuList(list);
    }

    private List<MenuMeta> buildNavigationMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("添加", getAddMenu(), 108));
        menuMetas.add(new MenuMeta("删除", getRemoveMenu()));
        menuMetas.add(new MenuMeta("列表", getListMenu()));
        menuMetas.add(new MenuMeta("排序", getSortMenu()));
        menuMetas.add(new MenuMeta("查找", getSearchMenu()));
        menuMetas.add(new MenuMeta("编辑", getEditMenu()));
        menuMetas.add(new MenuMeta("模式", getMenuTemplates().getPlayModeMenuList()));
        return menuMetas;
    }

    public void showNavigationContextMenu(MouseEvent event) {
        if(event.getButton() == MouseButton.SECONDARY) {
            getPlaybackQueueNavigationContextMenu()
                    .setMenuList(buildNavigationMenuList())
                    .show(event);
        } else {
            hideAllMenus(event);
        }
    }

    private boolean matchTrack(Track track, FileSearchOptions options) {
        String title = options.getTitle();
        String artist = options.getArtist();
        String album = options.getAlbum();
        boolean ignoreCase = options.isIgnoreCase();

        if(isEmpty(title) && isEmpty(artist) && isEmpty(album)) {
            return false;
        }

        if(options.isMatchWholeWords()) {
            if(!isEmpty(title) && !contentEquals(track.getTitle(), title, ignoreCase)) {
                return false;
            }
            if(!isEmpty(artist) && !contentEquals(track.getArtist(), artist, ignoreCase)) {
                return false;
            }
            if(!isEmpty(album) && !contentEquals(track.getAlbum(), album, ignoreCase)) {
                return false;
            }
        } else {
            if(!isEmpty(title) && !contains(track.getTitle(), title, ignoreCase)) {
                return false;
            }
            if(!isEmpty(artist) && !contains(track.getArtist(), artist, ignoreCase)) {
                return false;
            }
            if(!isEmpty(album) && !contains(track.getAlbum(), album, ignoreCase)) {
                return false;
            }
        }

        return true;
    }

    private int searchIndex(FileSearchOptions options, int fromIndex, boolean next) {
        boolean ignoreCase = options.isIgnoreCase();
        boolean reversed = options.isDirectionReversed();
        boolean upDirection = (reversed || !next);
        int step = upDirection ? -1 : 1;

        PlaybackQueue queue = getActivePlaybackQueue();
        int index = -1;
        for (int i = fromIndex; (upDirection ? i >= 0 : i < queue.size()); i=i+step) {
            if(matchTrack(queue.getTrack(i), options)) {
                index = i;
                break;
            }
        }
        return index;
    }


    public void search(FileSearchOptions options, boolean next) {
        PlaybackQueue queue = getActivePlaybackQueue();
        if(queue == null || queue.isEmpty()) {
            return ;
        }

        String title = options.getTitle();
        String artist = options.getArtist();
        String album = options.getAlbum();

        if(isEmpty(title) && isEmpty(artist) && isEmpty(album)) {
            return ;
        }

        boolean reversed = options.isDirectionReversed();
        int fromIndex = reversed ? (queue.size() - 1) : 0;
        boolean sameOptions = (lastSearchOptions != null
                && lastSearchOptions.equals(options));
        if(sameOptions) {
            boolean upDirection = (reversed || !next);
            fromIndex = lastSearchIndex + (upDirection ? -1 : 1);
        } else {
            lastSearchIndex = -1;

            data_list.getItems().forEach(item -> {
                item.getStyleClass().removeAll("selected", "current");
            });
        }
        lastSearchOptions = options;

        int index = searchIndex(options, fromIndex, next);
        if(index >= 0) {
            data_list.getItems().forEach(item -> {
                item.getStyleClass().removeAll("selected", "current");
            });

            scrollToTrack(index - 2);
            setupListHighlight(data_list, index, "selected", "current");
            lastSearchIndex = index;
        }
    }

    private void doMarkupAll(Predicate<Track> predicate) {
        PlaybackQueue queue = getActivePlaybackQueue();
        if(queue == null || queue.isEmpty()) {
            return ;
        }

        removeListStyleClass(data_list, "selected", "current");
        List<Track> data = queue.getData();
        int firstIndex = -1;
        for (int i = 0; i < data.size(); i++) {
            if(predicate.test(data.get(i))) {
                if(firstIndex < 0) {
                    firstIndex = i;
                }
                data_list.getItems().get(i)
                        .getStyleClass()
                        .add("selected");
            }
        }
        if (firstIndex >= 0) {
            data_list.getItems().get(firstIndex)
                    .getStyleClass()
                    .add("current");
            scrollToTrack(firstIndex);
        }
    }

    //快速定位
    public void markupAll(String keyword) {
        doMarkupAll(track -> {
            String title = trim(track.getTitle());
            return trimLowerCase(title).startsWith(trimLowerCase(keyword))
                    || pinyinMatch(title, keyword);
        });
    }

    //查找歌曲
    public void markupAll(FileSearchOptions options) {
        String title = options.getTitle();
        String artist = options.getArtist();
        String album = options.getAlbum();

        if(isEmpty(title) && isEmpty(artist) && isEmpty(album)) {
            return ;
        }

        doMarkupAll(track -> matchTrack(track, options));
    }

    @Override
    public void setupSkin() {
        super.setupSkin();
        setItemsHidden(close_btn, toolbar);

        SkinXml skin = getActiveSkinXml();
        SkinXmlWindowItem winItem = skin.getPlaylistWindow();

        winItem.items.forEach(item -> {
            if (item.isCloseItem()) {
                setItemsVisible(close_btn);
                setAnchorAuto(close_btn, skin, item, winItem);
            } //
            else if (item.isPlaylistItem()) {
                Rect rect = item.getPositionRect();
                setAnchorAll(playlist_box,
                        rect.y1(), winItem.width() - rect.x2(),
                        winItem.height() - rect.y2(), rect.x1()
                );
            } //
            else if (item.isScrollbarItem()) {
                /*
                double width = item.width();
                if (width <= 0) {
                    Size size = getSkinImageSize(skin, item.barImage);
                    width = size.width();
                }
                */
                Size size = getSkinImageSize(skin, item.barImage);
                double width = size.width();
                titleWidthOffsetProperty.set((int) (63 + width) * -1);
            } //
            else if (item.isToolbarItem()) {
                //底部居中显示
                if(item.isAlignBottomCenter()) {
                    AnchorPane.setTopAnchor(toolbar, null);
                    AnchorPane.setRightAnchor(toolbar, null);
                    AnchorPane.setBottomAnchor(toolbar,
                            (double) (winItem.height() - item.y2));
                    AnchorPane.setLeftAnchor(toolbar,
                            (getStage().getWidth() - item.width()) / 2D);

                    if(toolbarPosListener != null) {
                        getStage().widthProperty().removeListener(toolbarPosListener);
                    }
                    toolbarPosListener = (o, ov, nv) -> {
                        AnchorPane.setTopAnchor(toolbar, null);
                        AnchorPane.setRightAnchor(toolbar, null);
                        AnchorPane.setLeftAnchor(toolbar,
                                (getStage().getWidth() - item.width()) / 2D);
                        AnchorPane.setBottomAnchor(toolbar,
                                (double) (winItem.height() - item.y2));
                    };
                    getStage().widthProperty().addListener(toolbarPosListener);
                } else {
                    if(toolbarPosListener != null) {
                        getStage().widthProperty().removeListener(toolbarPosListener);
                        toolbarPosListener = null;
                    }
                    setAnchorAuto(toolbar, skin, item, winItem);
                }
                if (item.isProxyItem()) {
                    setItemsHidden(toolbar);
                } else {
                    setItemsVisible(toolbar);
                }
            } else if (item.isTitleItem()) {
                setPrefSize(view_title, item.size());
                //居中显示
                if(item.isAlignCenter()) {
                    AnchorPane.setBottomAnchor(view_title, null);
                    AnchorPane.setRightAnchor(view_title, null);
                    AnchorPane.setTopAnchor(view_title, (double) item.y1);
                    AnchorPane.setLeftAnchor(view_title,
                            (getStage().getWidth() - item.width()) / 2D);

                    if (titlePosListener != null) {
                        getStage().widthProperty().removeListener(titlePosListener);
                    }
                    titlePosListener = (o, ov, nv) -> {
                        AnchorPane.setBottomAnchor(view_title, null);
                        AnchorPane.setRightAnchor(view_title, null);
                        AnchorPane.setTopAnchor(view_title, (double) item.y1);
                        AnchorPane.setLeftAnchor(view_title,
                                (getStage().getWidth() - item.width()) / 2D);
                    };
                    getStage().widthProperty().addListener(titlePosListener);
                } else {
                    if(titlePosListener != null) {
                        getStage().widthProperty().removeListener(titlePosListener);
                        titlePosListener = null;
                    }
                    setAnchorAuto(view_title, skin, item, winItem);
                }
            }
        });
        refresh();
    }

}