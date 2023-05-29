package de.iani.cubesideutils.image;

public interface IndexedColorTable {
    int getNearestIndex(int rgb);

    int getRGBForIndex(int index);
}
