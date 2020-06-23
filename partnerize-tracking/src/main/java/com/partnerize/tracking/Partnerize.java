package com.partnerize.tracking;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.partnerize.tracking.ClickManager.ClickException;
import com.partnerize.tracking.ClickManager.VirtualClick;
import com.partnerize.tracking.ClickManager.VirtualClickManager;
import com.partnerize.tracking.Storage.PartnerizePreferences;


import java.util.HashSet;
import java.util.Hashtable;

public final class Partnerize implements IPartnerizeSDK {
    private PartnerizePreferences prefs;

    public Partnerize(Context context) {
        prefs = new PartnerizePreferences(context);
    }

    @Override
    public void beginConversion(Uri uri, final CompletableClick completable) throws IllegalArgumentException {
        if(uri == null)
            throw new IllegalArgumentException("Parameter uri must not be null");

        if(completable == null)
            throw new IllegalArgumentException("Parameter completable must not be null");

        VirtualClickManager manager = new VirtualClickManager();


        if(manager.isClickRequest(uri)) {

            manager.createVirtualClick(uri, new com.partnerize.tracking.ClickManager.CompletableClick() {
                @Override
                public void complete(VirtualClick click) {
                    String clickRef = click.getClickref();

                    prefs.setClickRef(clickRef);

                    completable.complete(Uri.parse(click.getDestination()), clickRef);
                }

                @Override
                public void error(ClickException ex) {
                    completable.error(new PartnerizeException("Failed to create click.", ex));
                }
            });
        } else {
            UriClickRef result = filterClickRef(uri);

            if(result.clickRef != null && result.clickRef.length() > 0) {
                prefs.setClickRef(result.clickRef);
            } else {
                Log.i("PARTNERIZE", "No clickRef received.");
            }

            completable.complete(result.uri, result.clickRef);
        }
    }

    static class UriClickRef {
        private Uri uri;
        private String clickRef;

        UriClickRef(Uri uri, String clickRef) {
            this.uri = uri;
            this.clickRef = clickRef;
        }
    }

    private UriClickRef filterClickRef(Uri uri) {

        String value = uri.getQueryParameter("app_clickref");

        UriClickRef result;
        if(value != null)
        {
            // temp store exiting params
            HashSet<String> queryParams = new HashSet<>(uri.getQueryParameterNames());

            // we don't need this
            queryParams.remove("app_clickref");

            Hashtable<String, String> map = new Hashtable<>();

            // store all other params for remapping
            for (String param : queryParams) {
                map.put(param, uri.getQueryParameter(param));
            }

            Uri.Builder builder = uri.buildUpon().clearQuery();

            // reconstruct the query string
            for (String param : map.keySet()) {
                builder.appendQueryParameter(param, map.get(param));
            }

            uri = builder.build();
        }

        return new UriClickRef(uri, value);
    }

}
