package de.iani.cubesideutils.adventure.plugin;

import de.iani.cubesideutils.adventure.translations.CubesideTranslations;
import de.iani.cubesideutils.plugin.CubesideUtils;

public abstract class CubesideUtilsAdventure extends CubesideUtils {

    static {
        CubesideTranslations.getTranslations(""); // trigger initialization
    }

}
