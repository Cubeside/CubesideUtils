package de.iani.cubesideutils.adventure.translations;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.iani.cubesideutils.plugin.CubesideUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;

public class TranslationLoader {
    public static void checkAndDownloadLangs() {
        CubesideUtils utils = CubesideUtils.getInstance();
        try {
            String mcVersion = utils.getMinecraftVersion(); // "1.21.11"
            File langDir = new File(utils.getDataFolder(), "langs/" + mcVersion);

            if (langDir.exists() && langDir.isDirectory() && langDir.listFiles() != null) {
                // plugin.getLogger().info("Lang-Dateien für " + mcVersion + " bereits vorhanden.");
                return;
            }

            utils.getLogger().info("Lade Lang-Dateien für Minecraft " + mcVersion + " herunter...");
            langDir.mkdirs();

            JsonObject versionManifest = readJson(
                    "https://launchermeta.mojang.com/mc/game/version_manifest.json");

            String versionUrl = null;
            for (JsonElement e : versionManifest.getAsJsonArray("versions")) {
                JsonObject v = e.getAsJsonObject();
                if (v.get("id").getAsString().equals(mcVersion)) {
                    versionUrl = v.get("url").getAsString();
                    break;
                }
            }

            if (versionUrl == null) {
                throw new IllegalStateException("Server-Version nicht im Manifest gefunden: " + mcVersion);
            }

            JsonObject versionJson = readJson(versionUrl);
            String assetIndexUrl = versionJson
                    .getAsJsonObject("assetIndex")
                    .get("url")
                    .getAsString();

            JsonObject assetIndex = readJson(assetIndexUrl);
            JsonObject objects = assetIndex.getAsJsonObject("objects");

            int count = 0;

            for (Map.Entry<String, JsonElement> entry : objects.entrySet()) {
                String path = entry.getKey();

                if (!path.startsWith("minecraft/lang/") || !path.endsWith(".json")) {
                    continue;
                }

                JsonObject obj = entry.getValue().getAsJsonObject();
                String hash = obj.get("hash").getAsString();

                String fileName = path.substring(path.lastIndexOf('/') + 1);
                File outFile = new File(langDir, fileName);

                if (outFile.exists()) {
                    continue;
                }

                String downloadUrl = "https://resources.download.minecraft.net/"
                        + hash.substring(0, 2) + "/"
                        + hash;

                downloadFile(downloadUrl, outFile);
                count++;
            }

            utils.getLogger().info("Fertig. " + count + " Sprachdateien heruntergeladen.");

        } catch (Exception e) {
            utils.getLogger().log(Level.SEVERE, "Fehler beim Laden der Lang-Dateien:", e);
        }
    }

    private static JsonObject readJson(String url) throws IOException, URISyntaxException {
        try (InputStream in = new URI(url).toURL().openStream();
                Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            return new JsonParser().parse(reader).getAsJsonObject();
        }
    }

    private static void downloadFile(String url, File target) throws IOException, URISyntaxException {
        try (InputStream in = new URI(url).toURL().openStream()) {
            Files.copy(in, target.toPath());
        }
    }
}
