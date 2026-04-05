package xyz.rive.jttplayer.common;

public class Metadata {
    //Tag
    private String title;
    private String artist;
    private String album;
    private String trackNumber;
    private String quality;
    private String rating;
    private String year;
    private String language;
    private String tags;
    private String comment;
    private String genre;
    //Headers
    private int trackLength;
    private long bitRate;
    private int sampleRate;
    private String channels;
    private String format;
    private String encodingType;

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

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getTrackLength() {
        return trackLength;
    }

    public void setTrackLength(int trackLength) {
        this.trackLength = trackLength;
    }

    public long getBitRate() {
        return bitRate;
    }

    public void setBitRate(long bitRate) {
        this.bitRate = bitRate;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", trackNumber='" + trackNumber + '\'' +
                ", quality='" + quality + '\'' +
                ", rating='" + rating + '\'' +
                ", year='" + year + '\'' +
                ", language='" + language + '\'' +
                ", tags='" + tags + '\'' +
                ", comment='" + comment + '\'' +
                ", genre='" + genre + '\'' +
                ", trackLength=" + trackLength +
                ", bitRate=" + bitRate +
                ", sampleRate=" + sampleRate +
                ", channels='" + channels + '\'' +
                ", format='" + format + '\'' +
                ", encodingType='" + encodingType + '\'' +
                '}';
    }
}
