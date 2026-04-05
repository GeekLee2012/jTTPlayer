package xyz.rive.jttplayer.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;

import static xyz.rive.jttplayer.util.FxUtils.getCssUrl;

public class VolumeBarVertical extends VBox {
    private final static double MAX_VALUE = 100;
    private final static double MIN_VALUE = 0;
    private final BooleanProperty isSliding = new SimpleBooleanProperty(false);
    private final DoubleProperty valueProperty = new SimpleDoubleProperty(MIN_VALUE);
    private Region fillBox;
    private Region statusBox;

    private EventHandler<MouseEvent> clickedHandler;
    private EventHandler<InputEvent> slideStartedHandler;
    private EventHandler<InputEvent> slidingHandler;
    private EventHandler<InputEvent> slideFinishedHandler;
    private EventHandler<MouseEvent> statusHandler;

    //private double prevX;
    private double prevY;

    public VolumeBarVertical() {
        this(12, 53);
    }

    public VolumeBarVertical(double width, double height) {
        getStylesheets().add(getCssUrl("volume-bar"));
        getStyleClass().add("volume_bar_vertical");
        setPrefWidth(width);
        setPrefHeight(height);
        setSpacing(4);

        init();
    }

    private void init() {
        fillBox = new Region();
        fillBox.getStyleClass().add("volume_fill");

        statusBox = new Region();
        //statusBox.setPrefHeight(11);
        statusBox.getStyleClass().add("volume_status");

        getChildren().addAll(fillBox, statusBox);

        valueProperty.addListener((o, ov, nv) -> {
            update(valueToHeight());
        });

        setOnMouseClicked(event -> {
            //event.consume();
            if(event.getY() >= (getFillBoxMaxHeight() - 0.02)){
                return ;
            }
            double percent = 1D - event.getY() / getFillBoxMaxHeight();
            setValue(percentToValue(percent));
            Optional.ofNullable(clickedHandler).ifPresent(handler -> handler.handle(event));
        });

        setOnMousePressed(event -> {
            event.consume();
            isSliding.set(true);
            //prevX = event.getX();
            prevY = event.getY();
            Optional.ofNullable(slideStartedHandler).ifPresent(
                    handler -> handler.handle(event));
        });

        setOnMouseDragged(event -> {
            event.consume();
            if(!isSliding.get()) {
                return ;
            }
            double distance = prevY - event.getY();
            setValue(getValue() + distance);
            prevY = event.getY();
            Optional.ofNullable(slidingHandler).ifPresent(
                    handler -> handler.handle(event));
        });

        setOnMouseReleased(event -> {
            event.consume();
            if(!isSliding.get()) {
                return ;
            }
            isSliding.set(false);
            Optional.ofNullable(slideFinishedHandler).ifPresent(
                    handler -> handler.handle(event));
        });

        setOnScrollStarted(event -> {
            event.consume();
            isSliding.set(true);
            Optional.ofNullable(slideStartedHandler).ifPresent(
                    handler -> handler.handle(event));
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

        setOnScrollFinished(event -> {
            event.consume();
            isSliding.set(true);
            Optional.ofNullable(slideFinishedHandler).ifPresent(
                    handler -> handler.handle(event));
        });

        statusBox.setOnMouseClicked(event -> {
            event.consume();
            Optional.ofNullable(statusHandler).ifPresent(
                    handler -> handler.handle(event));
        });

        setValue(MAX_VALUE);
    }

    private double percentToValue(double percent) {
        percent = Math.max(percent, 0);
        percent = Math.min(percent, 100);
        return percent * Math.abs(MAX_VALUE - MIN_VALUE);
    }

    private double getFillBoxMaxHeight() {
        return getPrefHeight() - statusBox.getPrefHeight() - getSpacing();
    }

    private void update(double height) {
        height = Math.max(height, 0);
        height = Math.min(height, getFillBoxMaxHeight());
        fillBox.setPrefHeight(height);
    }

    private double valueToHeight() {
        return getPercentValue() * getFillBoxMaxHeight();
    }

    private double heightToValue(double height) {
        return height / getFillBoxMaxHeight();
    }

    public void setValue(double value) {
        valueProperty.set(value);
    }

    public double getValue() {
        return valueProperty.get();
    }

    public double getPercentValue() {
        return getValue() / Math.abs(MAX_VALUE - MIN_VALUE);
    }

    public void updateStatusBtn(boolean mute) {
        statusBox.getStyleClass().remove("mute");
        if(mute)  {
            statusBox.getStyleClass().add("mute");
        }
    }

    public VolumeBarVertical onStatusBtnClicked(EventHandler<MouseEvent> handler) {
        this.statusHandler = handler;
        return this;
    }

    public VolumeBarVertical onMouseClicked(EventHandler<MouseEvent> handler) {
        this.clickedHandler = handler;
        return this;
    }

    public VolumeBarVertical onSlideStarted(EventHandler<InputEvent> handler) {
        this.slideStartedHandler = handler;
        return this;
    }

    public VolumeBarVertical onSliding(EventHandler<InputEvent> handler) {
        this.slidingHandler = handler;
        return this;
    }

    public VolumeBarVertical onSlideFinished(EventHandler<InputEvent> handler) {
        this.slideFinishedHandler = handler;
        return this;
    }
}
