package com.christianoette;

import com.squareup.okhttp.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Bootstrap for custom lambda runtime
 */

public class Bootstrap {

    public static final String RUNTIME_INVOCATION = "/2018-06-01/runtime/invocation";
    public static final String RUNTIME_INVOCATION_NEXT = RUNTIME_INVOCATION + "/next";
    public static final String LAMBDA_RUNTIME_AWS_REQUEST_ID_HEADER_KEY = "Lambda-Runtime-Aws-Request-Id";

    // For unit tests
    private boolean runEndless = true;
    private String runtimeAPIHostPort;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public void setRunEndless(boolean runEndless) {
        this.runEndless = runEndless;
    }

    public void setRuntimeAPIHostPort(String runtimeAPIHostPort) {
        this.runtimeAPIHostPort = runtimeAPIHostPort;
    }

    public void execute() {

        if (runtimeAPIHostPort==null) {
            runtimeAPIHostPort = System.getenv("AWS_LAMBDA_RUNTIME_API");
        }
        var endPointGet = "http://" + runtimeAPIHostPort + RUNTIME_INVOCATION_NEXT;
        var templateEndPointPostOk = "http://" + runtimeAPIHostPort + "/2018-06-01/runtime/invocation/{AwsRequestId}/response";
        var templateEndPointPostError = "http://" + runtimeAPIHostPort + "/2018-06-01/runtime/invocation/{AwsRequestId}/error";
        var endPointPostInitError = "http://" + runtimeAPIHostPort + "/2018-06-01/runtime/init/error";
        var lambdaARN = System.getenv("Lambda-Runtime-Invoked-Function-Arn");
        OkHttpClient client;

        // Known issue
        // The configured value for the handler is currently ignored.
        // var handlerClassName = System.getenv("_HANDLER").split("\\.")[0];
        // var handlerMethodName = System.getenv("_HANDLER").split("\\.")[1];
        App handler;

        try {
            handler = new App();
            client = new OkHttpClient();
        } catch (Exception e) {
            sendError(endPointPostInitError, RequestBody.create(JSON, "Init failure "+getStackTrace(e)));
            return;
        }

        do {
            try {
                Request request = new Request.Builder()
                        .url(endPointGet)
                        .get()
                        .build();
                Response response = client.newCall(request).execute();


                var requestId = response.header(LAMBDA_RUNTIME_AWS_REQUEST_ID_HEADER_KEY);
                var endPointPostOk = templateEndPointPostOk.replace("{AwsRequestId}", requestId);
                var handlerResponse = handler.handleRequest(response.body().toString());


                // Known issue
                // Use hardcoded proxyResponse for now

                String proxyResponse = "{" +
                        "    \"isBase64Encoded\": false," +
                        "    \"statusCode\": 200," +
                        "    \"headers\": { }," +
                        "    \"body\": \""+handlerResponse.getBody() +"\"" + "}";

                RequestBody body = RequestBody.create(JSON, proxyResponse);
                client.newCall(new Request.Builder()
                        .url(endPointPostOk)
                        .post(body)
                        .build()).execute();

            } catch (final IOException exception) {
                RequestBody body = RequestBody.create(JSON, getStackTrace(exception));
                sendError(templateEndPointPostError, body);
            }
        }
        while (runEndless);
    }

    private void sendError(String url, RequestBody body) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            client.newCall(request).execute();
        } catch (IOException ignored) { }
    }

    public String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.execute();
    }
}