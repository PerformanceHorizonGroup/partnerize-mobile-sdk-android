package com.partnerize.tracking.Networking;

public interface CompletableRequestWithResponse {
    void complete(int status, String response);
    void error(Exception ex);
}
