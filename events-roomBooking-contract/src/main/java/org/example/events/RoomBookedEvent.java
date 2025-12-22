package org.example.events;

import java.io.Serializable;
import java.time.LocalDateTime;

public record RoomBookedEvent(
        Long bookingId,
        Long roomId,
        Long hotelId,
        String documentNumber,
        String userName,
        String userSurname,
        String from,
        String to,
        String bookedAt,
        Double price,
        String currency
) implements Serializable {
}
