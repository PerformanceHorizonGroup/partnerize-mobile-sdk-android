package com.partnerize.tracking.Networking;

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
    public IPostRequest buildPostRequest(URL url) {
        return new PostRequest(url);
    }

    /**
     * Create a new GET request.
     * @param url The endpoint.
     * @return The IRequest object.
     */
    public IGetRequest buildGetRequest(URL url) {
        return new GetRequest(url);
    }
}