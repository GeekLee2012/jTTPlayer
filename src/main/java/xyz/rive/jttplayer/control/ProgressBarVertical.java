package xyz.rive.jttplayer.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.util.Optional;

import static xyz.rive.jttplayer.util.FxUtils.setCssBackgroundImage;


public class ProgressBarVertical extends AnchorPane {
    private final BooleanProperty enableProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty isSliding = new SimpleBooleanProperty(false);
    private final DoubleProperty valueProperty = new SimpleDoubleProperty(0);
    private Region progressBox;
    private Region thumbBox;
    private double minValue = 0;
    private double maxValue = 100;
    private EventHandler<MouseEvent> handler;
    private EventHandler<InputEvent> slidingHandler;
    private double prevY;

    public ProgressBarVertical() {
        this(6, 54);
    }

    public ProgressBarVertical(double width, double height) {
        getStyleClass().add("progress_bar_vertical");
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
            event.consume();
            if(!enableProperty.get()) {
                return ;
            }
            setValue(heightToValue(prefHeight(-1) - event.getY()));
            Optional.ofNullable(handler).ifPresent(__ -> handler.handle(event));
        });

        setOnMousePressed(event -> {
            event.consume();
            isSliding.set(true);
            prevY = event.getY();
        });

        setOnScroll(event -> {
            event.consume();
            if(!isSliding.get()) {
                return ;
            }
            double distance = heightToValue(0D - event.getDeltaY()) ;
            setValue(getValue() + distance);
            prevY = event.getDeltaY();
            Optional.ofNullable(slidingHandler).ifPresent(
                    handler -> handler.handle(event));
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

        setValue(0);
    }

    private void setupAlignCenter() {
        AnchorPane.setLeftAnchor(progressBox,
                (prefWidth(-1) - progressBox.prefWidth(-1)) / 2D);
        AnchorPane.setTopAnchor(thumbBox,
                (prefWidth(-1) - thumbBox.prefWidth(-1)) / 2D);
    }

    private double valueToHeight() {
        return getPercentValue() * getPrefHeight();
    }

    private double heightToValue(double height) {
        return height / getPrefHeight() * Math.abs(maxValue - minValue);
    }


    private void doUpdate(double height) {
        height = Math.max(height, 0);
        height = Math.min(height, getPrefHeight());

        double offset = thumbBox.prefHeight(-1) / 2D;
        double bottomAnchor = height - offset;
        bottomAnchor = Math.max(bottomAnchor, offset / -2D);
        bottomAnchor = Math.min(bottomAnchor, getPrefHeight() - offset * 1.5D);

        progressBox.setPrefHeight(height);
        AnchorPane.setBottomAnchor(thumbBox, bottomAnchor);
    }


    private void startSlide(MouseEvent event) {
        if(!enableProperty.get()) {
            return ;
        }
        event.consume();
        isSliding.set(true);
    }

    private void sliding(MouseEvent event) {
        doSliding(event);
    }

    private void exitSlide(MouseEvent event) {
        doSliding(event);
        isSliding.set(false);
    }

    private void doSliding(MouseEvent event) {
        if(!enableProperty.get()) {
            return ;
        }
        if(!isSliding.get()) {
            return ;
        }
        event.consume();
        Point2D cur = thumbBox.localToParent(event.getX(), event.getY());
        setValue(heightToValue(prefHeight(-1) - cur.getY()));
        Optional.ofNullable(slidingHandler).ifPresent(handler -> handler.handle(event));
    }

    public void setValue(double value) {
        valueProperty.set(value);
    }

    public double getValue() {
        return valueProperty.getValue();
    }

    public void setLimit(double min, double max) {
        this.minValue = min;
        this.maxValue = max;
    }

    public ProgressBarVertical onMouseClicked(EventHandler<MouseEvent> handler) {
        this.handler = handler;
        return this;
    }

    public ProgressBarVertical onSliding(EventHandler<InputEvent> handler) {
        this.slidingHandler = handler;
        return this;
    }

    public double getPercentValue() {
        double percent = valueProperty.get() / Math.abs(maxValue - minValue);
        percent = Math.max(percent, 0);
        percent = Math.min(percent, 100);
        return percent;
    }

    public boolean isSliding() {
        return isSliding.get();
    }

    public void setEnabled(boolean enabled) {
        enableProperty.set(enabled);
    }


}
