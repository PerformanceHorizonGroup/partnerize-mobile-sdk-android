package com.partnerize.tracking.Networking;

public interface CompletableRequest {
    void complete(int status);
    void error(Exception ex);
}
