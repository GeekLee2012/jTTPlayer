package xyz.rive.jttplayer.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaybackQueueOptions {
    private boolean allowDnd = true;
    private boolean banDiskDelete = true;
    private boolean showTrackTip = true;
    private int fontSize = 12;
    private boolean showSeqNo = true;
    private boolean useCustomTitleFormat = true;
    private String customTitleFormat = "%T - %A";
    private boolean useDefaultTitleFormat = true;
    private String defaultTitleFormat = "%F";

    public boolean isAllowDnd() {
        return allowDnd;
    }

    public void setAllowDnd(boolean allowDnd) {
        this.allowDnd = allowDnd;
    }

    public boolean isBanDiskDelete() {
        return banDiskDelete;
    }

    public void setBanDiskDelete(boolean banDiskDelete) {
        this.banDiskDelete = banDiskDelete;
    }

    public boolean isShowTrackTip() {
        return showTrackTip;
    }

    public void setShowTrackTip(boolean showTrackTip) {
        this.showTrackTip = showTrackTip;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isShowSeqNo() {
        return showSeqNo;
    }

    public void setShowSeqNo(boolean showSeqNo) {
        this.showSeqNo = showSeqNo;
    }

    public boolean isUseCustomTitleFormat() {
        return useCustomTitleFormat;
    }

    public void setUseCustomTitleFormat(boolean useCustomTitleFormat) {
        this.useCustomTitleFormat = useCustomTitleFormat;
    }

    public String getCustomTitleFormat() {
        return customTitleFormat;
    }

    public void setCustomTitleFormat(String customTitleFormat) {
        this.customTitleFormat = customTitleFormat;
    }

    public boolean isUseDefaultTitleFormat() {
        return useDefaultTitleFormat;
    }

    public void setUseDefaultTitleFormat(boolean useDefaultTitleFormat) {
        this.useDefaultTitleFormat = useDefaultTitleFormat;
    }

    public String getDefaultTitleFormat() {
        return defaultTitleFormat;
    }

    public void setDefaultTitleFormat(String defaultTitleFormat) {
        this.defaultTitleFormat = defaultTitleFormat;
    }

    @JsonIgnore
    public String getTitleFormat() {
        String format = null;
        if (useCustomTitleFormat) {
            format = customTitleFormat;
        }
        if (isEmpty(format)) {
            if(useDefaultTitleFormat) {
                format = defaultTitleFormat;
            }
        }
        if (isEmpty(format)) {
            format = "%F";
        }
        return format;
    }
}
