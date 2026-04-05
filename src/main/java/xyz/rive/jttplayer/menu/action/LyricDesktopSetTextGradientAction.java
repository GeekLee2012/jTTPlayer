package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

public class LyricDesktopSetTextGradientAction extends AbstractMenuAction {
    private final String styleClass;
    public LyricDesktopSetTextGradientAction(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getControllerManager().setLyricDesktopTextGradient(styleClass);
    }
}
