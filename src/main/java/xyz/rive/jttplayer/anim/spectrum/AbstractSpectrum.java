package xyz.rive.jttplayer.anim.spectrum;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import xyz.rive.jttplayer.ApplicationContext;
import xyz.rive.jttplayer.player.Player;
import xyz.rive.jttplayer.skin.StandaloneXml;


public abstract class AbstractSpectrum {
    protected ApplicationContext appContext;
    protected Canvas canvas;

    public AbstractSpectrum(ApplicationContext context, Canvas canvas) {
        this.appContext = context;
        this.canvas = canvas;
    }

    protected StandaloneXml getVisualXml() {
        return appContext.getActiveVisualXml();
    }

    protected Player getPlayer() {
        return appContext.getPlayerManager().getActivePlayer();
    }

    protected float[] getFftData() {
        return getPlayer().getFftData();
    }

    protected float[] getTimeDomainData() {
        return getPlayer().getTimeDomainData();
    }

    protected Paint getSpectrumColor(double percent, boolean pureColor) {
        StandaloneXml xml = getVisualXml();
        String topColor = xml.spectrumTopColor;
        if (pureColor) {
            return Color.valueOf(topColor);
        }

        String midColor = xml.spectrumMidColor;
        String btmColor = xml.spectrumBtmColor;
        String peakColor = xml.spectrumPeakColor;
        String colorStops = String.format("%s, %s", btmColor, btmColor);
        if (percent >= 0.9) {
            colorStops = peakColor  + " 0.5%," + topColor + " 6%," + midColor + " 16%," + btmColor;
        } else if (percent >= 0.75) {
            colorStops = topColor + " 0.5%," + midColor + " 6%," + btmColor;
        } else if (percent >= 0.5) {
            colorStops = midColor  + " 0.5%," + btmColor;
        }
        return LinearGradient.valueOf(String.format("linear-gradient(to bottom, %s)", colorStops));
    }



}
