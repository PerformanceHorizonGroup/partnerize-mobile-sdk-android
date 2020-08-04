package com.partnerize.tracking.ClickManager;

public class ClickException extends Exception {
    public ClickException(String message)  {
        super(message);
    }

    public ClickException(String message, Throwable cause)  {
        super(message, cause);
    }
}