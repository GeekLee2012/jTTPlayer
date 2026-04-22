package xyz.rive.jttplayer;


import javafx.application.Application;
import javafx.stage.*;
import xyz.rive.jttplayer.common.*;
import xyz.rive.jttplayer.manager.*;
import xyz.rive.jttplayer.service.AsyncService;
import xyz.rive.jttplayer.service.MetadataService;
import xyz.rive.jttplayer.service.PlaybackQueueService;
import xyz.rive.jttplayer.service.TrackService;
import xyz.rive.jttplayer.skin.SkinXml;
import xyz.rive.jttplayer.skin.StandaloneXml;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;


public class ApplicationContext {
    private static ApplicationContext SINGLETON = null;
    private static Configuration configuration;

    private AsyncService asyncService;
    private MetadataService metadataService;
    private TrackService trackService;
    private PlaybackQueueService playbackQueueService;

    private StageManager stageManager;
    private MenuManager menuManager;
    private ControllerManager controllerManager;
    private PlayerManager playerManager;
    private SkinManager skinManager;
    private TssManager tssManager;

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    private Application app;

    private ApplicationContext() {

    }

    public static ApplicationContext getInstance() {
        if (SINGLETON == null) {
            SINGLETON = new ApplicationContext();
        }
        return SINGLETON;
    }

    public Configuration getConfiguration() {
        if (configuration == null) {
            configuration = Configuration.load();
        }
        return configuration;
    }

    public Stage getMainStage() {
        return getStageManager().getMainStage();
    }

    public void start(Stage mainStage) {
        getStageManager().setMainStage(mainStage)
                .setupStagesByMode(true);
        getPlayerManager().setupExitHook()
                .registerStageKeys(mainStage);
        //setupShortcutKeys();
        getMenuManager().setupSystemTray();
        getPlayerManager().restore();
    }


    public AsyncService getAsyncService() {
        if(asyncService == null) {
            asyncService = new AsyncService();
        }
        return asyncService;
    }

    public MetadataService getMetadataService() {
        if(metadataService == null) {
            metadataService = new MetadataService();
        }
        return metadataService;
    }

    public TrackService getTrackService() {
        if(trackService == null) {
            trackService = new TrackService(this);
        }
        return trackService;
    }

    public PlaybackQueueService getPlaybackQueueService() {
        if(playbackQueueService == null) {
            playbackQueueService = new PlaybackQueueService(this);
        }
        return playbackQueueService;
    }

    public StageManager getStageManager() {
        if (stageManager == null) {
            stageManager = new StageManager(this);
        }
        return stageManager;
    }

    public MenuManager getMenuManager() {
        if (menuManager == null) {
            menuManager = new MenuManager(this);
        }
        return menuManager;
    }

    public ControllerManager getControllerManager() {
        if(controllerManager == null) {
            controllerManager = new ControllerManager(this);
        }
        return controllerManager;
    }

    public PlayerManager getPlayerManager() {
        if(playerManager == null) {
            playerManager = new PlayerManager(this);
        }
        return playerManager;
    }

    public SkinManager getSkinManager() {
        if(skinManager == null) {
            skinManager = new SkinManager(this);
        }
        return skinManager;
    }

    public TssManager getTssManager() {
        if(tssManager == null) {
            tssManager = new TssManager(this);
        }
        return tssManager;
    }


    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public ApplicationContext removeAttribute(String key) {
        attributes.remove(key);
        return this;
    }

    public Track getFileAttributesTrack() {
        return (Track) getAttribute("fileAttributesTrack");
    }

    public void setFileAttributesTrack(Track track) {
        setAttribute("fileAttributesTrack", track);
    }

    public void setupContextMenuTrigger(Object trigger) {
        setAttribute("contextMenuTrigger", trigger);
    }

    public Object getContextMenuTrigger() {
        return getAttribute("contextMenuTrigger");
    }

    public ScheduledFuture<?> runDelay(Runnable task, long millis) {
        return getAsyncService().scheduleDelayMillis(task, millis);
    }

    public ScheduledFuture<?> runEachDelay(Runnable task, long delay) {
        return getAsyncService().scheduleWithFixedDelay(task, 0, delay);
    }

    public Future<?> runTask(Runnable task) {
        return getAsyncService().submit(task);
    }


    public SkinXml getActiveSkinXml() {
        String activeSkinName = getConfiguration().getPlayerOptions()
                .getActiveSkinName();
        return getSkinManager().getSkinXml(activeSkinName);
    }

    public StandaloneXml getActiveLyricXml() {
        String activeSkinName = getConfiguration().getPlayerOptions()
                .getActiveSkinName();
        return getSkinManager().getLyricXml(activeSkinName);
    }

    public StandaloneXml getActiveVisualXml() {
        String activeSkinName = getConfiguration().getPlayerOptions()
                .getActiveSkinName();
        return getSkinManager().getVisualXml(activeSkinName);
    }

    public StandaloneXml getActivePlaylistXml() {
        String activeSkinName = getConfiguration().getPlayerOptions()
                .getActiveSkinName();
        return getSkinManager().getPlaylistXml(activeSkinName);
    }

    public String getWorkPath() {
        return getConfiguration().getWorkPath();
    }

    public ApplicationContext setApplication(Application app) {
        this.app = app;
        return this;
    }

    public void browseUrl(String url) {
        if (app != null) {
            app.getHostServices().showDocument(url);
        }
    }

}
