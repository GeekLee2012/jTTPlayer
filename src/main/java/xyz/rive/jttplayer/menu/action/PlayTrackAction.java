package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.PlaybackQueue;
import xyz.rive.jttplayer.common.Track;

public class PlayTrackAction extends AbstractMenuAction {
    private final Track track;

    public PlayTrackAction() {
        this(null);
    }

    public PlayTrackAction(Track track) {
        this.track = track;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        if(track != null) {
            playTrack(track);
        } else {
            Track ctxTrack = getContextMenuData(Track.class);
            if (ctxTrack != null) {
                playTrack(ctxTrack);
            }
        }

    }

    private void playTrack(Track track) {
        if(track != null) {
            int queueIndex = getPlayerManager().getPlaybackQueueIndex(track.getQueueId());
            PlaybackQueue queue = getPlayerManager().getPlaybackQueue(queueIndex);
            if(queue == null || queue.isEmpty()) {
                return ;
            }
            int index = queue.indexOf(track);
            if(index < 0) {
                return ;
            }
            getPlayerManager().setCurrentIndex(queueIndex, index);
        }
    }
}
