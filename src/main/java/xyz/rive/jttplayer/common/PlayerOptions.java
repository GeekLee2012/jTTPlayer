package xyz.rive.jttplayer.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static xyz.rive.jttplayer.common.PlaybackMode.Sequence;
import static xyz.rive.jttplayer.common.SortBy.None;
import static xyz.rive.jttplayer.skin.Constants.DEFAULT_SKIN_NAME;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerOptions {
    private boolean miniMode = false;
    private int playbackMode = Sequence.getValue();
    private double volume = 100;
    private boolean volumeMute;
    private double currentTime = 0;
    private int currentTrackIndex = -1;
    private int currentPlaybackQueueIndex = -1;
    private int activePlaybackQueueIndex = -1;
    private boolean musicOnlineShow;
    private boolean equalizerShow;
    private boolean playbackQueueShow;
    private boolean lyricShow;
    private boolean lyricDesktopMode;
    private boolean lyricDesktopLocked;
    private int lyricZhType = 0;
    private boolean alwaysOnTop;
    private boolean lyricViewAlwaysOnTop;
    private SortBy sortBy = None;
    private Bound mainViewBound;
    private Bound equalizerViewBound;
    private Bound playbackQueueViewBound;
    private Bound lyricViewBound;
    private boolean playTimeCountDownMode = true;
    private double opacity = 1;
    private boolean ignoreOpacityOnActive;
    private boolean autoSwitchPlaybackQueue;
    private boolean playFollowCursorMode;
    private String skinRoot;
    private String activeSkinName = DEFAULT_SKIN_NAME;
    private int activeVisualIndex = 0;

    public boolean isMiniMode() {
        return miniMode;
    }

    public void setMiniMode(boolean miniMode) {
        this.miniMode = miniMode;
    }

    public int getPlaybackMode() {
        return playbackMode;
    }

    public void setPlaybackMode(int playbackMode) {
        this.playbackMode = playbackMode;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public boolean isVolumeMute() {
        return volumeMute;
    }

    public void setVolumeMute(boolean volumeMute) {
        this.volumeMute = volumeMute;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
    }

    public int getCurrentTrackIndex() {
        return currentTrackIndex;
    }

    public void setCurrentTrackIndex(int currentTrackIndex) {
        this.currentTrackIndex = currentTrackIndex;
    }

    public int getCurrentPlaybackQueueIndex() {
        return currentPlaybackQueueIndex;
    }

    public void setCurrentPlaybackQueueIndex(int currentPlaybackQueueIndex) {
        this.currentPlaybackQueueIndex = currentPlaybackQueueIndex;
    }

    public int getActivePlaybackQueueIndex() {
        return activePlaybackQueueIndex;
    }

    public void setActivePlaybackQueueIndex(int activePlaybackQueueIndex) {
        this.activePlaybackQueueIndex = activePlaybackQueueIndex;
    }

    public boolean isMusicOnlineShow() {
        return musicOnlineShow;
    }

    public void setMusicOnlineShow(boolean musicOnlineShow) {
        this.musicOnlineShow = musicOnlineShow;
    }

    public boolean isEqualizerShow() {
        return equalizerShow;
    }

    public void setEqualizerShow(boolean equalizerShow) {
        this.equalizerShow = equalizerShow;
    }

    public boolean isPlaybackQueueShow() {
        return playbackQueueShow;
    }

    public void setPlaybackQueueShow(boolean playbackQueueShow) {
        this.playbackQueueShow = playbackQueueShow;
    }

    public boolean isLyricShow() {
        return lyricShow;
    }

    public void setLyricShow(boolean lyricShow) {
        this.lyricShow = lyricShow;
    }

    public boolean isLyricDesktopMode() {
        return lyricDesktopMode;
    }

    public void setLyricDesktopMode(boolean lyricDesktopMode) {
        this.lyricDesktopMode = lyricDesktopMode;
    }

    public boolean isLyricDesktopLocked() {
        return lyricDesktopLocked;
    }

    public void setLyricDesktopLocked(boolean lyricDesktopLocked) {
        this.lyricDesktopLocked = lyricDesktopLocked;
    }

    public int getLyricZhType() {
        return lyricZhType;
    }

    public void setLyricZhType(int lyricZhType) {
        this.lyricZhType = lyricZhType;
    }

    public boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop = alwaysOnTop;
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy;
    }

    public Bound getMainViewBound() {
        return mainViewBound;
    }

    public void setMainViewBound(Bound mainViewBound) {
        this.mainViewBound = mainViewBound;
    }

    public Bound getEqualizerViewBound() {
        return equalizerViewBound;
    }

    public void setEqualizerViewBound(Bound equalizerViewBound) {
        this.equalizerViewBound = equalizerViewBound;
    }

    public Bound getPlaybackQueueViewBound() {
        return playbackQueueViewBound;
    }

    public void setPlaybackQueueViewBound(Bound playbackQueueViewBound) {
        this.playbackQueueViewBound = playbackQueueViewBound;
    }

    public Bound getLyricViewBound() {
        return lyricViewBound;
    }

    public void setLyricViewBound(Bound lyricViewBound) {
        this.lyricViewBound = lyricViewBound;
    }

    public boolean isPlayTimeCountDownMode() {
        return playTimeCountDownMode;
    }

    public void setPlayTimeCountDownMode(boolean playTimeCountDownMode) {
        this.playTimeCountDownMode = playTimeCountDownMode;
    }

    public boolean isLyricViewAlwaysOnTop() {
        return lyricViewAlwaysOnTop;
    }

    public void setLyricViewAlwaysOnTop(boolean lyricViewAlwaysOnTop) {
        this.lyricViewAlwaysOnTop = lyricViewAlwaysOnTop;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public boolean isIgnoreOpacityOnActive() {
        return ignoreOpacityOnActive;
    }

    public void setIgnoreOpacityOnActive(boolean ignoreOpacityOnActive) {
        this.ignoreOpacityOnActive = ignoreOpacityOnActive;
    }

    public boolean isAutoSwitchPlaybackQueue() {
        return autoSwitchPlaybackQueue;
    }

    public void setAutoSwitchPlaybackQueue(boolean autoSwitchPlaybackQueue) {
        this.autoSwitchPlaybackQueue = autoSwitchPlaybackQueue;
    }

    public boolean isPlayFollowCursorMode() {
        return playFollowCursorMode;
    }

    public void setPlayFollowCursorMode(boolean playFollowCursorMode) {
        this.playFollowCursorMode = playFollowCursorMode;
    }

    public String getSkinRoot() {
        return skinRoot;
    }

    public void setSkinRoot(String skinRoot) {
        this.skinRoot = skinRoot;
    }

    public String getActiveSkinName() {
        return isEmpty(activeSkinName) ? DEFAULT_SKIN_NAME : activeSkinName;
    }

    public void setActiveSkinName(String activeSkinName) {
        this.activeSkinName = activeSkinName;
    }

    public int getActiveVisualIndex() {
        return activeVisualIndex;
    }

    public void setActiveVisualIndex(int activeVisualIndex) {
        this.activeVisualIndex = activeVisualIndex;
    }

}
