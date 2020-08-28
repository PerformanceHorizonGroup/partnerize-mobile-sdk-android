package com.partnerize.tracking.Networking;

class AsyncResponse {
    private int status;
    private String body;
    private Exception exception;

    public AsyncResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }

    public AsyncResponse(Exception exception) {
        this.exception = exception;
    }


    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public Exception getException() {
        return exception;
    }
}
