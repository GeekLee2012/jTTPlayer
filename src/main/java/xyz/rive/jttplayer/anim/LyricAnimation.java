package xyz.rive.jttplayer.anim;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.Lyric;
import xyz.rive.jttplayer.common.PlayState;

import java.util.function.Consumer;

import static xyz.rive.jttplayer.util.FxUtils.getUserData;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.toMillis;

//待考虑是否需要由一个AnimationTimer统一调度管理
public class LyricAnimation extends AnimationTimer {
    private final ApplicationContext context;
    //private final ScrollPane lyricPane;
    private final VBox lyricContent;
    private int state = -1;
    private long lastUpdated = -1;
    private long delayMillis = -1;
    public final static int MIN_INTERVAL_MILLIS = 1000; // 1 fps
    public final static double MIN_INTERVAL_NANOS = MIN_INTERVAL_MILLIS * 1e6;
    private final ScrollAnimation scrollAnimation;
    private long offset = 0;
    private Consumer<Node> postHandleListener;

    public LyricAnimation(ApplicationContext context, ScrollPane lyricPane) {
        this.context = context;
        //this.lyricPane = lyricPane;
        this.scrollAnimation = new ScrollAnimation(lyricPane);
        this.lyricContent = (VBox) lyricPane.getContent();
        this.setState(context.getPlayerManager().getPlayState());
        context.getPlayerManager().onPlayState((o, ov, nv) -> this.setState((int)nv));
    }

    private void setState(int state) {
        this.state = state;
        setupRunState();
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getOffset() {
        return offset;
    }

    private double calculateDestination(Node line) {
        Bounds bounds = line.getBoundsInParent();
        double y = (bounds.getMinY() + bounds.getMaxY()) / 2D;
        double paddingTop = lyricContent.getPadding().getTop();
        double paddingBottom = lyricContent.getPadding().getBottom();
        double maxHeight = lyricContent.getHeight() - paddingTop - paddingBottom;
        return (y - paddingTop) / maxHeight;
    }

    @Override
    public void handle(long now) {
        if(lastUpdated > 0 && (now - lastUpdated) < MIN_INTERVAL_NANOS) {
            return;
        }
        lastUpdated = now;
        if(System.currentTimeMillis() < delayMillis) {
            return ;
        }
        delayMillis = -1;

        scrollLyric(context.getPlayerManager().getTimePosition());
    }

    public void scrollLyric(double seconds) {
        ObservableList<Node> lines = lyricContent.getChildren();
        if(lines.isEmpty()) {
            return ;
        }
        long trackTime = (long)(seconds * 1000L - offset + MIN_INTERVAL_MILLIS);

        int index = -1;
        VBox currentLine = null;
        for (int i = 0; i < lines.size(); i++) {
            VBox line = (VBox) lines.get(i);
            String time = (String) line.getUserData();
            if(isEmpty(time)) {
                continue ;
            }
            long millis = toMillis(time);
            if(trackTime < millis) {
                break;
            }
            index = i;
            currentLine = line;
        }
        if(index < 0 || lines.isEmpty()) {
            return ;
        }

        setupHighlight(currentLine);

        scrollAnimation.setDestination(calculateDestination(currentLine));
        scrollAnimation.start();
        //setupHighlight(currentLine);

        if(postHandleListener != null) {
            postHandleListener.accept(currentLine);
        }
    }

    private void setupHighlight(Node current) {
        ObservableList<Node> lines = lyricContent.getChildren();
        if(lines.isEmpty()) {
            return ;
        }
        for (Node line : lines) {
            line.getStyleClass().removeAll("active", "locator_current");
            if (line == current) {
                line.getStyleClass().add("active");
            }
        }
    }

    private void resetHighlightWords(Node line) {
        for (Node node : line.lookupAll("Text")) {
            node.getStyleClass().removeAll("hilight");
        }
    }

    private void setupHighlightWords(Node line) {
        double timePos = context.getPlayerManager().getTimePosition();
        long trackTime = (long)(timePos * 1000L - offset + MIN_INTERVAL_MILLIS);
        for (Node node : line.lookupAll("Text")) {
            Text text = (Text) node;
            Lyric.WordToken token = getUserData(text, Lyric.WordToken.class);
            if (token == null) {
                continue;
            }
            long startMillis = toMillis(token.startTime);
            //long endMillis = toMillis(token.endTime);
            if(startMillis > trackTime) {
                break;
            }
            text.getStyleClass().add("hilight");
        }
    }

    @Override
    public void start() {
        setupRunState();
    }

    @Override
    public void stop() {
        super.stop();
        scrollAnimation.stop();
    }

    public void startLine(Node line) {
        if(line == null) {
            return ;
        }
        start();
        scrollAnimation.setDestination(calculateDestination(line));
        scrollAnimation.start();
        setupHighlight(line);
    }

    private void setupRunState() {
        if (state == PlayState.PLAYING.getValue()) {
            super.start();
        } else {
            stop();
        }
    }

    public void start(int delayMillis) {
        this.delayMillis = System.currentTimeMillis() + delayMillis;
        start();
    }

    public LyricAnimation postHandle(Consumer<Node> listener) {
        postHandleListener = listener;
        return this;
    }

}
