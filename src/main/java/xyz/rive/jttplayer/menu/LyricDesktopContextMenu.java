package xyz.rive.jttplayer.menu;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import xyz.rive.jttplayer.menu.action.*;

import java.util.ArrayList;
import java.util.List;

import static xyz.rive.jttplayer.util.FxUtils.getIconStyle;

public class LyricDesktopContextMenu extends AbstractContextMenu {

    public LyricDesktopContextMenu(){
        this(null);
    }

    public LyricDesktopContextMenu(Window owner) {
        super(owner);
    }

    protected List<MenuMeta> buildMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("预设方案", getMenuTemplates().getLyricDesktopTextSettingsMenu(false)));
        menuMetas.add(new MenuMeta("显示方式", (EventHandler<MouseEvent>) null));
        menuMetas.add(new MenuMeta("简繁转换", getMenuTemplates().getChineseMenu()));
        menuMetas.add(new MenuMeta("总在最前",
                (EventHandler<? super MouseEvent>) __ -> context.getStageManager().toggleLyricAlwaysTop(),
                (MenuMeta __) -> MenuMeta.toActiveState(context.getConfiguration()
                        .getPlayerOptions().isLyricViewAlwaysOnTop())));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("调整歌词",
                getMenuTemplates().getLyricOffsetMenu()));
        menuMetas.add(new MenuMeta("在线搜索",
                getIconStyle("common.png", 22),
                null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("歌词背景穿透",
                (EventHandler<MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)));
        menuMetas.add(new MenuMeta("锁定桌面歌词",
                getIconStyle("common.png", 48),
                (EventHandler<? super MouseEvent>) __ -> getStageManager().setLyricDesktopLocked(true))
        );
        menuMetas.add(new MenuMeta("返回窗口模式",
                getIconStyle("common.png", 33),
                (EventHandler<? super MouseEvent>) __ -> getStageManager().toggleLyricDesktopShow())
        );
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("选项",
                getIconStyle("common.png", 16),
                new ShowPreferenceViewOptionsAction("歌词秀", 1))
        );
        return menuMetas;
    }


}
