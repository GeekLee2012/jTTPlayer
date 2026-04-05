package xyz.rive.jttplayer.menu;

import javafx.stage.Window;
import xyz.rive.jttplayer.menu.action.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static xyz.rive.jttplayer.common.ActionSource.ContextMenu;
import static xyz.rive.jttplayer.util.CollectionUtils.sizeLt;

public class PlaybackQueueContextMenu extends AbstractContextMenu {

    public PlaybackQueueContextMenu() {
        this(null);
    }

    public PlaybackQueueContextMenu(Window owner) {
        super(owner);
    }

    protected List<MenuMeta> buildMenuList() {
        Function<MenuMeta, Integer> disableStateDetector = (MenuMeta __) ->
                MenuMeta.toDisabledState(sizeLt(getPlayerManager().getPlaybackQueues(), 2));
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("切换列表",
                new SwitchToPlaybackQueueAction(),
                disableStateDetector)
        );
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("新建列表",
                new CreatePlaybackQueueAction()));
        menuMetas.add(new MenuMeta("添加列表",
                new AddPlaybackQueueAction()));
        menuMetas.add(new MenuMeta("保存列表",
                new SavePlaybackQueueAction(ContextMenu))
        );
        menuMetas.add(new MenuMeta("删除列表",
                new RemovePlaybackQueueAction(ContextMenu),
                disableStateDetector)
        );
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("保存所有列表"));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("重命名",
                new RenamePlaybackQueueAction()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("按名称排序",
                new SortPlaybackQueuesAction(),
                (MenuMeta __) -> MenuMeta.toState(
                        sizeLt(getPlayerManager().getPlaybackQueues(), 2),
                        getPlayerManager().isPlaybackQueueSortByName()
                )
        ));
        return menuMetas;
    }

}
