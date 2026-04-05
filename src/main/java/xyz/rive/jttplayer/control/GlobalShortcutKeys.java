package xyz.rive.jttplayer.control;

import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import xyz.rive.jttplayer.ApplicationContext;

import java.awt.*;

import static xyz.rive.jttplayer.util.StringUtils.containsIgnoreCase;

public class GlobalShortcutKeys implements NativeKeyListener {
    private final ApplicationContext context;

    public GlobalShortcutKeys(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent event) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        //jnativehook - Fix Bugs
        //event.getSource()获取了个寂寞
        if(context.getPlayerManager().isIgnoreGlobalKeys()) {
            return ;
        }

        int keyCode = event.getKeyCode();
        if(keyCode == NativeKeyEvent.VC_SPACE) {
            context.getPlayerManager().togglePlay();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent event) {

    }
}
