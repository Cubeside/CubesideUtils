package de.iani.cubesideutils.adventure.translations;

import de.iani.cubesideutils.Pair;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.AbstractTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CubesideTranslations {

    private static class CubesideTranslator extends AbstractTranslationStore<Component> {

        private static final Key TRANSLATOR_KEY = Key.key("cubeside", "translator");

        private CubesideTranslator() {
            super(TRANSLATOR_KEY);

            GlobalTranslator.translator().addSource(this);
        }

        @Override
        public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
            CubesideTranslationData data = CubesideTranslations.getTranslations(key);
            if (data == null) {
                return null;
            }
            MessageFormat result = data.getOther(locale);
            if (result != null) {
                return result;
            }
            if ("de".equals(locale.getLanguage())) {
                return data.getGerman();
            }
            return data.getEnglish();
        }

    }

    static {
        new CubesideTranslator();
    }

    public static class CubesideTranslationData {
        private MessageFormat english;
        private MessageFormat german;
        private Map<Locale, MessageFormat> otherLocales;

        public CubesideTranslationData(MessageFormat english, MessageFormat german) {
            this.english = Objects.requireNonNull(english);
            this.german = Objects.requireNonNull(german);
            this.otherLocales = new LinkedHashMap<>();
        }

        public MessageFormat getEnglish() {
            return this.english;
        }

        private MessageFormat replaceEnglish(MessageFormat value) {
            MessageFormat old = this.english;
            this.english = value;
            return old;
        }

        public MessageFormat getGerman() {
            return this.german;
        }

        private MessageFormat replaceGerman(MessageFormat value) {
            MessageFormat old = this.german;
            this.german = value;
            return old;
        }

        public MessageFormat getOther(Locale locale) {
            return this.otherLocales.get(locale);
        }

        private MessageFormat setOther(Locale locale, MessageFormat value) {
            return this.otherLocales.put(locale, value);
        }
    }

    private static Map<String, CubesideTranslationData> knownTranslations = new LinkedHashMap<>();

    public static Pair<MessageFormat, MessageFormat> registerTranslation(String key, String english, String german) {
        return registerTranslation(key, quoteString(english), quoteString(german));
    }

    public static Pair<MessageFormat, MessageFormat> registerTranslation(String key, MessageFormat english, MessageFormat german) {
        CubesideTranslationData newData = new CubesideTranslationData(english, german);
        CubesideTranslationData oldData = knownTranslations.putIfAbsent(key, newData);
        if (oldData == null) {
            return null;
        }
        return new Pair<>(oldData.replaceEnglish(english), oldData.replaceGerman(german));
    }

    public static MessageFormat registerTranslation(String key, Locale locale, MessageFormat value) {
        CubesideTranslationData data = knownTranslations.get(key);
        return data.setOther(locale, value);
    }

    public static MessageFormat quoteString(String string) {
        return new MessageFormat(string.replace("'", "''"));
    }

    public static CubesideTranslationData getTranslations(String key) {
        return knownTranslations.get(key);
    }

    public static Component translateCubesideKeys(Component message, Locale locale) {
        message = message.children(message.children().stream().map(c -> translateCubesideKeys(c, locale)).toList());
        if (message instanceof TranslatableComponent tc && knownTranslations.containsKey(tc.key())) {
            message = GlobalTranslator.render(message.children(List.of()), locale).children(message.children());
        }
        return message;
    }

    public static class Keys {
        public static final String AND = "cubeside.and";
        public static final String AND_OR = "cubeside.and_or";
        public static final String ENCHANTED_WITH = "cubeside.enchanted_with";
        public static final String FOR = "cubeside.for";
        public static final String ROUGLY = "cubeside.roughly";
    }

    public static class Components {
        public static final Component AND = Component.translatable(Keys.AND);
        public static final Component AND_OR = Component.translatable(Keys.AND_OR);
        public static final Component ENCHANTED_WITH = Component.translatable(Keys.ENCHANTED_WITH);
        public static final Component FOR = Component.translatable(Keys.FOR);
        public static final Component ROUGLY = Component.translatable(Keys.ROUGLY);
    }

    static {
        registerTranslation(Keys.AND, "and", "und");
        registerTranslation(Keys.AND_OR, "and/or", "und/oder");
        registerTranslation(Keys.ENCHANTED_WITH, "enchanted with", "verzaubert mit");
        registerTranslation(Keys.FOR, "for", "für");
        registerTranslation(Keys.ROUGLY, "rougly", "ungefähr");
    }

}
