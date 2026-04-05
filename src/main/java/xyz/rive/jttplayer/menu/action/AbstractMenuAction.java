package xyz.rive.jttplayer.menu.action;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.ActionSource;
import xyz.rive.jttplayer.manager.ControllerManager;
import xyz.rive.jttplayer.manager.MenuManager;
import xyz.rive.jttplayer.manager.PlayerManager;
import xyz.rive.jttplayer.manager.StageManager;

import static xyz.rive.jttplayer.common.ActionSource.*;

@SuppressWarnings("unchecked")
public abstract class AbstractMenuAction implements EventHandler<MouseEvent> {
    protected final ActionSource actionSource;

    public AbstractMenuAction() {
        this(NavMenu);
    }

    public AbstractMenuAction(ActionSource actionSource) {
        this.actionSource = actionSource;
    }

    protected ApplicationContext getContext() {
        return ApplicationContext.getInstance();
    }

    protected StageManager getStageManager() {
        return getContext().getStageManager();
    }

    protected ControllerManager getControllerManager() {
        return getContext().getControllerManager();
    }

    protected MenuManager getMenuManager() {
        return getContext().getMenuManager();
    }

    protected PlayerManager getPlayerManager() {
        return getContext().getPlayerManager();
    }

    @Override
    public void handle(MouseEvent event) {
        event.consume();
        getMenuManager().hideAllPopups();
    }

    public void postHandle(Node item) {
    }

    protected <T> T getContextMenuData(Class<T> target) {
        try {
            Object trigger = getContext().getContextMenuTrigger();
            if(trigger instanceof Node) {
                Node node = (Node) trigger;
                Object data = node.getUserData();
                if(data != null && data.getClass() == target) {
                    return (T) data;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected boolean isContextMenuAction() {
        return actionSource == ContextMenu;
    }

    protected boolean isAppMainMenuAction() {
        return actionSource == AppMainMenu;
    }

}
