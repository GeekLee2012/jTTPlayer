package xyz.rive.jttplayer.player.bass;


import jouvieje.bass.Bass;
import jouvieje.bass.BassInit;
import jouvieje.bass.defines.BASS_ATTRIB;
import jouvieje.bass.defines.BASS_DATA;
import jouvieje.bass.defines.BASS_POS;
import jouvieje.bass.structures.BASS_BFX_BQF;
import jouvieje.bass.structures.HFX;
import jouvieje.bass.structures.HSTREAM;
import xyz.rive.jttplayer.common.Pair;
import xyz.rive.jttplayer.common.Track;
import xyz.rive.jttplayer.player.AbastractPlayer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static jouvieje.bass.defines.BASS_BFX_CHANNEL.BASS_BFX_CHANALL;
import static xyz.rive.jttplayer.util.FileUtils.*;
import static xyz.rive.jttplayer.util.FxUtils.isWindows;
import static xyz.rive.jttplayer.util.StringUtils.isEmpty;
import static xyz.rive.jttplayer.util.StringUtils.trim;

public class BassPlayer extends AbastractPlayer {
    private HSTREAM hstream;
    private boolean bassReady;
    private boolean started;
    private boolean playing;
    private final List<HFX> hfxList = new ArrayList<>(10);
    private final int[] eqValues = new int[10];
    private String tmpUrl;

    public BassPlayer() {
        setBassReady(false);
        setPlaying(false);
        Arrays.fill(eqValues, 0);
        setup();
    }

    private boolean isBassReady() {
        return bassReady;
    }

    private void setBassReady(boolean ready) {
        bassReady = ready;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    protected Future<Boolean> setup() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if(isEmpty(getPlayCorePath())) {
            future.complete(false);
            return future;
        }
        runTask(() -> {
            //java.library.path
            //org.lwjgl.librarypath
            System.setProperty("java.library.path", trim(getPlayCorePath()));
            try {
                BassInit.loadLibraries();
                boolean success = Bass.BASS_Init(-1, 44100, 0, null, null);
                //Loading Plugins
                //Bass.BASS_PluginLoad()
                setBassReady(success);
                future.complete(success);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return future;
    }

    private void initTrack(Track track) {
        if(!isBassReady()) {
            return ;
        }

        setCurrentTrack(track);
        if(!isTrackPlayable()) {
            return;
        }

        String url = trim(currentTrack.getUrl());
        if(url.startsWith("http")) {
            hstream = Bass.BASS_StreamCreateURL(url, 0, 0, null, null);
        } else {
            hstream = Bass.BASS_StreamCreateFile(false, patchWindowsUrl(url), 0, 0, 0);
        }
        setupVolume();

        setStarted(false);
        setDuration();
        notifyTimePosition();
        //setupEqualizer();
    }


    private String patchWindowsUrl(String url) {
        if(!isWindows()) {
            return url;
        }
        if (isEmpty(tempPath)) {
            return url;
        }

        cleanWindowsFile();

        tmpUrl = transformPath(String.format("%s/%s.tmp", tempPath, currentTrack.getId()));
        if (copy(url, tmpUrl)) {
            url = tmpUrl;
        }
        return url;
    }

    private void cleanWindowsFile() {
        if(!isWindows()) {
            return;
        }
        if (isEmpty(tmpUrl)) {
            return ;
        }
        deleteIfExists(Paths.get(tmpUrl));
    }

    private int getHandle() {
        return hstream == null ? 0 : hstream.asInt();
    }

    private void setDuration() {
        long length = Bass.BASS_ChannelGetLength(getHandle(), BASS_POS.BASS_POS_BYTE);
        duration = Bass.BASS_ChannelBytes2Seconds(getHandle(), length);
    }

    private void notifyTimePosition() {
        long position = Bass.BASS_ChannelGetPosition(getHandle(), BASS_POS.BASS_POS_BYTE);
        double timePos = Bass.BASS_ChannelBytes2Seconds(getHandle(), position);
        setTimePosition(timePos);
        Optional.ofNullable(timePositionListener)
                .ifPresent(listener ->  listener.accept(timePos));
        if (Math.abs(duration - timePos) <= 0.618) {
            Optional.ofNullable(stoppedListener)
                    .ifPresent(listener -> listener.accept(true));
        }
    }

    @Override
    public void play() {
        if (isBassReady()) {
            Bass.BASS_ChannelPlay(getHandle(), false);
            setPlaying(true);
            if(!isStarted()) {
                setStarted(true);
                Optional.ofNullable(startedListener)
                        .ifPresent(listener -> listener.accept(true));
            }
            Optional.ofNullable(pausedListener)
                    .ifPresent(listener -> listener.accept(false));
            tick(this::notifyTimePosition);
        }
    }

    @Override
    public void pause() {
        if (isBassReady()) {
            Bass.BASS_ChannelPause(getHandle());
            setPlaying(false);
            Optional.ofNullable(pausedListener)
                    .ifPresent(listener -> listener.accept(true));
            stopTick();
        }
    }

    @Override
    public void togglePause() {
        if (!isPlaying()) {
            play();
        } else {
            pause();
        }
    }

    @Override
    public void stop() {
        if (isBassReady()) {
            Bass.BASS_ChannelStop(getHandle());
            Bass.BASS_StreamFree(hstream);
            setPlaying(false);
            stopTick();
        }
    }

    @Override
    public void seekRelative(double seconds) {

    }

    @Override
    public void seek(double seconds) {
        if (isBassReady()) {
            long pos = Bass.BASS_ChannelSeconds2Bytes(getHandle(), seconds);
            if(pos >= 0) {
                Bass.BASS_ChannelSetPosition(getHandle(), pos, BASS_POS.BASS_POS_BYTE);
            }
        }
    }

    @Override
    public void mute() {
        setVolume(0);
    }

    @Override
    public void unmute() {
        setVolume(100);
    }

    @Override
    public void toggleMute() {
        setMute(!isMute());
        if (isMute()) {
            mute();
        } else {
            unmute();
        }
    }

    @Override
    public void quit() {
        if(isBassReady()) {
            Bass.BASS_Stop();
            Bass.BASS_Free();
        }
        cleanWindowsFile();
    }

    @Override
    public void play(Track track) {
        if (isBassReady()) {
            stop();
            initTrack(track);
            play();
        }
    }

    private void setupVolume() {
        Bass.BASS_ChannelSetAttribute(getHandle(),
                BASS_ATTRIB.BASS_ATTRIB_VOL,(float) volume / 100F);
    }

    @Override
    public void setVolume(double value) {
        super.setVolume(value);
        if(isBassReady()) {
            setupVolume();
            Optional.ofNullable(stateChangedListener).ifPresent(
                    listener -> listener.accept(new Pair("volume", volume)));
        }
    }

    public void setupEqualizer() {
        hfxList.clear();
        /*
        for (int i = 0; i < eqValues.length; i++) {
            BASS_BFX_BQF param = new BASS_BFX_BQF();
            param.setChannel(BASS_BFX_CHANALL);
            param.setCenter(eqValues[i]);
            param.setBandwidth(1.0F);
            param.setGain(0.0F);
            param.lFilter = BASS_BFX_BQF.BASS_BFX_BQF_LOWPASS; // 实际用 PEAKING

            int handle = Bass.BASS_ChannelSetFX(stream, BASSData.BASS_FX_BFX_BQF, 1);
            Bass.BASS_FXSetParameters(handle, param);
        }*/
    }

    @Override
    public void setEqualizer(int[] values) {
        /*for (int i = 0; i < eqValues.length; i++) {
            eqValues[i] = values[i];
            setBandGain(i, eqValues[i]);
        }*/
    }

    private void setBandGain(int index, int value) {
        HFX hfx = index < hfxList.size() ? hfxList.get(index) : null;
        if(hfx != null) {
            BASS_BFX_BQF param = BASS_BFX_BQF.allocate();
            param.setCenter(FREQUENCIES[index]);
            param.setBandwidth(1);
            param.setGain(value);
            Bass.BASS_FXSetParameters(hfx, param);
        }
    }

    @Override
    public void removeEqualizer() {
//        Arrays.fill(eqValues, 0);
//        setEqualizer(eqValues);
    }

    @Override
    public boolean isVisualSupported() {
        return true;
    }

    public float[] getFftData() {
        if (!isBassReady() || !isVisualSupported()) {
            return null;
        }

        int fftSize = 256;
        ByteBuffer buffer = ByteBuffer.allocateDirect(fftSize * 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        FloatBuffer fft = buffer.asFloatBuffer();
        int result = Bass.BASS_ChannelGetData(getHandle(), buffer, BASS_DATA.BASS_DATA_FFT256);
        if (result < 0) {
            return null;
        }

        float[] data = new float[fftSize];
        fft.rewind();
        float gain = 32;
        for (int i = 0; i < fftSize; i++) {
            data[i] = buffer.getFloat(i * 4) * gain;
        }
        return data;
    }

    public float[] getTimeDomainData() {
        if (!isBassReady() || !isVisualSupported()) {
            return null;
        }

        int tddSize = 256;
        int sample = tddSize * 4;
        ByteBuffer buffer = ByteBuffer.allocateDirect(sample);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        FloatBuffer fft = buffer.asFloatBuffer();
        int result = Bass.BASS_ChannelGetData(getHandle(), buffer, BASS_DATA.BASS_DATA_FLOAT | sample);
        if (result < 0) {
            return null;
        }

        float[] data = new float[tddSize];
        fft.rewind();
        for (int i = 0; i < tddSize; i++) {
            data[i] = buffer.getFloat(i * 4);
        }
        return data;
    }

}
