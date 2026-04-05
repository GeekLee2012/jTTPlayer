package xyz.rive.jttplayer.menu.strategy;

import javafx.stage.Window;
import xyz.rive.jttplayer.menu.AbstractContextMenu;
import xyz.rive.jttplayer.menu.MenuMeta;

import java.util.List;

public class VisualContextMenu extends AbstractContextMenu {

    public VisualContextMenu() {
        this(null);
    }

    public VisualContextMenu(Window owner) {
        super(owner);
    }

    @Override
    protected List<MenuMeta> buildMenuList() {
        return getMenuTemplates().getVisualMenu();
    }


}
