package org.example.hotelbookingapi.DTO;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;

@Relation(collectionRelation = "bookings", itemRelation = "bookRoom")
public class BookRoomResponse extends RepresentationModel<BookRoomResponse> {
    private final Long id;
    private final Long roomId;
    private final Long hotelId;
    private final LocalDateTime bookedAt;
    private final String numberDocument;
    private final String userName;
    private final String userSurname;
    private final String userNumber;
    private final String userCountry;
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final Double price;

    public BookRoomResponse(LocalDateTime bookedAt, Long id, Long roomId, Long hotelId, String numberDocument, String userName,
                            String userSurname, String userNumber, String userCountry, LocalDateTime from, LocalDateTime to) {
        this(bookedAt, id, roomId, hotelId, numberDocument, userName, userSurname, userNumber, userCountry, from, to, null);
    }

    public BookRoomResponse(LocalDateTime bookedAt, Long id, Long roomId, Long hotelId, String numberDocument, String userName,
                            String userSurname, String userNumber, String userCountry, LocalDateTime from, LocalDateTime to, Double price) {
        this.bookedAt = bookedAt;
        this.id = id;
        this.roomId = roomId;
        this.hotelId = hotelId;
        this.numberDocument = numberDocument;
        this.userName = userName;
        this.userSurname = userSurname;
        this.userNumber = userNumber;
        this.userCountry = userCountry;
        this.from = from;
        this.to = to;
        this.price = price;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public Long getId() {
        return id;
    }

    public String getNumberDocument() {
        return numberDocument;
    }

    public Long getRoomId() {
        return roomId;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BookRoomResponse that = (BookRoomResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(roomId, that.roomId) && Objects.equals(hotelId, that.hotelId) && Objects.equals(bookedAt, that.bookedAt) && Objects.equals(numberDocument, that.numberDocument) && Objects.equals(userName, that.userName) && Objects.equals(userSurname, that.userSurname) && Objects.equals(userNumber, that.userNumber) && Objects.equals(userCountry, that.userCountry) && Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, roomId, hotelId, bookedAt, numberDocument, userName, userSurname, userNumber, userCountry, from, to, price);
    }
}
