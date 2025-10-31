package org.example.hotelbookingapi.exception;

public class RoomAlreadyExistsException extends RuntimeException{
    public RoomAlreadyExistsException(String number, String hotelId) {
        super("Room with number = " + number + " and with hotelId = " + hotelId  + " already exists");
    }
}
