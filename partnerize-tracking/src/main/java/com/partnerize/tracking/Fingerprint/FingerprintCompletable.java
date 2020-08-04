package com.partnerize.tracking.Fingerprint;

import com.partnerize.tracking.Completable;

public interface FingerprintCompletable extends Completable {
    void fail(FingerprintException ex);
}
