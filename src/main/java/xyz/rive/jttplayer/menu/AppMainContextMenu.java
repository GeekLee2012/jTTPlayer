package xyz.rive.jttplayer.menu;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import xyz.rive.jttplayer.common.PlaybackQueue;
import xyz.rive.jttplayer.menu.action.*;
import xyz.rive.jttplayer.skin.SkinXml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static xyz.rive.jttplayer.common.ActionSource.AppMainMenu;
import static xyz.rive.jttplayer.common.Constants.*;
import static xyz.rive.jttplayer.util.FileUtils.guessSimpleName;
import static xyz.rive.jttplayer.util.FxUtils.getIconStyle;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class AppMainContextMenu extends AbstractContextMenu  {

    public AppMainContextMenu() {
        this(null);
    }

    public AppMainContextMenu(Window owner) {
        super(owner, true);
    }

    @Override
    protected List<MenuMeta> buildMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("千千选项",
                getIconStyle("TTPlayer_x24.png", 16, 16),
                new ShowStageAction(getStageManager().getPreferenceStage())));
        /*menuMetas.add(new MenuMeta("相关链接",
                getIconStyle("common.png", 28),
                getLinkMenuList()));*/
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("播放控制",
                getIconStyle("common2.png", 3),
                getPlayControlMenuList()));
        menuMetas.add(new MenuMeta("音量控制",
                getIconStyle("common.png", 25),
                getVolumeMenuList()));
        menuMetas.add(new MenuMeta("播放模式",
                getIconStyle("common.png", 5),
                getMenuTemplates().getPlayModeMenuList()));
        menuMetas.add(new MenuMeta("播放曲目",
                getIconStyle("common.png", 4),
                (EventHandler<? super MouseEvent>) null,
                //(MenuMeta __) -> getCurrentPlaybackQueueMenuList(),
                -1));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("视觉效果",
                getIconStyle("common.png", 8),
                (MenuMeta __) -> getMenuTemplates().getVisualMenu(),
                -1
        ));
        menuMetas.add(new MenuMeta("歌词秀",
                getIconStyle("common.png", 9),
                getLyricMenuList()));
        menuMetas.add(new MenuMeta("均衡器",
                getIconStyle("common.png", 7),
                getMenuTemplates().getEqualizerMenuList()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("皮肤",
                getIconStyle("common.png", 10),
                (MenuMeta __) -> getSkinMenuList(),
                -1));
        menuMetas.add(new MenuMeta("透明",
                getIconStyle("common.png", 11),
                getOpacityMenuList()));
        menuMetas.add(new MenuMeta("查看",
                getIconStyle("common.png", 6),
                getViewMenuList()));
        menuMetas.add(MenuMeta.separator());
        if(context.getConfiguration()
                .getPlayerOptions().isLyricDesktopMode()) {
            if(context.getConfiguration().getPlayerOptions().isLyricDesktopLocked()) {
                menuMetas.add(new MenuMeta("解锁桌面歌词",
                        getIconStyle("common.png", 49),
                        (EventHandler<? super MouseEvent>) __ -> getStageManager().setLyricDesktopLocked(false)));
            } else {
                menuMetas.add(new MenuMeta("锁定桌面歌词",
                        getIconStyle("common.png", 48),
                        (EventHandler<? super MouseEvent>) __ -> getStageManager().setLyricDesktopLocked(true)));
            }
        } else {
            menuMetas.add(new MenuMeta("显示桌面歌词",
                    getIconStyle("common.png", 32),
                    (EventHandler<? super MouseEvent>) __ -> getStageManager().toggleLyricDesktopShow(),
                    (MenuMeta __) -> MenuMeta.toActiveState(context.getConfiguration()
                            .getPlayerOptions().isLyricDesktopMode())));
        }

        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("最小化",
                getIconStyle("common.png", 2),
                (EventHandler<? super MouseEvent>) __ -> getStageManager().minimized())
        );
        menuMetas.add(new MenuMeta("全屏显示",
                getIconStyle("common.png", 29),
                getFullScreenMenuList()));
        menuMetas.add(new MenuMeta("退出",
                getIconStyle("common.png", 3),
                (EventHandler<? super MouseEvent>) __ -> getPlayerManager().exit()));
        return menuMetas;
    }

    private List<MenuMeta> getLinkMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("软件主页", getIconStyle("common.png", 34)));
        menuMetas.add(new MenuMeta("千千音乐在线", getIconStyle("common.png", 34)));
        menuMetas.add(new MenuMeta("论坛", getIconStyle("common.png", 34)));
        menuMetas.add(new MenuMeta("帮助", getIconStyle("common.png", 31)));
        return menuMetas;
    }

    private List<MenuMeta> getPlayControlMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("播放",
                getIconStyle("common2.png", 3),
                (EventHandler<? super MouseEvent>) __ -> getPlayerManager().togglePlay()));
        menuMetas.add(new MenuMeta("停止",
                getIconStyle("common.png", 24),
                (EventHandler<? super MouseEvent>) __ -> getPlayerManager().stopPlay()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("快进5秒",
                getIconStyle("common2.png", 5),
                new SeekTrackRelativeAction(5)));
        menuMetas.add(new MenuMeta("快退5秒",
                getIconStyle("common2.png", 6),
                new SeekTrackRelativeAction(-5)));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("上一首",
                getIconStyle("common.png", 26),
                (EventHandler<? super MouseEvent>) __ -> getPlayerManager().playPrevious()));
        menuMetas.add(new MenuMeta("下一首",
                getIconStyle("common.png", 27),
                (EventHandler<? super MouseEvent>) __ -> getPlayerManager().playNext()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("播放文件",
                getIconStyle("common.png", 23),
                new PlayFileAction()));
        menuMetas.add(new MenuMeta("播放 CD/VCD",
                getIconStyle("common.png", 18),
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true),
                145
        ));
        menuMetas.add(new MenuMeta("播放URL",
                getIconStyle("common.png", 35),
                new ShowStageAction(getStageManager().getPlayUrlStage())));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("关闭文件",
                getIconStyle("common.png", 17),
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        return menuMetas;
    }

    private List<MenuMeta> getVolumeMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("增大",
                getIconStyle("common.png", 38),
                (EventHandler<? super MouseEvent>) __ -> getPlayerManager().addVolume(5)));
        menuMetas.add(new MenuMeta("减小",
                getIconStyle("common.png", 37),
                (EventHandler<? super MouseEvent>) __ -> getPlayerManager().addVolume(-5)));
        menuMetas.add(new MenuMeta("静音",
                getIconStyle("common.png", 39),
                (EventHandler<? super MouseEvent>) __ -> getPlayerManager().toggleMute(),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().getActivePlayer().isMute())));
        return menuMetas;
    }

    private double getStagesOpacity() {
        return getStageManager().getStagesOpacity();
    }

    private List<MenuMeta> getOpacityMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("不透明",
                new SetStagesOpacityAction(1),
                (MenuMeta __) -> MenuMeta.toActiveState(getStagesOpacity() == 1)));
        menuMetas.add(new MenuMeta("10%透明",
                new SetStagesOpacityAction(0.9),
                (MenuMeta __) -> MenuMeta.toActiveState(getStagesOpacity() == 0.9)));
        menuMetas.add(new MenuMeta("20%透明",
                new SetStagesOpacityAction(0.8),
                (MenuMeta __) -> MenuMeta.toActiveState(getStagesOpacity() == 0.8)));
        menuMetas.add(new MenuMeta("30%透明",
                new SetStagesOpacityAction(0.7),
                (MenuMeta __) -> MenuMeta.toActiveState(getStagesOpacity() == 0.7)));
        menuMetas.add(new MenuMeta("40%透明",
                new SetStagesOpacityAction(0.6),
                (MenuMeta __) -> MenuMeta.toActiveState(getStagesOpacity() == 0.6)));
        menuMetas.add(new MenuMeta("50%透明",
                new SetStagesOpacityAction(0.5),
                (MenuMeta __) -> MenuMeta.toActiveState(getStagesOpacity() == 0.5)));
        menuMetas.add(new MenuMeta("60%透明",
                new SetStagesOpacityAction(0.4),
                (MenuMeta __) -> MenuMeta.toActiveState(getStagesOpacity() == 0.4)));
        menuMetas.add(new MenuMeta("70%透明",
                new SetStagesOpacityAction(0.3),
                (MenuMeta __) -> MenuMeta.toActiveState(getStagesOpacity() == 0.3)));
        menuMetas.add(new MenuMeta("80%透明",
                new SetStagesOpacityAction(0.2),
                (MenuMeta __) -> MenuMeta.toActiveState(getStagesOpacity() == 0.2)));
        menuMetas.add(new MenuMeta("90%透明",
                new SetStagesOpacityAction(0.1),
                (MenuMeta __) -> MenuMeta.toActiveState(getStagesOpacity() == 0.1)));
        menuMetas.add(new MenuMeta("激活时不透明",
                new ToggleStagesActiveOpacityOptionAction(),
                (MenuMeta __) -> MenuMeta.toActiveState(getStageManager().isStagesIgnoreOpacityOnActive())));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("窗口阴影",
                getIconStyle("common.png", 36),
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        return menuMetas;
    }

    private List<MenuMeta> getViewMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("歌词秀",
                getIconStyle("common.png", 9),
                new ToggleStageShowAction(LYRIC),
                (MenuMeta __) -> MenuMeta.toActiveState(getStageManager().isLyricShow()))
        );
        menuMetas.add(new MenuMeta("均衡器",
                getIconStyle("common.png", 7),
                new ToggleStageShowAction(EQUALIZER),
                (MenuMeta __) -> MenuMeta.toActiveState(getStageManager().isEqualizerShow()))
        );
        menuMetas.add(new MenuMeta("播放列表",
                getIconStyle("common2.png", 3),
                new ToggleStageShowAction(PLAYBACK_QUEUE),
                (MenuMeta __) -> MenuMeta.toActiveState(getStageManager().isPlaybackQueueShow()))
        );
        menuMetas.add(new MenuMeta("音乐窗口",
                getIconStyle("common.png", 30),
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("文件属性",
                getIconStyle("common.png"),
                new ShowFileAttributesAction(AppMainMenu)));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("媒体库模式",
                getIconStyle("common.png", 45),
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("迷你模式",
                getIconStyle("common.png", 46),
                new ToggleMiniModeAction(),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isMiniMode())
        ));
        menuMetas.add(new MenuMeta("重新排列",
                getIconStyle("common.png", 21),
                new SetupAutoLayoutAction()));
        menuMetas.add(new MenuMeta("总在最前",
                getIconStyle("common.png", 47),
                new ToggleAlwaysOnTopAction(),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isAlwaysOnTop()))
        );
        return menuMetas;
    }

    private List<MenuMeta> getLyricMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("调整歌词", getOffsetMenu()));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("在线搜索",
                getIconStyle("common.png", 22),
                new ShowSearchOnlineStageAction(),
                (MenuMeta __) -> MenuMeta.toDisabledState(
                        context.getConfiguration().getLyricSearchOptions()
                                .getServers().isEmpty()
                )
        ));
        menuMetas.add(new MenuMeta("关联歌词",
                getIconStyle("common.png", 12),
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("编辑歌词",
                getIconStyle("common.png", 13),
                (EventHandler<? super MouseEvent>) null,
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
                (EventHandler<? super MouseEvent>) null,
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
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("选项",
                getIconStyle("common.png", 16),
                new ShowPreferenceViewOptionsAction("歌词秀")));
        return menuMetas;
    }

    private List<MenuMeta> getFullScreenMenuList() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("歌词全屏"));
        menuMetas.add(new MenuMeta("视觉全屏"));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("歌词视觉同屏"));
        return menuMetas;
    }

    private List<MenuMeta> getOffsetMenu() {
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("本句提前0.5秒",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("本句延后0.5秒",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("其后提前0.5秒",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("其后延后0.5秒",
                (EventHandler<? super MouseEvent>) null,
                (MenuMeta __) -> MenuMeta.toDisabledState(true)
        ));
        menuMetas.add(new MenuMeta("全部提前0.5秒",
                new SetLyricOffsetAction(-500)));
        menuMetas.add(new MenuMeta("全部延后0.5秒",
                new SetLyricOffsetAction(500)));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("全部调整",
                new ShowStageAction(getStageManager().getLyricOffsetStage()), 150));
        menuMetas.add(MenuMeta.separator());
        menuMetas.add(new MenuMeta("鼠标滚轮调整",
                new SetLyricOffsetByMouseWheelAction(),
                (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isLyricOffsetSetByMouseWheelMode())));
        return menuMetas;
    }


    private List<MenuMeta>  getCurrentPlaybackQueueMenuList() {
        PlaybackQueue queue = getPlayerManager().getCurrentPlaybackQueue();
        if(queue == null || queue.isEmpty()) {
            return null;
        }
        AtomicInteger count = new AtomicInteger(0);
        List<MenuMeta> menuMetas = new ArrayList<>();
        queue.getData().forEach(track -> {
            int index = count.getAndIncrement();
            menuMetas.add(new MenuMeta(track.getTitle(),
                    new PlayTrackAction(track),
                    (MenuMeta __) -> MenuMeta.toActiveState(
                            getPlayerManager().getCurrentTrackIndex() == index
                    ), 183, 404));
        });
        return menuMetas;
    }

    private List<MenuMeta> getSkinMenuListExpand() {
        Map<String, SkinXml> skins = context.getSkinManager().getSkins();
        if(skins == null || skins.isEmpty()) {
            return null;
        }
        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("< 默认皮肤 >",
                new SwitchSkinAction("DEFAULT.skn", null),
                (MenuMeta __) -> MenuMeta.toActiveState(
                        getPlayerManager().isActiveSkin("DEFAULT.skn")
                ), 168, -1));
        menuMetas.add(MenuMeta.separator());
        int count = 0, limit = 16;
        for (Map.Entry<String, SkinXml> entry : skins.entrySet()) {
            String key = entry.getKey();
            SkinXml value = entry.getValue();
            String name = guessSimpleName(key);
            if ("DEFAULT".contentEquals(name)) {
                continue;
            }
            if(count++ >= limit) {
                break;
            }
            menuMetas.add(new MenuMeta(name,
                    new SwitchSkinAction(key, value),
                    (MenuMeta __) -> MenuMeta.toActiveState(
                            getPlayerManager().isActiveSkin(key)
                    )));
        }
        if (skins.size() > 1) {
            menuMetas.add(MenuMeta.separator());
        }
        menuMetas.add(new MenuMeta("选项",
                getIconStyle("common.png", 16),
                new ShowPreferenceViewOptionsAction("皮肤")));
        return menuMetas;
    }

    private List<MenuMeta> getSkinMenuList() {
        Map<String, SkinXml> skins = context.getSkinManager().getSkins();
        if(skins == null || skins.isEmpty()) {
            return null;
        }
        return skins.size() < 18 ?
                getSkinMenuListExpand() :
                getSkinMenuListGroups();
    }

    private List<MenuMeta> getSkinMenuListGroups() {
        Map<String, SkinXml> skins = context.getSkinManager().getSkins();
        if(skins == null || skins.isEmpty()) {
            return null;
        }
        /*
        List<String> alphabets = new ArrayList<>(27);
        skins.forEach((key, value) -> {
            if (isEmpty(key)) {
                return ;
            }
            String py = firstCharToPinyin(key);
            if (isEmpty(py)) {
                py = "#";
            }
            if (!alphabets.contains(py)) {
                alphabets.add(py);
            }
        });
        alphabets.sort(String::compareTo);
        */
        List<String> alphabets = new ArrayList<>(14);
        alphabets.add("其他");
        for (int i = 65; i < 91; i += 2) {
            alphabets.add((char)i + " — " + (char)( i + 1));
        }
        alphabets.sort(String::compareTo);

        List<MenuMeta> menuMetas = new ArrayList<>();
        menuMetas.add(new MenuMeta("< 默认皮肤 >",
                new SwitchSkinAction("DEFAULT.skn", null),
                (MenuMeta __) -> MenuMeta.toActiveState(
                        getPlayerManager().isActiveSkin("DEFAULT.skn")
                ), 143, -1));
        menuMetas.add(MenuMeta.separator());

        for (String ch : alphabets) {
            menuMetas.add(new MenuMeta(ch,
                    null,
                    (MenuMeta __) -> getSkinMenuListByGroup(ch),
                    -1)
            );
        }

        if (skins.size() > 1) {
            menuMetas.add(MenuMeta.separator());
        }
        menuMetas.add(new MenuMeta("选项",
                getIconStyle("common.png", 16),
                new ShowPreferenceViewOptionsAction("皮肤")));
        return menuMetas;
    }

    private List<MenuMeta> getSkinMenuListByGroup(String groupName) {
        Map<String, SkinXml> skins = context.getSkinManager().getSkins();
        if(skins == null || skins.isEmpty()) {
            return null;
        }
        String[] grpNames = groupName.split("—");
        List<MenuMeta> menuMetas = new ArrayList<>();
        int count = 0, limit = 20;
        for (Map.Entry<String, SkinXml> entry : skins.entrySet()) {
            String key = entry.getKey();
            SkinXml value = entry.getValue();
            String name = guessSimpleName(key);
            if ("DEFAULT".contentEquals(name)) {
                continue;
            }
            String ch = firstCharToPinyin(name);
            if (grpNames.length > 1
                    && (ch.startsWith(trim(grpNames[0]))
                    || ch.startsWith(trim(grpNames[1])))) {
                menuMetas.add(new MenuMeta(name,
                        new SwitchSkinAction(key, value),
                        (MenuMeta __) -> MenuMeta.toActiveState(
                                getPlayerManager().isActiveSkin(key)
                        ), 168, -1));
            } else if (contentEquals("其他", groupName)
                    && !ch.matches("[A-Z]")) {
                menuMetas.add(new MenuMeta(name,
                        new SwitchSkinAction(key, value),
                        (MenuMeta __) -> MenuMeta.toActiveState(
                                getPlayerManager().isActiveSkin(key)
                        ), 168, -1));
            }
        }
        /*
        if (skins.size() > limit) {
            menuMetas.add(MenuMeta.separator());
        }
        menuMetas.add(new MenuMeta("更多皮肤",
                getIconStyle("common.png", 16),
                new ShowPreferenceViewOptionsAction("皮肤")));
        */
        return menuMetas.isEmpty() ? null : menuMetas;
    }


}
