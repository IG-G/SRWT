package Connection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class ConnectionClient {
    private final String baseUri;
    private final int timeout_in_seconds;
    private final HttpClient client;

    final java.net.http.HttpClient.Version http_version = HttpClient.Version.HTTP_1_1;

    public ConnectionClient(String baseUri, int timeout) {
        this.baseUri = baseUri;
        timeout_in_seconds = timeout;
        client = HttpClient.newHttpClient();
    }

    private String createFullURI(String endpoint) {
        String URIEndpoint;
        if(baseUri.endsWith("/")) {
            URIEndpoint = baseUri + endpoint;
        } else {
            URIEndpoint = baseUri + "/" + endpoint;
        }
        return URIEndpoint;
    }

    private HttpRequest buildRequest(String httpMethod, String endpoint, String data) {
        String fullURI = createFullURI(endpoint);
        HttpRequest.Builder request =  HttpRequest.newBuilder();
        request.uri(URI.create(fullURI));
        request.version(http_version);
        request.timeout(Duration.of(timeout_in_seconds, SECONDS));
        request.headers("accept", "application/json",
                        "Content-Type", "application/json");
        HttpRequest.BodyPublisher bodyPublisher = null;
        if(data != null) {
            bodyPublisher = HttpRequest.BodyPublishers.
                    ofInputStream(() -> new ByteArrayInputStream(data.getBytes()));
        }
        switch (httpMethod) {
            case "POST" -> request.POST(bodyPublisher);
            case "PUT" -> request.PUT(bodyPublisher);
            case "DELETE" -> request.DELETE();
            case "GET" -> request.GET();
            /*TODO Log error*/
            default -> throw new UnsupportedOperationException();
        }
        return request.build();
    }

    public String sendRequest(String httpMethod, String endpoint, String data) throws Exception {
        HttpRequest request = buildRequest(httpMethod, endpoint, data);
        HttpResponse<String> response;
        try {
           response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch(IOException | InterruptedException e) {
            System.out.println("Error when sending request");
            return null;
        }
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            //TODO log sm
            return response.body();
        } else {
            throw new Exception();
        }
    }
}
