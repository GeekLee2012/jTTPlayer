package xyz.rive.jttplayer.menu;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import xyz.rive.jttplayer.menu.action.*;

import java.util.ArrayList;
import java.util.List;

import static xyz.rive.jttplayer.util.FxUtils.getIconStyle;

public class LyricContextMenu extends AbstractContextMenu {

    public LyricContextMenu() {
        this(null);
    }

    public LyricContextMenu(Window owner) {
        super(owner);
    }

    @Override
    protected List<MenuMeta> buildMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("调整歌词", getMenuTemplates().getLyricOffsetMenu()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("在线搜索",
                getIconStyle("common.png", 22),
                null,
                (MenuMeta __) -> MenuMeta.toDisabledState(
                        context.getConfiguration().getLyricSearchOptions()
                                .getServers().isEmpty()
                )
        ));
        menuMetas.add(new MenuMeta("关联歌词",
                getIconStyle("common.png", 12),
                null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("编辑歌词",
                getIconStyle("common.png", 13),
                null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("复制歌词",
                getIconStyle("common2.png", 11),
                new CopyLyricAction()));
        menuMetas.add(new MenuMeta("撤销歌词",
                getIconStyle("common.png", 15),
                new CancelLyricAction()));
        menuMetas.add(new MenuMeta("重新载入",
                getIconStyle("common.png", 14),
                new ReloadLyricAction()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("上传歌词",
                getIconStyle("common.png", 40),
                null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("内嵌歌词", getMenuTemplates().getEmbedLyricMenu()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("显示方式",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("简繁转换", getMenuTemplates().getChineseMenu()));
        menuMetas.add(new MenuMeta("字符编码",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("总在最前",
                getIconStyle("common.png", 41),
                new ToggleLyricStageAlwaysTopAction()));
        menuMetas.add(new MenuMeta("显示桌面歌词",
                getIconStyle("common.png", 32),
                (EventHandler<? super MouseEvent>) __ -> getStageManager().toggleLyricDesktopShow(),
                (MenuMeta __) -> MenuMeta.toActiveState(context.getConfiguration()
                        .getPlayerOptions().isLyricDesktopMode())
        ));
        menuMetas.add(new MenuMeta("全屏显示",
                getIconStyle("common.png", 29),
                null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("选项",
                getIconStyle("common.png", 16),
                new ShowPreferenceViewOptionsAction("歌词秀", 0)));
        return menuMetas;
    }

}
