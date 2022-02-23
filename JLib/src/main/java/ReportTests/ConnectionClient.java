package ReportTests;

import Enums.HttpMethod;

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
    private final Logger log;
    private String serverAddress;
    private final int timeoutInSeconds;
    private final HttpClient client;

    final java.net.http.HttpClient.Version http_version = HttpClient.Version.HTTP_1_1;

    public ConnectionClient(String serverAddress, int timeout) {
        log = Logger.getLogger(RemoteTestReporter.class.getName());
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

    private HttpRequest buildRequest(HttpMethod httpMethod, String endpoint, String data) {
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
            case POST:
                request.POST(bodyPublisher);
                break;
            case PUT:
                request.PUT(bodyPublisher);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return request.build();
    }

    public String sendRequest(HttpMethod httpMethod, String endpoint, String data) throws Exception {
        if (data != null)
            log.info("Data to be send to server: " + data);
        HttpRequest request = buildRequest(httpMethod, endpoint, data);
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new Exception("Error occurred when sending request: " + e.getMessage());
        }
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            String responseBody = response.body();
            if (!responseBody.equals("null"))
                log.info("Response from server: " + responseBody);
            return responseBody;
        } else {
            throw new Exception("Error from server. CampaignStatus code: "
                    + response.statusCode() + " Error message: " + response.body());
        }
    }
}
