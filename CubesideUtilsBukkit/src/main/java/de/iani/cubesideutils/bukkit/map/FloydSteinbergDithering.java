package de.iani.cubesideutils.bukkit.map;

import com.google.common.base.Preconditions;
import java.awt.Color;
import java.awt.image.BufferedImage;
import org.bukkit.map.MapPalette;

public class FloydSteinbergDithering {
    public static void direct(final BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int oldColor = image.getRGB(x, y);
                int nearestColor = getNearestMinecraftMapColor(oldColor);
                image.setRGB(x, y, nearestColor);
            }
        }
    }

    public static void applyDithering(final BufferedImage image) {
        applyDithering(image, 1);
    }

    public static void applyDithering(final BufferedImage image, int ditherReductionFactor) {
        Preconditions.checkArgument(ditherReductionFactor >= 1, "ditherReductionFactor must be postive");
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int oldColor = image.getRGB(x, y);
                int nearestColor = getNearestMinecraftMapColor(oldColor);
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

    @SuppressWarnings("deprecation")
    public static int getNearestMinecraftMapColor(int rgba) {
        return MapPalette.getColor(MapPalette.matchColor(new Color(rgba, true))).getRGB();
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
