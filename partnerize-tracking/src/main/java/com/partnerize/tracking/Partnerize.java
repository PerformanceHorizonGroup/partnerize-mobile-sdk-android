package com.partnerize.tracking;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.partnerize.tracking.ClickManager.ClickException;
import com.partnerize.tracking.ClickManager.CompletableClick;
import com.partnerize.tracking.ClickManager.VirtualClick;
import com.partnerize.tracking.ClickManager.VirtualClickManager;
import com.partnerize.tracking.Conversion.CompletableConversion;
import com.partnerize.tracking.Conversion.Conversion;
import com.partnerize.tracking.Conversion.ConversionException;
import com.partnerize.tracking.Storage.PartnerizePreferences;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public final class Partnerize implements IPartnerizeSDK {
    private PartnerizePreferences prefs;

    public Partnerize(Context context) {
        prefs = new PartnerizePreferences(context);
    }

    @Override
    public void beginConversion(URL url, final CompletableConversion completable) throws IllegalArgumentException  {
        if(url == null)
            throw new IllegalArgumentException("Parameter url must not be null");

        if(completable == null)
            throw new IllegalArgumentException("Parameter completable must not be null");


        VirtualClickManager manager = new VirtualClickManager();

        if(manager.isClickRequest(url)) {
            continueVirtualClick(manager, url, completable);
        }
    }

    @Override
    public void beginConversion(Intent intent, final CompletableConversion completable) throws IllegalArgumentException {
        if(intent == null)
            throw new IllegalArgumentException("Parameter intent must not be null");

        if(completable == null)
            throw new IllegalArgumentException("Parameter completable must not be null");

        VirtualClickManager manager = new VirtualClickManager();

        Uri uri = intent.getData();

        if(manager.isClickRequest(uri)) {
            URL url = null;
            try {
                url = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                completable.error(new ConversionException("Invalid Intent URI", e));
            }

            continueVirtualClick(manager, url, completable);
        }
    }

    private void continueVirtualClick(VirtualClickManager manager, URL url, final CompletableConversion completable) {
        manager.createVirtualClick(url, new CompletableClick() {
            @Override
            public void complete(VirtualClick click) {
                String clickRef = click.getClickref();

                prefs.setClickRef(clickRef);

                Conversion.Builder builder = new Conversion.Builder(clickRef);
                Conversion conversion = builder.build();
                completable.complete(conversion);
            }

            @Override
            public void error(ClickException ex) {
                completable.error(new ConversionException("Failed to create Conversion.", ex));
            }
        });
    }
}
