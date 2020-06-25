package com.partnerize.tracking.Mocks;

import com.partnerize.tracking.Networking.IGetRequest;
import com.partnerize.tracking.Networking.RequestBuilder;

import java.net.URL;

public class MockRequestBuilder extends RequestBuilder {
    private IGetRequest get;

    public void setGetRequest(IGetRequest request) {
        get = request;
    }

    @Override
    public IGetRequest buildGetRequest(URL url) {
        return get;
    }
}