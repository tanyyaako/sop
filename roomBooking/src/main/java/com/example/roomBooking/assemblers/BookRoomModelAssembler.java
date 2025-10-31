package com.example.roomBooking.assemblers;

import com.example.roomBooking.Controllers.HotelController;
import com.example.roomBooking.Controllers.RoomController;
import org.example.hotelbookingapi.DTO.BookRoomResponse;
import org.example.hotelbookingapi.DTO.RoomResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class BookRoomModelAssembler implements RepresentationModelAssembler<BookRoomResponse, EntityModel<BookRoomResponse>> {

    @Override
    public EntityModel<BookRoomResponse> toModel(BookRoomResponse bookRoomResponse) {
        return EntityModel.of(bookRoomResponse,
                linkTo(methodOn(RoomController.class).getBookings(bookRoomResponse.getRoomId().toString(), null, 0, 10
                )).withSelfRel(),
                linkTo(methodOn(RoomController.class).bookRoom(null)).withRel("bookRoom"),
                linkTo(methodOn(RoomController.class).getRoom(bookRoomResponse.getRoomId().toString())).withRel("room"),

                linkTo(methodOn(HotelController.class).getHotel(bookRoomResponse.getHotelId().toString())).withRel("hotel")

        );
    }

    @Override
    public CollectionModel<EntityModel<BookRoomResponse>> toCollectionModel(Iterable<? extends BookRoomResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(RoomController.class).getBookings(null, null, 0, 10)).withSelfRel());
    }
}
