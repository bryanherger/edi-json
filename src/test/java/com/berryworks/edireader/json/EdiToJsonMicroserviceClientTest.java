package com.berryworks.edireader.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.junit.Test;

import com.berryworks.edireader.benchmark.EDITestData;

import static com.berryworks.edireader.json.ResourceUtil.getResourceAsString;
import static org.junit.Assert.assertEquals;

public class EdiToJsonMicroserviceClientTest {

    public static final String SERVER = "kxfqaddyyc.execute-api.us-east-2.amazonaws.com";
    public static final String CONTEXT_PATH = "Preview/berryworks";
    public static final String SERVICE = "edi-to-json";

    private EdiToJsonMicroserviceClient client;
    private HttpResponse response;

    @Test
    public void canConstruct() {
        client = new EdiToJsonMicroserviceClient(SERVER, CONTEXT_PATH, SERVICE);
        assertEquals(SERVER, client.getServer());
        assertEquals(CONTEXT_PATH, client.getContextPath());
        assertEquals(SERVICE, client.getService());
    }

    @Test
    public void canExecuteWithEdiString() throws IOException {
        // Setup
        client = new EdiToJsonMicroserviceClient(SERVER, CONTEXT_PATH, SERVICE);
        String ediText = getResourceAsString("824.edi");

        // Call the microservice
        response = client.execute(ediText);

        // Confirm response
        assertEquals(200, response.getStatusLine().getStatusCode());
        final HttpEntity entity = response.getEntity();
        assertEquals("application/json", entity.getContentType().getValue());
        final String actual = asString(entity.getContent());
        final String expected = getResourceAsString("824.json");
        assertEquals(expected, actual);
    }

    @Test
    public void canExecuteWithEdifact() throws IOException {
        // Setup
        client = new EdiToJsonMicroserviceClient(SERVER, CONTEXT_PATH, SERVICE);
        String ediText = getResourceAsString("INVOIC.edi");

        // Call the microservice
        response = client.execute(ediText);

        // Confirm response
        assertEquals(200, response.getStatusLine().getStatusCode());
        final HttpEntity entity = response.getEntity();
        assertEquals("application/json", entity.getContentType().getValue());
        final String actual = asString(entity.getContent());
        final String expected = getResourceAsString("INVOIC.json");
        assertEquals(expected, actual);
    }

    @Test
    public void canExecuteWithSize21K() throws IOException {
        // Setup
        client = new EdiToJsonMicroserviceClient(SERVER, CONTEXT_PATH, SERVICE);
        String ediText = EDITestData.getAnsiInterchange(36);
        System.out.println("EDI input of size " + (ediText.length() / 1000.0) + " K");

        // Call the microservice
        response = client.execute(ediText);

        // Confirm response
        assertEquals(502, response.getStatusLine().getStatusCode());
        final HttpEntity entity = response.getEntity();
        assertEquals("application/json", entity.getContentType().getValue());
    }

    private String asString(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        char[] buffer = new char[1000];
        try (InputStreamReader isr = new InputStreamReader(inputStream)) {
            int n;
            while ((n = isr.read(buffer)) > -1) {
                result.append(buffer, 0, n);
            }
        }
        return result.toString();
    }
}
