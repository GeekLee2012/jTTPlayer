package xyz.rive.jttplayer.menu.action;

import javafx.scene.input.MouseEvent;
import xyz.rive.jttplayer.common.Track;

import java.io.File;
import java.util.Optional;

import static xyz.rive.jttplayer.util.FileUtils.guessSimpleName;
import static xyz.rive.jttplayer.util.FileUtils.transformPath;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.trim;

public class RenameFileAction extends AbstractMenuAction {
    private final String format;

    public RenameFileAction(String format) {
        this.format = format;
    }

    @Override
    public void handle(MouseEvent event) {
        super.handle(event);

        Optional.ofNullable(getContextMenuData(Track.class))
                .ifPresent(track -> {
                    String url = trim(track.getUrl());
                    if(url.startsWith("http")) {
                        return ;
                    }
                    File file = new File(url);
                    if(!file.exists() || !file.isFile()) {
                        return ;
                    }

                    String newName = getFileName(track, format);
                    if(isEmpty(newName)) {
                        return ;
                    }
                    String parent = trim(file.getParentFile().getAbsolutePath());
                    String newUrl = transformPath(String.format("%s/%s", parent, newName));
                    try {
                        File dest = new File(newUrl);
                        if(file.renameTo(dest)) {
                            track.setUrl(newUrl);
                            track.setFileName(guessSimpleName(dest.getName()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private String getFileName(Track track, String format) {
        if("歌曲名.扩展名".equals(format)) {
            String title = trim(track.getTitle());
            String extName = trim(track.getExtName());
            if(isEmpty(title) || isEmpty(extName)) {
                return null;
            }
            return String.format("%s.%s", title, extName);
        } else if("歌曲名 - 歌手.扩展名".equals(format)) {
            String title = trim(track.getTitle());
            String artist = trim(track.getArtist());
            String extName = trim(track.getExtName());
            if(isEmpty(title) || isEmpty(artist) || isEmpty(extName)) {
                return null;
            }
            return String.format("%s - %s.%s", title, artist, extName);
        } else if("歌手 - 歌曲名.扩展名".equals(format)) {
            String title = trim(track.getTitle());
            String artist = trim(track.getArtist());
            String extName = trim(track.getExtName());
            if(isEmpty(title) || isEmpty(artist) || isEmpty(extName)) {
                return null;
            }
            return String.format("%s - %s.%s", artist, title, extName);
        }
        return null;
    }

}
