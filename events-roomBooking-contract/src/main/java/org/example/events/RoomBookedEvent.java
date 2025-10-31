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
        LocalDateTime from,
        LocalDateTime to,
        LocalDateTime bookedAt
) implements Serializable {
}
