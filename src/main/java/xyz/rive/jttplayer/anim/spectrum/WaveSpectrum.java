package xyz.rive.jttplayer.anim.spectrum;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import xyz.rive.jttplayer.ApplicationContext;

public class WaveSpectrum extends AbstractSpectrum {

    public WaveSpectrum(ApplicationContext context, Canvas canvas) {
        super(context, canvas);
    }

    public void drawWave() {
        float[] tddData = getTimeDomainData();
        if (tddData == null) {
            return ;
        }
        byte[] byteData = floatToByteTimeDomain(tddData);

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, width, height);

        Paint paint = getSpectrumColor(-1, true);
        context.setStroke(paint);

        double percent = 1 / 2D;
        context.beginPath();
        for (int i = 0; i < width; i++) {
            int index = (int) (byteData.length * i / width);
            double y1 = height * percent - (height * ((byteData[index] & 0xFF) / 256D - percent));
            context.lineTo(i, y1);
            context.stroke();
        }
    }

    public void drawWave2() {
        float[] tddData = getTimeDomainData();
        if (tddData == null) {
            return ;
        }
        byte[] byteData = floatToByteTimeDomain(tddData);

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, width, height);

        Paint paint = getSpectrumColor(-1, true);
        context.setStroke(paint);

        double percent = 1 / 2D;
        context.beginPath();
        for (int i = 0; i < width; i++) {
            int index = (int) (byteData.length * i / width);
            double y1 = height * percent - (height * ((byteData[index] & 0xFF) / 256D - percent));
            context.lineTo(i, y1);
            context.stroke();
        }

        context.beginPath();
        for (int i = 0; i < width; i++) {
            int index = (int) (byteData.length * i / width);
            double y1 = height * ((byteData[index] & 0xFF) / 256D);
            context.lineTo(i, y1);
            context.stroke();
        }

    }

    public void drawWave3() {
        float[] tddData = getTimeDomainData();
        if (tddData == null) {
            return ;
        }
        byte[] byteData = floatToByteTimeDomain(tddData);

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, width, height);

        Paint paint = getSpectrumColor(-1, true);
        context.setStroke(paint);

        double barWidth = 1 / 2D;
        int spacing = 2;
        double percent = 1 / 2D;
        double halfHeight = height / 2D;
        for (double i = 0; i < width; ) {
            int index = (int) (byteData.length * i / width);
            double p = height * percent - (height * ((byteData[index] & 0xFF) / 256D - percent));
            double barHeight = Math.max(Math.abs(p - halfHeight) * 2, 1);
            double y = p > halfHeight ? p - barHeight : p;
            context.fillRect(i, y, barWidth, barHeight);
            context.strokeRect(i, y, barWidth, barHeight);
            i += barWidth + spacing;
        }
    }


    private static byte[] floatToByteTimeDomain(float[] data) {
        byte[] byteData = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            // clamp to [-1.0, 1.0]
            float f = Math.max(-1.0f, Math.min(1.0f, data[i]));
            // map [-1,1] -> [0,255]
            byteData[i] = (byte) ((f + 1.0f) * 127.5f); // 127.5 = 255/2
        }
        return byteData;
    }

}
