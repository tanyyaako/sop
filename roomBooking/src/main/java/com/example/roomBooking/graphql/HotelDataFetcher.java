package com.example.roomBooking.graphql;

import com.example.roomBooking.Service.HotelService;
import com.example.roomBooking.Service.RoomService;
import com.netflix.graphql.dgs.*;
import graphql.schema.DataFetchingEnvironment;
import org.example.hotelbookingapi.DTO.HotelRequest;
import org.example.hotelbookingapi.DTO.HotelResponse;
import org.example.hotelbookingapi.DTO.PagedResponse;
import org.example.hotelbookingapi.DTO.RoomResponse;

import java.util.List;
import java.util.Map;

@DgsComponent
public class HotelDataFetcher {
    private final HotelService hotelService;
    private final RoomService roomService;

    public HotelDataFetcher(HotelService hotelService, RoomService roomService) {
        this.hotelService = hotelService;
        this.roomService = roomService;
    }

    @DgsQuery
    public List<HotelResponse> hotels() {
        return hotelService.findAll();
    }

    @DgsQuery
    public HotelResponse hotelById(@InputArgument Long id,
                                   @InputArgument Integer gradeFilter,
                                   @InputArgument String addressFilter) {
        return hotelService.findById(id);
    }

    @DgsMutation
    public HotelResponse createHotel(@InputArgument("input") Map<String, Object> input) {
        HotelRequest request = new HotelRequest(
                (String) input.get("hotelName"),
                (String) input.get("address"),
                (String) input.get("grade")
        );
        return hotelService.create(request);
    }

    @DgsMutation
    public HotelResponse updateHotel(@InputArgument Long id, @InputArgument("input") Map<String, Object> input) {
        HotelRequest request = new HotelRequest(
                (String) input.get("hotelName"),
                (String) input.get("address"),
                (String) input.get("grade")
        );
        return hotelService.update(id, request);
    }

    @DgsMutation
    public Boolean deleteHotel(@InputArgument Long id) {
        hotelService.delete(id);
        return true;
    }

    @DgsData(parentType = "Hotel", field = "rooms")
    public List<RoomResponse> rooms(DataFetchingEnvironment dfe) {
        HotelResponse hotel = dfe.getSource();
        PagedResponse<RoomResponse> pagedResponse = roomService.findAllRooms(hotel.getId(), null, 0, 100);
        return pagedResponse.content();
    }
}
