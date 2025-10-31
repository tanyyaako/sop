package org.example.hotelbookingapi.exception;

public class HotelAlreadyExistsException extends RuntimeException{
    public HotelAlreadyExistsException(String address) {
        super("Hotel with address='" + address + "' already exists");
    }
}
