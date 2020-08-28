package com.partnerize.tracking.Mocks;

import com.partnerize.tracking.Networking.CompletableRequestWithResponse;
import com.partnerize.tracking.Networking.IGetRequest;

public class MockGetRequest implements IGetRequest {

    private final int status;
    private final String response;

    public MockGetRequest(int status, String response) {
        this.status = status;
        this.response = response;
    }

    @Override
    public void send(CompletableRequestWithResponse completableRequest) {
        completableRequest.complete(status, response);
    }
}