package xyz.rive.jttplayer.player.mpv;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.newsclub.net.unix.AFUNIXSelectorProvider;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import xyz.rive.jttplayer.player.AbastractPlayer;
import xyz.rive.jttplayer.player.Player;
import xyz.rive.jttplayer.common.Pair;
import xyz.rive.jttplayer.common.Track;
import xyz.rive.jttplayer.util.JsonUtils;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static xyz.rive.jttplayer.util.FileUtils.transformPath;
import static xyz.rive.jttplayer.util.FxUtils.isWindows;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.trim;

public class MpvPlayer extends AbastractPlayer {
    public static final String DEFAULT_SOCKET_NAME = "/tmp/jttplayer-mpv.sock";
    public static final String DEFAULT_SOCKET_NAME_WINDOWS = "\\\\.\\pipe\\jttplayer-mpvserver";
    private final StringProperty socketNameProperty = new SimpleStringProperty();
    private IpcChannel ipcChannel;
    private Process mpvCommadProcess;
    private IpcCommand ipcCommand;
    private boolean debug = false;
    private boolean loaded = false;
    private double pendingSeek = -1;
    private boolean serverReady = false;
    private boolean clientReady = false;

    public MpvPlayer() {
        super();
        socketNameProperty.addListener((__, oldValue, newValue) -> setup());
        setSocketFile(isWindows() ? DEFAULT_SOCKET_NAME_WINDOWS : DEFAULT_SOCKET_NAME);
    }

    private void setSocketFile(String filename) {
        socketNameProperty.set(filename);
    }

    private boolean setServerReady(boolean value) {
        serverReady = value;
        return serverReady;
    }

    private boolean setClientReady(boolean value) {
        clientReady = value;
        return clientReady;
    }

    protected Future<Boolean> setup() {
        CompletableFuture<Boolean> setupFuture = new CompletableFuture<>();
        if (isReady()) {
            setupFuture.complete(true);
            return setupFuture;
        }
        //Unready
        if(isEmpty(getPlayCorePath()) || isEmpty(socketNameProperty.get())) {
            setupFuture.complete(false);
            return setupFuture;
        }
        Future<Boolean> serverFuture = serverReady ? null : startIpcServer();
        runTask(() -> {
            try {
                if(setServerReady(serverFuture != null ? serverFuture.get() : true)) {
                    if (debug) {
                        System.out.println(">>> IPC Server Started");
                    }
                    Future<Boolean> clientFuture = startIpcClient();
                    runTask(() -> {
                        try {
                            if(setClientReady(clientFuture.get())) {
                                if (debug) {
                                    System.out.println(">>> IPC Client Started");
                                }
                                ipcCommand = new IpcCommand(ipcChannel);
                                setupFuture.complete(true);
                            }
                        } catch (Exception e) {
                           e.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return setupFuture;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        ipcCommand.setDebug(debug);
    }

    private void setLoaded(boolean value) {
        this.loaded = value;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void setCurrentTrack(Track currentTrack) {
        super.setCurrentTrack(currentTrack);
        this.setLoaded(false);
    }

    public double getPendingSeek() {
        return pendingSeek;
    }

    public void setPendingSeek(double value) {
        this.pendingSeek = value;
    }

    private String getCurrentUrl() {
        return currentTrack != null ? currentTrack.getUrl() : null;
    }

    public Future<Boolean> startIpcServer() {
        String socketFileName = socketNameProperty.get();
        String mpvBinary = trim(getPlayCorePath());

        if(!isWindows()) {
            Path socketPath = Paths.get(socketFileName);
            try {
                Files.deleteIfExists(socketPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String[] cmdArray = {
                mpvBinary,
                "--idle",
                "--no-audio-display",
                "--no-terminal",
                "--no-video",
                "--really-quiet",
                "--input-ipc-server=".concat(socketFileName)
        };
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        new Thread(() -> {
            try {
                mpvCommadProcess = Runtime.getRuntime().exec(cmdArray);
                Thread.sleep(1688);
                future.complete(true);
                mpvCommadProcess.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
                future.complete(false);
                try {
                    if (mpvCommadProcess != null) {
                        mpvCommadProcess.destroyForcibly();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }, "CommandServer").start();
        return future;
    }

    private void closeIpcChannel() throws IOException {
        if (ipcChannel != null) {
            ipcChannel.close();
        }
    }

    private Future<Boolean> openIpcChannel(String socketFileName) throws Exception {
        closeIpcChannel();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        runTask(() -> {
            try {
                future.complete(isWindows() ?
                        openPipeChanel(socketFileName) :
                        openSocketChannel(socketFileName)
                );
            } catch (Exception e) {
                e.printStackTrace();
                future.complete(false);
            }
        });
        return future;
    }

    private boolean openSocketChannel(String socketFileName) throws Exception {
        AFUNIXSelectorProvider provider = AFUNIXSelectorProvider.provider();
        SocketChannel channel = provider.openSocketChannel();
        ipcChannel = new SocketIpcChannel(channel);
        return ipcChannel.connect(socketFileName);
    }

    private boolean openPipeChanel(String socketFileName) throws IOException {
        ipcChannel = new NamedPipeIpcChannel(socketFileName);
        return ipcChannel.connect(socketFileName);
    }

    private Future<Boolean> startIpcClient() throws Exception {
        CompletableFuture<Boolean> clientFuture = new CompletableFuture<>();
        Future<Boolean> channelFuture = openIpcChannel(socketNameProperty.get());
        runTask(() -> {
            try {
                if (channelFuture.get()) {
                    setRunning(true);
                    startReceiver();
                    clientFuture.complete(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                clientFuture.complete(false);
            }
        });
        return clientFuture;
    }

    private void startReceiver() {
        new Thread(() -> {
            while(running) {
                try {
                    readSocketMessage(ipcChannel)
                            .ifPresent(this::processMessages);
                    if(isWindows()) {
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "MessageReceiver").start();
    }

    private void processMessages(String message) {
        String[] lines = message.split("\n");

        boolean hasTimePosMessage = message.contains("time-pos");
        if(debug) {
            if(!hasTimePosMessage || lines.length != 1) {
                System.out.println("\n[Message]\n".concat(message));
            }
        }

        List<IpcResult> ipcResults = new ArrayList<>();
        for(String line : lines) {
            IpcResult result = processMessageLine(line);
            if(result != null) {
                ipcResults.add(result);
            }
        }

        for (IpcResult result : ipcResults) {
            String event = result.getEvent();
            if("property-change".equalsIgnoreCase(event)) {
                Consumer<Object> stateListener = stateChangedListener;
                String name = result.getName();
                Object data = result.getData();
                Object param;
                if("time-pos".equalsIgnoreCase(name)) {
                    param = data;
                    setTimePosition(data == null ? -1 : (double) data);
                    stateListener = timePositionListener;
                } else if("duration".equalsIgnoreCase(name)) {
                    duration = data == null ? -1 : (double) data;
                    continue;
                } else if("volume".equalsIgnoreCase(name)) {
                    volume = data == null ? 100 : (double) data;
                    param = new Pair(name, data);
                } else if("mute".equalsIgnoreCase(name)) {
                    mute = (boolean) data;
                    param = new Pair(name, data);
                } else if("pause".equalsIgnoreCase(name)) {
                    Optional.ofNullable(pausedListener)
                            .ifPresent(listener -> listener.accept((Boolean) data));
                    continue;
                } else {
                    param = new Pair(name, data);
                }
                Optional.ofNullable(stateListener).ifPresent(
                        listener -> listener.accept(param));
                continue ;
            }
            else if("idle".equalsIgnoreCase(event)) {
                Optional.ofNullable(stoppedListener)
                        .ifPresent(listener -> listener.accept(true));
            } else if("playback-restart".equalsIgnoreCase(event)) {
                Optional.ofNullable(startedListener)
                        .ifPresent(listener -> listener.accept(true));
            } else if("pause".equalsIgnoreCase(event)) {
                Optional.ofNullable(pausedListener)
                        .ifPresent(listener -> listener.accept(true));
            } else if("unpause".equalsIgnoreCase(event)) {
                Optional.ofNullable(pausedListener)
                        .ifPresent(listener -> listener.accept(false));
            }

            Integer requestId = result.getRequest_id();
            if(requestId == null) {
                requestId = result.getId();
            }
            if(requestId != null) {
                CompletableFuture<Object> future = (CompletableFuture<Object>) ipcCommand.getRequestTask(requestId);
                if(future != null) {
                    Object data = result.getData();
                    future.complete(data);
                    ipcCommand.removeRequestTask(requestId);
                }
            }
        }
    }

    private IpcResult processMessageLine(String message) {
        try {
            return JsonUtils.parseJson(message, IpcResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Optional<String> readSocketMessage(IpcChannel channel) throws Exception {
        return Optional.ofNullable(channel.read());
    }

    private void execCommand(Runnable runnable) {
        Optional.ofNullable(ipcCommand).ifPresent(__ -> runnable.run());
    }

    @Override
    public void setVolume(double value) {
        if(ipcCommand == null) {
            return;
        }

        super.setVolume(value);
        ipcCommand.setProperty("volume", volume);
    }

    @Override
    public Player onTimePosition(Consumer<Object> listener) {
        super.onTimePosition(listener);
        if(ipcCommand != null) {
            ipcCommand.observeProperty("time-pos", 0);
        }
        return this;
    }

    @Override
    public Player onStateChanged(Consumer<Object> listener)  {
        super.onStateChanged(listener);
        if(ipcCommand == null) {
            return this;
        }
        String[] keys = {
                "mute",
                "pause",
                "duration",
                "volume",
                "filename",
                "path",
                "media-title",
                "playlist-pos",
                "playlist-count",
                "loop"
                /*"fullscreen",
                "sub-visibility"*/
        };
        Arrays.asList(keys).forEach(key -> {
            execCommand(() -> ipcCommand.observeProperty(key));
        });
        return this;
    }

    private boolean load(String url, String mode) {
        if(ipcCommand == null) {
            return false;
        }
        if (loaded) {
            return false;
        }
        setLoaded(true);

        //replace、append、append-only
        if(mode == null) {
            mode = "replace";
        }
        url = transformPath(url);
        ipcCommand.sendCommand("loadfile", url, mode);
        return true;
    }

    private boolean load(String url) {
        return load(url, null);
    }

    @Override
    public void play()  {
        if(!isTrackPlayable()) {
            return ;
        }
        if(!load(getCurrentUrl())) {
            execCommand(() -> ipcCommand.setProperty("pause", false));
        }
    }

    @Override
    public void pause() {
        if(!isTrackPlayable()) {
            return ;
        }
        execCommand(() -> ipcCommand.setProperty("pause", true));
    }

    @Override
    public void togglePause() {
        if(!isTrackPlayable()) {
            return ;
        }
        execCommand(() -> ipcCommand.cycleProperty("pause"));
    }

    @Override
    public void stop() {
        execCommand(() -> ipcCommand.sendCommand("stop"));
    }

    @Override
    public void seekRelative(double seconds) {
        if(!isTrackPlayable()) {
           return ;
        }
        execCommand(() -> {
            ipcCommand.sendCommand("seek", seconds, "relative");
        });
    }

    @Override
    public void seek(double seconds) {
        setPendingSeek(seconds);
        if(!isTrackPlayable() || seconds < 0) {
            return ;
        }
        execCommand(() -> {
            ipcCommand.sendCommand("seek", seconds, "absolute", "exact");
        });
        setPendingSeek(-1);
    }

    @Override
    public void mute() {
        execCommand(() -> ipcCommand.setProperty("mute", true));
    }

    @Override
    public void unmute() {
        execCommand(() -> ipcCommand.setProperty("mute", false));
    }

    @Override
    public void toggleMute() {
        execCommand(() -> ipcCommand.cycleProperty("mute"));
    }

    @Override
    public void quit() {
        setRunning(false);
        execCommand(() -> ipcCommand.sendCommand("quit"));
        try {
            if(mpvCommadProcess != null) {
                mpvCommadProcess.destroyForcibly();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if(ipcChannel != null) {
                ipcChannel.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTrack(Track track) {
        //关闭上一曲
        if(isTrackPlayable()) {
            stop();
        }
        setCurrentTrack(track);
    }

    @Override
    public void play(Track track) {
        setTrack(track);
        if(isTrackPlayable()) {
            load(track.getUrl());
            play();
        }
    }

    @Override
    public void setEqualizer(int[] values) {
        execCommand(() -> {
            StringJoiner joiner = new StringJoiner(",");
            for(int i = 0; i < FREQUENCIES.length; i++) {
                joiner.add(buildEqualizer(i, FREQUENCIES[i], values[i]));
            }
            ipcCommand.af("set", joiner.toString());
        });
    }

    private String buildEqualizer(int index, int frequency, int value) {
        return String.format("@eq%1$s:equalizer=f=%2$s:t=o:w=1:g=%3$s",
                index, frequency, value);
    }

    private String getEqualizersLabels() {
        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < FREQUENCIES.length; i++) {
            joiner.add("@eq" + i);
        }
        return joiner.toString();
    }

    public void removeEqualizer() {
        execCommand(() -> {
            ipcCommand.af("remove", getEqualizersLabels());
        });
    }

    public void showSpectrum() {
        execCommand(() -> {
            ipcCommand.af("add", "lavfi=showspectrumpic=s=1024x1024");
        });
    }

}
