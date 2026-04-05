package xyz.rive.jttplayer.common;

import static xyz.rive.jttplayer.util.StringUtils.contentEquals;

public class FileSearchOptions {
    private String title;
    private String artist;
    private String album;
    private boolean ignoreCase;
    private boolean matchWholeWords;
    private boolean directionReversed;

    public FileSearchOptions() {
    }

    public FileSearchOptions(String title, String artist, String album) {
        this.title = title;
        this.artist = artist;
        this.album = album;
    }

    public FileSearchOptions(
            String title, String artist, String album,
            boolean ignoreCase, boolean matchWholeWords, boolean directionReversed
    ) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.ignoreCase = ignoreCase;
        this.matchWholeWords = matchWholeWords;
        this.directionReversed = directionReversed;
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

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public boolean isMatchWholeWords() {
        return matchWholeWords;
    }

    public void setMatchWholeWords(boolean matchWholeWords) {
        this.matchWholeWords = matchWholeWords;
    }

    public boolean isDirectionReversed() {
        return directionReversed;
    }

    public void setDirectionReversed(boolean directionReversed) {
        this.directionReversed = directionReversed;
    }

    public boolean equals(FileSearchOptions options) {
        if(options == null) {
            return false;
        }
        return contentEquals(title, options.title, false)
                && contentEquals(artist, options.artist, false)
                && contentEquals(album, options.album, false)
                && (ignoreCase == options.ignoreCase)
                && (matchWholeWords == options.matchWholeWords)
                && (directionReversed == options.directionReversed);
    }
}
