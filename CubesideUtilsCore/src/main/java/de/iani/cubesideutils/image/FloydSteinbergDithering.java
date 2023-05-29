package de.iani.cubesideutils.image;

import java.awt.image.BufferedImage;

public class FloydSteinbergDithering implements ImageProcessing {
    private IndexedColorTable colorTable;
    private int ditherReductionFactor;

    public FloydSteinbergDithering(IndexedColorTable colorTable) {
        this(colorTable, 1);
    }

    public FloydSteinbergDithering(IndexedColorTable colorTable, int ditherReductionFactor) {
        if (ditherReductionFactor < 1) {
            throw new IllegalArgumentException("ditherReductionFactor must be postive");
        }
        this.colorTable = colorTable;
        this.ditherReductionFactor = ditherReductionFactor;
    }

    @Override
    public void apply(final BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int oldColor = image.getRGB(x, y);
                int nearestColor = colorTable.getRGBForIndex(colorTable.getNearestIndex(oldColor));
                image.setRGB(x, y, nearestColor);

                int a = (oldColor >> 24) & 0xff;
                int r = (oldColor >> 16) & 0xff;
                int g = (oldColor >> 8) & 0xff;
                int b = oldColor & 0xff;

                int na = (nearestColor >> 24) & 0xff;
                int nr = (nearestColor >> 16) & 0xff;
                int ng = (nearestColor >> 8) & 0xff;
                int nb = nearestColor & 0xff;

                int errA = a - na;
                int errR = r - nr;
                int errG = g - ng;
                int errB = b - nb;

                if (x + 1 < image.getWidth()) {
                    int updated = adjustPixel(image.getRGB(x + 1, y), errA, errR, errG, errB, 7, ditherReductionFactor);
                    image.setRGB(x + 1, y, updated);
                }
                if (y + 1 < image.getHeight()) {
                    int updated = adjustPixel(image.getRGB(x, y + 1), errA, errR, errG, errB, 5, ditherReductionFactor);
                    image.setRGB(x, y + 1, updated);
                    if (x - 1 >= 0) {
                        updated = adjustPixel(image.getRGB(x - 1, y + 1), errA, errR, errG, errB, 3, ditherReductionFactor);
                        image.setRGB(x - 1, y + 1, updated);
                    }
                    if (x + 1 < image.getWidth()) {
                        updated = adjustPixel(image.getRGB(x + 1, y + 1), errA, errR, errG, errB, 1, ditherReductionFactor);
                        image.setRGB(x + 1, y + 1, updated);
                    }
                }
            }
        }
    }

    private static int adjustPixel(final int colorRgb, final int errA, final int errR, final int errG, final int errB, final int mul, int ditherReductionFactor) {
        int a = clampToByte(((colorRgb >> 24) & 0xff) + errA * mul / (16 * ditherReductionFactor));
        int r = clampToByte(((colorRgb >> 16) & 0xff) + errR * mul / (16 * ditherReductionFactor));
        int g = clampToByte(((colorRgb >> 8) & 0xff) + errG * mul / (16 * ditherReductionFactor));
        int b = clampToByte((colorRgb & 0xff) + errB * mul / (16 * ditherReductionFactor));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int clampToByte(int value) {
        return Math.min(Math.max(value, 0), 0xff);
    }
}
