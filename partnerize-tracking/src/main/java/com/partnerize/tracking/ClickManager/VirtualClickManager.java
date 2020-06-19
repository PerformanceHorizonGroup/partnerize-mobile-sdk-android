package com.partnerize.tracking.ClickManager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.partnerize.tracking.Conversion.Conversion;
import com.partnerize.tracking.Networking.CompletableRequestWithResponse;
import com.partnerize.tracking.Networking.IGetRequest;
import com.partnerize.tracking.Networking.RequestBuilder;
import com.partnerize.tracking.Storage.PartnerizePreferences;

import java.net.URL;

public class VirtualClickManager {
    private RequestBuilder requestBuilder;
    private VirtualClick.VirtualClickBuilder clickBuilder;

    public VirtualClickManager() {
        requestBuilder = new RequestBuilder();
        clickBuilder = new VirtualClick.VirtualClickBuilder();
    }
    public boolean isClickRequest(Uri uri) {
        return ClickHelper.isClickRequest(uri);
    }

    public boolean isClickRequest(URL url) {
        return ClickHelper.isClickRequest(url);
    }

    public void createVirtualClick(URL url, final CompletableClick completable) {

        try {
            URL modifiedUrl = ClickHelper.addAPIModeToUri(url);

            IGetRequest request = requestBuilder.buildGetRequest(modifiedUrl);
            request.send(new CompletableRequestWithResponse() {
                @Override
                public void complete(int status, String response) {
                    try {
                        VirtualClick click = clickBuilder.buildWithJSON(response);
                        completable.complete(click);
                    } catch (ClickException e) {
                        e.printStackTrace();
                        completable.error(e);
                    }
                }

                @Override
                public void error(Exception ex) {
                    completable.error(new ClickException("Click request failed", ex));
                }
            });
        } catch (ClickException e) {
            completable.error(e);
        }
    }
}
