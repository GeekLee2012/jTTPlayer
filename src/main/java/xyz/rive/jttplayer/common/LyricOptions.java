package xyz.rive.jttplayer.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LyricOptions {
    //窗口模式
    private int winModeScrollMode;
    private int winModeAlignment;
    private int winModeFadeInFadeOutType;
    private int winModeLineSpacing;
    private boolean winModeFadeInFadeOutCurrentLine;
    private boolean winModeKalaOkCurrentLine;
    private boolean winModeTransparentUseBackground;
    private boolean winModeTransparentWithSkin;
    private boolean winModeAutoAdjustWindowSize;
    private boolean winModeOnlyAdjustVerticalScrollMode;
    private String winModeNormalColor = "#5277a8";
    private String winModeHilightColor = "#11243c";
    private String winModeBackgroundColor = "#e6f2ff";
    private String winModeFontFamily;
    private int winModeFontSize = 14;
    private int winModeHilightFontSize = 16;
    private String winModeFontWeight;
    //桌面模式
    private int deskModeLyricLines;
    private int deskModeAlignment;
    private boolean deskModeKalaOkCurrentLine;
    private boolean deskModeAutoAdjustWindowSize;
    private String deskModeFontFamily;
    private int deskModeFontSize = 36;
    private String deskModeFontWeight;
    private boolean deskModeFontBorder;
    private boolean deskModeAutoUnlock = true;
    private boolean deskModeFontShadow = true;
    private boolean deskModeFontSmoothEffect;
    private String deskModeBorderColor;
    private String deskModePlayedColor;
    private String deskModeUnplayColor;
    private boolean deskModeUseBackgroundColor;
    private String deskModeBackgroundColor;
    private int deskModeFontOpacity;
    private int deskModeBackgroundOpacity;
    private String deskModeTextGradientStyle = "qqjtwy";
    //选项
    private boolean autoLoadLyricOnPlaying;
    private boolean trimSpacesOnLoading;
    private boolean autoSetEmbedLyricForAudioFile;
    private boolean neverLoadEmbedLyric;
    private boolean autoShowStageByLyric;
    private boolean positionByLeftButtonDragging;
    private boolean saveMemorySizeByCompressingLine;
    private int whenToSaveModifiedLyric;

    public int getWinModeScrollMode() {
        return winModeScrollMode;
    }

    public void setWinModeScrollMode(int winModeScrollMode) {
        this.winModeScrollMode = winModeScrollMode;
    }

    public int getWinModeAlignment() {
        return winModeAlignment;
    }

    public void setWinModeAlignment(int winModeAlignment) {
        this.winModeAlignment = winModeAlignment;
    }

    public int getWinModeFadeInFadeOutType() {
        return winModeFadeInFadeOutType;
    }

    public void setWinModeFadeInFadeOutType(int winModeFadeInFadeOutType) {
        this.winModeFadeInFadeOutType = winModeFadeInFadeOutType;
    }

    public int getWinModeLineSpacing() {
        return winModeLineSpacing;
    }

    public void setWinModeLineSpacing(int winModeLineSpacing) {
        this.winModeLineSpacing = winModeLineSpacing;
    }

    public boolean isWinModeFadeInFadeOutCurrentLine() {
        return winModeFadeInFadeOutCurrentLine;
    }

    public void setWinModeFadeInFadeOutCurrentLine(boolean winModeFadeInFadeOutCurrentLine) {
        this.winModeFadeInFadeOutCurrentLine = winModeFadeInFadeOutCurrentLine;
    }

    public boolean isWinModeKalaOkCurrentLine() {
        return winModeKalaOkCurrentLine;
    }

    public void setWinModeKalaOkCurrentLine(boolean winModeKalaOkCurrentLine) {
        this.winModeKalaOkCurrentLine = winModeKalaOkCurrentLine;
    }

    public boolean isWinModeTransparentUseBackground() {
        return winModeTransparentUseBackground;
    }

    public void setWinModeTransparentUseBackground(boolean winModeTransparentUseBackground) {
        this.winModeTransparentUseBackground = winModeTransparentUseBackground;
    }

    public boolean isWinModeTransparentWithSkin() {
        return winModeTransparentWithSkin;
    }

    public void setWinModeTransparentWithSkin(boolean winModeTransparentWithSkin) {
        this.winModeTransparentWithSkin = winModeTransparentWithSkin;
    }

    public boolean isWinModeAutoAdjustWindowSize() {
        return winModeAutoAdjustWindowSize;
    }

    public void setWinModeAutoAdjustWindowSize(boolean winModeAutoAdjustWindowSize) {
        this.winModeAutoAdjustWindowSize = winModeAutoAdjustWindowSize;
    }

    public boolean isWinModeOnlyAdjustVerticalScrollMode() {
        return winModeOnlyAdjustVerticalScrollMode;
    }

    public void setWinModeOnlyAdjustVerticalScrollMode(boolean winModeOnlyAdjustVerticalScrollMode) {
        this.winModeOnlyAdjustVerticalScrollMode = winModeOnlyAdjustVerticalScrollMode;
    }

    public String getWinModeNormalColor() {
        return winModeNormalColor;
    }

    public void setWinModeNormalColor(String winModeNormalColor) {
        this.winModeNormalColor = winModeNormalColor;
    }

    public String getWinModeHilightColor() {
        return winModeHilightColor;
    }

    public void setWinModeHilightColor(String winModeHilightColor) {
        this.winModeHilightColor = winModeHilightColor;
    }

    public String getWinModeBackgroundColor() {
        return winModeBackgroundColor;
    }

    public void setWinModeBackgroundColor(String winModeBackgroundColor) {
        this.winModeBackgroundColor = winModeBackgroundColor;
    }

    public String getWinModeFontFamily() {
        return winModeFontFamily;
    }

    public void setWinModeFontFamily(String winModeFontFamily) {
        this.winModeFontFamily = winModeFontFamily;
    }

    public int getWinModeFontSize() {
        return winModeFontSize;
    }

    public void setWinModeFontSize(int winModeFontSize) {
        this.winModeFontSize = winModeFontSize;
    }

    public int getWinModeHilightFontSize() {
        return winModeHilightFontSize;
    }

    public void setWinModeHilightFontSize(int winModeHilightFontSize) {
        this.winModeHilightFontSize = winModeHilightFontSize;
    }

    public String getWinModeFontWeight() {
        return winModeFontWeight;
    }

    public void setWinModeFontWeight(String winModeFontWeight) {
        this.winModeFontWeight = winModeFontWeight;
    }

    public int getDeskModeLyricLines() {
        return deskModeLyricLines;
    }

    public void setDeskModeLyricLines(int deskModeLyricLines) {
        this.deskModeLyricLines = deskModeLyricLines;
    }

    public int getDeskModeAlignment() {
        return deskModeAlignment;
    }

    public void setDeskModeAlignment(int deskModeAlignment) {
        this.deskModeAlignment = deskModeAlignment;
    }

    public boolean isDeskModeKalaOkCurrentLine() {
        return deskModeKalaOkCurrentLine;
    }

    public void setDeskModeKalaOkCurrentLine(boolean deskModeKalaOkCurrentLine) {
        this.deskModeKalaOkCurrentLine = deskModeKalaOkCurrentLine;
    }

    public boolean isDeskModeAutoAdjustWindowSize() {
        return deskModeAutoAdjustWindowSize;
    }

    public void setDeskModeAutoAdjustWindowSize(boolean deskModeAutoAdjustWindowSize) {
        this.deskModeAutoAdjustWindowSize = deskModeAutoAdjustWindowSize;
    }

    public String getDeskModeFontFamily() {
        return deskModeFontFamily;
    }

    public void setDeskModeFontFamily(String deskModeFontFamily) {
        this.deskModeFontFamily = deskModeFontFamily;
    }

    public int getDeskModeFontSize() {
        return deskModeFontSize;
    }

    public void setDeskModeFontSize(int deskModeFontSize) {
        this.deskModeFontSize = deskModeFontSize;
    }

    public String getDeskModeFontWeight() {
        return deskModeFontWeight;
    }

    public void setDeskModeFontWeight(String deskModeFontWeight) {
        this.deskModeFontWeight = deskModeFontWeight;
    }

    public boolean isDeskModeFontBorder() {
        return deskModeFontBorder;
    }

    public void setDeskModeFontBorder(boolean deskModeFontBorder) {
        this.deskModeFontBorder = deskModeFontBorder;
    }

    public boolean isDeskModeAutoUnlock() {
        return deskModeAutoUnlock;
    }

    public void setDeskModeAutoUnlock(boolean deskModeAutoUnlock) {
        this.deskModeAutoUnlock = deskModeAutoUnlock;
    }

    public boolean isDeskModeFontShadow() {
        return deskModeFontShadow;
    }

    public void setDeskModeFontShadow(boolean deskModeFontShadow) {
        this.deskModeFontShadow = deskModeFontShadow;
    }

    public boolean isDeskModeFontSmoothEffect() {
        return deskModeFontSmoothEffect;
    }

    public void setDeskModeFontSmoothEffect(boolean deskModeFontSmoothEffect) {
        this.deskModeFontSmoothEffect = deskModeFontSmoothEffect;
    }

    public String getDeskModeBorderColor() {
        return deskModeBorderColor;
    }

    public void setDeskModeBorderColor(String deskModeBorderColor) {
        this.deskModeBorderColor = deskModeBorderColor;
    }

    public String getDeskModePlayedColor() {
        return deskModePlayedColor;
    }

    public void setDeskModePlayedColor(String deskModePlayedColor) {
        this.deskModePlayedColor = deskModePlayedColor;
    }

    public String getDeskModeUnplayColor() {
        return deskModeUnplayColor;
    }

    public void setDeskModeUnplayColor(String deskModeUnplayColor) {
        this.deskModeUnplayColor = deskModeUnplayColor;
    }

    public boolean isDeskModeUseBackgroundColor() {
        return deskModeUseBackgroundColor;
    }

    public void setDeskModeUseBackgroundColor(boolean deskModeUseBackgroundColor) {
        this.deskModeUseBackgroundColor = deskModeUseBackgroundColor;
    }

    public String getDeskModeBackgroundColor() {
        return deskModeBackgroundColor;
    }

    public void setDeskModeBackgroundColor(String deskModeBackgroundColor) {
        this.deskModeBackgroundColor = deskModeBackgroundColor;
    }

    public int getDeskModeFontOpacity() {
        return deskModeFontOpacity;
    }

    public void setDeskModeFontOpacity(int deskModeFontOpacity) {
        this.deskModeFontOpacity = deskModeFontOpacity;
    }

    public int getDeskModeBackgroundOpacity() {
        return deskModeBackgroundOpacity;
    }

    public void setDeskModeBackgroundOpacity(int deskModeBackgroundOpacity) {
        this.deskModeBackgroundOpacity = deskModeBackgroundOpacity;
    }

    public String getDeskModeTextGradientStyle() {
        return deskModeTextGradientStyle;
    }

    public void setDeskModeTextGradientStyle(String deskModeTextGradientStyle) {
        this.deskModeTextGradientStyle = deskModeTextGradientStyle;
    }

    public boolean isAutoLoadLyricOnPlaying() {
        return autoLoadLyricOnPlaying;
    }

    public void setAutoLoadLyricOnPlaying(boolean autoLoadLyricOnPlaying) {
        this.autoLoadLyricOnPlaying = autoLoadLyricOnPlaying;
    }

    public boolean isTrimSpacesOnLoading() {
        return trimSpacesOnLoading;
    }

    public void setTrimSpacesOnLoading(boolean trimSpacesOnLoading) {
        this.trimSpacesOnLoading = trimSpacesOnLoading;
    }

    public boolean isAutoSetEmbedLyricForAudioFile() {
        return autoSetEmbedLyricForAudioFile;
    }

    public void setAutoSetEmbedLyricForAudioFile(boolean autoSetEmbedLyricForAudioFile) {
        this.autoSetEmbedLyricForAudioFile = autoSetEmbedLyricForAudioFile;
    }

    public boolean isNeverLoadEmbedLyric() {
        return neverLoadEmbedLyric;
    }

    public void setNeverLoadEmbedLyric(boolean neverLoadEmbedLyric) {
        this.neverLoadEmbedLyric = neverLoadEmbedLyric;
    }

    public boolean isAutoShowStageByLyric() {
        return autoShowStageByLyric;
    }

    public void setAutoShowStageByLyric(boolean autoShowStageByLyric) {
        this.autoShowStageByLyric = autoShowStageByLyric;
    }

    public boolean isPositionByLeftButtonDragging() {
        return positionByLeftButtonDragging;
    }

    public void setPositionByLeftButtonDragging(boolean positionByLeftButtonDragging) {
        this.positionByLeftButtonDragging = positionByLeftButtonDragging;
    }

    public boolean isSaveMemorySizeByCompressingLine() {
        return saveMemorySizeByCompressingLine;
    }

    public void setSaveMemorySizeByCompressingLine(boolean saveMemorySizeByCompressingLine) {
        this.saveMemorySizeByCompressingLine = saveMemorySizeByCompressingLine;
    }

    public int getWhenToSaveModifiedLyric() {
        return whenToSaveModifiedLyric;
    }

    public void setWhenToSaveModifiedLyric(int whenToSaveModifiedLyric) {
        this.whenToSaveModifiedLyric = whenToSaveModifiedLyric;
    }
}
