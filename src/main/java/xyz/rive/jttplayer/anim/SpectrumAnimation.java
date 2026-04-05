package xyz.rive.jttplayer.anim;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.anim.spectrum.BarSpectrum;
import xyz.rive.jttplayer.anim.spectrum.WaveSpectrum;
import xyz.rive.jttplayer.player.Player;
import xyz.rive.jttplayer.skin.StandaloneXml;

//待考虑是否需要由一个AnimationTimer统一调度管理
public class SpectrumAnimation extends AnimationTimer {
    private final ApplicationContext context;
    private final Canvas canvas;
    private long lastUpdated = -1;
    public final static int MIN_INTERVAL_MILLIS = 1000 / 8; //8 fps
    public final static double MIN_INTERVAL_NANOS = MIN_INTERVAL_MILLIS * 1e6;
    private BarSpectrum barSpectrum;
    private WaveSpectrum waveSpectrum;


    public SpectrumAnimation(ApplicationContext context, Canvas canvas) {
        this.context = context;
        this.canvas = canvas;
    }

    public BarSpectrum getBarSpectrum() {
        if (barSpectrum == null) {
            barSpectrum = new BarSpectrum(context, canvas);
        }
        return barSpectrum;
    }

    public WaveSpectrum getWaveSpectrum() {
        if (waveSpectrum == null) {
            waveSpectrum = new WaveSpectrum(context, canvas);
        }
        return waveSpectrum;
    }

    @Override
    public void handle(long now) {
        if(lastUpdated > 0 && (now - lastUpdated) < MIN_INTERVAL_NANOS) {
            return;
        }
        lastUpdated = now;
        switch (getActiveVisualIndex()) {
            case 2:
                getBarSpectrum().draw3();
                break;
            case 3:
                getBarSpectrum().draw(1/2D, 1);
                break;
            case 4:
                getBarSpectrum().draw4();
                break;
            case 5:
                getWaveSpectrum().drawWave();
                break;
            case 6:
                getWaveSpectrum().drawWave2();
                break;
            case 7:
                getWaveSpectrum().drawWave3();
                break;
            default:
                getBarSpectrum().draw();
                break;
        }
    }

    @Override
    public void start() {
        if (isVisualSupported()) {
            super.start();
        } else {
            stop();
        }
    }

    public void stop(boolean reset) {
        if (reset) {
            resetCanvas();
        }
        super.stop();
    }

    private boolean isVisualSupported() {
        return context.getPlayerManager()
                .getActivePlayer()
                .isVisualSupported();
    }

    private int getActiveVisualIndex() {
        return context.getConfiguration()
                .getPlayerOptions()
                .getActiveVisualIndex();
    }

    private void resetCanvas() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, width, height);
    }


}
