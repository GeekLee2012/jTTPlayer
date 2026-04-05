package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.ActionSource;
import xyz.rive.jttplayer.common.Track;

import static xyz.rive.jttplayer.common.ActionSource.*;

public class ShowFileAttributesAction extends AbstractMenuAction {
    private final ActionSource source;

    public ShowFileAttributesAction() {
        this(MainStage);
    }

    public ShowFileAttributesAction(ActionSource source) {
        this.source = source;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        Track track = null;
        if(source == ContextMenu) {
            track = getContextMenuData(Track.class);
        }
        if(track == null) {
            track = getPlayerManager().getCurrentTrack();
        }
        getContext().setFileAttributesTrack(track);
        getStageManager().getFileAttributesStage().show();
    }

}