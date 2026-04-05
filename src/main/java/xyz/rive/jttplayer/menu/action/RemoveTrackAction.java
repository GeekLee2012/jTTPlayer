package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.Track;

import java.util.Optional;

public class RemoveTrackAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        Optional.ofNullable(getContextMenuData(Track.class)).ifPresent(track -> {
            Optional.ofNullable(getPlayerManager().getActivePlaybackQueue())
                    .ifPresent(queue -> {
                        if(queue.remove(track)) {
                            getPlayerManager().refreshActivePlaybackQueue();
                        }
                    });
        });
    }
}
