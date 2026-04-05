package xyz.rive.jttplayer.manager;

import javafx.stage.Stage;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.GeneralOptions;
import xyz.rive.jttplayer.common.Position;
import xyz.rive.jttplayer.menu.*;
import xyz.rive.jttplayer.menu.strategy.SharedStrategies;
import xyz.rive.jttplayer.menu.strategy.ShowStrategy;
import xyz.rive.jttplayer.menu.strategy.ShowUnderItemStrategy;
import xyz.rive.jttplayer.menu.strategy.VisualContextMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static xyz.rive.jttplayer.util.FxUtils.*;

public class MenuManager extends AbstractManager {
    private AppMainContextMenu appMainContextMenu;
    private PopMenu menuBarPopMenu;
    private PopMenu playbackQueueNavigationContextMenu;
    private PopMenu equalizerNavigationContextMenu;
    private PlaybackQueueContextMenu playbackQueueContextMenu;
    private TrackContextMenu trackContextMenu;
    private EqualizerContextMenu equalizerContextMenu;
    private PlayModeContextMenu playModeContextMenu;
    private VisualContextMenu visualContextMenu;
    private LyricContextMenu lyricContextMenu;
    private LyricDesktopContextMenu lyricDesktopContextMenu;
    private PopMenu lyricDesktopTextSettingsPopMenu;
    private MenuTemplates menuTemplates;

    private TrayIcon trayIcon;

    public MenuManager(ApplicationContext context) {
        super(context);
    }

    public List<PopMenu> getAllPopups() {
        return Arrays.asList(
                appMainContextMenu,
                menuBarPopMenu,
                playbackQueueContextMenu,
                trackContextMenu,
                equalizerContextMenu,
                equalizerNavigationContextMenu,
                playbackQueueNavigationContextMenu,
                lyricContextMenu,
                lyricDesktopContextMenu,
                lyricDesktopTextSettingsPopMenu,
                playModeContextMenu,
                visualContextMenu
        );
    }

    public MenuManager hideMenu(PopMenu menu) {
        Optional.ofNullable(menu)
                .ifPresent(__ -> {
                    if (menu.isShowing()) {
                        menu.hide();
                    }
                });
        return this;
    }

    public MenuManager hideAllPopups(PopMenu... excludes) {
        getAllPopups().forEach(menu -> {
            if(Arrays.asList(excludes).contains(menu)) {
                return ;
            }
            hideMenu(menu);
        });
        return this;
    }

    private Stage getMainStage() {
        return getContext().getMainStage();
    }

    public PopMenu getAppMainContextMenu() {
        if(appMainContextMenu == null) {
            appMainContextMenu = new AppMainContextMenu();
            appMainContextMenu.setShowStrategy(SharedStrategies.getSharedDefault());
            appMainContextMenu.setAlwaysOnTop(true);
            appMainContextMenu.setOnShown(event -> hideAllPopups(appMainContextMenu));
        }
        return appMainContextMenu;
    }

    public PopMenu getMenuBarPopMenu() {
        if(menuBarPopMenu == null) {
            menuBarPopMenu = new PopMenu(getMainStage());
            menuBarPopMenu.setShowStrategy(new ShowUnderItemStrategy(0, 4, true));
            menuBarPopMenu.setOnShown(event -> hideAllPopups(menuBarPopMenu));
        }
        return menuBarPopMenu;
    }

    public PopMenu getPlaybackQueueNavigationContextMenu() {
        if(playbackQueueNavigationContextMenu == null) {
            playbackQueueNavigationContextMenu = new PopMenu(getMainStage());
            playbackQueueNavigationContextMenu.setOnShown(
                    event -> hideAllPopups(playbackQueueNavigationContextMenu));
        }
        return playbackQueueNavigationContextMenu;
    }

    public PopMenu getEqualizerNavigationContextMenu() {
        if(equalizerNavigationContextMenu == null) {
            equalizerNavigationContextMenu = new PopMenu(getMainStage());
            equalizerNavigationContextMenu.setOnShown(
                    event -> hideAllPopups(equalizerNavigationContextMenu)
            );
        }
        return equalizerNavigationContextMenu;
    }

    public PlaybackQueueContextMenu getPlaybackQueueContextMenu() {
        if(playbackQueueContextMenu == null) {
            playbackQueueContextMenu = new PlaybackQueueContextMenu(getMainStage());
            playbackQueueContextMenu.setOnShown(
                    event -> hideAllPopups(playbackQueueContextMenu)
            );
        }
        return playbackQueueContextMenu;
    }

    public TrackContextMenu getTrackContextMenu() {
        if(trackContextMenu == null) {
            trackContextMenu = new TrackContextMenu(getMainStage());
            trackContextMenu.setOnShown(event -> hideAllPopups(trackContextMenu));
        }
        return trackContextMenu;
    }

    public EqualizerContextMenu getEqualizerContextMenu() {
        if(equalizerContextMenu == null) {
            equalizerContextMenu = new EqualizerContextMenu(getMainStage());
            equalizerContextMenu.setOnShown(event -> hideAllPopups(equalizerContextMenu));
        }
        return equalizerContextMenu;
    }

    public PlayModeContextMenu getPlayModeContextMenu() {
        if(playModeContextMenu == null) {
            playModeContextMenu = new PlayModeContextMenu(getMainStage());
            playModeContextMenu.setOnShown(event -> hideAllPopups(playModeContextMenu));
        }
        return playModeContextMenu;
    }

    public VisualContextMenu getVisualContextMenu() {
        if(visualContextMenu == null) {
            visualContextMenu = new VisualContextMenu(getMainStage());
            visualContextMenu.setOnShown(event -> hideAllPopups(visualContextMenu));
        }
        return visualContextMenu;
    }

    public LyricContextMenu getLyricContextMenu() {
        if(lyricContextMenu == null) {
            lyricContextMenu = new LyricContextMenu(getMainStage());
            lyricContextMenu.setOnShown(event -> hideAllPopups(lyricContextMenu));
        }
        return lyricContextMenu;
    }

    public PopMenu getLyricDesktopTextSettingsPopMenu() {
        if(lyricDesktopTextSettingsPopMenu == null) {
            lyricDesktopTextSettingsPopMenu = new PopMenu(
                    getContext().getStageManager().getLyricDesktopStage());
            lyricDesktopTextSettingsPopMenu.setShowStrategy(new ShowUnderItemStrategy(0, 4));
            //lyricDesktopTextSettingsPopMenu.setAlwaysOnTop(true);
            lyricDesktopTextSettingsPopMenu.setMenuList(getMenuTemplates().getLyricDesktopTextSettingsMenu(true));
            lyricDesktopTextSettingsPopMenu.setOnShown(event -> hideAllPopups(lyricDesktopTextSettingsPopMenu));
        }
        return lyricDesktopTextSettingsPopMenu;
    }

    public LyricDesktopContextMenu getLyricDesktopContextMenu() {
        if(lyricDesktopContextMenu == null) {
            lyricDesktopContextMenu = new LyricDesktopContextMenu(
                    getContext().getStageManager().getLyricDesktopStage());
            lyricDesktopContextMenu.setAlwaysOnTop(true);
            lyricDesktopContextMenu.setOnShown(event -> hideAllPopups(lyricDesktopContextMenu));
        }
        return lyricDesktopContextMenu;
    }


    public MenuManager toggleAppMainContextMenu(Position position) {
        PopMenu menu = getAppMainContextMenu();
        if(menu.isShowing()) {
            menu.hide();
            return this;
        }
        menu.show();
        ShowStrategy strategy = menu.getShowStrategy();
        if(strategy != null) {
            Position newPosition = strategy.adjustPosition(position, null, menu);
            menu.setupPosition(newPosition);
        }
        return this;
    }

    public MenuManager hidePlaybackQueueNavigationContextMenu() {
        return hideMenu(playbackQueueNavigationContextMenu);
    }

    public MenuManager hidePlayModeContextMenu() {
        return hideMenu(playModeContextMenu);
    }

    public MenuManager hideLyricContextMenu() {
        return hideMenu(lyricContextMenu);
    }

    public MenuManager hideLyricDesktopTextGradientContextMenuContextMenu() {
        return hideMenu(lyricDesktopTextSettingsPopMenu);
    }

    public MenuManager hideAppMainMenu() {
        return hideMenu(appMainContextMenu);
    }

    public MenuManager hideTrackContextMenu() {
        return hideMenu(trackContextMenu);
    }

    public MenuTemplates getMenuTemplates() {
        if (menuTemplates == null) {
            menuTemplates = new MenuTemplates(getContext());
        }
        return menuTemplates;
    }

    public void setupSystemTray() {
        if(!SystemTray.isSupported()) {
            return ;
        }
        GeneralOptions options = getContext().getConfiguration().getGeneralOptions();
        try {
            if(!options.isAllowTrayIcon()) {
                getContext().runTask(() -> {
                    SystemTray.getSystemTray().remove(trayIcon);
                    trayIcon = null;
                });
                return ;
            } else if (trayIcon != null) {
                return ;
            }

            //Platform.setImplicitExit(false);
            Image image = new ImageIcon(getImageUrl("TTPlayer.png")).getImage();
            trayIcon = new TrayIcon(image);
            trayIcon.setImageAutoSize(true);
            //trayIcon.setPopupMenu(menu);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    e.consume();
                    if (e.getClickCount() > 1){
                        runFx(() -> getMainStage().setIconified(false));
                    } else if(e.getButton() == MouseEvent.BUTTON3) {
                        runFx(() -> toggleAppMainContextMenu(new Position(e.getX() - 16, e.getY())));
                    }
                }
            });
            SystemTray.getSystemTray().add(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPopMenuShowing(PopMenu menu) {
        return menu != null && menu.isShowing();
    }

    public boolean isLyricDesktopContextMenuShowing() {
        return isPopMenuShowing(lyricDesktopContextMenu);
    }

    public boolean isLyricDesktopTextSettingsPopMenuShowing() {
        return isPopMenuShowing(lyricDesktopTextSettingsPopMenu);
    }

    public boolean isAppMainMenuShowing() {
        return isPopMenuShowing(appMainContextMenu);
    }
}
