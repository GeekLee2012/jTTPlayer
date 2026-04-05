package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class SetLyricOffsetAction extends AbstractMenuAction {
    private final int offset;
    public SetLyricOffsetAction(int offset) {
        this.offset = offset;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getControllerManager().setLyricOffset(offset);
    }

}
