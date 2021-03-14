package com.christianoette;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "*");
        headers.put("Access-Control-Allow-Methods", "*");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        String output = formatMessage(ZonedDateTime.now());
        return response
                .withStatusCode(200)
                .withBody(output);
    }

    // Known issue
    // You need a stringify version of the response in the proxy response.
    // The serialization is not yet implemented in a proper way
    protected String formatMessage(ZonedDateTime now) {
        return String.format("{ \\\"message\\\":\\\"hello world from graalvm\\\",\\\"now\\\":\\\"%s\\\"}", now);
    }

    public APIGatewayProxyResponseEvent handleRequest(String request) {
        return handleRequest(new APIGatewayProxyRequestEvent(), null);
    }
}
