package ReportTests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Logger;

import static java.time.temporal.ChronoUnit.SECONDS;

public class ConnectionClient {
    private String serverAddress;
    private final int timeoutInSeconds;
    private final HttpClient client;

    final java.net.http.HttpClient.Version http_version = HttpClient.Version.HTTP_1_1;

    public ConnectionClient(String serverAddress, int timeout) {
        initURIWithDashAtTheEnd(serverAddress);
        timeoutInSeconds = timeout;
        client = HttpClient.newHttpClient();
    }

    private void initURIWithDashAtTheEnd(String uri) {
        if (!uri.endsWith("/")) {
            serverAddress = uri + "/";
        } else
            serverAddress = uri;
    }

    private HttpRequest buildRequest(String httpMethod, String endpoint, String data) {
        String fullURI = serverAddress + endpoint;
        HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(URI.create(fullURI));
        request.version(http_version);
        request.timeout(Duration.of(timeoutInSeconds, SECONDS));
        request.headers("accept", "application/json",
                "accept", "application/text",
                "Content-Type", "application/json");
        HttpRequest.BodyPublisher bodyPublisher = null;
        if (data != null) {
            bodyPublisher = HttpRequest.BodyPublishers.
                    ofInputStream(() -> new ByteArrayInputStream(data.getBytes()));
        }
        switch (httpMethod) {
            case "POST":
                request.POST(bodyPublisher);
                break;
            case "PUT":
                request.PUT(bodyPublisher);
                break;
            case "DELETE":
                request.DELETE();
                break;
            case "GET":
                request.GET();
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return request.build();
    }

    public String sendGetRequest(String endpoint) throws Exception {
        HttpRequest request = buildRequest("GET", endpoint, "");
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new Exception("Error occurred when sending request: " + e.getMessage());
        }
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception("Error from server. CampaignStatus code: "
                    + response.statusCode() + " Error message: " + response.body());
        }
    }

    public String sendPostRequest(String endpoint, String data) throws Exception {
        HttpRequest request = buildRequest("POST", endpoint, data);
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new Exception("Error occurred when sending request: " + e.getMessage());
        }
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception("Error from server. CampaignStatus code: "
                    + response.statusCode() + " Error message: " + response.body());
        }
    }

    public String sendPutRequest(String endpoint, String data) throws Exception {
        HttpRequest request = buildRequest("PUT", endpoint, data);
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new Exception("Error occurred when sending request: " + e.getMessage());
        }
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception("Error from server. CampaignStatus code: "
                    + response.statusCode() + " Error message: " + response.body());
        }
    }
}
