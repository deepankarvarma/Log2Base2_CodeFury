package com.hsbc.exception.subscription;

public class SubscriptionInactiveException extends RuntimeException{
    public SubscriptionInactiveException() {
    }

    public SubscriptionInactiveException(String message) {
        super(message);
    }

    public SubscriptionInactiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubscriptionInactiveException(Throwable cause) {
        super(cause);
    }

    public SubscriptionInactiveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
