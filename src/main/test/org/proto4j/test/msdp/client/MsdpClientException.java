package org.proto4j.test.msdp.client; //@date 18.09.2022

public class MsdpClientException extends RuntimeException {

    public MsdpClientException() {
    }

    public MsdpClientException(String message) {
        super(message);
    }

    public MsdpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public MsdpClientException(Throwable cause) {
        super(cause);
    }

    public MsdpClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
