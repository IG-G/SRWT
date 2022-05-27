package RemoteTestReporter;

import Enums.HttpMethod;
import JSON.JsonApiHandler;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.logging.Logger;


public class ConnectionClient {
    private final Logger log;
    private final CloseableHttpClient client;
    private final String username;
    private final String password;
    private String serverAddress;
    private String JWTToken;


    public ConnectionClient(String serverAddress, String username, String password) {
        log = Logger.getLogger(RemoteTestReporter.class.getName());
        initURIWithDashAtTheEnd(serverAddress);
        client = HttpClients.createDefault();

        this.username = username;
        this.password = password;
    }

    private void initURIWithDashAtTheEnd(String uri) {
        if (!uri.endsWith("/")) {
            serverAddress = uri + "/";
        } else
            serverAddress = uri;
    }

    private HttpUriRequestBase buildRequest(HttpMethod httpMethod, String endpoint, String data) {
        String fullURI = serverAddress + endpoint;
        HttpUriRequestBase request;
        switch (httpMethod) {
            case POST:
                request = new HttpPost(fullURI);
                break;
            case PUT:
                request = new HttpPut(fullURI);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        request.setEntity(new StringEntity(data));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        request.setHeader("Authorization", "Bearer " + JWTToken);
        return request;
    }

    private void raiseForStatus(CloseableHttpResponse response) throws IOException {
        if (response.getCode() >= 200 && response.getCode() < 300) {
            log.info("Response from server with code: " + response.getCode());
        } else {
            if (response.getCode() == 401 && JWTToken != null) {
                //token expired
                JWTToken = null;
            }
            throw new IOException("Error from server. Code: "
                    + response.getCode() + " Error message: " + getResponseBody(response));
        }
    }

    private String getResponseBody(CloseableHttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader
                (new InputStreamReader(
                        response.getEntity().getContent()));
        String line;
        StringBuilder responseStringBuilder = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            responseStringBuilder.append(line);
        }
        log.info("Response body from server: " + responseStringBuilder);
        return responseStringBuilder.toString();
    }

    private String getJWTToken() throws IOException, ParseException {
        HttpUriRequestBase request = new HttpPost(serverAddress + "token");
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/x-www-form-urlencoded");
        String entity = "grant_type=&username=" + username + "&password=" + password + "&scope=&client_id=&client_secret=";
        request.setEntity(new StringEntity(entity));
        CloseableHttpResponse response = client.execute(request);
        raiseForStatus(response);
        String body = getResponseBody(response);
        return JsonApiHandler.getTokenFromResponse(body);
    }

    public String sendRequest(HttpMethod httpMethod, String endpoint, String data) throws Exception {
        if (JWTToken == null) {
            JWTToken = getJWTToken();
        }
        log.fine("Http Method: " + httpMethod + ", Endpoint: " + endpoint + ", Data to be send to server: " + data);
        HttpUriRequestBase request = buildRequest(httpMethod, endpoint, data);
        log.fine("Request to server: " + request.getEntity());
        CloseableHttpResponse response = client.execute(request);
        raiseForStatus(response);
        return getResponseBody(response);
    }

    private HttpEntity createEntityWithImage(String pathToFile) throws FileNotFoundException {
        File f = new File(pathToFile);
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.addBinaryBody(
                "file",
                new FileInputStream(f),
                ContentType.APPLICATION_OCTET_STREAM,
                f.getName()
        );
        return entityBuilder.build();
    }

    public void sendImage(String endpoint, String path) throws IOException {
        log.fine("Send image to server. Endpoint: " + endpoint + " Path: " + path);
        HttpPut uploadFileRequest = new HttpPut(serverAddress + endpoint);
        HttpEntity entityWithImage = createEntityWithImage(path);
        uploadFileRequest.setEntity(entityWithImage);
        uploadFileRequest.setHeader("Authorization", "Bearer " + JWTToken);
        CloseableHttpResponse response = client.execute(uploadFileRequest);
        raiseForStatus(response);
    }
}
