package com.partnerize.tracking;

import android.net.Uri;

interface PartnerizeSDK {
    void beginConversion(Uri uri, final CompletableClick completable);
    String getClickRef();
}
