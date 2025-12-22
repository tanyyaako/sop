package org.example.hotelbookingapi.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.Objects;

@Relation(collectionRelation = "rooms", itemRelation = "room")
public class RoomResponse extends RepresentationModel<RoomResponse> {
    private final Long id;
    private final HotelResponse hotel;
    private final Double area;
    private final Integer floor;
    private final String numberRoom;
    private final Double basePrice;
    private List<String> roomPhotoUrl;
    @JsonIgnore
    private List<BookRoomResponse> books;

    public RoomResponse(Double area, Long id, HotelResponse hotel, Integer floor, String numberRoom, List<BookRoomResponse> books, List<String> roomPhotoUrl) {
        this(area, id, hotel, floor, numberRoom, books, roomPhotoUrl, null);
    }

    public RoomResponse(Double area, Long id, HotelResponse hotel, Integer floor, String numberRoom, List<BookRoomResponse> books, List<String> roomPhotoUrl, Double basePrice) {
        this.area = area;
        this.id = id;
        this.hotel = hotel;
        this.floor = floor;
        this.numberRoom = numberRoom;
        this.books = books;
        this.roomPhotoUrl = roomPhotoUrl;
        this.basePrice = basePrice;
    }

    public Double getArea() {
        return area;
    }

    public List<BookRoomResponse> getBooks() {
        return books;
    }

    public void setBooks(List<BookRoomResponse> books) {
        this.books = books;
    }

    public Integer getFloor() {
        return floor;
    }

    public HotelResponse getHotel() {
        return hotel;
    }

    public Long getId() {
        return id;
    }

    public String getNumberRoom() {
        return numberRoom;
    }

    public List<String> getRoomPhotoUrl() {
        return roomPhotoUrl;
    }

    public void setRoomPhotoUrl(List<String> roomPhotoUrl) {
        this.roomPhotoUrl = roomPhotoUrl;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RoomResponse that = (RoomResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(hotel, that.hotel) && Objects.equals(area, that.area) && Objects.equals(floor, that.floor) && Objects.equals(numberRoom, that.numberRoom) && Objects.equals(roomPhotoUrl, that.roomPhotoUrl) && Objects.equals(books, that.books) && Objects.equals(basePrice, that.basePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, hotel, area, floor, numberRoom, roomPhotoUrl, books, basePrice);
    }
}