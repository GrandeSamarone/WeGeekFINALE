package com.marlostrinidad.wegeek.nerdzone.EventBus;

public class HelloWorldEvent {
    private final String message;

    public HelloWorldEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}