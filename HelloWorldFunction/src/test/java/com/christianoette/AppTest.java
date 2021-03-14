package com.christianoette;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;


public class AppTest {

  private final Logger logger = LoggerFactory.getLogger(AppTest.class);


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
}
