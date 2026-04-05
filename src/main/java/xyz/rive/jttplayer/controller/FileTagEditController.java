package xyz.rive.jttplayer.controller;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import xyz.rive.jttplayer.common.Pair;
import xyz.rive.jttplayer.menu.MenuMeta;
import xyz.rive.jttplayer.menu.PopMenu;
import xyz.rive.jttplayer.menu.strategy.SharedStrategies;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.trim;

public class FileTagEditController extends CommonController {

    @FXML
    private BorderPane file_tag_edit_view;
    @FXML
    private ComboBox<String> field_name;
    @FXML
    private TextArea field_value;
    @FXML
    private Label traditional_btn;
    private PopMenu traditionalZhMenu;
    private Consumer<Pair> okAction;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupController(this, file_tag_edit_view);

        field_name.getItems().addAll(Arrays.asList(
                "ALBUM", "ARTIST",
                "COMMENT", "DATE",
                "GENRE", "LYRICS",
                "RATING", "TITLE",
                "TRACKNUMBER"
        ));
    }

    public void applyClose(MouseEvent event) {
        //consumeEvent(event);
        String key = field_name.getValue();
        String value = field_value.getText();

        closeView();
        if (isEmpty(key)) {
            return ;
        }

        Pair data = new Pair(trim(key), trim(value));
        Optional.ofNullable(okAction).ifPresent(__ -> {
            okAction.accept(data);
        });
    }

    public void setOkAction(Consumer<Pair> action) {
        this.okAction = action;
    }

    public void loadContent(Pair pair) {
        field_name.setDisable(true);
        field_name.setValue(pair.key());
        field_value.setText((String) pair.value());
    }

    @Override
    public void beforeCloseView() {
        field_name.setDisable(false);
        field_name.setValue("");
        field_value.setText("");
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
        String key = field_name.getValue();
        if (isEmpty(key)) {
            return ;
        }

        String value = trim(field_value.getText());
        if(traditional) {
            field_value.setText(ZhConverterUtil.toTraditional(value));
        } else {
            field_value.setText(ZhConverterUtil.toSimple(value));
        }
    }

    public void toggleTraditionalMenu(MouseEvent event) {
        getTraditionalZhMenu().toggle(event);
    }

    public void hideMenus(MouseEvent event) {
        hideMenu(event, getTraditionalZhMenu());
    }
}
