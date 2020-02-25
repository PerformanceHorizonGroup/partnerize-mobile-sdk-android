package com.partnerize.tracking.Fingerprint;

public interface CompletableResult<T> {
    void complete(T result);
}
