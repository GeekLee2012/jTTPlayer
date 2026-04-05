package xyz.rive.jttplayer.menu.strategy;

import javafx.scene.input.InputEvent;
import xyz.rive.jttplayer.common.Bound;
import xyz.rive.jttplayer.common.Position;
import xyz.rive.jttplayer.menu.PopMenu;

import static xyz.rive.jttplayer.util.FxUtils.getScreenBound;

public interface ShowStrategy {
    Position getPosition(InputEvent event, PopMenu popMenu);

    default  Position adjustPosition(Position position, InputEvent event, PopMenu popMenu) {
        double nX = position.x();
        double nY = position.y();
        Bound bound = getScreenBound();
        double width = bound.getWidth();
        double height = bound.getHeight();
        double offsetWidth = nX + popMenu.getWidth() - width;
        //右侧溢出
        if(offsetWidth >= 20) {
            nX = nX - popMenu.getWidth();
            PopMenu parentPopMenu = popMenu.getParentPopMenu();
            if (parentPopMenu != null) {
                double sloganWidth = parentPopMenu.isSloganEnable() ? 27 : 0;
                nX = nX - parentPopMenu.getWidth() + sloganWidth + 3;
            }
        }
        //再次确认
        offsetWidth = nX + popMenu.getWidth() - width;
        if(offsetWidth >= 20) {
            nX = nX - popMenu.getWidth();
            PopMenu parentPopMenu = popMenu.getParentPopMenu();
            if (parentPopMenu != null) {
                nX = nX - parentPopMenu.getWidth() + 3;
            }
        }

        double offsetHeight = nY + popMenu.getHeight() - height;
        //显示一半在当前鼠标位置之上
        if(offsetHeight >= -88) {
            nY = nY - popMenu.getHeight() / 2D + 28;
        }
        //全部显示在当前鼠标位置之上
        offsetHeight = nY + popMenu.getHeight() - height;
        if(offsetHeight >= - 88) {
            nY = nY - popMenu.getHeight() / 2D;
        }
        return new Position(nX, nY);
    }
}
