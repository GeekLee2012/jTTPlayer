package xyz.rive.jttplayer.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayOptions {
    //播放
    private int playCoreType = 1;
    private String playCorePath;
    private String lastPlayCorePaths;
    private boolean playOnStartup;
    private boolean continueLastPlay;
    private int playInterval;
    private boolean stopPlayIfErrorOccurred;
    private int playPriority;
    private long wholeFileBuffer;
    //声音淡入淡出
    private boolean fadeInOutOnPlaying;
    private long fadeInOutOnPlayingMillis;
    private boolean fadeInOutOnPaused;
    private long fadeInOutOnPausedMillis;
    private boolean fadeInOutOnInteracting;
    private long fadeInOutOnInteractingMillis;
    private boolean fadeInOutOnStopped;
    private long fadeInOutOnStoppedMillis;
    private boolean fadeInOutOnAutoSwitchPlay;
    private long fadeInOutOnAutoSwitchPlayMillis;
    //回放增益
    private boolean replayGainEnabled;
    private boolean autoScanGainEnabled;
    private boolean ignoreFilesExistsGain;

    public int getPlayCoreType() {
        return playCoreType;
    }

    public void setPlayCoreType(int playCoreType) {
        this.playCoreType = playCoreType;
    }

    public String getPlayCorePath() {
        return playCorePath;
    }

    public void setPlayCorePath(String playCorePath) {
        this.playCorePath = playCorePath;
    }

    public String getLastPlayCorePaths() {
        return lastPlayCorePaths;
    }

    public void setLastPlayCorePaths(String lastPlayCorePaths) {
        this.lastPlayCorePaths = lastPlayCorePaths;
    }

    public boolean isPlayOnStartup() {
        return playOnStartup;
    }

    public void setPlayOnStartup(boolean playOnStartup) {
        this.playOnStartup = playOnStartup;
    }

    public boolean isContinueLastPlay() {
        return continueLastPlay;
    }

    public void setContinueLastPlay(boolean continueLastPlay) {
        this.continueLastPlay = continueLastPlay;
    }

    public int getPlayInterval() {
        return playInterval;
    }

    public void setPlayInterval(int playInterval) {
        this.playInterval = playInterval;
    }

    public boolean isStopPlayIfErrorOccurred() {
        return stopPlayIfErrorOccurred;
    }

    public void setStopPlayIfErrorOccurred(boolean stopPlayIfErrorOccurred) {
        this.stopPlayIfErrorOccurred = stopPlayIfErrorOccurred;
    }

    public int getPlayPriority() {
        return playPriority;
    }

    public void setPlayPriority(int playPriority) {
        this.playPriority = playPriority;
    }

    public long getWholeFileBuffer() {
        return wholeFileBuffer;
    }

    public void setWholeFileBuffer(long wholeFileBuffer) {
        this.wholeFileBuffer = wholeFileBuffer;
    }

    public boolean isFadeInOutOnPlaying() {
        return fadeInOutOnPlaying;
    }

    public void setFadeInOutOnPlaying(boolean fadeInOutOnPlaying) {
        this.fadeInOutOnPlaying = fadeInOutOnPlaying;
    }

    public long getFadeInOutOnPlayingMillis() {
        return fadeInOutOnPlayingMillis;
    }

    public void setFadeInOutOnPlayingMillis(long fadeInOutOnPlayingMillis) {
        this.fadeInOutOnPlayingMillis = fadeInOutOnPlayingMillis;
    }

    public boolean isFadeInOutOnPaused() {
        return fadeInOutOnPaused;
    }

    public void setFadeInOutOnPaused(boolean fadeInOutOnPaused) {
        this.fadeInOutOnPaused = fadeInOutOnPaused;
    }

    public long getFadeInOutOnPausedMillis() {
        return fadeInOutOnPausedMillis;
    }

    public void setFadeInOutOnPausedMillis(long fadeInOutOnPausedMillis) {
        this.fadeInOutOnPausedMillis = fadeInOutOnPausedMillis;
    }

    public boolean isFadeInOutOnInteracting() {
        return fadeInOutOnInteracting;
    }

    public void setFadeInOutOnInteracting(boolean fadeInOutOnInteracting) {
        this.fadeInOutOnInteracting = fadeInOutOnInteracting;
    }

    public long getFadeInOutOnInteractingMillis() {
        return fadeInOutOnInteractingMillis;
    }

    public void setFadeInOutOnInteractingMillis(long fadeInOutOnInteractingMillis) {
        this.fadeInOutOnInteractingMillis = fadeInOutOnInteractingMillis;
    }

    public boolean isFadeInOutOnStopped() {
        return fadeInOutOnStopped;
    }

    public void setFadeInOutOnStopped(boolean fadeInOutOnStopped) {
        this.fadeInOutOnStopped = fadeInOutOnStopped;
    }

    public long getFadeInOutOnStoppedMillis() {
        return fadeInOutOnStoppedMillis;
    }

    public void setFadeInOutOnStoppedMillis(long fadeInOutOnStoppedMillis) {
        this.fadeInOutOnStoppedMillis = fadeInOutOnStoppedMillis;
    }

    public boolean isFadeInOutOnAutoSwitchPlay() {
        return fadeInOutOnAutoSwitchPlay;
    }

    public void setFadeInOutOnAutoSwitchPlay(boolean fadeInOutOnAutoSwitchPlay) {
        this.fadeInOutOnAutoSwitchPlay = fadeInOutOnAutoSwitchPlay;
    }

    public long getFadeInOutOnAutoSwitchPlayMillis() {
        return fadeInOutOnAutoSwitchPlayMillis;
    }

    public void setFadeInOutOnAutoSwitchPlayMillis(long fadeInOutOnAutoSwitchPlayMillis) {
        this.fadeInOutOnAutoSwitchPlayMillis = fadeInOutOnAutoSwitchPlayMillis;
    }

    public boolean isReplayGainEnabled() {
        return replayGainEnabled;
    }

    public void setReplayGainEnabled(boolean replayGainEnabled) {
        this.replayGainEnabled = replayGainEnabled;
    }

    public boolean isAutoScanGainEnabled() {
        return autoScanGainEnabled;
    }

    public void setAutoScanGainEnabled(boolean autoScanGainEnabled) {
        this.autoScanGainEnabled = autoScanGainEnabled;
    }

    public boolean isIgnoreFilesExistsGain() {
        return ignoreFilesExistsGain;
    }

    public void setIgnoreFilesExistsGain(boolean ignoreFilesExistsGain) {
        this.ignoreFilesExistsGain = ignoreFilesExistsGain;
    }
}
