package Main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Test {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        String sampleData = "{" +
                "\"name\": \"test\"," +
                " \"description\": \"string\"," +
                " \"price\": 0," +
                "\"tax\": 0" +
                "}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://127.0.0.1:8000/items/"))
                .version(HttpClient.Version.HTTP_1_1)
                .timeout(Duration.of(10, SECONDS))
                .headers("accept", "application/json",
                        "Content-Type", "application/json")

                .POST(HttpRequest.BodyPublishers
                .ofInputStream(() -> new ByteArrayInputStream(sampleData.getBytes())))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}
