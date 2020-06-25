package com.partnerize.tracking.ClickManager;

import android.net.Uri;

import com.partnerize.tracking.Networking.CompletableRequestWithResponse;
import com.partnerize.tracking.Networking.IGetRequest;
import com.partnerize.tracking.Networking.RequestBuilder;

import java.net.MalformedURLException;
import java.net.URL;

public class VirtualClickManager {
    private VirtualClick.VirtualClickBuilder clickBuilder;

    public VirtualClickManager() {
        clickBuilder = new VirtualClick.VirtualClickBuilder();
    }

    protected RequestBuilder getRequestBuilder() {
        return new RequestBuilder();
    }

    public boolean isClickRequest(Uri uri) {
        if(uri == null) {
            return false;
        }
        return ClickHelper.isClickRequest(uri);
    }

    public void createVirtualClick(Uri uri, final CompletableClick completable) {

        try {
            Uri modifiedUrl = ClickHelper.addAPIModeToUri(uri);

            URL url = new URL(modifiedUrl.toString());

            RequestBuilder requestBuilder = getRequestBuilder();

            IGetRequest request = requestBuilder.buildGetRequest(url);

            request.send(new CompletableRequestWithResponse() {
                @Override
                public void complete(int status, String response) {
                    if(status >= 200 && status < 300) {
                        try {
                            VirtualClick click = clickBuilder.buildWithJSON(response);
                            completable.complete(click);
                        } catch (ClickException e) {
                            e.printStackTrace();
                            completable.error(e);
                        }
                    } else {
                        completable.error(new ClickException("Error Status: " + status));
                    }
                }

                @Override
                public void error(Exception ex) {
                    completable.error(new ClickException("Click request failed", ex));
                }
            });
        } catch (ClickException e) {
            completable.error(e);
        } catch (MalformedURLException e) {
            completable.error(new ClickException("Malformed URL", e));
        }
    }
}
