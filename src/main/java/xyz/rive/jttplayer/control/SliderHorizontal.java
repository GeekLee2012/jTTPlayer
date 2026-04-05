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
import xyz.rive.jttplayer.service.AsyncService;
import xyz.rive.jttplayer.util.FxUtils;

import static xyz.rive.jttplayer.util.FxUtils.*;


public class SliderHorizontal extends AnchorPane {
    private final BooleanProperty isSliding = new SimpleBooleanProperty(false);
    private final DoubleProperty valueProperty = new SimpleDoubleProperty(50);
    private Region progressBox;
    private Region thumbBox;
    private double minValue = 0;
    private double maxValue = 100;

    public SliderHorizontal() {
        this(66, 6);
    }

    public SliderHorizontal(double width, double height) {
        getStylesheets().add(getCssUrl("slider"));
        getStyleClass().add("slider_horizontal");
        setPrefWidth(width);
        setPrefHeight(height);

        init();
    }

    private void init() {
        progressBox = new Region();
        progressBox.getStyleClass().add("progress");

        thumbBox = new Region();
        thumbBox.getStyleClass().add("thumb");
        getChildren().addAll(progressBox, thumbBox);

        AnchorPane.setLeftAnchor(progressBox, 0D);

        setOnMouseClicked(event -> {
            //event.consume();
            if(isSliding.get()) {
                return ;
            }
            update(event.getX());
        });

        thumbBox.setOnMousePressed(this::startSlide);
        thumbBox.setOnMouseDragged(this::sliding);
        thumbBox.setOnMouseReleased(this::exitSlide);

        prefHeightProperty().addListener(__ -> setupAlignCenter());
        progressBox.prefHeightProperty().addListener(__ -> setupAlignCenter());
        thumbBox.prefHeightProperty().addListener(__ -> setupAlignCenter());

        prefWidthProperty().addListener(__ -> doUpdate(valueToWidth()));
        thumbBox.prefWidthProperty().addListener(__ -> doUpdate(valueToWidth()));

        doUpdate(valueToWidth());
    }

    private void setupAlignCenter() {
        AnchorPane.setTopAnchor(progressBox,
                (prefHeight(-1) - progressBox.prefHeight(-1)) / 2D);
        AnchorPane.setTopAnchor(thumbBox,
                (prefHeight(-1) - thumbBox.prefHeight(-1)) / 2D);
    }

    private double valueToWidth() {
        return valueProperty.get() / Math.abs(maxValue - minValue) * getPrefWidth();
    }

    private void update(double width) {
        double percent = doUpdate(width);
        double value = percent * Math.abs(maxValue - minValue);
        setValue(value);
    }

    private double doUpdate(double width) {
        width = Math.max(width, 0);
        width = Math.min(width, getPrefWidth());

        double offset = thumbBox.prefWidth(-1) / 2D;
        double leftAnchor = width - offset;
        leftAnchor = Math.max(leftAnchor, -1D * offset);
        leftAnchor = Math.min(leftAnchor, getPrefWidth() - offset);

        progressBox.setPrefWidth(width);
        AnchorPane.setLeftAnchor(thumbBox, leftAnchor);
        return width / getPrefWidth();
    }

    private void setValue(double value) {
        valueProperty.set(value);
    }

    private void startSlide(MouseEvent event) {
        event.consume();
        isSliding.set(true);
    }

    private void sliding(MouseEvent event) {
        event.consume();
        Point2D cur = thumbBox.localToParent(event.getX(), event.getY());
        update(cur.getX());
    }

    private void exitSlide(MouseEvent event) {
        event.consume();
        isSliding.set(false);
    }

    public DoubleProperty valueProperty() {
        return valueProperty;
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

    public void setLimit(double min, double max) {
        this.minValue = min;
        this.maxValue = max;
    }

}
