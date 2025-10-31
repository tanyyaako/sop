package org.example.hotelbookingapi.DTO;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;

@Relation(collectionRelation = "unbooks", itemRelation = "unbookRoom")
public class UnbookRoomResponse extends RepresentationModel<UnbookRoomResponse> {
    private final Long bookingId;
    private final Long hotelId;
    private final Long roomId;
    private final String documentNumber;
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final LocalDateTime unbookingTime;

    public UnbookRoomResponse(Long bookingId, Long hotelId, Long roomId, String documentNumber,
                              LocalDateTime from, LocalDateTime to, LocalDateTime unbookingTime) {
        this.bookingId = bookingId;
        this.hotelId = hotelId;
        this.roomId = roomId;
        this.documentNumber = documentNumber;
        this.from = from;
        this.to = to;
        this.unbookingTime = unbookingTime;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public LocalDateTime getUnbookingTime() {
        return unbookingTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UnbookRoomResponse that = (UnbookRoomResponse) o;
        return Objects.equals(bookingId, that.bookingId) && Objects.equals(hotelId, that.hotelId) && Objects.equals(roomId, that.roomId) && Objects.equals(documentNumber, that.documentNumber) && Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(unbookingTime, that.unbookingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bookingId, hotelId, roomId, documentNumber, from, to, unbookingTime);
    }
}
