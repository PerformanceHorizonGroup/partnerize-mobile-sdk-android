package com.partnerize.tracking;

/**
 * Interface to a network request.
 */
public interface IRequest {
    void send(String body, CompletableRequest completableRequest);
}
