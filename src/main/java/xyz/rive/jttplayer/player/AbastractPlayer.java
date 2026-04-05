package xyz.rive.jttplayer.player;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.Track;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

public abstract class AbastractPlayer implements Player {
    private final StringProperty playCorePath = new SimpleStringProperty();
    protected volatile boolean running = false;
    protected Track currentTrack = null;
    //每个状态/属性值，仅支持设置一个Listener
    protected Consumer<Boolean> startedListener;
    protected Consumer<Boolean> pausedListener;
    protected Consumer<Boolean> stoppedListener;
    protected Consumer<Object> timePositionListener;
    protected Consumer<Object> stateChangedListener;

    protected volatile double timePosition = -1;
    protected volatile double duration = -1;
    protected volatile double volume = 100D;
    protected volatile boolean mute = false;

    public static final int[] FREQUENCIES = { 31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000 };

    private ScheduledFuture<?> tickFuture;

    public AbastractPlayer() {
        playCorePath.addListener((__, oldValue, newValue) -> {
            if (setup()) {
                rebindListeners();
            }
        });
    }

    protected abstract boolean setup();

    public void setPlayCorePath(String path) {
        playCorePath.set(path);
    }

    @Override
    public Track getCurrentTrack() {
        return currentTrack;
    }

    @Override
    public void setCurrentTrack(Track track) {
        this.currentTrack = track;
    }

    protected boolean isTrackAvailable() {
        return currentTrack != null && !isEmpty(currentTrack.getUrl());
    }

    @Override
    public Player onStarted(Consumer<Boolean> listener) {
        startedListener = listener;
        return this;
    }

    @Override
    public Player onPaused(Consumer<Boolean> listener) {
        pausedListener = listener;
        return this;
    }

    @Override
    public Player onStopped(Consumer<Boolean> listener) {
        stoppedListener = listener;
        return this;
    }

    @Override
    public Player onTimePosition(Consumer<Object> listener) {
        timePositionListener = listener;
        return this;
    }

    @Override
    public Player onStateChanged(Consumer<Object> listener) {
        stateChangedListener = listener;
        return this;
    }

    @Override
    public Consumer<Boolean> getStartedListener() {
        return startedListener;
    }

    @Override
    public Consumer<Boolean> getPausedListener() {
        return pausedListener;
    }

    @Override
    public Consumer<Boolean> getStoppedListener() {
        return stoppedListener;
    }

    @Override
    public Consumer<Object> getTimePositionListener() {
        return timePositionListener;
    }

    @Override
    public Consumer<Object> getStateListener() {
        return stateChangedListener;
    }

    private void rebindListeners() {
        Optional.ofNullable(startedListener).ifPresent(this::onStarted);
        Optional.ofNullable(pausedListener).ifPresent(this::onPaused);
        Optional.ofNullable(stoppedListener).ifPresent(this::onStopped);
        Optional.ofNullable(timePositionListener).ifPresent(this::onTimePosition);
        Optional.ofNullable(stateChangedListener).ifPresent(this::onStateChanged);
    }

    @Override
    public void setVolume(double value) {
        value = Math.max(value, 0);
        value = Math.min(value, 100);
        volume = value;
    }

    @Override
    public double getVolume() {
        return volume;
    }

    @Override
    public double getTimePosition() {
        return timePosition;
    }

    protected void setTimePosition(double pos) {
        timePosition = pos;
    }

    @Override
    public double getDuration() {
        return duration;
    }

    @Override
    public boolean isMute() {
        return mute;
    }

    protected void setMute(boolean value) {
        mute = value;
    }

    public String getPlayCorePath() {
        return playCorePath.get();
    }

    public void tick(Runnable task) {
        tick(task, 1000);
    }

    public void tick(Runnable task, long millis) {
        stopTick();
        tickFuture = ApplicationContext.getInstance()
                .runEachDelay(task, millis);
    }

    public void stopTick() {
        if(tickFuture != null) {
            tickFuture.cancel(true);
            tickFuture = null;
        }
    }

    @Override
    public boolean isVisualSupported() {
        return false;
    }

    @Override
    public float[] getFftData() {
        return null;
    }

    @Override
    public float[] getTimeDomainData() {
        return null;
    }

}
