package xyz.rive.jttplayer.menu;

import javafx.stage.Window;

import java.util.List;

public abstract class AbstractContextMenu extends PopMenu  {

    public AbstractContextMenu() {
        this(null);
    }

    public AbstractContextMenu(Window owner) {
        this(owner, false);
    }

    public AbstractContextMenu(Window owner, boolean slogan) {
        super(owner, slogan);
        setMenuList(buildMenuList());
    }

    protected abstract List<MenuMeta> buildMenuList();

    public void refresh() {
        setMenuList(buildMenuList());
        super.refresh();
    }

}
