package com.rodrigodip.workshop_springboot4_jpa.services.exceptions;

public class DataBaseException extends RuntimeException {
    public DataBaseException(String msg) {
        super(msg);
    }
}
