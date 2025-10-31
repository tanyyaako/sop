package com.example.roomBooking.Controllers;

import com.example.roomBooking.Service.HotelService;
import com.example.roomBooking.Service.RoomService;
import com.example.roomBooking.assemblers.HotelModelAssembler;
import jakarta.validation.Valid;
import org.example.hotelbookingapi.DTO.HotelRequest;
import org.example.hotelbookingapi.DTO.HotelResponse;
import org.example.hotelbookingapi.DTO.PagedResponse;
import org.example.hotelbookingapi.api.HotelApi;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
public class HotelController implements HotelApi {

    private final HotelService hotelService;
    private final HotelModelAssembler hotelModelAssembler;
    private final RoomService roomService;

    public HotelController(HotelService hotelService, HotelModelAssembler hotelModelAssembler, RoomService roomService) {
        this.hotelService = hotelService;
        this.hotelModelAssembler = hotelModelAssembler;
        this.roomService = roomService;
    }
    @Override
    public CollectionModel<EntityModel<HotelResponse>> getHotels(String hotelName, String address, String grade) {
        List<HotelResponse> hotels = hotelService.findAll();
        return hotelModelAssembler.toCollectionModel(hotels);
    }
    @Override
    public EntityModel<HotelResponse> getHotel(String id) {
        Long hotelId = Long.parseLong(id);
        HotelResponse hotel = hotelService.findById(hotelId);
        return hotelModelAssembler.toModel(hotel);
    }
    @Override
    public ResponseEntity<EntityModel<HotelResponse>> createHotel(@Valid HotelRequest hotelRequest) {
        HotelResponse hotel = hotelService.create(hotelRequest);
        EntityModel<HotelResponse> entityModel = hotelModelAssembler.toModel(hotel);
        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }
    @Override
    public EntityModel<HotelResponse> updateHotel(String id, @Valid HotelRequest hotelRequest) {
        Long hotelId = Long.parseLong(id);
        HotelResponse hotel = hotelService.update(hotelId, hotelRequest);
        return hotelModelAssembler.toModel(hotel);
    }
    @Override
    public void deleteHotel(String id) {
        Long hotelId = Long.parseLong(id);
        hotelService.delete(hotelId);
        roomService.deleteRoomsByHotelId(hotelId);
    }
}
