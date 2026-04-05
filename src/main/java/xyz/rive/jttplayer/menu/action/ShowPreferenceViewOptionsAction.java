package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;

import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

public class ShowPreferenceViewOptionsAction extends AbstractMenuAction {
    private static final String DEFAULT_NAME = "关于";
    private final String name;
    private int tabIndex = -1;

    public ShowPreferenceViewOptionsAction() {
        this(DEFAULT_NAME);
    }

    public ShowPreferenceViewOptionsAction(String name) {
        this.name = isEmpty(name) ? DEFAULT_NAME: name;
    }

    public ShowPreferenceViewOptionsAction(String name, int tabIndex) {
        this.name = isEmpty(name) ? DEFAULT_NAME: name;
        this.tabIndex = tabIndex;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getStageManager().getPreferenceStage().show();
        getControllerManager().setPreferenceActiveNavItem(name, tabIndex);
    }
}
