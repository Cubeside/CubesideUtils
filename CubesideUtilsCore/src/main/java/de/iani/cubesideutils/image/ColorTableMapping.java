package de.iani.cubesideutils.image;

import java.awt.image.BufferedImage;

public class ColorTableMapping implements ImageProcessing {
    private IndexedColorTable colorTable;

    public ColorTableMapping(IndexedColorTable colorTable) {
        this.colorTable = colorTable;
    }

    @Override
    public void apply(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int oldColor = image.getRGB(x, y);
                int nearestColor = colorTable.getRGBForIndex(colorTable.getNearestIndex(oldColor));
                image.setRGB(x, y, nearestColor);
            }
        }
    }
}
