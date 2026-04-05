package xyz.rive.jttplayer.control;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.FontOption;
import xyz.rive.jttplayer.controller.FontSelectionController;

import java.util.Optional;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.util.FxUtils.getCssUrl;

public class FontButton extends Label {
    private final ApplicationContext context = ApplicationContext.getInstance();
    private String family;
    private String weight;
    private int size = 14;
    private int defaultSize = 14;
    private Consumer<FontOption> okAction;

    public FontButton() {
        this("选择");
    }

    public FontButton(String text) {
        setText(text);
        getStyleClass().add("font_btn");
        getStylesheets().setAll(getCssUrl("font-btn"));

        setGraphicTextGap(7);
        Region graphic = new Region();
        graphic.getStyleClass().add("graphic");
        setGraphic(graphic);

        setOnMouseClicked(event -> {
            context.getStageManager().getFontSelectionStage()
                    .show();
            context.getControllerManager().setupFont(family, weight, size, defaultSize, option -> {
                setFontFamily(option.getFamily());
                setFontWeight(option.getWeight());
                setFontSize(option.getSize());

                Optional.ofNullable(okAction)
                        .ifPresent(action -> action.accept(option));
            });
        });
    }

    public void setFontFamily(String family) {
        this.family = family;
    }

    public void setFontWeight(String weight) {
        this.weight = weight;
    }

    public void setFontSize(int size) {
        setFontSize(size, 14);
    }

    public void setFontSize(int size, int defaultSize) {
        this.size = size;
        this.defaultSize = defaultSize;
    }

    public void setOkAction(Consumer<FontOption> action) {
        this.okAction = action;
    }

}
