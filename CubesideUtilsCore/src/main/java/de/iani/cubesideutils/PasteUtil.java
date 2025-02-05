package de.iani.cubesideutils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PasteUtil {
    private static final List<PasteProvider> PROVIDERS = new ArrayList<>();

    static {
        PROVIDERS.add(new FantaPasteProvider());
        PROVIDERS.add(new PasteDevProvider());
    }

    private PasteUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
    }

    public static void addProvider(PasteProvider provider) {
        PROVIDERS.add(provider);
    }

    public static void paste(String content, String language, PasteCompletedListener listener) {
        paste(content, language, "CubesideUtils", listener);
    }

    public static void paste(String content, String language, String userAgent, PasteCompletedListener listener) {
        if (content == null || listener == null) {
            throw new NullPointerException();
        }

        new Thread("Paste Thread") {
            @Override
            public void run() {
                for (PasteProvider provider : PROVIDERS) {
                    try {
                        String key = provider.uploadPaste(content, language, userAgent);
                        listener.onSuccess(provider.getPasteUrl(key));
                        return;
                    } catch (Exception e) {
                        // fallthrough
                    }
                }
                listener.onError(new IOException("All paste providers failed"));
            }
        }.start();
    }

    public interface PasteCompletedListener {
        void onSuccess(String url);

        void onError(Exception exception);
    }

    public interface PasteProvider {
        String uploadPaste(String content, String language, String userAgent) throws Exception;

        String getPasteUrl(String key);
    }

    private static class FantaPasteProvider implements PasteProvider {
        private static final URI UPLOAD_URI = URI.create("https://api.fpaste.de/post");
        private static final String PASTE_URL_FORMAT = "https://fpaste.de/%s";

        private final HttpClient client = HttpClient.newHttpClient();
        private final JSONParser jsonParser = new JSONParser();

        @Override
        public String uploadPaste(String content, String language, String userAgent) throws Exception {
            HttpRequest request = HttpRequest.newBuilder(UPLOAD_URI)
                    .header("Content-Type", "text/" + (language != null ? language : "plain"))
                    .header("User-Agent", userAgent)
                    .POST(HttpRequest.BodyPublishers.ofString(content))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String location = response.headers().firstValue("Location").orElse(null);
            if (location != null) {
                return location.substring(location.lastIndexOf('/') + 1);
            }

            try {
                JSONObject json = (JSONObject) jsonParser.parse(response.body());
                String key = (String) json.get("key");
                if (key == null) {
                    throw new IOException("Invalid response from paste service");
                }
                return key;
            } catch (ParseException | ClassCastException e) {
                throw new IOException("Failed to parse response", e);
            }
        }

        @Override
        public String getPasteUrl(String key) {
            return String.format(PASTE_URL_FORMAT, key);
        }
    }

    private static class PasteDevProvider implements PasteProvider {
        private static final URI UPLOAD_URI = URI.create("https://api.pastes.dev/post");
        private static final String PASTE_URL_FORMAT = "https://pastes.dev/%s";

        private final HttpClient client = HttpClient.newHttpClient();
        private final JSONParser jsonParser = new JSONParser();

        @Override
        public String uploadPaste(String content, String language, String userAgent) throws Exception {
            HttpRequest request = HttpRequest.newBuilder(UPLOAD_URI)
                    .header("Content-Type", "text/" + (language != null ? language : "plain"))
                    .header("User-Agent", userAgent)
                    .POST(HttpRequest.BodyPublishers.ofString(content))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String location = response.headers().firstValue("Location").orElse(null);
            if (location != null) {
                return location.substring(location.lastIndexOf('/') + 1);
            }

            try {
                JSONObject json = (JSONObject) jsonParser.parse(response.body());
                String key = (String) json.get("key");
                if (key == null) {
                    throw new IOException("Invalid response from paste service");
                }
                return key;
            } catch (ParseException | ClassCastException e) {
                throw new IOException("Failed to parse response", e);
            }
        }

        @Override
        public String getPasteUrl(String key) {
            return String.format(PASTE_URL_FORMAT, key);
        }
    }
}