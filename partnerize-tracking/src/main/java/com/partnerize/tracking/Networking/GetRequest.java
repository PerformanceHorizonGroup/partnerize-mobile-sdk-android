package com.partnerize.tracking.Networking;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRequest implements IGetRequest {
    private URL url;

    GetRequest(URL url) {
        this.url = url;
    }

    @Override
    public void send(CompletableRequestWithResponse completableRequest) {
        try {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(20000);

            int status = connection.getResponseCode();


            InputStream inputStream;
            if(status >= 200 && status < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            inputStream));

            StringBuilder response = new StringBuilder();
            String currentLine;

            while ((currentLine = in.readLine()) != null)
                response.append(currentLine);

            in.close();

            completableRequest.complete(status, response.toString());
        } catch (Exception e) {
            completableRequest.error(e);
        }
    }
}
