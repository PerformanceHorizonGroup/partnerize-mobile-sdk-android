package com.partnerize.tracking;

public class PartnerizeException extends Exception {
    public PartnerizeException(String message)  {
        super(message);
    }

    public PartnerizeException(String message, Throwable cause)  {
        super(message, cause);
    }
}
