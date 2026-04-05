package xyz.rive.jttplayer.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import xyz.rive.jttplayer.common.Size;
import xyz.rive.jttplayer.util.FxUtils;

import java.util.Optional;

import static xyz.rive.jttplayer.util.FxUtils.getCssUrl;
import static xyz.rive.jttplayer.util.FxUtils.setCssBackgroundImage;


public class ProgressBarHorizontal extends AnchorPane {
    private final BooleanProperty enableProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty isSliding = new SimpleBooleanProperty(false);
    private final DoubleProperty valueProperty = new SimpleDoubleProperty(0);
    private Region progressBox;
    private Region thumbBox;
    private double minValue = 0;
    private double maxValue = 100;
    private EventHandler<MouseEvent> handler;
    private EventHandler<MouseEvent> slidingHandler;

    public ProgressBarHorizontal() {
        this(144, 4);
    }

    public ProgressBarHorizontal(double width, double height) {
        getStyleClass().add("progress_bar_horizontal");
        setPrefWidth(width);
        setPrefHeight(height);

        init();
    }

    private void init() {
        progressBox = new Region();
        progressBox.getStyleClass().add("progress");
        //progressBox.prefHeightProperty().bind(prefHeightProperty());

        thumbBox = new Region();
        thumbBox.getStyleClass().add("thumb");

        getChildren().addAll(progressBox, thumbBox);
        AnchorPane.setLeftAnchor(progressBox, 0D);

        setOnMouseClicked(event -> {
            event.consume();
            if(!enableProperty.get()) {
                return ;
            }
            setValue(widthToValue(event.getX()));
            Optional.ofNullable(handler).ifPresent(__ -> handler.handle(event));
        });

        thumbBox.setOnMousePressed(this::startSlide);
        thumbBox.setOnMouseDragged(this::sliding);
        thumbBox.setOnMouseReleased(this::exitSlide);

        prefHeightProperty().addListener(__ -> setupAlignCenter());
        progressBox.prefHeightProperty().addListener(__ -> setupAlignCenter());
        thumbBox.prefHeightProperty().addListener(__ -> setupAlignCenter());

        valueProperty.addListener((o, ov, nv) -> doUpdate(valueToWidth()));
        prefWidthProperty().addListener(__ -> doUpdate(valueToWidth()));
        thumbBox.prefWidthProperty().addListener(__ -> doUpdate(valueToWidth()));

        setValue(0);
    }

    private void setupAlignCenter() {
        AnchorPane.setTopAnchor(progressBox,
                (prefHeight(-1) - progressBox.prefHeight(-1)) / 2D);
        AnchorPane.setTopAnchor(thumbBox,
                (prefHeight(-1) - thumbBox.prefHeight(-1)) / 2D);
    }

    private double valueToWidth() {
        return getPercentValue() * getPrefWidth();
    }

    private double widthToValue(double width) {
        return width / getPrefWidth() * Math.abs(maxValue - minValue);
    }


    private void doUpdate(double width) {
        width = Math.max(width, 0);
        width = Math.min(width, getPrefWidth());

        double offset = thumbBox.prefWidth(-1) / 2D;
        double leftAnchor = width - offset;
        leftAnchor = Math.max(leftAnchor, offset / -2D);
        leftAnchor = Math.min(leftAnchor, getPrefWidth() - offset * 1.5D);

        progressBox.setPrefWidth(width);
        AnchorPane.setLeftAnchor(thumbBox, leftAnchor);
        //thumbBox.setLayoutX(leftAnchor);
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
        setValue(widthToValue(cur.getX()));
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

    public ProgressBarHorizontal onMouseClicked(EventHandler<MouseEvent> handler) {
        this.handler = handler;
        return this;
    }

    public ProgressBarHorizontal onSliding(EventHandler<MouseEvent> handler) {
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
