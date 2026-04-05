package xyz.rive.jttplayer.anim;

import javafx.animation.AnimationTimer;
import javafx.scene.control.ScrollPane;

import static xyz.rive.jttplayer.util.FxUtils.easeInOutQuad;

//待考虑是否需要由一个AnimationTimer统一调度管理
public class ScrollAnimation extends AnimationTimer {
    private final ScrollPane scrollPane;
    private double startValue = - 1;
    private double distance = 1;
    private final double duration;
    private final double step;
    private double current = 0;

    public ScrollAnimation(ScrollPane pane) {
        this(pane, 300, 5);
    }

    public ScrollAnimation(ScrollPane pane, double duration, double step) {
        this.scrollPane = pane;
        this.duration = duration;
        this.step = step;
    }

    public void setDestination(double value) {
        this.startValue = scrollPane.getVvalue();
        this.distance = value - startValue;
        this.current = 0;
    }

    @Override
    public void handle(long now) {
        if(current > duration) {
            stop();
            return ;
        }
        double updated = easeInOutQuad(current, startValue, distance, duration);
        if (updated >= 0 && updated <= 1) {
            scrollPane.setVvalue(updated);
        }
        current += step;
    }

}
