package xyz.rive.jttplayer.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import xyz.rive.jttplayer.ApplicationContext;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static xyz.rive.jttplayer.util.StringUtils.compareToIgnoreCase;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaybackQueue {
    private String id;
    private String name;
    private List<Track> data;
    private long created = -1;
    private long updated = -1;
    private final IntegerProperty sizeProperty = new SimpleIntegerProperty(0);


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "默认" : name;
    }

    public void setName(String name) {
        this.name = name;
        updateTime();
    }

    @JsonIgnore
    private void updateTime() {
        long now = System.currentTimeMillis();
        if(created < 0) {
            setCreated(now);
        }
        setUpdated(now);
    }

    public List<Track> getData() {
        if(data == null) {
            data = new CopyOnWriteArrayList<>();
        }
        return data;
    }

    public void setData(List<Track> data) {
        if(data != null && !data.isEmpty()) {
            data.forEach(track -> track.setQueueId(id));
        }
        getData().clear();
        getData().addAll(data);
        sizeProperty.set(size());

        updateTime();
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    @JsonIgnore
    public void addAll(Collection<Track> tracks) {
        if(tracks == null || tracks.isEmpty()) {
            return ;
        }

        tracks.forEach(track -> track.setQueueId(id));

        getData().addAll(tracks);
        sizeProperty.set(size());

        long now = System.currentTimeMillis();
        if(created < 0) {
            setCreated(now);
        }
        setUpdated(now);
    }

    @JsonIgnore
    public void addAll(Track... tracks) {
        if(tracks == null || tracks.length < 1) {
            return ;
        }
        addAll(Arrays.asList(tracks));
    }

    @JsonIgnore
    public int size() {
        return getData().size();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return size() < 1;
    }

    @JsonIgnore
    public Track getTrack(int index) {
        if(index < 0 || index >= getData().size()) {
            return null;
        }
        return getData().get(index);
    }

    @JsonIgnore
    public boolean remove(Track track) {
        if (size() < 1) {
            return false;
        }
        boolean success = getData().remove(track);
        if(success) {
            sizeProperty.set(size());
            setUpdated(System.currentTimeMillis());
        }
        return success;
    }

    @JsonIgnore
    public IntegerProperty sizeProperty() {
        return sizeProperty;
    }

    @JsonIgnore
    public int indexOf(Track track) {
        if(size() < 1) {
            return -1;
        }
        if(!track.getQueueId().equals(id)) {
            return -1;
        }
        return getData().indexOf(track);
    }

    @JsonIgnore
    public void sort(SortBy sortBy) {
        if(isEmpty()) {
            return ;
        }
        if(sortBy == null) {
            sortBy = SortBy.None;
        }
        if (SortBy.Random == sortBy) {
            Collections.shuffle(getData());
            return ;
        }
        Comparator<Track> comparator = null;
        switch (sortBy) {
            case None:
                break;
            case Title:
                String format = ApplicationContext.getInstance()
                        .getConfiguration()
                        .getPlaybackQueueOptions()
                        .getTitleFormat();
                //comparator = (t1, t2) -> compareToIgnoreCase(t1.getTitle(), t2.getTitle());
                comparator = (t1, t2) -> compareToIgnoreCase(
                        t1.getFormattedTitle(format),
                        t2.getFormattedTitle(format)
                );
                break;
            case FileName:
                comparator = (t1, t2) -> compareToIgnoreCase(t1.getFileName(), t2.getFileName());
                break;
            case Url:
                comparator = (t1, t2) -> compareToIgnoreCase(t1.getUrl(), t2.getUrl());
                break;
            case AlbumName:
                comparator = (t1, t2) -> compareToIgnoreCase(t1.getAlbum(), t2.getAlbum());
                break;
            case PublishDate:
                comparator = (t1, t2) -> compareToIgnoreCase(t1.getDate(), t2.getDate());
                break;
            case Rating:
                comparator = (t1, t2) -> compareToIgnoreCase(t1.getRating(), t2.getRating());
                break;
            case TrackNumber:
                comparator = (t1, t2) ->  compareToIgnoreCase(t1.getTrackNumber(), t2.getTrackNumber());
                break;
            case Duration:
                comparator = (t1, t2) -> (int) (t1.getTrackLength() - t2.getTrackLength());
                break;
        }
        if(comparator != null) {
            getData().sort(comparator);
        }
    }

    public void clear() {
        getData().clear();
        sizeProperty.set(size());
    }

    public void setAll(Collection<Track> uniqueSet) {
        clear();
        addAll(uniqueSet);
    }

    public void move(int fromIndex, int toIndex) {
        if(isEmpty()) {
            return ;
        }
        if (fromIndex == toIndex) {
            return ;
        }

        Track track = getTrack(fromIndex);
        if(track == null) {
            return ;
        }
        getData().add(toIndex, track);
        boolean downAction = toIndex > fromIndex;
        getData().remove(downAction ? fromIndex : fromIndex + 1);
    }

    public void add(int index, Track track) {
        if(isEmpty()) {
            return ;
        }
        if(index < 0 || index > size()) {
            return ;
        }
        track.setQueueId(id);
        getData().add(index, track);
    }

}
