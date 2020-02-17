package com.partnerize.tracking;

public class FingerprintException extends Exception {
    public FingerprintException(String message)  {
        super(message);
    }

    public FingerprintException(String message, Throwable cause)  {
        super(message, cause);
    }
}
