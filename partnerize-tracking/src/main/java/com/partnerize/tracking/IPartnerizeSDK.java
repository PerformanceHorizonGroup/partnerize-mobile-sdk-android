package com.partnerize.tracking;

import android.content.Intent;

import com.partnerize.tracking.Conversion.CompletableConversion;

import java.net.URL;

interface IPartnerizeSDK {
    void beginConversion(URL url, CompletableConversion completable);

    void beginConversion(Intent intent, CompletableConversion completable);
}
