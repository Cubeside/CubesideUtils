package de.iani.cubesideutils.adventure.translations;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.iani.cubesideutils.Pair;
import de.iani.cubesideutils.plugin.CubesideUtils;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TranslationLoader {
    static class MetaData {
        public long lastModfified;
        public String loadedVersion;
        public String loadedVersionTime;
        public String assetIndexHash;
        public Map<String, String> languageHashes = new HashMap<>();
    }

    public static void checkAndDownloadLangs(File langDir) {
        CubesideUtils utils = CubesideUtils.getInstance();
        Gson gson = new Gson();

        try (InputStream in = utils.getServerClassLoader().getResourceAsStream("assets/minecraft/lang/en_us.json")) {
            if (in != null) {
                Files.copy(in, new File(langDir, "en_us.json").toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                utils.getLogger().info("Could not find en_us.json");
            }
        } catch (IOException e1) {
            utils.getLogger().info("Could not extract en_us.json");
        }

        File metaFile = new File(langDir, "langmeta.json");

        MetaData metaData = null;
        if (metaFile.exists()) {
            try (Reader r = new FileReader(metaFile, StandardCharsets.UTF_8)) {
                metaData = gson.fromJson(r, MetaData.class);
            } catch (Exception ignored) {
            }
        }
        if (metaData == null) {
            metaData = new MetaData();
        }

        try {
            String mcVersion = utils.getMinecraftVersion(); // "1.21.11"
            langDir.mkdirs();

            if (!mcVersion.equals(metaData.loadedVersion)) {
                metaData.loadedVersionTime = null;
            }

            utils.getLogger().info("Checking lang files for Minecraft " + mcVersion + "...");

            Pair<JsonObject, Long> result = readJson(gson, "https://launchermeta.mojang.com/mc/game/version_manifest.json", metaData.lastModfified);
            if (result == null) {
                utils.getLogger().info("Lang files not changed or not online.");
                return; // not updated or not online
            }
            JsonObject versionManifest = result.first;
            metaData.lastModfified = result.second;

            String versionUrl = null;
            for (JsonElement e : versionManifest.getAsJsonArray("versions")) {
                JsonObject v = e.getAsJsonObject();
                if (v.get("id").getAsString().equals(mcVersion)) {
                    String versionUpdateTime = v.get("time").getAsString();
                    if (versionUpdateTime.equals(metaData.loadedVersionTime)) {
                        saveMetaData(gson, metaFile, metaData);
                        utils.getLogger().info("Version meta was not changed.");
                        return; // version not updated
                    }
                    versionUrl = v.get("url").getAsString();
                    metaData.loadedVersionTime = versionUpdateTime;
                    break;
                }
            }

            if (versionUrl == null) {
                throw new IllegalStateException("Could not find server version in version manifest: " + mcVersion);
            }

            JsonObject versionJson = readJson(versionUrl);
            JsonObject assetIndexEntry = versionJson.getAsJsonObject("assetIndex");
            String assetIndexHash = assetIndexEntry.get("sha1").getAsString();
            if (assetIndexHash.equals(metaData.assetIndexHash)) {
                saveMetaData(gson, metaFile, metaData);
                utils.getLogger().info("Asset index was not changed.");
                return; // asset index not updated
            }
            metaData.assetIndexHash = assetIndexHash;
            String assetIndexUrl = assetIndexEntry.get("url").getAsString();

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
                if (hash.equals(metaData.languageHashes.get(fileName))) {
                    continue; // lang file not changed
                }
                metaData.languageHashes.put(fileName, hash);

                File outFile = new File(langDir, fileName);

                String downloadUrl = "https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash;

                downloadFile(downloadUrl, outFile);
                count++;
            }
            saveMetaData(gson, metaFile, metaData);

            utils.getLogger().info("Fertig. " + count + " Sprachdateien heruntergeladen.");

        } catch (Exception e) {
            utils.getLogger().log(Level.SEVERE, "Fehler beim Laden der Lang-Dateien:", e);
        }
    }

    private static void saveMetaData(Gson gson, File metaDataFile, MetaData metaData) throws IOException {
        try (Writer writer = Files.newBufferedWriter(metaDataFile.toPath())) {
            gson.toJson(metaData, writer);
        }
    }

    public static Pair<JsonObject, Long> readJson(Gson gson, String url, long lastModified) throws IOException {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URI(url).toURL().openConnection();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IOException(e);
        }
        if (lastModified > 0) {
            conn.setIfModifiedSince(lastModified);
        }

        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(10_000);
        conn.connect();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
            return null;
        }

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Manifest HTTP " + conn.getResponseCode());
        }

        lastModified = conn.getLastModified();
        try (InputStream in = conn.getInputStream(); Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            return new Pair<>(gson.fromJson(reader, JsonObject.class), lastModified);
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
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
