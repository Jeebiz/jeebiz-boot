/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.exception;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert;

@SuppressWarnings("serial")
public class PayloadExceptionEvent extends ApplicationEvent {

    private final Exception payload;

    /**
     * Create a new PayloadExceptionEvent.
     *
     * @param source  the object on which the event initially occurred (never {@code null})
     * @param payload the Exception object (never {@code null})
     */
    public PayloadExceptionEvent(Object source, Exception payload) {
        super(source);
        Assert.notNull(payload, "Payload must not be null");
        this.payload = payload;
    }

    /**
     * Return the payload of the event.
     */
    public Exception getPayload() {
        return this.payload;
    }

}
