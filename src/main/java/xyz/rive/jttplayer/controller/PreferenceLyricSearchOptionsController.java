package xyz.rive.jttplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import xyz.rive.jttplayer.common.ItemOrder;
import xyz.rive.jttplayer.common.LyricSearchOptions;

import java.io.File;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import static xyz.rive.jttplayer.common.Constants.LYRIC_DOWNLOAD_DIR_PLACEHOLDER;
import static xyz.rive.jttplayer.common.Constants.TRACK_DIR_PLACEHOLDER;
import static xyz.rive.jttplayer.util.FileUtils.transformPath;
import static xyz.rive.jttplayer.util.StringUtils.contentEquals;

public class PreferenceLyricSearchOptionsController extends CommonController {

    @FXML
    private VBox lyric_search_options;
    @FXML
    private ListView<CheckBox> search_orders;
    @FXML
    private ComboBox<String> server_list;
    @FXML
    private TextField download_path;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, lyric_search_options);
    }

    public void loadContent() {
        loadSearchOrderList();
        loadServerList();
    }

    private void loadSearchOrderList() {
        LyricSearchOptions options = getConfiguration().getLyricSearchOptions();
        List<ItemOrder> itemOrders = options.prepareSearchOrders();
        search_orders.getItems().clear();
        itemOrders.stream()
                .sorted(Comparator.comparingInt(ItemOrder::getOrder))
                .forEach(order -> {
                    CheckBox item = new CheckBox(order.getPath());
                    item.setSelected(order.isDeep());
                    search_orders.getItems().add(item);
                });
    }

    private void loadServerList() {
        LyricSearchOptions options = getConfiguration().getLyricSearchOptions();
        server_list.getItems().clear();
        options.getServers().forEach(server -> {
            server_list.getItems().add(server.getName());
        });
    }

    private void syncSearchOrders() {
        LyricSearchOptions options = getConfiguration().getLyricSearchOptions();
        options.clearItemOrders();
        int i = 0;
        for (CheckBox item : search_orders.getItems()) {
            options.addItemOrder(item.getText(), item.isSelected(), i++);
        }
    }

    @Override
    public void afterShowView() {
        super.afterShowView();
        loadContent();
    }

    private boolean isPresetFolder(String dir) {
        return contentEquals(dir, TRACK_DIR_PLACEHOLDER)
                || contentEquals(dir, LYRIC_DOWNLOAD_DIR_PLACEHOLDER);
    }

    public void addFolder(MouseEvent event) {
        consumeEvent(event);
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择目录");
        File selection = chooser.showDialog(getStageManger().getPreferenceStage());
        if (selection == null) {
            return ;
        }
        String path = transformPath(selection.getAbsolutePath());
        search_orders.getItems().add(
                new CheckBox(path)
        );
        syncSearchOrders();
    }

    public void removeFolder(MouseEvent event) {
        consumeEvent(event);
        CheckBox selection = search_orders.getSelectionModel().getSelectedItem();
        if (selection == null) {
            return ;
        }
        if (isPresetFolder(selection.getText())) {
            return ;
        }
        search_orders.getItems().remove(selection);
        syncSearchOrders();
    }

    private void moveToIndex(CheckBox item, int index) {
        if (item == null || index < 0) {
            return ;
        }
        CheckBox nItem = new CheckBox(item.getText());
        nItem.setUserData(item.getUserData());
        nItem.setSelected(item.isSelected());
        search_orders.getItems().add(index, nItem);
        search_orders.getItems().remove(item);
        search_orders.getSelectionModel().select(nItem);
        syncSearchOrders();
    }

    public void moveForward(MouseEvent event) {
        consumeEvent(event);
        CheckBox selection = search_orders.getSelectionModel().getSelectedItem();
        if (selection == null) {
            return ;
        }
        int index = search_orders.getItems().indexOf(selection);
        if (index < 1) {
            return ;
        }
        moveToIndex(selection, index - 1);
    }

    public void moveBackward(MouseEvent event) {
        consumeEvent(event);
        CheckBox selection = search_orders.getSelectionModel().getSelectedItem();
        if (selection == null) {
            return ;
        }
        int index = search_orders.getItems().indexOf(selection);
        int maxIndex = search_orders.getItems().size() - 1;
        if (index < 0 || index >= maxIndex) {
            return ;
        }
        moveToIndex(selection, index + 2);
    }

    public void clearSelections(MouseEvent event) {
        consumeEvent(event);
        lyric_search_options.requestFocus();
        search_orders.getSelectionModel().clearSelection();
    }

    public void setDownloadPath(MouseEvent event) {
        consumeEvent(event);
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择目录");
        File selection = chooser.showDialog(getStageManger().getPreferenceStage());
        if (selection == null) {
            return ;
        }
        String path = transformPath(selection.getAbsolutePath());
        download_path.setText(path);
        getConfiguration().getLyricSearchOptions()
                .setDownloadPath(path);
    }

    public void showLyricServerManageView(MouseEvent event) {
        consumeEvent(event);
        String value = server_list.getValue();
        Stage stage = getStageManger().getLyricServerManageStage();
        stage.setOnHidden(__ -> {
            loadServerList();
            boolean isExists = server_list.getItems().contains(value);
            server_list.setValue(isExists ? value : null);
        });
        stage.show();
    }
}
