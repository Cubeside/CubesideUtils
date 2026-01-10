package de.iani.cubesideutils.adventure.translations;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;

public final class ChainedTranslator implements Translator {
    private final @NotNull Key name;
    private final @NotNull List<Translator> translators;

    public ChainedTranslator(@NotNull Key name, @NotNull List<Translator> translators) {
        this.name = name;
        this.translators = List.copyOf(translators);
    }

    public ChainedTranslator(@NotNull Key name, @NotNull Translator... translators) {
        this(name, Arrays.asList(translators));
    }

    @Override
    public MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        for (Translator translator : translators) {
            MessageFormat result = translator.translate(key, locale);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public @NotNull Key name() {
        return name;
    }
}
