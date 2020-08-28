package com.partnerize.tracking.Networking;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRequest extends AsyncTask<Void, Void, AsyncResponse> implements IGetRequest {

    private URL url;
    private CompletableRequestWithResponse completable;

    GetRequest(URL url) {
        this.url = url;
    }

    @Override
    public void send(final CompletableRequestWithResponse completableRequest) {
        this.completable = completableRequest;
        this.execute();
    }


    @Override
    protected AsyncResponse doInBackground(Void... voids) {

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
            AsyncResponse r = new AsyncResponse(status, response.toString());
            return r;
        } catch (Exception e) {
            AsyncResponse r = new AsyncResponse(e);
            return r;
        }
    }

    @Override
    protected void onPostExecute(AsyncResponse asyncResponse) {
        if(asyncResponse.getException() != null) {
            completable.error(asyncResponse.getException());
        } else {
            completable.complete(asyncResponse.getStatus(), asyncResponse.getBody());
        }
    }
}
