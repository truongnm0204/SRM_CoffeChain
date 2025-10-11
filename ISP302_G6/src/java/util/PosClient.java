package util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class PosClient {
    public static class PosToken {
        public final String token;
        public final Instant expiresAt;

        public PosToken(String token, Instant exp) {
            this.token = token;
            this.expiresAt = exp;
        }
    }

    private static final HttpClient http = HttpClient.newHttpClient();

    public static PosToken login(String authUrl, String clientId, String clientSecret) throws Exception {
        String body = "{\"client_id\":\"" + clientId + "\",\"client_secret\":\"" + clientSecret + "\"}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(authUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new RuntimeException("POS login failed: HTTP " + res.statusCode());
        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
        return new PosToken(json.get("token").getAsString(), Instant.now().plusSeconds(55 * 60));
    }

    public static String fetchOrders(String ordersUrl, String bearerToken) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(ordersUrl))
                .header("Authorization", "Bearer " + bearerToken)
                .GET()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new RuntimeException("Fetch orders failed: HTTP " + res.statusCode());
        return res.body();
    }
}
