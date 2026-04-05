package xyz.rive.jttplayer.menu;

import javafx.stage.Window;

import java.util.List;

public class PlayModeContextMenu extends AbstractContextMenu {

    public PlayModeContextMenu() {
        this(null);
    }

    public PlayModeContextMenu(Window owner) {
        super(owner);
    }

    @Override
    protected List<MenuMeta> buildMenuList() {
        return getMenuTemplates().getPlayModeMenuList();
    }


}
