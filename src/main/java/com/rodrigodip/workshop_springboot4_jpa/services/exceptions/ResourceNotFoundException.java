package com.rodrigodip.workshop_springboot4_jpa.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Object id) {
        super("Resource not found. ID: " + id);
    }
}
