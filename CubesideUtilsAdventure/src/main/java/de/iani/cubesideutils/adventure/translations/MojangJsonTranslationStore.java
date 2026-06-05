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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.AbstractTranslationStore;
import org.jetbrains.annotations.NotNull;

public final class MojangJsonTranslationStore extends AbstractTranslationStore<MessageFormat> {
    private static final MojangJsonTranslationStore INSTANCE = new MojangJsonTranslationStore();

    private final File langDir;
    private final ConcurrentHashMap<Locale, File> loadableLanguageFiles = new ConcurrentHashMap<>();
    private final Set<Locale> loadedLanguageFiles = ConcurrentHashMap.newKeySet();

    public static MojangJsonTranslationStore translationStore() {
        return INSTANCE;
    }

    private MojangJsonTranslationStore() {
        super(Key.key("cubeside:mojangtranslations"));

        langDir = new File(CubesideUtils.getInstance().getDataFolder(), "minecraft_langs");
        TranslationLoader.checkAndDownloadLangs(langDir);

        File[] files = langDir.listFiles((dir, name) -> name.endsWith(".json") && !name.equals("langmeta.json"));
        if (files == null) {
            throw new RuntimeException("Fehler beim Lesen des Verzeichnisses");
        }
        for (File file : files) {
            String localeName = file.getName().replace(".json", "");
            Locale locale = parseLocale(localeName);
            loadableLanguageFiles.put(locale, file);
        }
        CubesideUtils.getInstance().getLogger().info("Found " + loadableLanguageFiles.size() + " language files. ");

        loadLanguage(Locale.US);
    }

    private void loadLanguage(Locale locale) {
        if (loadedLanguageFiles.contains(locale)) {
            return;
        }
        if (!loadableLanguageFiles.containsKey(locale)) {
            CubesideUtils.getInstance().getLogger().warning("Language not found: " + locale);
            return;
        }
        File file = loadableLanguageFiles.remove(locale);
        if (file != null && file.isFile()) {
            loadedLanguageFiles.add(locale);
            CubesideUtils.getInstance().getLogger().info("Loading locale " + locale);
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                JsonObject json = new Gson().fromJson(reader, JsonObject.class);
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    if (!entry.getValue().isJsonPrimitive()) {
                        continue;
                    }

                    String key = entry.getKey();
                    String value = entry.getValue().getAsString();

                    register(key, locale, new MessageFormat(value, locale));
                }
            } catch (IOException e) {
                CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Could not read language file " + file.getName(), e);
            }
        }
    }

    @Override
    public @org.jetbrains.annotations.Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        loadLanguage(locale);
        return translationValue(key, locale);
    }

    private static Locale parseLocale(String mojangName) {
        String[] parts = mojangName.split("_");
        if (parts.length == 1) {
            return new Locale(parts[0]);
        }
        return new Locale(parts[0], parts[1].toUpperCase());
    }
}