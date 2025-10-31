package com.example.roomBooking.exception;

import org.example.hotelbookingapi.DTO.StatusResponse;
import org.example.hotelbookingapi.exception.HotelAlreadyExistsException;
import org.example.hotelbookingapi.exception.ResourceNotFoundException;
import org.example.hotelbookingapi.exception.RoomAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StatusResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new StatusResponse("error", ex.getMessage()));
    }

    @ExceptionHandler(RoomAlreadyExistsException.class)
    public ResponseEntity<StatusResponse> handleRoomAlreadyExists(RoomAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new StatusResponse("error", ex.getMessage()));
    }

    @ExceptionHandler(HotelAlreadyExistsException.class)
    public ResponseEntity<StatusResponse> handleHotelAlreadyExists(HotelAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new StatusResponse("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StatusResponse> handleAllExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StatusResponse("error", "An unexpected error occurred: " + ex.getMessage()));
    }
}

