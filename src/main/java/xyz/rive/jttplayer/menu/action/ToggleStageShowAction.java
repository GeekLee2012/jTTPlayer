package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

import static xyz.rive.jttplayer.common.Constants.*;

public class ToggleStageShowAction extends AbstractMenuAction {
    private final String name;

    public ToggleStageShowAction(String name) {
        this.name = name;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        if(EQUALIZER.equalsIgnoreCase(name)) {
            getStageManager().toggleEqualizerShow();
        } else if(LYRIC.equalsIgnoreCase(name)) {
            getStageManager().toggleLyricShow();
        } else if(PLAYBACK_QUEUE.equalsIgnoreCase(name)) {
            getStageManager().togglePlaybackQueueShow();
        }

    }
}
