package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.Lyric;

import java.util.Optional;

public class ReadEmbedLyricForTrackAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        Optional.ofNullable(getPlayerManager().getCurrentTrack())
                .ifPresent(track -> {
                    String text = getContext().getMetadataService()
                            .readLyric(track.getUrl());
                    track.setLyric(Lyric.parseFromText(text));
                    getControllerManager().startLyric();
        });

    }

}
