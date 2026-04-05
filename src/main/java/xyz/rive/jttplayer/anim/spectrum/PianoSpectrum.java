package xyz.rive.jttplayer.anim.spectrum;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import xyz.rive.jttplayer.ApplicationContext;

import static xyz.rive.jttplayer.util.FxUtils.nextInt;

public class PianoSpectrum extends AbstractSpectrum {
    private int count = 0;

    public PianoSpectrum(ApplicationContext context, Canvas canvas) {
        super(context, canvas);
    }

    public void draw(Canvas canvas, float[] data, Paint paint) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, width, height);

        double arcRadius = 4;
        double wkSize = 52;
        double wkw = 10;
        if(wkw * wkSize < width) {
            wkw = Math.ceil(width / wkSize);
        }
        double wkh = height - 3;
        double bkw = wkw * 9 / 13D;
        double bkh = wkh * 0.618;
        ++count;
        //可能会溢出
        count = Math.max(1, count);

        int wkeyFill = 0, bkeyFill = 0;
        for (int i = 0; i < wkSize; i++) {
            WhiteKey wkey = new WhiteKey(i + "", i + "", i * wkw, 0, wkw, wkh, arcRadius);
            Paint wkeyFillStyle = null;
            if(hitRandom(count) && wkeyFill < 4) {
                wkeyFillStyle = paint;
                ++wkeyFill;
            }
            wkey.draw(context);
            wkey.fill(context, wkeyFillStyle);

            if(i < 1) continue;
            double x = 0, j = (i - 3);
            if(i == 1) {
                x = i * wkw - bkw / 2;
            } else if(j % 7 != 0 && j % 7 != 3) {
                x = (i - 1) * wkw - bkw / 2;
            } else {
                continue;
            }

            Paint bkeyFillStyle = null;
            BlackKey bkey = new BlackKey(i + "", i + "", x, 0, bkw, bkh);
            if(hitRandom(count) && bkeyFill < 2) {
                bkeyFillStyle = paint;
                ++bkeyFill;
            }
            bkey.draw(context, bkeyFillStyle);

            if(i * wkw >= width) break;
        }

        context.setStroke(Color.valueOf("#333333"));
        context.beginPath();
        context.moveTo(0, wkh - arcRadius + 1);
        context.lineTo(0, 0);
        context.lineTo(wkSize, 0);
        context.stroke();
    }

    private boolean hitRandom(int factor) {
        int limit = 100, modLimit = 30;
        //int factorModLimit = (int) (Math.random() * 10 + 20);
        return (nextInt(limit) % modLimit == 3
                || nextInt(limit) % modLimit == 5
                || nextInt(limit) % modLimit == 7);
        //&& (factor % factorModLimit == 0);
    }



    private static class WhiteKey {
        public String name;
        public String tone;
        public double x;
        public double y;
        public double width;
        public double height;
        public double arcRadius;

        public WhiteKey(String name, String tone, double x, double y, double width, double height, double arcRadius) {
            this.name = name;
            this.tone = tone;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.arcRadius = arcRadius;
        }

        public void draw(GraphicsContext ctx) {
            ctx.beginPath();
            ctx.moveTo(x, y);
            ctx.lineTo(x, y + height - arcRadius);
            ctx.arcTo(x, y + height,  x + arcRadius, y + height ,arcRadius);
            ctx.lineTo(x + width - 2 * arcRadius, y + height);
            ctx.arcTo(x + width, y + height, x + width, y + height - arcRadius, arcRadius);
            //ctx.lineTo(x + width, y);
            ctx.stroke();
        }

        public void fill(GraphicsContext ctx, Paint fillStyle) {
            fillStyle = fillStyle == null ? Color.valueOf("#ffffff") : fillStyle;
            ctx.setFill(fillStyle);
            ctx.beginPath();
            ctx.moveTo(x, y);
            ctx.lineTo(x, y + height - arcRadius);
            ctx.arcTo(x, y + height,  x + arcRadius, y + height ,arcRadius);
            ctx.lineTo(x + width - 2 * arcRadius, y + height);
            ctx.arcTo(x + width, y + height, x + width, y + height - arcRadius, arcRadius);
            ctx.lineTo(x + width, y);
            ctx.closePath();
            ctx.stroke();
            ctx.fill();
        }
    }

    private static class BlackKey {
        public String name;
        public String tone;
        public double x;
        public double y;
        public double width;
        public double height;

        public BlackKey(String name, String tone, double x, double y, double width, double height) {
            this.name = name;
            this.tone = tone;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void draw(GraphicsContext ctx, Paint fillStyle) {
            fillStyle = fillStyle == null ? Color.valueOf("#000000") : fillStyle;
            ctx.setFill(fillStyle);
            ctx.fillRect(x, y, width, height);
        }
    }
}
