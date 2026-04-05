package xyz.rive.jttplayer.menu;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import xyz.rive.jttplayer.menu.action.*;

import java.util.ArrayList;
import java.util.List;

import static xyz.rive.jttplayer.common.ActionSource.ContextMenu;
import static xyz.rive.jttplayer.util.FxUtils.getIconStyle;

public class TrackContextMenu extends AbstractContextMenu {

    public TrackContextMenu(){
        this(null);
    }

    public TrackContextMenu(Window owner) {
        super(owner);
    }

    protected List<MenuMeta> buildMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("播放",
                getIconStyle("common2.png", 3),
                new PlayTrackAction()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("文件属性",
                getIconStyle("common.png"),
                new ShowFileAttributesAction(ContextMenu)));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("评级", getRatingMenuList()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("删除",
                getIconStyle("common.png", 19),
                new RemoveTrackAction()));
        menuMetas.add(new MenuMeta("物理删除",
                null, null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)));
        menuMetas.add(new MenuMeta("编辑列表", getEditMenuList()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("转换格式",
                getIconStyle("common.png", 5),
                null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("回放增益",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("重命名文件", getRenameMenuList()));
        menuMetas.add(new MenuMeta("发送到",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)));
        menuMetas.add(new MenuMeta("网上搜索",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("浏览文件",
                getIconStyle("common.png", 20),
                new ShowInFolderAction()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("选项",
                getIconStyle("common.png", 16),
                new ShowPreferenceViewOptionsAction("播放列表")));
        return menuMetas;
    }

    protected List<MenuMeta> getRatingMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("1星级(1)", new SetRatingAction("1")));
        menuMetas.add(new MenuMeta("2星级(2)", new SetRatingAction("2")));
        menuMetas.add(new MenuMeta("3星级(3)", new SetRatingAction("3")));
        menuMetas.add(new MenuMeta("4星级(4)", new SetRatingAction("4")));
        menuMetas.add(new MenuMeta("5星级(5)", new SetRatingAction("5")));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("允许鼠标左键评级",
                null,
                null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true),
                163
        ));
        return menuMetas;
    }

    protected List<MenuMeta> getRenameMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("歌曲名.扩展名",
                new RenameFileAction("歌曲名.扩展名"), 186));
        menuMetas.add(new MenuMeta("歌曲名 - 歌手.扩展名",
                new RenameFileAction("歌曲名 - 歌手.扩展名")));
        menuMetas.add(new MenuMeta("歌手 - 歌曲名.扩展名",
                new RenameFileAction("歌手 - 歌曲名.扩展名")));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("自定义格式",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        return menuMetas;
    }


    protected List<MenuMeta> getEditMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("剪切",
                getIconStyle("common2.png", "-159 0"),
                new PlaybackQueueSetCopyingAction(true)));
        menuMetas.add(new MenuMeta("复制",
                getIconStyle("common2.png", 11),
                new PlaybackQueueSetCopyingAction()));
        menuMetas.add(new MenuMeta("粘贴",
                getIconStyle("common2.png", 12),
                new PlaybackQueuePasteAction()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("移动到列表",
                new CopyToPlaybackQueueAction(true)));
        menuMetas.add(new MenuMeta("复制到列表",
                new CopyToPlaybackQueueAction()));
        return menuMetas;
    }

}
