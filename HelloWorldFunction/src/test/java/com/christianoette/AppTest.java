package com.christianoette;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;


public class AppTest {

    public static final int PORT = 18089;
    private final Logger logger = LoggerFactory.getLogger(AppTest.class);

    @BeforeAll
    public static void set() {
        System.setProperty("AWS_LAMBDA_RUNTIME_API", "todo");
    }

    @Test
    public void successfulResponse() {
        App app = new App();
        APIGatewayProxyResponseEvent result = app.handleRequest(null, null);
        assertEquals(result.getStatusCode().intValue(), 200);
        assertEquals(result.getHeaders().get("Content-Type"), "application/json");
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\\\"message\\\""));
        assertTrue(content.contains("\\\"hello world from graalvm\\\""));
        assertTrue(content.contains("\\\"now\\\""));
    }

    @Test
    public void testLogging() {
        logger.info("Logger is available in classpath");
    }

    @Test
    public void testSuccesfullInvocation() {
        WireMockServer wireMockServer = new WireMockServer(options().port(PORT));
        wireMockServer.start();

        String requestId = "REQUEST-ID-12345";
        wireMockServer.stubFor(WireMock.get(urlEqualTo(Bootstrap.RUNTIME_INVOCATION_NEXT))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withHeader(Bootstrap.LAMBDA_RUNTIME_AWS_REQUEST_ID_HEADER_KEY, requestId)
                        .withBody("{}")));

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.setRunEndless(false);
        bootstrap.setRuntimeAPIHostPort("localhost:"+PORT);
        bootstrap.execute();

        UrlPattern urlPattern = urlEqualTo(Bootstrap.RUNTIME_INVOCATION + "/" + requestId + "/response");
        wireMockServer.verify(exactly(1), WireMock.postRequestedFor(urlPattern));
        wireMockServer.stop();

    }
}
