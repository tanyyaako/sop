package com.example.roomBooking.graphql;

import com.example.roomBooking.Service.RoomService;
import com.netflix.graphql.dgs.*;
import graphql.schema.DataFetchingEnvironment;
import org.example.hotelbookingapi.DTO.*;

import java.util.List;
import java.util.Map;

@DgsComponent
public class RoomDataFetcher {

    private final RoomService roomService;

    public RoomDataFetcher(RoomService roomService) {
        this.roomService = roomService;
    }

    @DgsQuery
    public RoomResponse roomById(@InputArgument Long id) {
        return roomService.findRoomById(id);
    }

    @DgsQuery
    public PagedResponse<RoomResponse> rooms(@InputArgument Long hotelId, @InputArgument int page, @InputArgument int size, @InputArgument String areaFilter) {
        return roomService.findAllRooms(hotelId, areaFilter, page, size);
    }

    @DgsQuery
    public PagedResponse<BookRoomResponse> bookings(@InputArgument Long roomId, @InputArgument String documentNumber, @InputArgument int page, @InputArgument int size) {
        return roomService.findBookingsWithPagination(roomId, documentNumber, page, size);
    }

    @DgsData(parentType = "Room", field = "hotel")
    public HotelResponse hotel(DataFetchingEnvironment dfe) {
        RoomResponse room = dfe.getSource();
        return room.getHotel();
    }

    @DgsData(parentType = "Room", field = "books")
    public List<BookRoomResponse> books(DataFetchingEnvironment dfe) {
        RoomResponse room = dfe.getSource();
        return room.getBooks() != null ? room.getBooks() : List.of();
    }

    @DgsMutation
    @SuppressWarnings("unchecked")
    public RoomResponse createRoom(@InputArgument("input") Map<String, Object> input) {
        RoomRequest request = new RoomRequest(
                (String) input.get("hotelId"),
                (String) input.get("area"),
                (Integer) input.get("floor"),
                (String) input.get("numberRoom"),
                (List<String>) input.get("roomPhotoUrl")
        );
        return roomService.createRoom(request);
    }

    @DgsMutation
    @SuppressWarnings("unchecked")
    public RoomResponse updateRoom(@InputArgument Long id, @InputArgument("input") Map<String, Object> input) {
        UpdateRoomRequest request = new UpdateRoomRequest(
                (String) input.get("area"),
                (Integer) input.get("floor"),
                (String) input.get("numberRoom"),
                (List<String>) input.get("roomPhotoUrl")
        );
        return roomService.updateRoom(id, request);
    }

    @DgsMutation
    public Boolean deleteRoom(@InputArgument Long id) {
        roomService.deleteRoom(id);
        return true;
    }

    @DgsMutation
    public BookRoomResponse bookRoom(@InputArgument("input") Map<String, Object> input) {
        BookRoomRequest request = new BookRoomRequest(
                (String) input.get("roomId"),
                (String) input.get("userName"),
                (String) input.get("userSurname"),
                (String) input.get("userNumber"),
                (String) input.get("documentNumber"),
                (String) input.get("userCountry"),
                (String) input.get("dateFrom"),
                (String) input.get("dateTo")
        );
        return roomService.bookRoom(request);
    }

    @DgsMutation
    public UnbookRoomResponse unbookRoom(@InputArgument("input") Map<String, Object> input) {
        UnbookRoomRequest request = new UnbookRoomRequest(
                (String) input.get("bookingId")
        );
        return roomService.unbookRoom(request);
    }
}
