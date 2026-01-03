package pc.ado;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

/**
 * Handles HTTP communication with Azure DevOps API. Encapsulates authentication
 * and request/response handling.
 */
public class AdoHttpClient {

    private final HttpClient httpClient;
    private final String encodedCredentials;

    public AdoHttpClient(String patToken) {
        this.httpClient = HttpClient.newHttpClient();
        this.encodedCredentials = encodeBasicAuth(patToken);
    }

    /**
     * Encodes credentials for HTTP Basic Authentication.
     */
    private String encodeBasicAuth(String patToken) {
        String credentials = ":" + patToken;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    /**
     * Sends a GET request to the specified URL and returns the response body.
     *
     * @param url the URL to request
     * @return the response body as a string
     * @throws Exception if the request fails
     */
    public String get(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + encodedCredentials)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("API request failed with status code: " + response.statusCode());
        }

        return response.body();
    }

    /**
     * Closes the HTTP client and releases resources.
     */
    public void close() {
        // HttpClient doesn't need explicit closure, but this method provides cleanup capability
    }
}
