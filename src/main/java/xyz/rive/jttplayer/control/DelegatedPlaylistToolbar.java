package xyz.rive.jttplayer.control;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.controller.PlaybackQueueController;
import xyz.rive.jttplayer.menu.MenuMeta;
import xyz.rive.jttplayer.menu.PopMenu;

import java.util.List;

public class DelegatedPlaylistToolbar extends HBox {

    public DelegatedPlaylistToolbar() {
        setup();
    }

    private PlaybackQueueController getPlaybackQueueController() {
        ApplicationContext context = ApplicationContext.getInstance();
        context.getStageManager().getPlaybackQueueStage(); //初始化
        return context.getControllerManager()
                .getController(PlaybackQueueController.class);
    }

    private PopMenu getMenuBarPopMenu() {
        return getPlaybackQueueController().getMenuBarPopMenu();
    }

    public void setup() {
        for (int i = 0; i < 7; i++) {
            int index = i;
            HBox item = new HBox(new Region());
            item.getStyleClass().setAll("item_" + (index + 1));
            item.setOnMouseClicked(event ->
                    buildMenuBarPopMenu(index)
                            .toggle(event));

            item.setOnMouseEntered(event -> {
                if(getMenuBarPopMenu().isShowing()) {
                    buildMenuBarPopMenu(index).show(event);
                }
            });
            getChildren().add(item);
        }
    }

    public PopMenu buildMenuBarPopMenu(int index) {
        List<MenuMeta> list = null;
        if(index == 0) {
            list = getPlaybackQueueController().getAddMenu();
        } else if(index == 1) {
            list = getPlaybackQueueController().getRemoveMenu();
        } else if(index == 2) {
            list = getPlaybackQueueController().getListMenu();
        } else if(index == 3) {
            list = getPlaybackQueueController().getSortMenu();
        } else if(index == 4) {
            list = getPlaybackQueueController().getSearchMenu();
        } else if(index == 5) {
            list = getPlaybackQueueController().getEditMenu();
        } else if(index == 6) {
            list = getPlaybackQueueController().getPlayModeMenu();
        }
        return getMenuBarPopMenu()
                .setMenuList(list);
    }

}
