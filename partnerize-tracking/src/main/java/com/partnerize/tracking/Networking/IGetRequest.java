package com.partnerize.tracking.Networking;

import com.partnerize.tracking.CompletableRequestWithResponse;

public interface IGetRequest {
    void send(CompletableRequestWithResponse completableRequest);
}
