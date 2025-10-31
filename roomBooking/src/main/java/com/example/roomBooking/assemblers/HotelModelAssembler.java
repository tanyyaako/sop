package com.example.roomBooking.assemblers;

import com.example.roomBooking.Controllers.HotelController;
import com.example.roomBooking.Controllers.RoomController;
import org.example.hotelbookingapi.DTO.HotelResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class HotelModelAssembler implements RepresentationModelAssembler<HotelResponse, EntityModel<HotelResponse>> {

    @Override
    public EntityModel<HotelResponse> toModel(HotelResponse hotel) {
        return EntityModel.of(hotel,
                linkTo(methodOn(HotelController.class).getHotel(hotel.getId().toString())).withSelfRel(),
                linkTo(methodOn(RoomController.class).getRooms(hotel.getId().toString(), null, 0, 10)).withRel("rooms"),
                linkTo(methodOn(HotelController.class).getHotels(null, null, null)).withRel("collection")
        );
    }

    @Override
    public CollectionModel<EntityModel<HotelResponse>> toCollectionModel(Iterable<? extends HotelResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(HotelController.class).getHotels(null, null, null)).withSelfRel());
    }

}
