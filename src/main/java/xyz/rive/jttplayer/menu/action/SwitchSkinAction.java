package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.skin.SkinXml;

public class SwitchSkinAction extends AbstractMenuAction {
    private final String sknFilename;
    private final SkinXml skin;

    public SwitchSkinAction(String sknFilename, SkinXml skin) {
        this.sknFilename = sknFilename;
        this.skin = skin;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);
        getPlayerManager().setActiveSkin(sknFilename);
    }

}
