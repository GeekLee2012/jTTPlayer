package xyz.rive.jttplayer.skin;

import xyz.rive.jttplayer.common.Rect;
import xyz.rive.jttplayer.common.Size;

import static xyz.rive.jttplayer.skin.Constants.Item.*;
import static xyz.rive.jttplayer.skin.Constants.Item.TITLE;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

public class PositionBasedItem {
    public String name;
    public String image;
    //position: left, top, right, bottom
    public int x1, y1, x2, y2;
    public Size size;

    public void setPosition(String[] pos) {
        if (pos != null && pos.length == 4) {
            setPosition(Integer.parseInt(pos[0]),
                    Integer.parseInt(pos[1]),
                    Integer.parseInt(pos[2]),
                    Integer.parseInt(pos[3]));
        }
    }

    public void setPosition(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Rect getPositionRect() {
        return new Rect(x1, y1, x2, y2);
    }

    public int width() {
        return x2 - x1;
    }

    public int height() {
        return y2 - y1;
    }

    public Size size() {
        if (size == null) {
            size = new Size(width(), height());
        }
        return size;
    }

    public boolean isItem(String name) {
        return this != null && !isEmpty(name)
                && this.name.contentEquals(name);
    }

    public boolean isPlayItem() {
        return isItem(PLAY);
    }

    public boolean isPauseItem() {
        return isItem(PAUSE);
    }

    public boolean isPrevItem() {
        return isItem(PREV);
    }

    public boolean isNextItem() {
        return isItem(NEXT);
    }

    public boolean isSetItem() {
        return isItem(SET);
    }

    public boolean isStopItem() {
        return isItem(STOP);
    }

    public boolean isOpenItem() {
        return isItem(OPEN);
    }

    public boolean isModeSingleItem() {
        return isItem(MODE_SINGLE);
    }

    public boolean isModeLoopItem() {
        return isItem(MODE_LOOP);
    }

    public boolean isModeSliderItem() {
        return isItem(MODE_SLIDER);
    }

    public boolean isModeCircleItem() {
        return isItem(MODE_CIRCLE);
    }

    public boolean isModeRandomItem() {
        return isItem(MODE_RANDOM);
    }

    public boolean isMuteItem() {
        return isItem(MUTE);
    }

    public boolean isLyricItem() {
        return isItem(LYRIC);
    }

    public boolean isEqualizerItem() {
        return isItem(EQUALIZER);
    }

    public boolean isPlaylistItem() {
        return isItem(PLAYLIST);
    }

    public boolean isBrowserItem() {
        return isItem(BROWSER);
    }

    public boolean isMinimizeItem() {
        return isItem(MINIMIZE);
    }

    public boolean isMiniModeItem() {
        return isItem(MINIMODE);
    }

    public boolean isExitItem() {
        return isItem(EXIT);
    }

    public boolean isProgressItem() {
        return isItem(PROGRESS);
    }

    public boolean isVolumeItem() {
        return isItem(VOLUME);
    }

    public boolean isIconItem() {
        return isItem(ICON);
    }

    public boolean isInfoItem() {
        return isItem(INFO);
    }

    public boolean isLedItem() {
        return isItem(LED);
    }

    public boolean isVisualItem() {
        return isItem(VISUAL);
    }

    public boolean isCloseItem() {
        return isItem(CLOSE);
    }

    public boolean isOntopItem() {
        return isItem(ONTOP);
    }

    public boolean isDesklrcItem() {
        return isItem(DESKLRC);
    }

    public boolean isEnabledItem() {
        return isItem(ENABLED);
    }

    public boolean isResetItem() {
        return isItem(RESET);
    }

    public boolean isProfileItem() {
        return isItem(PROFILE);
    }

    public boolean isBalanceItem() {
        return isItem(BALANCE);
    }

    public boolean isSurroundItem() {
        return isItem(SURROUND);
    }

    public boolean isPreampItem() {
        return isItem(PREAMP);
    }

    public boolean isEqfactorItem() {
        return isItem(EQFACTOR);
    }

    public boolean isToolbarItem() {
        return isItem(TOOLBAR);
    }

    public boolean isScrollbarItem() {
        return isItem(SCROLLBAR);
    }

    public boolean isStaticTipItem() {
        return isItem(STATIC_TIP);
    }

    public boolean isListItem() {
        return isItem(LIST);
    }

    public boolean isSettingsItem() {
        return isItem(SETTINGS);
    }

    public boolean isKalaokItem() {
        return isItem(KALAOK);
    }

    public boolean isLinesItem() {
        return isItem(LINES);
    }

    public boolean isLockItem() {
        return isItem(LOCK);
    }

    public boolean isReturnItem() {
        return isItem(RETURN);
    }

    public boolean isTitleItem() {
        return isItem(TITLE);
    }

    public boolean isStereoItem() {
        return isItem(STEREO);
    }

    public boolean isStatusItem() {
        return isItem(STATUS);
    }

    @Override
    public String toString() {
        return String.format("%s [%d,%d-%d,%d] image=%s", name, x1, y1, x2, y2, image);
    }

}
