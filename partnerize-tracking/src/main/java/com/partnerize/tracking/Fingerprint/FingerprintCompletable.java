package com.partnerize.tracking.Fingerprint;

public interface FingerprintCompletable extends Completable {
    void fail(FingerprintException ex);
}
