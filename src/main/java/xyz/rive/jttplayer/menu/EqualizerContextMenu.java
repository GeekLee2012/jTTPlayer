package xyz.rive.jttplayer.menu;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import xyz.rive.jttplayer.menu.action.EqualizerSelectAction;

import java.util.ArrayList;
import java.util.List;

import static xyz.rive.jttplayer.util.FxUtils.getIconStyle;

public class EqualizerContextMenu extends AbstractContextMenu {

    public EqualizerContextMenu() {
        this(null);
    }

    public EqualizerContextMenu(Window owner) {
        super(owner);
    }

    @Override
    protected List<MenuMeta> buildMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("推荐配置",
                getIconStyle("common.png", 42),
                new EqualizerSelectAction(0),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(0)))
        );
        menuMetas.add(new MenuMeta("自定义",
                getIconStyle("common.png", 43),
                new EqualizerSelectAction(1),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(1)))
        );
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("流行音乐",
                new EqualizerSelectAction(2),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(2)))
        );
        menuMetas.add(new MenuMeta("摇滚",
                new EqualizerSelectAction(3),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(3)))
        );
        menuMetas.add(new MenuMeta("金属乐",
                new EqualizerSelectAction(4),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(4)))
        );
        menuMetas.add(new MenuMeta("舞曲",
                new EqualizerSelectAction(5),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(5)))
        );
        menuMetas.add(new MenuMeta("电子乐",
                new EqualizerSelectAction(6),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(6)))
        );
        menuMetas.add(new MenuMeta("乡村音乐",
                new EqualizerSelectAction(7),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(7)))
        );
        menuMetas.add(new MenuMeta("爵士乐",
                new EqualizerSelectAction(8),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(8)))
        );
        menuMetas.add(new MenuMeta("古典乐",
                new EqualizerSelectAction(9),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(9)))
        );
        menuMetas.add(new MenuMeta("布鲁斯",
                new EqualizerSelectAction(10),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(10)))
        );
        menuMetas.add(new MenuMeta("怀旧音乐",
                new EqualizerSelectAction(11),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(11)))
        );
        menuMetas.add(new MenuMeta("歌剧",
                new EqualizerSelectAction(12),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(12)))
        );
        menuMetas.add(new MenuMeta("语音",
                new EqualizerSelectAction(13),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isCurrentEqualizerIndex(13)))
        );
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("从文件加载",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("保存到文件",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        return menuMetas;
    }
}
