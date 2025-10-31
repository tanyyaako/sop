package org.example.hotelbookingapi.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.Objects;

@Relation(collectionRelation = "hotels", itemRelation = "hotel")
public class HotelResponse extends RepresentationModel<HotelResponse> {
    private final Long id;
    private final String hotelName;
    private final Integer grade;
    private final String address;
    @JsonIgnore
    private List<RoomResponse> roomResponses;

    public HotelResponse(Long id, String hotelName, Integer grade, String address, List<RoomResponse> roomResponses) {
        this.id = id;
        this.hotelName = hotelName;
        this.grade = grade;
        this.address = address;
        this.roomResponses = roomResponses;
    }

    public String getAddress() {
        return address;
    }

    public Integer getGrade() {
        return grade;
    }

    public String getHotelName() {
        return hotelName;
    }

    public Long getId() {
        return id;
    }

    public List<RoomResponse> getRoomResponses() {
        return roomResponses;
    }

    public void setRoomResponses(List<RoomResponse> roomResponses) {
        this.roomResponses = roomResponses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HotelResponse that = (HotelResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(hotelName, that.hotelName) && Objects.equals(grade, that.grade) && Objects.equals(address, that.address) && Objects.equals(roomResponses, that.roomResponses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, hotelName, grade, address, roomResponses);
    }
}