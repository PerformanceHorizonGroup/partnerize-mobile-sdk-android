package com.partnerize.tracking.Mocks;

import com.partnerize.tracking.ClickManager.VirtualClickManager;
import com.partnerize.tracking.Networking.RequestBuilder;

public class MockVirtualClickManager extends VirtualClickManager {
    private RequestBuilder builder;

    public MockVirtualClickManager(RequestBuilder builder) {
        this.builder = builder;
    }

    @Override
    protected RequestBuilder getRequestBuilder() {
        return builder;
    }
}