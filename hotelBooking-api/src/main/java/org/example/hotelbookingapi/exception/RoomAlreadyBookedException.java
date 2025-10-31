package org.example.hotelbookingapi.exception;

import java.time.LocalDateTime;
import java.util.List;

public class RoomAlreadyBookedException extends RuntimeException {
    public RoomAlreadyBookedException(LocalDateTime dateFrom, LocalDateTime dateTo, Object id) {
        super(String.format("Номер с ID %s с %s по %s уже забронирована", dateFrom, dateTo, id));
    }
}
