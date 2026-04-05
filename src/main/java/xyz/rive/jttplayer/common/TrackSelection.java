package xyz.rive.jttplayer.common;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class TrackSelection {
    private Track track;
    private int queueIndex;
    private int index;

    public TrackSelection(Track track, int queueIndex, int index) {
        this.track = track;
        this.queueIndex = queueIndex;
        this.index = index;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public int getQueueIndex() {
        return queueIndex;
    }

    public void setQueueIndex(int queueIndex) {
        this.queueIndex = queueIndex;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackSelection selection = (TrackSelection) o;
        return queueIndex == selection.queueIndex
                && index == selection.index
                && Objects.equals(track, selection.track);
    }

    @Override
    public int hashCode() {
        return Objects.hash(track, queueIndex, index);
    }

}
