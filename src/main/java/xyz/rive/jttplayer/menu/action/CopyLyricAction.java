package xyz.rive.jttplayer.menu.action;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;

import java.util.Optional;

import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

public class CopyLyricAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        Optional.ofNullable(getPlayerManager().getCurrentTrack()).ifPresent(track -> {
            String lyric = null;
            if(track.hasLyric()) {
                lyric = track.getLyricText();
            }
            if(isEmpty(lyric) && track.hasEmbedLyric()) {
                lyric= track.getLyricEmbedText();
            }
            if(isEmpty(lyric)) {
                return ;
            }
            int lyricZhType = getContext().getConfiguration()
                    .getPlayerOptions()
                    .getLyricZhType();
            if(lyricZhType > 1) {
                lyric = ZhConverterUtil.toTraditional(lyric);
            } else {
                lyric = ZhConverterUtil.toSimple(lyric);
            }
            ClipboardContent content = new ClipboardContent();
            content.putString(lyric);

            Clipboard.getSystemClipboard()
                    .setContent(content);
        });
    }

}
