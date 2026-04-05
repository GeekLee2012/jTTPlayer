package xyz.rive.jttplayer.skin;

import java.util.HashMap;
import java.util.Map;

import static xyz.rive.jttplayer.skin.Constants.*;

public class SkinXml {
    public String version;
    public String name;
    public String author;
    public String url;
    public String email;
    public String transparentColor;
    public Map<String, SkinXmlWindowItem> windows = new HashMap<>();
    public String filename;

    public SkinXml() {

    }

    public SkinXml(String version, String name,
                   String author, String url,
                   String email, String transparentColor) {
        this.name = name;
        this.version = version;
        this.author = author;
        this.url = url;
        this.email = email;
        this.transparentColor = transparentColor;
    }

    public void addWindowItem(String name, SkinXmlWindowItem window) {
        windows.put(name, window);
    }

    private SkinXmlWindowItem getWindow(String name) {
        return windows.get(name);
    }

    public SkinXmlWindowItem getPlayerWindow() {
        return getWindow(PLAYER_WINDOW);
    }

    public SkinXmlWindowItem getPlaylistWindow() {
        return getWindow(PLAYLIST_WINDOW);
    }

    public SkinXmlWindowItem getEqualizerWindow() {
        return getWindow(EQUALIZER_WINDOW);
    }

    public SkinXmlWindowItem getLyricWindow() {
        return getWindow(LYRIC_WINDOW);
    }

    public SkinXmlWindowItem getMiniWindow() {
        return getWindow(MINI_WINDOW);
    }

    public SkinXmlWindowItem getLyricDesktopBar() {
        return getWindow(DESKLRC_BAR);
    }

}
