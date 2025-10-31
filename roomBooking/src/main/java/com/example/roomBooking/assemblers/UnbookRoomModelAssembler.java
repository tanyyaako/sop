package com.example.roomBooking.assemblers;

import com.example.roomBooking.Controllers.HotelController;
import com.example.roomBooking.Controllers.RoomController;
import org.example.hotelbookingapi.DTO.UnbookRoomResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UnbookRoomModelAssembler implements RepresentationModelAssembler<UnbookRoomResponse, EntityModel<UnbookRoomResponse>> {

    @Override
    public EntityModel<UnbookRoomResponse> toModel(UnbookRoomResponse unbookRoomResponse) {
        return EntityModel.of(unbookRoomResponse,
                linkTo(methodOn(RoomController.class).unbookRoom(null)).withSelfRel(),
                linkTo(methodOn(RoomController.class).getRoom(unbookRoomResponse.getRoomId().toString())).withRel("room"),
                linkTo(methodOn(HotelController.class).getHotel(unbookRoomResponse.getHotelId().toString())).withRel("hotel")
        );
    }
}

