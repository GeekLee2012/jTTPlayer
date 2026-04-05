package xyz.rive.jttplayer.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import xyz.rive.jttplayer.util.JsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

import static xyz.rive.jttplayer.util.FileUtils.*;
import static xyz.rive.jttplayer.util.StringUtils.getAppDataPath;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {

    public Configuration() {
        initDefaultPaths();
    }

    public String getWorkPath() {
        return getAppDataPath("Work");
    }

    private void initDefaultPaths() {
        Arrays.asList("Skin", "Work").forEach(name -> {
            Path path = Paths.get(getAppDataPath(name));
            if(!Files.exists(path) || !Files.isDirectory(path)) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static String getConfigPath() {
        return getAppDataPath("default.cfg");
    }

    public static Configuration load() {
        Configuration config = null;
        try {
            String text = readText(getConfigPath());
            config = JsonUtils.parseJson(text, Configuration.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(config == null) {
            config = new Configuration();
        }
        return config;
    }

    public void save() {
        try {
            writeText(getConfigPath(), JsonUtils.stringify(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****** Options ******/
    private GeneralOptions generalOptions;
    private PlayOptions playOptions;
    private PlaybackQueueOptions playbackQueueOptions;
    private EqualizerOptions equalizerOptions;
    private PlayerOptions playerOptions;
    private LyricOptions lyricOptions;
    private LyricSearchOptions lyricSearchOptions;


    public GeneralOptions getGeneralOptions() {
        if(generalOptions == null) {
            generalOptions = new GeneralOptions();
        }
        return generalOptions;
    }

    public void setGeneralOptions(GeneralOptions generalOptions) {
        this.generalOptions = generalOptions;
    }

    public PlayOptions getPlayOptions() {
        if(playOptions == null) {
            setPlayOptions(new PlayOptions());
        }
        return playOptions;
    }

    public void setPlayOptions(PlayOptions playOptions) {
        this.playOptions = playOptions;
    }

    public PlaybackQueueOptions getPlaybackQueueOptions() {
        if(playbackQueueOptions == null) {
            playbackQueueOptions = new PlaybackQueueOptions();
        }
        return playbackQueueOptions;
    }

    public void setPlaybackQueueOptions(PlaybackQueueOptions playbackQueueOptions) {
        this.playbackQueueOptions = playbackQueueOptions;
    }

    public EqualizerOptions getEqualizerOptions() {
        if(equalizerOptions == null) {
            setEqualizerOptions(new EqualizerOptions());
        }
        return equalizerOptions;
    }

    public void setEqualizerOptions(EqualizerOptions equalizerOptions) {
        this.equalizerOptions = equalizerOptions;
    }

    public PlayerOptions getPlayerOptions() {
        if(playerOptions == null) {
            setPlayerOptions(new PlayerOptions());
        }
        return playerOptions;
    }

    public void setPlayerOptions(PlayerOptions playerOptions) {
        this.playerOptions = playerOptions;
    }

    public LyricOptions getLyricOptions() {
        if(lyricOptions == null) {
            lyricOptions = new LyricOptions();
        }
        return lyricOptions;
    }

    public void setLyricOptions(LyricOptions lyricOptions) {
        this.lyricOptions = lyricOptions;
    }

    public LyricSearchOptions getLyricSearchOptions() {
        if (lyricSearchOptions == null) {
            lyricSearchOptions = new LyricSearchOptions();
        }
        return lyricSearchOptions;
    }

    public void setLyricSearchOptions(LyricSearchOptions lyricSearchOptions) {
        this.lyricSearchOptions = lyricSearchOptions;
    }
}
