package com.partnerize.tracking;

import android.net.Uri;

interface IPartnerizeSDK {
    void beginConversion(Uri uri, final CompletableClick completable);
}
