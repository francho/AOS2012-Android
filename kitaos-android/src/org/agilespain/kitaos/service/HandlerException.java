package org.agilespain.kitaos.service;

import java.io.IOException;

/**
 * Handler Exception
 */
class HandlerException extends IOException {
    public HandlerException(String message) {
        super(message);
    }

    public HandlerException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    @Override
    public String toString() {
        if (getCause() != null) {
            return getLocalizedMessage() + ": " + getCause();
        } else {
            return getLocalizedMessage();
        }
    }
}
