package xyz.rive.jttplayer.control;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import xyz.rive.jttplayer.common.Size;
import static xyz.rive.jttplayer.util.StringUtils.toMMss;

public class PlayTime extends HBox {
    private Region timeFlag;
    private Region hMinutes;
    private Region lMinutes;
    private Region hSeconds;
    private Region lSeconds;
    private Region timeSp;
    private boolean countDownMode;
    private int lastSeconds = -1;
    private double lastDuration = -1;

    public PlayTime() {
        this(true);
    }

    public PlayTime(boolean countDownMode) {
        init();
        setCountDownMode(countDownMode);
    }

    private void init() {
        //getStylesheets().add(getCssUrl("play-time"));
        getStyleClass().add("play_time");

        timeFlag = new Region();
        timeFlag.getStyleClass().add("time_flag");
        timeFlag.widthProperty().addListener(__ -> {
            update(lastSeconds, lastDuration);
        });

        hMinutes = new Region();
        hMinutes.getStyleClass().add("minutes_h");

        lMinutes = new Region();
        lMinutes.getStyleClass().add("minutes_l");

        timeSp = new Region();
        timeSp.getStyleClass().add("time_sp");

        hSeconds = new Region();
        hSeconds.getStyleClass().add("seconds_h");

        lSeconds = new Region();
        lSeconds.getStyleClass().add("seconds_l");

        getChildren().addAll(timeFlag, hMinutes, lMinutes, timeSp, hSeconds, lSeconds);
    }

    public void setCountDownMode(boolean enabled) {
        countDownMode = enabled;
        timeFlag.setVisible(countDownMode);
    }

    public void update(int seconds, double duration) {
        lastSeconds = seconds;
        lastDuration = duration;
        if(duration <=  0 || seconds > duration) {
            return ;
        }
        int current = countDownMode ? (int) (duration - seconds) : seconds;
        if(current < 0) {
            return ;
        }
        try {
            String[] chars = toMMss(current).split("");
            Region[] items = { hMinutes, lMinutes, null, hSeconds, lSeconds };
            for(int i = 0; i < items.length; i++) {
                Region item = items[i];
                if (item == null) {
                    continue;
                }
                if(!Character.isDigit(chars[i].charAt(0))) {
                    continue;
                }
                int x = (int) (Integer.parseInt(chars[i]) * -1 * item.prefWidth(-1));
                item.setStyle("-fx-background-position: " + x + " 0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
