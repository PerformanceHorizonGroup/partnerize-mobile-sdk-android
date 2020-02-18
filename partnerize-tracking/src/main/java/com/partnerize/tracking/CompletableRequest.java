package com.partnerize.tracking;

public interface CompletableRequest {
    void complete(int status);
    void error(Exception ex);
}
