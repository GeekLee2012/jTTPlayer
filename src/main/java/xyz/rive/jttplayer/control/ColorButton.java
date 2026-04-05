package xyz.rive.jttplayer.control;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.Optional;

import static xyz.rive.jttplayer.util.FxUtils.getCssUrl;
import static xyz.rive.jttplayer.util.FxUtils.setupTip;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

public class ColorButton extends Label {
    private ColorPicker picker;
    private StringProperty valueProperty = new SimpleStringProperty("");

    public ColorButton() {
        getStyleClass().add("color_btn");
        getStylesheets().setAll(getCssUrl("color-btn"));

        Region color = new Region();
        color.getStyleClass().add("color");

        HBox colorBox = new HBox(color, getPicker());
        colorBox.getStyleClass().add("box");
        setGraphic(colorBox);

        valueProperty.addListener((o, ov, nv) -> {
            color.setStyle(String.format("-fx-background-color: %s", nv));
            //getTooltip().setText(getValue());
        });

        setOnMouseClicked(event -> {
            Optional.ofNullable(toColor(valueProperty.get()))
                    .ifPresent(picker::setValue);
            picker.show();
        });

        //addCustomColor("#8dbac5");
        //addCustomColor("#31475b");
        //addCustomColors("#5277a8", "#11243c", "#e6f2ff");

        //setTooltip(setupTip(this, getValue()));
    }

    public String getValue() {
        return valueProperty.get();
    }

    public void setValue(String value) {
        valueProperty.setValue(value);
    }

    public StringProperty valueProperty()  {
        return valueProperty;
    }

    private ColorPicker getPicker() {
        if(picker == null) {
            picker = new ColorPicker();
            picker.setManaged(false);
            picker.setVisible(false);
            picker.setTranslateX(-5);
            picker.setTranslateY(21);
            picker.setOnAction(event -> {
                String value = "#" + picker.getValue().toString()
                        .replaceAll("0x", "");
                setValue(value);
            });
        }
        return picker;
    }

    public void clearCustomColors() {
        getPicker().getCustomColors().clear();
    }

    public void addCustomColor(String value) {
        Optional.ofNullable(toColor(value))
                .ifPresent(getPicker().getCustomColors()::add);
    }

    public void addCustomColors(String... values) {
        if (values != null) {
            for (String value: values) {
                addCustomColor(value);
            }
        }
    }

    private Color toColor(String value) {
        if (isEmpty(value)) {
            return null;
        }
        try {
            return Color.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
