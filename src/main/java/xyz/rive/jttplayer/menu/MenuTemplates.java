package xyz.rive.jttplayer.menu;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.PlaybackMode;
import xyz.rive.jttplayer.manager.PlayerManager;
import xyz.rive.jttplayer.menu.action.*;

import java.util.*;
import java.util.function.Supplier;

import static xyz.rive.jttplayer.common.PlaybackMode.*;
import static xyz.rive.jttplayer.util.FxUtils.getIconStyle;

public class MenuTemplates {
    private final ApplicationContext context;
    private final Map<String, List<MenuMeta>> TEMPLATES = new HashMap<>();

    public MenuTemplates(ApplicationContext context) {
        this.context = context;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public PlayerManager getPlayerManager() {
        return getContext().getPlayerManager();
    }

    private List<MenuMeta> getDefault(String key, Supplier<List<MenuMeta>> supplier) {
        if(TEMPLATES.containsKey(key)) {
            return TEMPLATES.get(key);
        }
        List<MenuMeta> list = supplier.get();
        TEMPLATES.put(key, list);
        return list;
    }

    public List<MenuMeta> getPlayModeMenuList() {
        return getDefault("PlayMode", () -> {
            List<MenuMeta> menuMetas = new ArrayList<>();
            menuMetas.add(new MenuMeta("单曲播放",
                    getIconStyle("mode.png"),
                    new SetPlaybackModeAction(OnlyOne),
                    (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isPlaybackMode(OnlyOne)))
            );
            menuMetas.add(new MenuMeta("单曲循环",
                    getIconStyle("mode.png", 1),
                    new SetPlaybackModeAction(RepeatOne),
                    (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isPlaybackMode(RepeatOne)))
            );
            menuMetas.add(new MenuMeta("顺序播放",
                    getIconStyle("mode.png", 2),
                    new SetPlaybackModeAction(Sequence),
                    (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isPlaybackMode(Sequence)))
            );
            menuMetas.add(new MenuMeta("循环播放",
                    getIconStyle("mode.png", 3),
                    new SetPlaybackModeAction(RepeatAll),
                    (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isPlaybackMode(RepeatAll)))
            );
            menuMetas.add(new MenuMeta("随机播放",
                    getIconStyle("mode.png", 4),
                    new SetPlaybackModeAction(PlaybackMode.Random),
                    (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isPlaybackMode(PlaybackMode.Random)))
            );
            menuMetas.add(MenuMeta.separator());
            menuMetas.add(new MenuMeta("播放跟随光标",
                    getIconStyle("mode.png", 5),
                    (EventHandler<? super MouseEvent>) __ -> getPlayerManager().togglePlayFollowCursorMode(),
                    (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isPlayFollowCursorMode()))
            );
            menuMetas.add(new MenuMeta("自动切换列表",
                    getIconStyle("mode.png", 6),
                    __ -> getPlayerManager().toggleAutoSwitchPlaybackQueue(),
                    (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isAutoSwitchPlaybackQueue()))
            );
            return menuMetas;
        });
    }

    public List<MenuMeta> getEqualizerMenuList() {
        return getDefault("Equalizer", () -> {
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
                menuMetas.add(new MenuMeta("可选类别", getAvailableEqualizerMenuList()));
                menuMetas.add(MenuMeta.separator());
                menuMetas.add(new MenuMeta("启用均衡器",
                        getIconStyle("common.png", 7),
                        new ToggleEqualizerEnabledAction(),
                        (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isEqualizerEnabled()))
                );
                menuMetas.add(new MenuMeta("启用杜比环绕",
                        getIconStyle("ommon.png", 44),
                        null,
                        (MenuMeta __) -> MenuMeta.toDisabledState(true)
                ));
                return menuMetas;
        });
    }

    public List<MenuMeta> getAvailableEqualizerMenuList() {
        return getDefault("AvailableEqualizer", () -> {
            List<MenuMeta> menuMetas = new ArrayList<>();
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
        });
    }

    public List<MenuMeta> getLyricOffsetMenu() {
        return getDefault("LyricOffset", () -> {
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
                    new ShowStageAction(getContext().getStageManager().getLyricOffsetStage()), 150));
            menuMetas.add(MenuMeta.separator());
            menuMetas.add(new MenuMeta("鼠标滚轮调整",
                    new SetLyricOffsetByMouseWheelAction(),
                    (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isLyricOffsetSetByMouseWheelMode())));
            return menuMetas;
        });
    }

    public List<MenuMeta> getLyricDesktopTextSettingsMenu(boolean withOptions) {
        return getDefault("LyricDesktopTextSetting".concat(withOptions ? "" : "-"), () -> {
            List<MenuMeta> menuMetas = new ArrayList<>();
            menuMetas.add(new MenuMeta("千千静听物语",
                    null,
                    new LyricDesktopSetTextGradientAction("qqjtwy"),
                    (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isActiveLyricDesktopTextGradient("qqjtwy"))
            ));
            menuMetas.add(new MenuMeta("盛夏果实",
                    null,
                    new LyricDesktopSetTextGradientAction("sxgs"),
                    (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isActiveLyricDesktopTextGradient("sxgs"))
            ));
            menuMetas.add(new MenuMeta("桃之夭夭",
                    null,
                    new LyricDesktopSetTextGradientAction("tzyy"),
                    (MenuMeta __) -> MenuMeta.toActiveState(getPlayerManager().isActiveLyricDesktopTextGradient("tzyy"))
            ));
            if(withOptions) {
                menuMetas.add(MenuMeta.separator());
                menuMetas.add(new MenuMeta("选项",
                        getIconStyle("common.png", 16),
                        new ShowPreferenceViewOptionsAction("歌词秀")));
            }
            return menuMetas;
        });
    }

    public List<MenuMeta> getChineseMenu() {
        return getDefault("Chinese", () -> {
            List<MenuMeta> menuMetas = new ArrayList<>();
            menuMetas.add(new MenuMeta("简体 -> 繁体", new SwitchZhLyricAction(true), 139));
            menuMetas.add(new MenuMeta("繁体 -> 简体", new SwitchZhLyricAction(false)));
            return menuMetas;
        });
    }

    public List<MenuMeta> getEmbedLyricMenu() {
        return getDefault("EmbedLyric", () -> {
            List<MenuMeta> menuMetas = new ArrayList<>();
            menuMetas.add(new MenuMeta("嵌入到音频文件", new AddEmbedLyricForTrackAction(), 163));
            menuMetas.add(new MenuMeta("从音频文件中读取", new ReadEmbedLyricForTrackAction()));
            menuMetas.add(new MenuMeta("从音频文件中删除", new RemoveEmbedLyricFromTrackAction()));
            return menuMetas;
        });
    }

    public List<MenuMeta> getVisualMenu() {
        return getDefault("Visual", () -> {
            List<MenuMeta> menuMetas = new ArrayList<>();
            menuMetas.add(new MenuMeta("频谱分析 <默认>",
                    new SetVisualIndexAction(1),
                    (MenuMeta __) -> MenuMeta.toActiveState(
                            getPlayerManager().isActiveVisualIndex(1)
                    ))
            );
            menuMetas.add(new MenuMeta("频谱分析 <细线>",
                    new SetVisualIndexAction(2),
                    (MenuMeta __) -> MenuMeta.toActiveState(
                            getPlayerManager().isActiveVisualIndex(2)
                    ))
            );
            menuMetas.add(new MenuMeta("频谱分析 <火焰>",
                    new SetVisualIndexAction(3),
                    (MenuMeta __) -> MenuMeta.toActiveState(
                            getPlayerManager().isActiveVisualIndex(3)
                    ))
            );
            menuMetas.add(new MenuMeta("频谱分析 <能量>",
                    new SetVisualIndexAction(4),
                    (MenuMeta __) -> MenuMeta.toActiveState(
                            getPlayerManager().isActiveVisualIndex(4)
                    ))
            );
            menuMetas.add(MenuMeta.separator());
            menuMetas.add(new MenuMeta("示波显示 <单波>",
                    new SetVisualIndexAction(5),
                    (MenuMeta __) -> MenuMeta.toActiveState(
                            getPlayerManager().isActiveVisualIndex(5)
                    ))
            );
            menuMetas.add(new MenuMeta("示波显示 <双波>",
                    new SetVisualIndexAction(6),
                    (MenuMeta __) -> MenuMeta.toActiveState(
                            getPlayerManager().isActiveVisualIndex(6)
                    ))
            );
            menuMetas.add(new MenuMeta("示波显示 <衍变>",
                    new SetVisualIndexAction(7),
                    (MenuMeta __) -> MenuMeta.toActiveState(
                            getPlayerManager().isActiveVisualIndex(7)
                    ))
            );
            menuMetas.add(MenuMeta.separator());
            menuMetas.add(new MenuMeta("专辑封面",
                    new SetVisualIndexAction(0),
                    (MenuMeta __) -> MenuMeta.toActiveState(
                            getPlayerManager().isActiveVisualIndex(0)
                    ), 153, -1)
            );
            menuMetas.add(MenuMeta.separator());
            menuMetas.add(new MenuMeta("选项",
                    getIconStyle("common.png", 16),
                    new ShowPreferenceViewOptionsAction("视觉效果")));
            return menuMetas;
        });
    }
}
