package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class SwitchZhLyricAction extends AbstractMenuAction {
    private final boolean traditional;

    public SwitchZhLyricAction(boolean traditional) {
        this.traditional = traditional;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getContext().getConfiguration().getPlayerOptions()
                .setLyricZhType(traditional ? 2 : 1);
        getControllerManager().switchZhLyric();
    }

}
