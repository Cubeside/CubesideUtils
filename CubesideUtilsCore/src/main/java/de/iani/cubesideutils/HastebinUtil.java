package de.iani.cubesideutils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HastebinUtil {
    private HastebinUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    /**
     * Paste some text to the hastebin paste service.
     *
     * @param content
     *            The text to post
     * @param listener
     *            The listener that will be called when the paste is completed
     */
    public static void paste(String content, PasteCompletedListener listener) {
        if (content == null) {
            throw new NullPointerException("content");
        }
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        new Thread("Hastebin Paste Thread") {
            @Override
            public void run() {
                HttpClient client = HttpClient.newHttpClient();
                try {
                    HttpRequest request = HttpRequest.newBuilder(URI.create("https://cpaste.de/documents")).POST(HttpRequest.BodyPublishers.ofString(content)).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String content = response.body();
                    JSONObject responseJson = (JSONObject) new JSONParser().parse(content);
                    String key = (String) responseJson.get("key");
                    if (key != null) {
                        listener.onSuccess("https://cpaste.de/" + key);
                        return;
                    } else {
                        // fallthrough listener.onError(new IOException("No key was returned"));
                    }
                } catch (IOException | InterruptedException | ParseException | ClassCastException ex) {
                    // fallthrough
                }
                try {
                    HttpRequest request = HttpRequest.newBuilder(URI.create("https://hastebin.com/documents")).POST(HttpRequest.BodyPublishers.ofString(content)).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String content = response.body();
                    JSONObject responseJson = (JSONObject) new JSONParser().parse(content);
                    String key = (String) responseJson.get("key");
                    if (key != null) {
                        listener.onSuccess("https://hastebin.com/" + key);
                        return;
                    } else {
                        // fallthrough listener.onError(new IOException("No key was returned"));
                    }
                } catch (IOException | InterruptedException | ParseException | ClassCastException ex) {
                    // fallthrough
                }
                try {
                    HttpRequest request = HttpRequest.newBuilder(URI.create("https://hasteb.in/documents")).POST(HttpRequest.BodyPublishers.ofString(content)).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String content = response.body();
                    JSONObject responseJson = (JSONObject) new JSONParser().parse(content);
                    String key = (String) responseJson.get("key");
                    if (key != null) {
                        listener.onSuccess("https://hasteb.in/" + key);
                    } else {
                        listener.onError(new IOException("No key was returned"));
                    }
                } catch (IOException | InterruptedException | ParseException | ClassCastException ex) {
                    listener.onError(ex);
                }
            }
        }.start();
    }

    public static interface PasteCompletedListener {
        /**
         * Called asyncronously when the paste has been posted
         *
         * @param url
         *            The url for the paste
         */
        public void onSuccess(String url);

        /**
         * Called asyncronously when the paste could not be posted
         *
         * @param exception
         *            The exception that occured
         */
        public void onError(Exception exception);
    }
}
