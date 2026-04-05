package xyz.rive.jttplayer.control;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import static xyz.rive.jttplayer.util.FxUtils.getCssUrl;

public class ConfigButton extends Label {

    public ConfigButton() {
        setText("配置文件");
        getStyleClass().add("config_btn");
        getStylesheets().setAll(getCssUrl("config-btn"));

        setGraphicTextGap(7);
        Region graphic = new Region();
        graphic.getStyleClass().add("graphic");
        setGraphic(graphic);
    }

}
