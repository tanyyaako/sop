package org.example.hotelbookingapi.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Object id) {
        super(String.format("%s с ID %s не найден", resource, id));
    }
}

