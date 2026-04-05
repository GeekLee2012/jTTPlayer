package xyz.rive.jttplayer.menu.action;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import javafx.scene.input.MouseEvent;

import java.util.Optional;

import static xyz.rive.jttplayer.util.StringUtils.isEmpty;

public class AddEmbedLyricForTrackAction extends AbstractMenuAction {

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        Optional.ofNullable(getPlayerManager().getCurrentTrack())
                .ifPresent(track -> {
                    String lyric = track.getLyricText();
                    if (isEmpty(lyric)) {
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
                    getContext().getMetadataService()
                            .writeLyric(track.getUrl(), lyric);
        });

    }

}
