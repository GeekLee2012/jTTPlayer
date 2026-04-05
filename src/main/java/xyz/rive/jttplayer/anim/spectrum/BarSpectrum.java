package xyz.rive.jttplayer.anim.spectrum;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import xyz.rive.jttplayer.ApplicationContext;

public class BarSpectrum extends AbstractSpectrum {
    private double[] flipBarHeights;

    public BarSpectrum(ApplicationContext context, Canvas canvas) {
        super(context, canvas);
    }


    public void draw() {
        draw(2, 2);
    }

    public void draw(double barWidth, double spacing) {
        float[] data = getFftData();
        if (data == null) {
            return ;
        }
        int dataLen = data.length;

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, width, height);

        Paint paint = getSpectrumColor(-1, true);
        context.setStroke(paint);
        context.setFill(paint);

        double x = 2;
        int step = 1;

        int skipped = 0;
        for (int i = 0; i < dataLen; i = i + step) {
            if (x >= width) {
                break;
            }
            double percent = Math.min(1.0D, data[i]);
            //消除峰值，让波形错落有致
            if(percent >= 0.95) {
                ++skipped;
                continue ;
            }
            double barHeight = percent * (height - 3);
            double y = (height - barHeight); //alignment => bottom

            context.fillRect(x, y, barWidth, barHeight);
            context.strokeRect(x, y, barWidth, barHeight);

            x += barWidth + spacing;
        }
    }

    public void draw2() {
        float[] data = getFftData();
        if (data == null) {
            return ;
        }
        int dataLen = data.length;

        if (flipBarHeights == null) {
            flipBarHeights = new double[dataLen];
        }

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, width, height);

        Paint paint = getSpectrumColor(-1, true);
        context.setStroke(paint);

        double barWidth = 3, x = 2, spacing = 2;
        int step = 1;
        //barWidth = (width / (dataLen * 3));
        double flipBarHeight = 1, flipStep = 1D;

        int skipped = 0;
        for (int i = 0; i < dataLen; i = i + step) {
            if (x >= width) break;
            double percent = Math.min(1.0D, data[i]);
            //消除峰值，让波形错落有致
            if(percent >= 0.95) {
                ++skipped;
                flipBarHeights[i] = -1;
                continue ;
            }
            double barHeight = percent * (height - 3);

            double y = (height - barHeight); //alignment => bottom

            context.fillRect(x, y, barWidth, barHeight);
            context.strokeRect(x, y, barWidth, barHeight);

            //顶部跳块
            //未初始化时，设置默认值
            double minFlipHeight = barHeight + flipBarHeight + 1;
            flipBarHeights[i] = flipBarHeights[i] < 0 ? minFlipHeight : flipBarHeights[i];
            flipBarHeights[i] = Math.max(flipBarHeights[i], 1);
            double dropHeight = (flipBarHeights[i] - flipStep);
            //double heightGap = minFlipHeight - flipBarHeights[i];
            double flipWeight = 15;
            double maxFlipHeight = barHeight > 1 ? barHeight + flipBarHeight * flipWeight : 0;

            flipBarHeights[i] = dropHeight >= minFlipHeight ? dropHeight : maxFlipHeight;

            //偷懒，不做下落过程的其他过渡色啦
            context.setFill(paint);

            double flipY = (height - flipBarHeights[i]);
            context.fillRect(x, flipY, barWidth, flipBarHeight);
            context.strokeRect(x, flipY, barWidth, flipBarHeight);

            x += barWidth + spacing;
        }
    }

    public void draw3() {
        float[] data = getFftData();
        if (data == null) {
            return ;
        }

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, width, height);

        Paint paint = getSpectrumColor(-1, true);

        int bands = data.length / 4;
        double barWidth = width / bands * 1.36;
        int offset = 6, skipped = 0;
        for (int i = offset; i < (bands + offset); i++) {
            double percent = Math.min(1.0D, data[i]);
            //消除峰值，让波形错落有致
            if(percent >= 0.95) {
                ++skipped;
                continue ;
            }
            double barHeight = percent * (height - 3);
            int barIndex = i - offset - skipped;
            double x = barIndex * barWidth - 3;

            context.setStroke(paint);
            context.strokeLine(x, height, x, height - barHeight);
            if (barIndex * barWidth > width) {
                break ;
            }
        }
    }

    public void draw4() {
        float[] data = getFftData();
        if (data == null) {
            return ;
        }
        int dataLen = data.length;

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, width, height);

        Paint paint = getSpectrumColor(-1, true);
        context.setStroke(paint);
        context.setFill(paint);

        double barWidth = 6, cellHeight = 2, x = 3, hspacing = 2, vspacing = 1;
        int step = 1, skipped = 0;

        for (int i = 0; i < dataLen; i = i + step) {
            if ((x + barWidth + hspacing) >= width) {
                break;
            }
            double percent = Math.min(1.0D, data[i]);
            //消除峰值，让波形错落有致
            if(percent >= 0.95) {
                ++skipped;
                continue ;
            }
            double barHeight = percent * (height - 3);
            barHeight = Math.max(barHeight, cellHeight);
            double cellSize = Math.max(1,
                    Math.floor((barHeight + vspacing) / (cellHeight + vspacing))
            );

            for (int j = 0; j < cellSize; j++) {
                double _barHeight = (j + 1) * (cellHeight + vspacing);
                //alignment => bottom
                double y = height - _barHeight;

                context.fillRect(x, y, barWidth, cellHeight);
            }
            x += barWidth + hspacing;
        }
    }

}
