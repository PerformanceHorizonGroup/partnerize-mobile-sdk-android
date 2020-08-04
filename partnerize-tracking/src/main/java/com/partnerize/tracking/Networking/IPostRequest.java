package com.partnerize.tracking.Networking;

/**
 * Interface to a network request.
 */
public interface IPostRequest {
    void send(String body, CompletableRequest completableRequest);
}
