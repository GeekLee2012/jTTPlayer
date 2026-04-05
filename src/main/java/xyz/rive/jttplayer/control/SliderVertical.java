package xyz.rive.jttplayer.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import xyz.rive.jttplayer.common.Size;
import xyz.rive.jttplayer.util.FxUtils;

import static xyz.rive.jttplayer.util.FxUtils.*;
import static xyz.rive.jttplayer.util.FxUtils.setCssBackgroundImage;


public class SliderVertical extends AnchorPane {
    private final BooleanProperty slidingProperty = new SimpleBooleanProperty(false);
    private final DoubleProperty valueProperty = new SimpleDoubleProperty(0);
    private Region progressBox;
    private Region thumbBox;
    private double minValue = 0;
    private double maxValue = 100;
    private boolean enabled = false;
    private boolean isEventTrigger = false;


    public SliderVertical() {
        this(6, 54);
    }

    public SliderVertical(double width, double height) {
        getStyleClass().add("slider_vertical");
        setPrefWidth(width);
        setPrefHeight(height);

        init();
    }

    private void init() {
        progressBox = new Region();
        progressBox.getStyleClass().add("progress");
        progressBox.setPrefHeight(0);

        thumbBox = new Region();
        thumbBox.getStyleClass().add("thumb");

        getChildren().addAll(progressBox, thumbBox);
        AnchorPane.setBottomAnchor(progressBox, 0D);

        setOnMouseClicked(event -> {
            //event.consume();
            if(!enabled) {
                return ;
            }
            if(isSliding()) {
                return ;
            }
            setValue(heightToValue(getPrefHeight() - event.getY()), true);
        });

        thumbBox.setOnMousePressed(this::startSlide);
        thumbBox.setOnMouseDragged(this::sliding);
        thumbBox.setOnMouseReleased(this::exitSlide);

        prefWidthProperty().addListener(__ -> setupAlignCenter());
        progressBox.prefWidthProperty().addListener(__ -> setupAlignCenter());
        thumbBox.prefWidthProperty().addListener(__ -> setupAlignCenter());

        valueProperty.addListener((o, ov, nv) -> doUpdate(valueToHeight()));
        prefHeightProperty().addListener(__ -> doUpdate(valueToHeight()));
        thumbBox.prefHeightProperty().addListener(__ -> doUpdate(valueToHeight()));

        setValue(50);
    }

    private void setupAlignCenter() {
        AnchorPane.setLeftAnchor(thumbBox,
                (prefWidth(-1) - thumbBox.prefWidth(-1)) / 2D);
        AnchorPane.setLeftAnchor(progressBox,
                (prefWidth(-1) - progressBox.prefWidth(-1)) / 2D);
    }

    private double valueToHeight() {
        return (valueProperty.get() - minValue) /
                Math.abs(maxValue - minValue) * getPrefHeight();
    }

    private double heightToValue(double height) {
        return height / getPrefHeight() * Math.abs(maxValue - minValue) ;
    }

    private void doUpdate(double height) {
        height = Math.max(height, 0);
        height = Math.min(height, getPrefHeight());

        double offset = thumbBox.prefHeight(-1) / 2D;
        double bottomAnchor = height - offset;
        bottomAnchor = Math.max(bottomAnchor, -1D * offset);
        bottomAnchor = Math.min(bottomAnchor, getPrefHeight() - offset);

        progressBox.setPrefHeight(height);
        AnchorPane.setBottomAnchor(thumbBox, bottomAnchor);
    }

    public void setValue(double value) {
        setValue(value, false);
    }

    private void setValue(double value, boolean isEventTrigger) {
        value = Math.max(value, minValue);
        value = Math.min(value, maxValue);

        valueProperty.set(value);
        this.isEventTrigger = isEventTrigger;
    }

    private void startSlide(MouseEvent event) {
        event.consume();
        if(!enabled) {
            return ;
        }
        slidingProperty.set(true);
    }

    private void sliding(MouseEvent event) {
        event.consume();
        if(!enabled) {
            return ;
        }
        Point2D cur = thumbBox.localToParent(event.getX(), event.getY());
        setValue(heightToValue(getPrefHeight() - cur.getY()), true);
    }

    private void exitSlide(MouseEvent event) {
        event.consume();
        if(!enabled) {
            return ;
        }
        slidingProperty.set(false);
    }

    public DoubleProperty valueProperty() {
        return valueProperty;
    }

    public BooleanProperty slidingProperty() {
        return slidingProperty;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getValue() {
        return valueProperty.get();
    }

    public boolean isSliding() {
        return slidingProperty.get();
    }

    public boolean shouldNotifyChanged() {
        return !isSliding() && isEventTrigger;
    }

}
