package xyz.rive.jttplayer.player;

import xyz.rive.jttplayer.common.Track;

import java.util.function.Consumer;

public interface Player {
    void setPlayCorePath(String path);
    void setCurrentTrack(Track track);
    Track getCurrentTrack();
    Player onStarted(Consumer<Boolean> listener);
    Player onPaused(Consumer<Boolean> listener);
    Player onStopped(Consumer<Boolean> listener);
    Player onTimePosition(Consumer<Object> listener);
    Player onStateChanged(Consumer<Object> listener);
    Consumer<Boolean> getStartedListener();
    Consumer<Boolean> getPausedListener();
    Consumer<Boolean> getStoppedListener();
    Consumer<Object> getTimePositionListener();
    Consumer<Object> getStateListener();
    void setVolume(double volume);
    double getVolume();
    double getTimePosition();
    double getDuration();
    void play();
    void pause();
    void togglePause();
    void stop();
    void seekRelative(double seconds);
    void seek(double seconds);
    boolean isMute();
    void mute();
    void unmute();
    void toggleMute();
    void quit();
    void play(Track track);
    void setEqualizer(int[] values);
    void removeEqualizer();
    boolean isVisualSupported();
    float[] getFftData();
    float[] getTimeDomainData();
    boolean isReady();
    Player setTempPath(String path);

    default int getTimePositionSeconds() {
        double pos = getTimePosition();
        return pos >= 0 ? (int) (pos * 60) : -1;
    }
}
