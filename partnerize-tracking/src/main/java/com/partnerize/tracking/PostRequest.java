package com.partnerize.tracking;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostRequest implements IRequest {

    private URL url;

    PostRequest(URL url) {
        this.url = url;
    }

    public void send(String body, CompletableRequest completableRequest) {
        try {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(20000);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
            os.flush();
            os.close();

            int status = connection.getResponseCode();
            completableRequest.complete(status);
        } catch (Exception e) {
            completableRequest.error(e);
        }
    }
}
