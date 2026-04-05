package xyz.rive.jttplayer.skin;

import xyz.rive.jttplayer.common.Rect;

import java.util.ArrayList;
import java.util.List;

import static xyz.rive.jttplayer.skin.Constants.Item.*;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

public class SkinXmlWindowItem extends PositionBasedItem {
    //resize_rect: left, top, right, bottom
    public int rrx1, rry1, rrx2, rry2;
    public int eq_interval;
    public List<SkinXmlItem> items = new ArrayList<>();

    public SkinXmlWindowItem(String name) {
        this.name = name;
    }

    public SkinXmlWindowItem(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public void setResizeRect(String[] pos) {
        if (pos != null && pos.length == 4) {
            setResizeRect(Integer.parseInt(pos[0]),
                    Integer.parseInt(pos[1]),
                    Integer.parseInt(pos[2]),
                    Integer.parseInt(pos[3]));
        }
    }

    public void setResizeRect(int x1, int y1, int x2, int y2) {
        rrx1 = x1;
        rry1 = y1;
        rrx2 = x2;
        rry2 = y2;
    }

    public Rect getResizeRect() {
        return new Rect(rrx1, rry1, rrx2, rry2);
    }

    public Rect getWindowCornersRect() {
        return new Rect(rry1, width() - rrx2, height() - rry2, rrx1);
    }

    public void addItem(SkinXmlItem item) {
        items.add(item);
    }

    public boolean resizable() {
        return rrx1 > 0 && rry1 > 0 && rrx2 > 0 && rry2 > 0;
    }

    public SkinXmlItem getItem(String name) {
        for (SkinXmlItem elem : items) {
            if (elem.name.contentEquals(name)) {
                return elem;
            }
        }
        return null;
    }

    public SkinXmlItem getPlayItem(){
        return getItem(PLAY);
    }

    public SkinXmlItem getPauseItem(){
        return getItem(PAUSE);
    }

    public SkinXmlItem getPrevItem(){
        return getItem(PREV);
    }

    public SkinXmlItem getNextItem(){
        return getItem(NEXT);
    }

    public SkinXmlItem getSetItem(){
        return getItem(SET);
    }

    public SkinXmlItem getMuteItem(){
        return getItem(MUTE);
    }

    public SkinXmlItem getLyricItem(){
        return getItem(LYRIC);
    }

    public SkinXmlItem getEqualizerItem(){
        return getItem(EQUALIZER);
    }

    public SkinXmlItem getPlaylistItem(){
        return getItem(PLAYLIST);
    }

    public SkinXmlItem getBrowserItem(){
        return getItem(BROWSER);
    }

    public SkinXmlItem getMinimizeItem(){
        return getItem(MINIMIZE);
    }

    public SkinXmlItem getMiniModeItem(){
        return getItem(MINIMODE);
    }

    public SkinXmlItem getExitItem(){
        return getItem(EXIT);
    }

    public SkinXmlItem getProgressItem(){
        return getItem(PROGRESS);
    }

    public SkinXmlItem getVolumeItem(){
        return getItem(VOLUME);
    }

    public SkinXmlItem getIconItem(){
        return getItem(ICON);
    }

    public SkinXmlItem getInfoItem(){
        return getItem(INFO);
    }

    public SkinXmlItem getLedItem(){
        return getItem(LED);
    }

    public SkinXmlItem getVisualItem(){
        return getItem(VISUAL);
    }

    public SkinXmlItem getStereoItem(){
        return getItem(STEREO);
    }

    public SkinXmlItem getStatusItem(){
        return getItem(STATUS);
    }

    public SkinXmlItem getCloseItem(){
        return getItem(CLOSE);
    }

    public SkinXmlItem getOntopItem(){
        return getItem(ONTOP);
    }

    public SkinXmlItem getDesklrcItem(){
        return getItem(DESKLRC);
    }

    public SkinXmlItem getEnabledItem(){
        return getItem(ENABLED);
    }

    public SkinXmlItem getResetItem(){
        return getItem(RESET);
    }

    public SkinXmlItem getProfileItem(){
        return getItem(PROFILE);
    }

    public SkinXmlItem getBalanceItem(){
        return getItem(BALANCE);
    }

    public SkinXmlItem getSurroundItem(){
        return getItem(SURROUND);
    }

    public SkinXmlItem getPreampItem(){
        return getItem(PREAMP);
    }

    public SkinXmlItem getEqfactorItem(){
        return getItem(EQFACTOR);
    }

    public SkinXmlItem getToolbarItem(){
        return getItem(TOOLBAR);
    }

    public SkinXmlItem getScrollbarItem(){
        return getItem(SCROLLBAR);
    }

    public SkinXmlItem getStaticTipItem(){
        return getItem(STATIC_TIP);
    }

    public SkinXmlItem getListItem(){
        return getItem(LIST);
    }

    public SkinXmlItem getSettingsItem(){
        return getItem(SETTINGS);
    }

    public SkinXmlItem getKalaokItem(){
        return getItem(KALAOK);
    }

    public SkinXmlItem getLinesItem(){
        return getItem(LINES);
    }

    public SkinXmlItem getLockItem(){
        return getItem(LOCK);
    }

    public SkinXmlItem getReturnItem(){
        return getItem(RETURN);
    }

}
