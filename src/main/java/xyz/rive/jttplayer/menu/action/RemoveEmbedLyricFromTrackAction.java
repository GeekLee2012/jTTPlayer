package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

import java.util.Optional;

public class RemoveEmbedLyricFromTrackAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        Optional.ofNullable(getPlayerManager().getCurrentTrack())
                .ifPresent(track -> {
                    track.setLyricEmbed(null);
                    getContext().getMetadataService().writeLyric(track.getUrl(), null);
                });
    }

}
