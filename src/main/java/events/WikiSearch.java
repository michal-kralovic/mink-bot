package events;

import org.jsoup.Jsoup;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class WikiSearch {
    public static CompletableFuture<String> search(String query) {
        String readyQuery = "";
        readyQuery = query.replace(' ', '_');

        char[] array = readyQuery.toCharArray();
        array[0] = Character.toUpperCase(array[0]);
        readyQuery = new String(array);

        // request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://en.wikipedia.org/wiki/" + readyQuery))
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                // Jsoup parses the body of the response and selects the first <p>, and extracts its text
                .thenApply(response -> Objects.requireNonNull(Objects.requireNonNull(Jsoup.parse(response.body()).select("div#mw-content-text > div > p:first-of-type").first()).text()));
    }
}
