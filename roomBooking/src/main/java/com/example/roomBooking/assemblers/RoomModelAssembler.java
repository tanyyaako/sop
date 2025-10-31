package com.example.roomBooking.assemblers;

import com.example.roomBooking.Controllers.HotelController;
import com.example.roomBooking.Controllers.RoomController;
import org.example.hotelbookingapi.DTO.BookRoomRequest;
import org.example.hotelbookingapi.DTO.HotelResponse;
import org.example.hotelbookingapi.DTO.RoomResponse;
import org.example.hotelbookingapi.DTO.UnbookRoomRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RoomModelAssembler implements RepresentationModelAssembler<RoomResponse, EntityModel<RoomResponse>> {

    @Override
    public EntityModel<RoomResponse> toModel(RoomResponse room) {
        return EntityModel.of(room,
                linkTo(methodOn(RoomController.class).getRoom(room.getId().toString())).withSelfRel(),
                linkTo(methodOn(RoomController.class).getRooms(null, null, 0, 10)).withRel("collection"),
                linkTo(methodOn(HotelController.class).getHotels(null, null, null)).withRel("hotels"),
                linkTo(methodOn(RoomController.class).bookRoom(new BookRoomRequest(null, null, null, null,
                        null, room.getId().toString(), null, null))).withRel("book"),
                linkTo(methodOn(RoomController.class).unbookRoom(new UnbookRoomRequest(null))).withRel("unBook")
        );
    }

    @Override
    public CollectionModel<EntityModel<RoomResponse>> toCollectionModel(Iterable<? extends RoomResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(RoomController.class).getRooms(null, null, 0, 10)).withSelfRel());
    }

}

