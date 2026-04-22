package xyz.rive.jttplayer.anim.spectrum;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import xyz.rive.jttplayer.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class WaveSpectrum extends AbstractSpectrum {
    private List<Point2D> points = new ArrayList<>(64);

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

    public void drawWave1() {
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

        points.clear();
        for (int i = 0; i < width; i += 2) {
            int index = (int) (byteData.length * i / width);
            double y1 = height * (byteData[index] & 0xFF) / 256D;
            points.add(new Point2D(i, y1));
        }

        // 使用 Catmull-Rom 转 Bezier 方法
        double[] coords = catmullRomToBezier(points);
        context.beginPath();
        context.moveTo(coords[0], coords[1]);

        for (int i = 2; i < coords.length; i += 6) {
            context.bezierCurveTo(
                    coords[i],     coords[i + 1],
                    coords[i + 2], coords[i + 3],
                    coords[i + 4], coords[i + 5]
            );
        }
        context.stroke();
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
        double halfHeight = height / 2D;
        for (double i = 0; i < width; ) {
            int index = (int) (byteData.length * i / width);
            double p = height * (1 - (byteData[index] & 0xFF) / 256D);
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

    /**
     * 将 Catmull-Rom 样条点转换为 Cubic Bezier 控制点序列
     * 返回数组格式: [x0,y0, cp1x,cp1y, cp2x,cp2y, x1,y1, cp1x,...]
     */
    public static double[] catmullRomToBezier(List<Point2D> points) {
        final double tension = 0.5; // 通常取 0.5（Cardinal 样条）
        int n = points.size();
        double[] result = new double[(n - 1) * 6 + 2];
        int idx = 0;

        // 起点
        Point2D p0 = points.get(0);
        result[idx++] = p0.getX();
        result[idx++] = p0.getY();

        for (int i = 0; i < n - 1; i++) {
            Point2D pIm1 = (i > 0) ? points.get(i - 1) : points.get(0);
            Point2D pI   = points.get(i);
            Point2D pIp1 = points.get(i + 1);
            Point2D pIp2 = (i + 2 < n) ? points.get(i + 2) : pIp1;

            // 计算切线（导数）
            double dx = (pIp1.getX() - pIm1.getX()) * tension;
            double dy = (pIp1.getY() - pIm1.getY()) * tension;

            double c1x = pI.getX() + dx / 3.0;
            double c1y = pI.getY() + dy / 3.0;

            dx = (pIp2.getX() - pI.getX()) * tension;
            dy = (pIp2.getY() - pI.getY()) * tension;

            double c2x = pIp1.getX() - dx / 3.0;
            double c2y = pIp1.getY() - dy / 3.0;

            result[idx++] = c1x;
            result[idx++] = c1y;
            result[idx++] = c2x;
            result[idx++] = c2y;
            result[idx++] = pIp1.getX();
            result[idx++] = pIp1.getY();
        }

        return result;
    }


}
