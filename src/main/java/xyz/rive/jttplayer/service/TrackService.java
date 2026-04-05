package xyz.rive.jttplayer.service;

import com.github.houbb.heaven.reflect.api.IField;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.common.*;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.concurrent.Future;

import static xyz.rive.jttplayer.common.Constants.*;
import static xyz.rive.jttplayer.util.FileUtils.*;
import static xyz.rive.jttplayer.util.FileUtils.readLines;
import static xyz.rive.jttplayer.util.StringUtils.*;

public class TrackService {
    private final ApplicationContext context;

    public TrackService(ApplicationContext context) {
        this.context = context;
    }

    public Track parseTrack(File file) {
        if(file == null) {
            return null;
        }
        try {
            MetadataService service = context.getMetadataService();
            Metadata metadata = service.read(file);
            if(metadata == null) {
                return null;
            }

            Track track = new Track();
            track.setUrl(file.getAbsolutePath());
            track.setFileSize(file.length());
            track.setFileName(guessSimpleName(file.getName()));
            track.setExtName(guessExtName(file.getName()));
            syncMetadataToTrack(metadata, track);
            return track;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Track parseTrack(String url) {
        return parseTrack(new File(url));
    }

    public void syncMetadataToTrack(Metadata metadata, Track track) {
        if(track == null || metadata == null) {
            return ;
        }
        //track.setId(String.valueOf(UUID.randomUUID()));
        track.setTitle(metadata.getTitle());
        track.setArtist(metadata.getArtist());
        track.setAlbum(metadata.getAlbum());
        //track.setUrl(file.getAbsolutePath());
        track.setTrackLength(metadata.getTrackLength());
        //track.setFileSize(file.length());
        //track.setFileName(guessFileName(file.getName()));
        //track.setExtName(guessExtName(file.getName()));
        track.setGenre(metadata.getGenre());
        track.setComment(metadata.getComment());
        track.setFormat(metadata.getFormat());
        track.setSampleRate(metadata.getSampleRate());
        track.setBitRate(metadata.getBitRate());
        track.setEncodingType(metadata.getEncodingType());
        track.setChannels(metadata.getChannels());
        track.setDate(metadata.getYear());
        track.setRating(metadata.getRating());
        track.setTrackNumber(metadata.getTrackNumber());
        //track.setQuality(metadata.getQuality());

        if(isEmpty(track.getTitle())) {
            track.setTitle(track.getFileName());
        }
    }

    public void appendToPlaybackQueue(List<File> files) {
        if(files != null && files.size() == 1) {
            if(files.get(0).isDirectory()) {
                appendToPlaybackQueue(files.get(0));
                return ;
            }
        }
        context.getAsyncService().submit(() -> {
            Optional.ofNullable(files).ifPresent(__ -> {
                List<Track> tracks = new ArrayList<>();
                for (File file : files) {
                    Optional.ofNullable(parseTrack(file))
                            .ifPresent(tracks::add);
                }
                if(!tracks.isEmpty()) {
                    context.getPlayerManager().appendToPlaybackQueue(tracks);
                }
            });
        });
    }

    public void appendToPlaybackQueue(File file) {
        Optional.ofNullable(listAudioFiles(file)).ifPresent(files -> {
            if(files.length > 0) {
                appendToPlaybackQueue(Arrays.asList(files));
            }
        });
    }

    public boolean isSupportedAudios(File file) {
        if(file == null || !file.exists() || file.isDirectory()) {
            return false;
        }
        String extName = guessExtName(file.getName());
        return AUDIO_SUFFIXES.contains(".".concat(extName));
    }

    public File[] listAudioFiles(File file) {
        if(file == null || !file.exists()) {
            return null;
        }
        File[] files = null;
        if(file.isDirectory()) {
            files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if(file.isDirectory()) {
                        return false;
                    }
                    String extName = guessExtName(file.getName());
                    return AUDIO_SUFFIXES.contains(".".concat(extName));
                }
            });
        } else {
            files = new File[1];
            files[0] = file;
        }
        return files;
    }

    public Future<?> loadLyricAsync(Track track) {
        return context.getAsyncService().submit(() -> loadLyric(track));
    }

    public void loadLyricSync(Track track) {
        try {
            Future<?> future = loadLyricAsync(track);
            future.get();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    //优先级顺序：内嵌歌词 -> 关联歌词 -> 本地歌词 -> 在线歌词
    public void loadLyric(Track track) {
        Optional.ofNullable(track).ifPresent(__ -> {
            if (track.hasLyric() || track.hasEmbedLyric()) {
                return ;
            }

            String url = track.getUrl();
            //内嵌歌词
            String embedLyric = context.getMetadataService().readLyric(url);
            Lyric lyric = Lyric.parseFromText(embedLyric);
            if (lyric.hasData()) {
                track.setLyricEmbed(lyric);
                return ;
            }
            //根据配置搜索歌词
            LyricSearchOptions options = context.getConfiguration()
                    .getLyricSearchOptions();
            for (ItemOrder order : options.prepareSearchOrders()) {
                loadLyricFromPath(track, transformLyricSearchPath(track, order.getPath()));
                if (track.hasLyric()) {
                    break;
                }
            }
            //在线搜索
        });
    }

    private String transformLyricSearchPath(Track track, String path) {
        if (contentEquals(path, TRACK_DIR_PLACEHOLDER)) {
            return track.getParentUrl();
        } else if (contentEquals(path, LYRIC_DOWNLOAD_DIR_PLACEHOLDER)) {
            return context.getConfiguration()
                    .getLyricSearchOptions()
                    .getDownloadPath();
        }
        return path;
    }

    private void loadLyricFromPath(Track track, String path) {
        if (track == null
                || track.hasLyric()
                || isEmpty(path)) {
            return;
        }
        //歌词文件名称
        String shortName = track.getShortFileName();
        String suffix = ".lrc";
        String lrcUrl = String.format("%s/%s%s", path, shortName, suffix);
        //歌词文件不存在
        if (!exists(lrcUrl)) {
            return ;
        }
        Lyric lyric = Lyric.parseFromLines(readLines(lrcUrl));
        //歌词不存在/无效
        if (!lyric.hasData()) {
            return ;
        }
        track.setLyric(lyric);
        //歌词翻译，对多空格进行兼容处理
        String transLrcUrl;
        for (int i = 0; i < 12; i++) {
            transLrcUrl = String.format("%s/%s", path, shortName)
                    .concat(generateSpacing(i))
                    .concat("[Trans]").concat(suffix);
            if(exists(transLrcUrl)) {
                track.setLyricTrans(
                        Lyric.parseFromLines(readLines(transLrcUrl))
                );
                break ;
            }
        }
    }

}
