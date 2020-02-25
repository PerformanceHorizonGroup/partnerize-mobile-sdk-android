package com.partnerize.tracking;

import java.net.URL;

/**
 * Builder class used to construct network requests.
 */
public class RequestBuilder {
    /**
     * Create a new POST request.
     * @param url The endpoint.
     * @return The IRequest object.
     */
    public IRequest buildPostRequest(URL url) {
        return new PostRequest(url);
    }
}