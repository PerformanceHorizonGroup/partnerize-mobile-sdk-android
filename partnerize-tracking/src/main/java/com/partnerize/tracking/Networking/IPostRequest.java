package com.partnerize.tracking.Networking;

import com.partnerize.tracking.CompletableRequest;

/**
 * Interface to a network request.
 */
public interface IPostRequest {
    void send(String body, CompletableRequest completableRequest);
}
