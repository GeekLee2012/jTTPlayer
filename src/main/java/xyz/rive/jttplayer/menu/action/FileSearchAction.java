package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class FileSearchAction extends AbstractMenuAction {
    private final boolean next;

    public FileSearchAction() {
        this(true);
    }

    public FileSearchAction(boolean next) {
        this.next = next;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getControllerManager().searchFile(next);
    }
}
