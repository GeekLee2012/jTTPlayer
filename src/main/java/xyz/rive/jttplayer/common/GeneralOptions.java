package xyz.rive.jttplayer.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralOptions {
    private boolean allowTrayIcon = true;
    private boolean allowMetadataAutoSwitch = true;
    private int metadataAutoSwitchInterval;
    private boolean allowWinAutoAttach = true;
    private int winAutoAttachLimit = 25;

    public boolean isAllowTrayIcon() {
        return allowTrayIcon;
    }

    public void setAllowTrayIcon(boolean allowTrayIcon) {
        this.allowTrayIcon = allowTrayIcon;
    }

    public boolean isAllowMetadataAutoSwitch() {
        return allowMetadataAutoSwitch;
    }

    public void setAllowMetadataAutoSwitch(boolean allowMetadataAutoSwitch) {
        this.allowMetadataAutoSwitch = allowMetadataAutoSwitch;
    }

    public int getMetadataAutoSwitchInterval() {
        return metadataAutoSwitchInterval;
    }

    public void setMetadataAutoSwitchInterval(int metadataAutoSwitchInterval) {
        this.metadataAutoSwitchInterval = metadataAutoSwitchInterval;
    }

    public boolean isAllowWinAutoAttach() {
        return allowWinAutoAttach;
    }

    public void setAllowWinAutoAttach(boolean allowWinAutoAttach) {
        this.allowWinAutoAttach = allowWinAutoAttach;
    }

    public int getWinAutoAttachLimit() {
        return winAutoAttachLimit;
    }

    public void setWinAutoAttachLimit(int winAutoAttachLimit) {
        this.winAutoAttachLimit = winAutoAttachLimit;
    }
}
