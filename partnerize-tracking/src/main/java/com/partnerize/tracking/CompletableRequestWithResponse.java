package com.partnerize.tracking;

public interface CompletableRequestWithResponse {
    void complete(int status, String response);
    void error(Exception ex);
}
