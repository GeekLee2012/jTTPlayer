package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class ToggleStagesActiveOpacityOptionAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getStageManager().toggleStagesActiveOpacityOption();
    }
}
