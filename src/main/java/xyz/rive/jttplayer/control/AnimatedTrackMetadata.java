package xyz.rive.jttplayer.control;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import xyz.rive.jttplayer.common.Track;

import java.util.concurrent.atomic.AtomicInteger;

import static xyz.rive.jttplayer.common.Constants.APP_TITLE_VERSION;
import static xyz.rive.jttplayer.util.FxUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class AnimatedTrackMetadata extends VBox {
    private final IntegerProperty trackMetaIndex = new SimpleIntegerProperty(0);

    private final SlideAnimation animation = new SlideAnimation();

    public AnimatedTrackMetadata() {
        init();
    }

    public void setClip(double width, double height) {
        setClip(new Rectangle(width, height));
    }

    private void init() {
        setMinWidth(168);
        setMaxWidth(202);
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(0);

        //getStylesheets().add(getCssUrl("animated-track-metadata"));
        getStyleClass().add("track_meta");

        setClip(168, 25);
        prefWidthProperty().addListener(__ -> {
            setClip(prefWidth(-1), prefHeight(-1));
        });
        prefHeightProperty().addListener(__ -> {
            setClip(prefWidth(-1), prefHeight(-1));
        });

        trackMetaIndex.addListener((o, ov, nv) -> setupMetadataPosition());

        for(int i = 0; i < 6; i++) {
            Label label = new Label(APP_TITLE_VERSION);
            label.setId("meta_" + (i + 1));
            label.prefHeightProperty().bind(prefHeightProperty());
            label.minHeightProperty().bind(prefHeightProperty());
            label.maxHeightProperty().bind(prefHeightProperty());
            label.setAlignment(Pos.CENTER_LEFT);
            /*
            label.setStyle(
                    String.format("-fx-text-fill: %s;" + "-fx-font-size: %s;", "#ffffff", 13)
            );
            */
            getChildren().add(label);
        }

        //setOnMouseClicked(this::nextMetadata);
        animation.start();
    }

    private int size() {
        return getChildren().size();
    }

    private boolean isLast() {
        return trackMetaIndex.get() == (size() - 1);
    }

    private int getScrollTop(int index) {
        index = index % size();
        return (int) (-1 * prefHeight(-1) * index);
    }

    public void setupMetadataPosition() {
        animation.setupRound(true);
    }

    private void setScrollTop(double scrollTop) {
        setStyle("-fx-padding: " + scrollTop +" 0 0 0");
    }

    public void nextMetadata(MouseEvent... event) {
        int index = (trackMetaIndex.get() + 1) % getChildren().size();
        trackMetaIndex.set(index);
    }

    public void resetMetadataPosition() {
        trackMetaIndex.set(-1);
    }

    public void setInterval(int seconds) {
        animation.setInterval(seconds);
    }

    public void updateMetadata(int index, Track track) {
        if(track == null) {
            return ;
        }
        String[] metadata = {
                String.valueOf(index + 1).concat(".").concat(track.getTitle()),
                "标题: ".concat((trim(track.getTitle()))),
                "艺术家: ".concat(trim(track.getArtist())),
                "专辑: ".concat(trim(track.getAlbum())),
                "格式: ".concat(String.format("%s  %s  %s",
                        trim(track.getExtName()),
                        track.getTransformedSampleRate(),
                        track.getTransformedBitRate())),
                "时长: ".concat(toMMss(track.getDuration())),
        };
        AtomicInteger count = new AtomicInteger(0);
        getChildren().forEach(item -> {
            ((Label)item).setText(metadata[count.getAndIncrement()]);
        });
    }

    //待考虑是否需要由一个AnimationTimer统一调度管理
    protected class SlideAnimation extends AnimationTimer {
        private double interval = 5;
        private double startValue = - 1;
        private double distance = 1;
        private double duration = 300;
        private double current = 0;
        private double step = 5;
        private long lastUpdated = -1;
        private boolean forceUpdated;

        public void setupRound(boolean forceUpdated) {
            int index = trackMetaIndex.get();
            int startIndex = isLast() ? -1 : index;
            int destIndex = isLast() ? 0 : index + 1;

            startValue = getScrollTop(startIndex);
            double destValue = getScrollTop(destIndex);
            distance = destValue - startValue;
            current = 0;
            this.forceUpdated = forceUpdated;
        }

        public double getInterval() {
            return interval;
        }

        public void setInterval(double interval) {
            this.interval = interval;
        }

        private double getIntervalNanos() {
            if(interval < 1) {
                setInterval(5);
            }
            return interval * 1e9;
        }

        @Override
        public void handle(long now) {
            if(current > duration) {
                this.forceUpdated = false;
                if(now - lastUpdated >= getIntervalNanos()) {
                    nextMetadata();
                }
                return ;
            }
            if(!forceUpdated && lastUpdated > 0
                    && (now - lastUpdated) < getIntervalNanos()) {
                return;
            }
            lastUpdated = now;
            double updated = easeInOutQuad(current, startValue, distance, duration);
            setScrollTop(updated);
            current += step;
        }
    }

    public void setupStyle(String color, String fontFamily, int fontSize) {
        String textColor = isEmpty(color) ? "#ffffff" : color;
        int textSize = fontSize < 6 ? 13 : fontSize;
        getChildren().forEach(item -> {
            item.setStyle(
                    String.format("-fx-text-fill: %s;"
                            + "-fx-font-size: %s;",
                            textColor,
                            textSize
                    )
            );
        });
    }

}
