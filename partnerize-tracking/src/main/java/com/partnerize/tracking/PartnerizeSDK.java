package com.partnerize.tracking;

import android.net.Uri;

interface PartnerizeSDK {
    void beginConversion(Uri uri, final CompletableClick completable);

    void beginConversion(Uri uri, CompletableVirtualClick completable);

    boolean isClickRequest(Uri uri);

    String getClickRef();
}
