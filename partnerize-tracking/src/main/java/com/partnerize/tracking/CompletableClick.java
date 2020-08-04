package com.partnerize.tracking;

import android.net.Uri;


public interface CompletableClick {
    void complete(Uri destination, String clickRef);
    void error(PartnerizeException exception);
}
