package xyz.rive.jttplayer.service;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.*;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.StandardArtwork;
import xyz.rive.jttplayer.common.Metadata;
import xyz.rive.jttplayer.common.Track;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.jaudiotagger.tag.FieldKey.*;
import static xyz.rive.jttplayer.util.FileUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.*;


public class MetadataService {

    public <T> T read(File file, BiFunction<AudioHeader, Tag, T> handle) {
        try {
            if(file == null || !file.exists() || !file.isFile()) {
                return null;
            }
            AudioFile audioFile = AudioFileIO.read(file);
            if(audioFile != null) {
                Tag tag = audioFile.getTag();
                AudioHeader audioHeader = audioFile.getAudioHeader();

                if(handle != null) {
                    return handle.apply(audioHeader, tag);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T read(String url, BiFunction<AudioHeader, Tag, T> handle) {
        url = trim(url);
        if(isEmpty(url) || url.startsWith("http")) {
            return null;
        }
        return read(new File(transformPath(url)), handle);
    }

    public void read(File file, Consumer<AudioFile> handle) {
        try {
            if(file == null || !file.exists() || !file.isFile()) {
                return ;
            }
            AudioFile audioFile = AudioFileIO.read(file);
            Optional.ofNullable(handle).ifPresent(__ -> handle.accept(audioFile));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void read(String url, Consumer<AudioFile> handle) {
        url = trim(url);
        if(isEmpty(url) || url.startsWith("http")) {
            return ;
        }
        read(new File(url), handle);
    }

    public String getFirstField(Tag tag, FieldKey id) {
        List<TagField> fields = tag.getFields(id);
        if(fields == null) {
            return null;
        }
        for (TagField field : fields) {
            if(field == null) {
                continue;
            }
            try {
                byte[] raw = field.getRawContent();
                String value = new String(raw, "UTF-8");
                if(!isEmpty(value)) {
                    return value;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Metadata read(File file) {
        return read(file, (audioHeader, tag) -> {
            Metadata metadata = null;
            try {
                if(tag != null) {
                    metadata = new Metadata();
                    metadata.setTitle(tag.getFirst(TITLE));
                    metadata.setArtist(tag.getFirst(ARTIST));
                    metadata.setAlbum(tag.getFirst(ALBUM));
                    metadata.setRating(tag.getFirst(RATING));
                    metadata.setYear(tag.getFirst(YEAR));
                    metadata.setLanguage(tag.getFirst(LANGUAGE));
                    metadata.setQuality(tag.getFirst(QUALITY));
                    metadata.setTags(tag.getFirst(TAGS));
                    metadata.setTrackNumber(tag.getFirst(TRACK));
                    metadata.setComment(tag.getFirst(COMMENT));
                    metadata.setGenre(tag.getFirst(GENRE));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if(audioHeader != null) {
                    if(metadata == null) {
                        metadata = new Metadata();
                    }
                    metadata.setTrackLength(audioHeader.getTrackLength());
                    metadata.setBitRate(audioHeader.getBitRateAsNumber());
                    metadata.setSampleRate(audioHeader.getSampleRateAsNumber());
                    metadata.setChannels(audioHeader.getChannels());
                    metadata.setFormat(audioHeader.getFormat());
                    metadata.setEncodingType(audioHeader.getEncodingType());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return metadata;
        });
    }

    public String readLyric(String url) {
        return read(url, (audioHeader, tag) -> {
            if(tag != null) {
                return tag.getFirst(LYRICS);
            }
            return null;
        });
    }

    public byte[] readCover(String url) {
        return read(url, (audioHeader, tag) -> {
            if(tag != null) {
                Artwork artwork = tag.getFirstArtwork();
                return artwork != null ?
                        artwork.getBinaryData() : null;
            }
            return null;
        });
    }

    private Tag syncTag(Tag source, Tag dest) {
        if(source == null || dest == null) {
            return null;
        }
        Iterator<TagField> iter = source.getFields();
        while (iter.hasNext()) {
            TagField field = iter.next();
            try {
                dest.setField(field);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dest;
    }

    private <T> void writeFields(String url, BiConsumer<Tag, T> handle, T data) {
        if (handle == null) {
            return ;
        }
        read(url, audioFile -> {
            try {
                Tag tag = null;
                if(audioFile != null) {
                    tag = audioFile.getTag();
                    //audioHeader = audioFile.getAudioHeader();
                }
                if(tag != null) {
                    //移除ID3V1，避免中文乱码
                    if(tag instanceof ID3v1Tag) {
                        audioFile.setTag(syncTag(tag, new ID3v24Tag()));
                        tag = audioFile.getTag();
                    }
                    handle.accept(tag, data);
                    AudioFileIO.write(audioFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void writeFields(Track track, BiConsumer<Tag, Track> handle) {
        if(track == null) {
            return ;
        }
        writeFields(track.getUrl(), handle, track);
    }

    private void toggleSetField(Tag tag, FieldKey key, String value) {
        try {
            if(isEmpty(value)) {
                tag.deleteField(key);
            } else {
                tag.setField(key, trim(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleArtwork(Tag tag, File cover) {
        try {
            if(cover == null || !cover.exists() || !cover.isFile() ) {
                tag.deleteArtworkField();
            } else {
                Artwork artwork = tag.getFirstArtwork();
                if(artwork == null) {
                    artwork = new StandardArtwork();
                }
                artwork.setFromFile(cover);
                tag.setField(artwork);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFields(Track track) {
        writeFields(track, (tag, __) -> {
            toggleSetField(tag, TITLE, track.getTitle());
            toggleSetField(tag, ARTIST, track.getArtist());
            toggleSetField(tag, ALBUM, track.getAlbum());
            toggleSetField(tag, YEAR, track.getDate());
            toggleSetField(tag, GENRE, track.getGenre());
            toggleSetField(tag, TRACK, track.getTrackNumber());
            toggleSetField(tag, RATING, track.getRating());
            toggleSetField(tag, COMMENT, track.getComment());
            toggleSetField(tag, LYRICS, track.getLyricEmbedText());
        });
    }

    public void writeCover(String url, File cover) {
        writeFields(url, (tag, __) -> toggleArtwork(tag, cover), cover);
    }

    public void writeField(String url, FieldKey key, String value) {
        writeFields(url, (tag, __) -> toggleSetField(tag, key, value), value);
    }

    public void writeLyric(String url, String lyric) {
        writeField(url, LYRICS, lyric);
    }

    public void writeField(Track track, FieldKey key, String value) {
        writeFields(track, (tag, __) -> toggleSetField(tag, key, value));
    }

    public void writeRating(Track track, String rating) {
        writeField(track, RATING, rating);
    }

}
