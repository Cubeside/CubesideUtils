package de.iani.cubesideutils.adventure.plugin;

import de.iani.cubesideutils.adventure.translations.CubesideTranslations;
import de.iani.cubesideutils.adventure.translations.MojangJsonTranslationStore;
import de.iani.cubesideutils.plugin.CubesideUtils;

public abstract class CubesideUtilsAdventure extends CubesideUtils {

    @Override
    protected void onEnableInternal() throws Throwable {
        super.onEnableInternal();

        CubesideTranslations.getTranslations(""); // trigger initialization
        MojangJsonTranslationStore.translationStore(); // trigger initialization
    }

}
