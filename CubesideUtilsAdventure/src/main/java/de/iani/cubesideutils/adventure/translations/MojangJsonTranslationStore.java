package de.iani.cubesideutils.adventure.translations;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.iani.cubesideutils.plugin.CubesideUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.AbstractTranslationStore;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;

public final class MojangJsonTranslationStore extends AbstractTranslationStore<MessageFormat> {

    private MojangJsonTranslationStore(Map<Locale, Map<String, MessageFormat>> translations) {
        super(Key.key("cubeside:mojangtranslations"));
        for (Entry<Locale, Map<String, MessageFormat>> localTranslations : translations.entrySet()) {
            for (Entry<String, MessageFormat> localTranslation : localTranslations.getValue().entrySet()) {
                register(localTranslation.getKey(), localTranslations.getKey(), localTranslation.getValue());
            }
        }
    }

    @Override
    public @org.jetbrains.annotations.Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return translationValue(key, locale);
    }

    public static @NotNull Translator load() {
        TranslationLoader.checkAndDownloadLangs(CubesideUtils.getInstance());
        String mcVersion = CubesideUtils.getInstance().getMinecraftVersion(); // "1.21.11"
        File langDir = new File(CubesideUtils.getInstance().getDataFolder(), "langs/" + mcVersion);
        try {
            return loadFromDirectory(langDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Factory-Methode zum Laden aller Mojang-Langdateien eines Versionsordners
     */
    public static MojangJsonTranslationStore loadFromDirectory(File langDir) throws IOException {
        if (!langDir.isDirectory()) {
            throw new IllegalArgumentException("Kein Verzeichnis: " + langDir);
        }

        Map<Locale, Map<String, MessageFormat>> result = new HashMap<>();

        File[] files = langDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            throw new IOException("Fehler beim Lesen des Verzeichnisses");
        }

        Gson gson = new Gson();

        for (File file : files) {
            String localeName = file.getName().replace(".json", "");
            Locale locale = parseLocale(localeName);

            try (Reader reader = new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8)) {

                JsonObject json = gson.fromJson(reader, JsonObject.class);
                Map<String, MessageFormat> map = new HashMap<>();

                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    if (!entry.getValue().isJsonPrimitive()) {
                        continue;
                    }

                    String key = entry.getKey();
                    String value = entry.getValue().getAsString();

                    map.put(key, new MessageFormat(value, locale));
                }

                result.put(locale, Map.copyOf(map));
            }
        }

        return new MojangJsonTranslationStore(Map.copyOf(result));
    }

    private static Locale parseLocale(String mojangName) {
        // Mojang: de_de, en_us, pt_br
        String[] parts = mojangName.split("_");
        if (parts.length == 1) {
            return new Locale(parts[0]);
        }
        return new Locale(parts[0], parts[1].toUpperCase());
    }
}