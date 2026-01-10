package de.iani.cubesideutils.adventure.translations;

import java.util.Locale;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;

public class GlobalAndMinecraftTranslator {
    private static final ChainedTranslator INSTANCE;
    private static final TranslatableComponentRenderer<Locale> RENDERER;

    static {
        INSTANCE = new ChainedTranslator(Key.key("cubeside:globalandminecraft"), GlobalTranslator.translator(), MojangJsonTranslationStore.load());
        RENDERER = TranslatableComponentRenderer.usingTranslationSource(INSTANCE);
    }

    public static ChainedTranslator translator() {
        return INSTANCE;
    }

    public static @NotNull TranslatableComponentRenderer<Locale> renderer() {
        return RENDERER;
    }

    public static @NotNull Component render(final @NotNull Component component, final @NotNull Locale locale) {
        return renderer().render(component, locale);
    }
}
