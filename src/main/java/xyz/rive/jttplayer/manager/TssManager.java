package xyz.rive.jttplayer.manager;

import javafx.scene.paint.Color;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.Rect;
import xyz.rive.jttplayer.common.Size;
import xyz.rive.jttplayer.skin.SkinXml;
import xyz.rive.jttplayer.skin.SkinXmlItem;
import xyz.rive.jttplayer.skin.SkinXmlWindowItem;
import xyz.rive.jttplayer.skin.StandaloneXml;

import static xyz.rive.jttplayer.skin.Constants.Item.*;
import static xyz.rive.jttplayer.util.FileUtils.*;
import static xyz.rive.jttplayer.util.FxUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class TssManager extends AbstractManager {

    public TssManager(ApplicationContext context) {
        super(context);
        setupCommonTss();
    }

    private void setupCommonTss() {
        writeCssFile(getCommonTss(), "common");
    }

    public String getTssWorkPath() {
        return getAppDataPath("Work");
    }

    public String getTssContent(String tssName) {
        return readText(getResourceAsStream(
                String.format("skin/%s.tss", tssName)
        ));
    }

    public String getPlayerWindowTss() {
        return getTssContent("player-window");
    }

    public String getPlaylistWindowTss() {
        return getTssContent("playlist-window");
    }

    public String getLyricWindowTss() {
        return getTssContent("lyric-window");
    }

    public String getEqualizerWindowTss() {
        return getTssContent("equalizer-window");
    }

    public String getPlayerMiniWindowTss() {
        return getTssContent("player-mini-window");
    }

    public String getLyricMiniWindowTss() {
        return getTssContent("lyric-mini-window");
    }

    public String getDesklrcBarTss() {
        return getTssContent("desklrc-bar");
    }

    public String getCommonTss() {
        return getTssContent("common");
    }

    public String getPlayTimeTss() {
        return getTssContent("play-time");
    }

    public String getProgressBarTss() {
        return getTssContent("progress-bar");
    }

    public String getProgressBarVerticalTss() {
        return getTssContent("progress-bar-vertical");
    }

    public String getSliderTss() {
        return getTssContent("slider");
    }

    public String getVSliderTss() {
        return getTssContent("vslider");
    }

    public Size getImageSize(SkinXml skin, String imageName) {
        return getContext().getSkinManager().getImageSize(skin, imageName);
    }

    public String getImageUrl(SkinXml skin, String name) {
        if (trim(name).startsWith("file:")) {
            return encodeUrl(name);
        }
        return encodeUrl(getContext().getSkinManager().getSknEntryUrl(skin, name));
    }

    public String getImagePath(SkinXml skin, String name) {
        return getContext().getSkinManager().getSknEntryPath(skin, name);
    }

    private String setupItemCommon(String tss, double width, double height, String prefix) {
        prefix = trimUpperCase(prefix);
        return tss.replaceAll("\\$" + prefix + "_WIDTH", width + "")
                .replaceAll("\\$" + prefix + "_HEIGHT", height + "");
    }

    private String setupItemCommon(String tss, Size size, String prefix) {
        return setupItemCommon(tss, size.width(), size.height(), prefix);
    }

    private String setupActionItem(
            String tss, String img,
            double width, double height,
            String prefix, int index) {
        prefix = trimUpperCase(prefix);
        return tss.replaceAll("\\$" + prefix + "_IMG", encodeUrl(img))
                .replaceAll("\\$" + prefix + "_WIDTH", width + "")
                .replaceAll("\\$" + prefix + "_HEIGHT", height + "")
                .replaceAll("\\$" + prefix + "_POS_0", String.format("%s %s", -1 * index * width, 0))
                .replaceAll("\\$" + prefix + "_POS_1", String.format("%s %s", -1 * width, 0))
                .replaceAll("\\$" + prefix + "_POS_2", String.format("%s %s", -2 * width, 0))
                .replaceAll("\\$" + prefix + "_VPOS_0", String.format("%s %s", -1 * index * width, -1 * height));
    }

    private String setupActionItem(
            String tss, String img,
            double width, double height,
            String prefix) {
        return setupActionItem(tss, img, width, height, prefix, 0);
    }

    private String setupActionItem(String tss, SkinXml skin, String img, int hSlices, int vSlices, String prefix) {
        Size size = getImageSize(skin, img);
        hSlices = Math.max(hSlices, 1);
        vSlices = Math.max(vSlices, 1);
        return setupActionItem(tss,
                getImageUrl(skin, img),
                size.width() / hSlices,
                size.height() / vSlices,
                prefix
        );
    }

    private String setupActionItem(String tss, SkinXml skin, String img, int slices, String prefix) {
        return setupActionItem(tss, skin, img, slices, 1, prefix);
    }

    private String setupActionItem(String tss, SkinXml skin, String img, String prefix) {
        return setupActionItem(tss, skin, img, 1, prefix);
    }

    private String setupActionItem(String tss, SkinXml skin, SkinXmlItem item, String prefix) {
        return setupActionItem(
                tss,
                getImageUrl(skin, item.icon()),
                item.width(),
                item.height(),
                prefix
        );
    }

    private String setupTextItem(String tss, SkinXml skin, SkinXmlItem item, String prefix) {
        String textColor = isEmpty(item.color) ? "#ffffff" : item.color;
        int fontSize = item.fontSize > 0 ? item.fontSize : 13;
        prefix = trimUpperCase(prefix);
        return tss.replaceAll("\\$" + prefix + "_TEXT_COLOR", textColor)
                .replaceAll("\\$" + prefix + "_FONT_SIZE",  fontSize + "");
    }

    private String writeCssFile(String tss, String cssFileName) {
        String cssDest = String.format("%s/%s.css", getTssWorkPath(), cssFileName);
        writeText(cssDest, patchEntireTss(tss));
        return encodeUrl("file:///".concat(cssDest));
    }

    private String patchEntireTss(String tss) {
        return tss.replaceAll("\\$.*_WIDTH", "0")
                .replaceAll("\\$.*_HEIGHT", "0")
                .replaceAll("\\$.*_POS_1", "0 0")
                .replaceAll("\\$.*_POS_2", "0 0")
                .replaceAll("\\$.*_POS_00", "0 0")
                .replaceAll("\\$.*_VPOS_0", "0 0")
                .replaceAll("\\$.*_POS_0", "0 0")
                .replaceAll("\\$.*_POS", "0 0")
                .replaceAll("\\$.*_BKGND", "none")
                .replaceAll("\\$.*_COLOR", "none")
                .replaceAll("url\\(\"\"\\)", "none")
                .replaceAll("url\\(\"\\$.*_IMG\"\\)", "none")
                .replaceAll("-fx-background-image: none;", "")
                .replaceAll("\\$.*_TEXT_COLOR", "none")
                .replaceAll("\\$.*_FONT_SIZE", "none")
                .replaceAll("-fx-background-color: none !important;", "")
                .replaceAll("-fx-background-color: none;", "")
                .replaceAll("-fx-border-color: ;", "")
                .replaceAll("-fx-border-color: none;", "")
                .replaceAll("-fx-text-fill: none !important;", "")
                .replaceAll("-fx-text-fill: none;", "")
                .replaceAll("-fx-font-size: none;", "")
                .replaceAll("-fx-background-color:  !important;", "");
    }

    public String boostrapProgressBar(SkinXml skin, SkinXmlItem item, boolean vertical, String cssFilename) {
        String tss = vertical ? getProgressBarVerticalTss() : getProgressBarTss();
        tss = setupActionItem(tss, skin, item.fillImage, "PROGRESS");
        tss = setupActionItem(tss, skin, item.thumbImage, 4, "THUMB");
        return writeCssFile(tss, cssFilename);
    }

    public String boostrapProgressBar(SkinXml skin, SkinXmlItem item, String cssFilename) {
        return boostrapProgressBar(skin, item, false, cssFilename);
    }

    public String boostrapProgressBar(SkinXml skin, SkinXmlItem item) {
        return boostrapProgressBar(skin, item, "progress-bar");
    }

    public String boostrapProgressBarVertical(SkinXml skin, SkinXmlItem item, String cssFilename) {
        return boostrapProgressBar(skin, item, true, cssFilename);
    }

    public String boostrapProgressBarVertical(SkinXml skin, SkinXmlItem item) {
        return boostrapProgressBarVertical(skin, item, "progress-bar-vertical");
    }

    public String boostrapSlider(SkinXml skin, SkinXmlItem item, String cssFilename) {
        String tss = getSliderTss();
        tss = setupActionItem(tss, skin, item.fillImage, "PROGRESS");
        tss = setupActionItem(tss, skin, item.thumbImage, 4, "THUMB");
        return writeCssFile(tss, cssFilename);
    }

    public String boostrapSlider(SkinXml skin, SkinXmlItem item) {
        return boostrapSlider(skin, item, "slider");
    }

    public String boostrapVSlider(SkinXml skin, SkinXmlItem item, String cssFilename) {
        String tss = getVSliderTss();
        tss = setupActionItem(tss, skin, item.fillImage, "PROGRESS");
        tss = setupActionItem(tss, skin, item.thumbImage, 4, "THUMB");
        return writeCssFile(tss, cssFilename);
    }

    public String boostrapVSlider(SkinXml skin, SkinXmlItem item) {
        return boostrapVSlider(skin, item, "vslider");
    }

    public String boostrapPlayTime(SkinXml skin, SkinXmlItem item, String cssFilename) {
        String tss = getPlayTimeTss();
        Size imgSize = getImageSize(skin, item.image);
        double digitWidth = Math.floor(imgSize.width() / 12);
        double digitHeight = imgSize.height();
        tss = setupActionItem(tss, getImageUrl(skin, item.image),
                digitWidth, digitHeight, "NUMBER", 0)
                .replaceAll("\\$NUMBER_FLAG_POS_0",
                        String.format("%s %s", -11 * digitWidth, 0))
                .replaceAll("\\$NUMBER_SP_POS_0",
                        String.format("%s %s", -10 * digitWidth, 0));
        return writeCssFile(tss, cssFilename);
    }

    public String boostrapPlayTime(SkinXml skin, SkinXmlItem item) {
        return boostrapPlayTime(skin, item, "play-time");
    }

    public String boostrapPlayerWindow(SkinXml skin, String cssFilename) {
        String tss = getPlayerWindowTss();
        SkinXmlWindowItem winItem = skin.getPlayerWindow();
        tss = tss.replaceAll("\\$BKGND", getImageUrl(skin, winItem.image))
                .replaceAll("\\$TOP_HEIGHT", "32");

        for (SkinXmlItem item : winItem.items) {
            if (item.isIconItem()) {
                tss = setupActionItem(tss, skin, item, ICON);
            } //
            else if (item.isSetItem()) {
                tss = setupActionItem(tss, skin, item, SET);
            } else if (item.isMinimizeItem()) {
                tss = setupActionItem(tss, skin, item, MINI);
            } else if (item.isMiniModeItem()) {
                tss = setupActionItem(tss, skin, item, MINIMODE);
            } else if (item.isExitItem()) {
                tss = setupActionItem(tss, skin, item, CLOSE);
            } //
            else if (item.isVisualItem()) {

            } else if (item.isInfoItem()) {

            } else if (item.isStereoItem()) {
                tss = setupTextItem(tss, skin, item, STEREO);
            } else if (item.isStatusItem()) {
                tss = setupTextItem(tss, skin, item, STATUS);
            } else if (item.isProgressItem()) {

            } else if (item.isLedItem()) {

            } //
            else if (item.isPrevItem()) {
                tss = setupActionItem(tss, skin, item, PREV);
            } else if (item.isPlayItem()) {
                tss = setupActionItem(tss, skin, item, PLAY);
            } else if (item.isPauseItem()) {
                tss = setupActionItem(tss, skin, item, PAUSE);
            } else if (item.isNextItem()) {
                tss = setupActionItem(tss, skin, item, NEXT);
            } //
            else if (item.isModeSingleItem()) {
                tss = setupActionItem(tss, skin, item, MODE_SINGLE);
            } else if (item.isModeLoopItem()) {
                tss = setupActionItem(tss, skin, item, MODE_LOOP);
            } else if (item.isModeSliderItem()) {
                tss = setupActionItem(tss, skin, item, MODE_SLIDER);
            } else if (item.isModeCircleItem()) {
                tss = setupActionItem(tss, skin, item, MODE_CIRCLE);
            } else if (item.isModeRandomItem()) {
                tss = setupActionItem(tss, skin, item, MODE_RANDOM);
            } //
            else if (item.isMuteItem()) {
                tss = setupActionItem(tss, skin, item, MUTE);
            } else if (item.isVolumeItem()) {

            } //
            else if(item.isPlaylistItem()) {
                tss = setupActionItem(tss, skin, item, PLAYLIST);
            } else if(item.isEqualizerItem()) {
                tss = setupActionItem(tss, skin, item, EQ);
            } else if(item.isLyricItem()) {
                tss = setupActionItem(tss, skin, item, LYRIC);
            } else if(item.isBrowserItem()) {
                tss = setupActionItem(tss, skin, item, BROWSER);
            } //
            else if (item.isStopItem()) {
                tss = setupActionItem(tss, skin, item, STOP);
            } else if (item.isOpenItem()) {
                tss = setupActionItem(tss, skin, item, OPEN);
            } else if (item.isToolbarItem()) {
                int itemCount = 7;
                int itemWidth = (int) (item.width() / itemCount);
                Size imgSize = getImageSize(skin, item.image);
                int itemImgWidth = (int) (imgSize.width() / itemCount);
                int itemImgHeight = (int) (imgSize.height() / itemCount);
                tss = tss.replaceAll("\\$TOOLBAR_IMG", getImageUrl(skin, item.image))
                        .replaceAll("\\$TOOLBAR_HOT_IMG", getImageUrl(skin, item.hotImage))
                        .replaceAll("\\$TOOLBAR_BAR_IMG", getImageUrl(skin, item.barImage))
                        .replaceAll("\\$TOOLBAR_WIDTH", item.width() + "")
                        .replaceAll("\\$TOOLBAR_HEIGHT", item.height() + "")
                        .replaceAll("\\$TOOLBAR_ITEM_WIDTH", itemWidth + "")
                        .replaceAll("\\$TOOLBAR_ITEM_HEIGHT", item.height() + "")
                        .replaceAll("\\$TOOLBAR_ITEM_IMG_WIDTH", itemImgWidth + "").
                        replaceAll("\\$TOOLBAR_ITEM_IMG_HEIGHT", itemImgHeight + "");
                for (int i = 0; i < itemCount; i++) {
                    tss = setupActionItem(tss,
                            "",
                            itemImgWidth,
                            itemImgHeight,
                            "TOOLBAR_ITEM_" + (i + 1),
                            i
                    );
                }
            } else if (item.isPlayMiniItem()) {
                tss = setupActionItem(tss, skin, item, PLAY_MINI);
            } else if (item.isPauseMiniItem()) {
                tss = setupActionItem(tss, skin, item, PAUSE_MINI);
            }
        }
        return writeCssFile(tss, cssFilename);
    }

    public String boostrapPlayerWindow(SkinXml skin) {
        return boostrapPlayerWindow(skin, "player-window");
    }

    public String boostrapLyricWindow(SkinXml skin) {
        String tss = getLyricWindowTss();
        SkinXmlWindowItem winItem = skin.getLyricWindow();
        Rect resizeRect = winItem.getWindowCornersRect();
        tss = tss.replaceAll("\\$BKGND", getImageUrl(skin, winItem.image))
                .replaceAll("\\$RESIZE_RECT", resizeRect.toString2())
                .replaceAll("\\$TOP_HEIGHT", resizeRect.x1() + "");

        for (SkinXmlItem item : winItem.items) {
            if (item.isTitleItem()) {
                tss = setupActionItem(tss, skin, item, TITLE);
            } else if (item.isDesklrcItem()) {
                tss = setupActionItem(tss, skin, item, DESKLRC);
            } else if (item.isOntopItem()) {
                tss = setupActionItem(tss, skin, item, ONTOP);
            } else if (item.isCloseItem()) {
                tss = setupActionItem(tss, skin, item, CLOSE);
            }
        }

        try {
            tss = setupLyricStandaloneTss(tss, getContext().getActiveLyricXml());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writeCssFile(tss, "lyric-window");
    }

    public String boostrapEqualizerWindow(SkinXml skin) {
        String tss = getEqualizerWindowTss();
        SkinXmlWindowItem winItem = skin.getEqualizerWindow();
        if (winItem == null) {
            return "";
        }
        tss = tss.replaceAll("\\$BKGND", getImageUrl(skin, winItem.image))
                .replaceAll("\\$TOP_HEIGHT", "32");

        for (SkinXmlItem item : winItem.items) {
            if (item.isEnabledItem()) {
                tss = setupActionItem(tss, skin, item, ENABLED);
            } else if (item.isResetItem()) {
                tss = setupActionItem(tss, skin, item, RESET);
            } else if (item.isProfileItem()) {
                tss = setupActionItem(tss, skin, item, PROFILE);
            } else if (item.isCloseItem()) {
                tss = setupActionItem(tss, skin, item, CLOSE);
            }
        }
        return writeCssFile(tss, "equalizer-window");
    }

    public String boostrapPlaylistWindow(SkinXml skin) {
        String tss = getPlaylistWindowTss();
        SkinXmlWindowItem winItem = skin.getPlaylistWindow();
        Rect resizeRect = winItem.getWindowCornersRect();
        tss = tss.replaceAll("\\$BKGND", getImageUrl(skin, winItem.image))
                .replaceAll("\\$RESIZE_RECT", resizeRect.toString2())
                .replaceAll("\\$TOP_HEIGHT", resizeRect.x1() + "");

        for (SkinXmlItem item : winItem.items) {
            if (item.isTitleItem()) {
                tss = setupActionItem(tss, skin, item, TITLE);
            } else if(item.isCloseItem()) {
                tss = setupActionItem(tss, skin, item, CLOSE);
            } else if (item.isScrollbarItem()) {
                String thumbImage = item.thumbImage;
                if (!isEmpty(thumbImage)) {
                    thumbImage = "scrollbar_thumb_crop1." + guessExtName(thumbImage);
                    String dest = String.format("%s/%s", getActiveSkinRoot(), thumbImage);
                    cropImage(getImagePath(skin, item.thumbImage), dest,3, 1);
                }
                tss = setupActionItem(tss, skin, item.barImage, SCROLLBAR);
                tss = setupActionItem(tss, skin, item.buttonsImage, 3, 2, "SCROLLBAR_BTN");
                tss = setupActionItem(tss, skin, thumbImage, 3, "SCROLLBAR_THUMB");
            } //
            else if (item.isToolbarItem()) {
                int itemCount = 7;
                int itemWidth = (int) (item.width() / itemCount);
                Size imgSize = getImageSize(skin, item.image);
                int itemImgWidth = (int) (imgSize.width() / itemCount);
                int itemImgHeight = (int) (imgSize.height() / itemCount);
                tss = tss.replaceAll("\\$TOOLBAR_IMG", getImageUrl(skin, item.image))
                        .replaceAll("\\$TOOLBAR_HOT_IMG", getImageUrl(skin, item.hotImage))
                        .replaceAll("\\$TOOLBAR_ITEM_WIDTH", itemWidth + "")
                        .replaceAll("\\$TOOLBAR_ITEM_HEIGHT", item.height() + "")
                        .replaceAll("\\$TOOLBAR_ITEM_IMG_WIDTH", itemImgWidth + "").
                        replaceAll("\\$TOOLBAR_ITEM_IMG_HEIGHT", itemImgHeight + "");
                for (int i = 0; i < itemCount; i++) {
                    tss = setupActionItem(tss,
                            "",
                            itemImgWidth,
                            itemImgHeight,
                            "TOOLBAR_ITEM_" + (i + 1),
                            i
                    );
                }
            }
        }

        try {
            tss = setupPlaylistStandaloneTss(tss, getContext().getActivePlaylistXml());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writeCssFile(tss, "playlist-window");
    }

    private String getActiveSkinRoot() {
        return String.format("%s/%s", getWorkPath(),
                guessSimpleName(getContext().getConfiguration()
                                .getPlayerOptions().getActiveSkinName())
        );
    }

    private String setupLyricStandaloneTss(String tss, StandaloneXml xml) {
        String textColor = isEmpty(xml.hilightColor) ? xml.textColor : xml.hilightColor;
        textColor = isEmpty(textColor) ? "#11243c" : textColor;
        String locatorColor = Color.valueOf(textColor)
                .darker().toString()
                .replace("0x", "#");
        return tss.replaceAll("\\$LOCATOR_TEXT_COLOR", locatorColor)
                .replaceAll("\\$LOCATOR_VLINE_COLOR", locatorColor)
                .replaceAll("\\$LOCATOR_LINE_COLOR", locatorColor);
    }

    private String setupPlaylistStandaloneTss(String tss, StandaloneXml xml) {
        xml.textColor = isEmpty(xml.textColor) ? "#777777" : xml.textColor;
        String dColor = Color.valueOf(xml.textColor)
                .darker().toString()
                .replace("0x", "#");
        String lColor = Color.valueOf(xml.textColor)
                .brighter().toString()
                .replace("0x", "#");
        String spBkgnd = String.format("linear-gradient(to right, %s, %s)", lColor, dColor);
        String selectedBkgnd = "#047ddb";
        String selectedTextColor = "#eeeeee";
        return tss.replaceAll("\\$TEXT_COLOR", xml.textColor)
                .replaceAll("\\$HILIGHT_COLOR", isEmpty(xml.hilightColor) ? xml.textColor : xml.hilightColor)
                .replaceAll("\\$DATA_SELECT_BKGND", selectedBkgnd)
                .replaceAll("\\$DATA_SELECT_BORDER_COLOR", xml.selectColor)
                .replaceAll("\\$DATA_SELECT_TEXT_COLOR", selectedTextColor)
                .replaceAll("\\$DATA_BKGND_2", xml.bkgndColor2)
                .replaceAll("\\$DATA_BKGND", xml.bkgndColor)
                .replaceAll("\\$SPLITTER_BKGND", spBkgnd)
                .replaceAll("\\$SPLITTER_BTN_COLOR", xml.bkgndColor)
                .replaceAll("\\$SPLITTER_BORDER_COLOR",  xml.textColor);
    }

    public String boostrapDesklrcBar(SkinXml skin) {
        String tss = getDesklrcBarTss();
        SkinXmlWindowItem barItem = skin.getLyricDesktopBar();
        if (barItem == null) {
            skin = getContext().getSkinManager().getDefaultSkinXml();
            barItem = skin.getLyricDesktopBar();
        }

        Size size = getImageSize(skin, barItem.image);
        tss = tss.replaceAll("\\$BKGND", getImageUrl(skin, barItem.image));
        tss = setupItemCommon(tss, size, DESKLRC_BAR);

        for (SkinXmlItem item : barItem.items) {
            if (item.isIconItem()) {
                tss = setupActionItem(tss, skin, item, ICON);
            } else if (item.isPrevItem()) {
                tss = setupActionItem(tss, skin, item, PREV);
            } else if (item.isPlayItem()) {
                tss = setupActionItem(tss, skin, item, PLAY);
            } else if (item.isPauseItem()) {
                tss = setupActionItem(tss, skin, item, PAUSE);
            } else if (item.isNextItem()) {
                tss = setupActionItem(tss, skin, item, NEXT);
            } else if (item.isListItem()) {
                tss = setupActionItem(tss, skin, item, LIST);
            } else if (item.isSettingsItem()) {
                tss = setupActionItem(tss, skin, item, SETTINGS);
            } else if (item.isKalaokItem()) {
                tss = setupActionItem(tss, skin, item, KALAOK);
            } else if (item.isLockItem()) {
                tss = setupActionItem(tss, skin, item, LOCK);
            } else if (item.isLinesItem()) {
                tss = setupActionItem(tss, skin, item, LINES);
            } else if (item.isReturnItem()) {
                tss = setupActionItem(tss, skin, item, RETURN);
            } else if (item.isOntopItem()) {
                tss = setupActionItem(tss, skin, item, ONTOP);
            } else if (item.isCloseItem()) {
                tss = setupActionItem(tss, skin, item, CLOSE);
            }
        }
        return writeCssFile(tss, "desklrc-bar");
    }

    public String boostrapPlayerMiniWindow(SkinXml skin) {
        String tss = getPlayerMiniWindowTss();
        SkinXmlWindowItem winItem = skin.getMiniWindow();
        if (winItem == null) {
            skin = getContext().getSkinManager().getDefaultSkinXml();
            winItem = skin.getMiniWindow();
        }

        tss = tss.replaceAll("\\$BKGND", getImageUrl(skin, winItem.image));

        for (SkinXmlItem item : winItem.items) {
            if (item.isIconItem()) {
                tss = setupActionItem(tss, skin, item, ICON);
            } else if (item.isPrevItem()) {
                tss = setupActionItem(tss, skin, item, PREV);
            } else if (item.isPlayItem()) {
                tss = setupActionItem(tss, skin, item, PLAY);
            } else if (item.isPauseItem()) {
                tss = setupActionItem(tss, skin, item, PAUSE);
            } else if (item.isNextItem()) {
                tss = setupActionItem(tss, skin, item, NEXT);
            } else if (item.isLyricItem()) {
                tss = setupActionItem(tss, skin, item, LYRIC);
            } else if (item.isMuteItem()) {
                tss = setupActionItem(tss, skin, item, MUTE);
            } else if (item.isStopItem()) {
                tss = setupActionItem(tss, skin, item, STOP);
            } else if (item.isOpenItem()) {
                tss = setupActionItem(tss, skin, item, OPEN);
            } else if (item.isMinimizeItem()) {
                tss = setupActionItem(tss, skin, item, MINI);
            } else if (item.isMiniModeItem()) {
                tss = setupActionItem(tss, skin, item, MINIMODE);
            } else if (item.isCloseItem() || item.isExitItem()) {
                tss = setupActionItem(tss, skin, item, CLOSE);
            }
        }
        return writeCssFile(tss, "player-mini-window");
    }

    public String boostrapLyricMiniWindow(SkinXml skin) {
        String tss = getLyricMiniWindowTss();
        SkinXmlWindowItem winItem = skin.getMiniWindow();
        if (winItem == null) {
            skin = getContext().getSkinManager().getDefaultSkinXml();
            winItem = skin.getMiniWindow();
        }
        Size size = getImageSize(skin, winItem.image);
        tss = tss.replaceAll("\\$BKGND_SIZE", "296 " + size.height())
                .replaceAll("\\$BKGND", getImageUrl(skin, winItem.image))
                .replaceAll("\\$DATA_HEIGHT", size.height() + "");
        StandaloneXml xml = getContext().getActiveLyricXml();
        try {
            tss = setupPlaylistStandaloneTss(tss, xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writeCssFile(tss, "lyric-mini-window");
    }
}
