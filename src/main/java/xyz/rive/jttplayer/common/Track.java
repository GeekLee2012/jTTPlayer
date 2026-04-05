package xyz.rive.jttplayer.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import xyz.rive.jttplayer.util.FxUtils;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.rive.jttplayer.util.FileUtils.guessSimpleName;
import static xyz.rive.jttplayer.util.FileUtils.transformPath;
import static xyz.rive.jttplayer.util.FxUtils.isMacOS;
import static xyz.rive.jttplayer.util.StringUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.contentEqualsIgnoreCase;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Track implements Cloneable {
    private String id;
    private String queueId;
    //Tag元数据
    private String title;
    private String artist;
    private String album;
    private String quality;
    private String rating;
    private String date;
    private String language;
    private String tags;
    private String trackNumber;
    private String comment;
    private String genre;
    //文件相关
    private String url;
    private String cover;
    private double fileSize;
    private String fileName;
    private String extName;
    @JsonIgnore
    private Lyric lyric;
    @JsonIgnore
    private Lyric lyricTrans;
    @JsonIgnore
    private Lyric lyricEmbed;
    //Headers
    private double trackLength;
    private long bitRate;
    private int sampleRate;
    private String channels;
    private String format;
    private String encodingType;

    public Track() {
        setId(String.valueOf(UUID.randomUUID()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public Lyric getLyric() {
        return lyric;
    }

    public void setLyric(Lyric lyric) {
        this.lyric = lyric;
    }

    public Lyric getLyricTrans() {
        return lyricTrans;
    }

    public void setLyricTrans(Lyric lyricTrans) {
        this.lyricTrans = lyricTrans;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
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

    public double getTrackLength() {
        return trackLength;
    }

    public void setTrackLength(double trackLength) {
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtName() {
        return extName;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    @JsonIgnore
    public Lyric getLyricEmbed() {
        return lyricEmbed;
    }

    @JsonIgnore
    public void setLyricEmbed(Lyric lyricEmbed) {
        this.lyricEmbed = lyricEmbed;
    }

    /* 自定义 */


    public double getDuration() {
        return trackLength / 60D;
    }

    @JsonIgnore
    public String getTransformedSampleRate() {
        if(sampleRate <= 0) {
            return "";
        }
        double rate = sampleRate / 1000D;
        if(rate < 1) {
            return String.format("%sHz", sampleRate);
        }
        return String.format("%skHz", rate);
    }

    @JsonIgnore
    public String getTransformedBitRate() {
        if(bitRate <= 0) {
            return "";
        } else if(bitRate >= 1000D) {
           return String.format("%1$.2fM", bitRate / 1000D);
        }
        return String.format("%1$sk", bitRate);
    }

    @JsonIgnore
    public String getTransformedBitRate2() {
        if(bitRate <= 0) {
            return "";
        } else if(bitRate >= 1000D) {
            return String.format("%1$.2f Mbps", bitRate / 1000D);
        }
        return String.format("%1$s kbps", bitRate);
    }

    @JsonIgnore
    public String getTransformedTitle() {
        String title = getTitle();
        return startsWithIgnoreCase(title, "http")
                ? title.substring(0, 24).concat("...") : title;
    }

    @JsonIgnore
    public String getTransformedEncodingType() {
        if(isEmpty(encodingType)) {
            return "";
        }
        String[] parts = encodingType.trim().split(" ");
        return parts.length > 0 ? parts[0] : encodingType;
    }

    @JsonIgnore
    public String getTransformedBits() {
        if(isEmpty(encodingType)) {
            return "";
        }
        String lwEncodingType = trimLowerCase(encodingType);
        Pattern pattern = Pattern.compile("\\d+\\s?bits");
        Matcher matcher = pattern.matcher(lwEncodingType);
        if(matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    @JsonIgnore
    public String getTransformedFileSize() {
        double unit = isMacOS() ? 1000 : 1024;
        double mb = fileSize / (unit * unit);
        return String.format("%.1f MB", mb);
    }

    @JsonIgnore
    public boolean hasLyric() {
        return lyric != null && lyric.hasData();
    }

    @JsonIgnore
    public String getLyricText() {
        return hasLyric() ? lyric.toString() : "";
    }


    @JsonIgnore
    public boolean hasEmbedLyric() {
        return lyricEmbed != null && lyricEmbed.hasData();
    }

    @JsonIgnore
    public boolean hasLyricTrans() {
        return lyricTrans != null && lyricTrans.hasData();
    }

    @JsonIgnore
    public String getLyricEmbedText() {
        return hasEmbedLyric() ? lyricEmbed.toString() : "";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Track track = (Track) o;
        return contentEquals(id, track.id)
                || contentEquals(url, track.url);
    }

    public boolean isMetadataSimilar(Track t) {
        if((containsIgnoreCase(title, t.title) || containsIgnoreCase(t.title, title))
                && (contentEqualsIgnoreCase(artist, t.artist) || contentEqualsIgnoreCase(album, t.album))
                && Math.abs(trackLength - t.trackLength) <= 10
                && bitRate == t.bitRate
                && sampleRate == t.sampleRate
                && contentEqualsIgnoreCase(channels, t.channels)
        ) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url);
    }


    @JsonIgnore
    public String simplifyMetadata() {
        return String.format("标题: %s\n艺术家: %s\n专辑: %s\n格式: %s\n时长: %s\n大小: %s\n文件名: %s",
                trim(title),
                trim(artist),
                trim(album),
                String.format("%s  %s  %s",
                        trim(extName),
                        getTransformedSampleRate(),
                        getTransformedBitRate()),
                toMMss(getDuration()),
                getTransformedFileSize(),
                transformPath(url)
        );
    }

    @JsonIgnore
    public String basicMetadata() {
        if(isEmpty(artist)) {
            return trim(title);
        }
        return String.format("%s - %s", trim(title), trim(artist));
    }

    @Override
    public Track clone() {
        try {
            return (Track) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getParentUrl() {
        if (isEmpty(url)) {
            return url;
        }
        String transUrl = transformPath(url);
        int index = transUrl.lastIndexOf("/");
        if(index < 0) {
            return "/";
        }
        return transUrl.substring(0, index + 1);
    }

    @JsonIgnore
    public String getFormattedTitle(String format) {
        String title = trim(getTitle());
        if(startsWithIgnoreCase(title, "http")) {
            return getTransformedTitle();
        }

        if(contentEquals("%F", format)) {
            return trim(getFileName());
        } else if(contentEquals("%T", format)) {
            return title;
        } else if(contentEquals("%A - %T", format)) {
            String artist = trim(getArtist());
            if(isEmpty(artist)) {
                return title;
            }
            if (isEmpty(title)) {
                return trim(getFileName());
            }
            return String.format("%s - %s", artist, title);
        } else if(contentEquals("%T - %A", format)) {
            String artist = trim(getArtist());
            if(isEmpty(artist)) {
                return title;
            }
            if (isEmpty(title)) {
                return trim(getFileName());
            }
            return String.format("%s - %s", title, artist);
        }
        return getTransformedTitle();
    }

    public String getShortFileName() {
        return guessSimpleName(fileName);
    }
}
