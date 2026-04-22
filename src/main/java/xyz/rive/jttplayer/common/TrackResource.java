package xyz.rive.jttplayer.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class TrackResource {
    private String id;
    private String title;
    private String artist;
    private String album;
    private String lrc;
    private String trc;
    private double duration;
    private String source;
    private String mid;
    private String cover;
    //附加参数，解决平台参数不一致问题
    private Map<String, String> extras;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public String getTrc() {
        return trc;
    }

    public void setTrc(String trc) {
        this.trc = trc;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    public void addExtra(String key, String value) {
        if (extras == null) {
            setExtras(new HashMap<>());
        }
        extras.put(key, value);
    }

    @JsonIgnore
    public String getExtra(String key) {
        if (getExtras() == null) {
            return null;
        }
        return getExtras().get(key);
    }
}
