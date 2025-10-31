package com.example.roomBooking.Controllers;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class RootController {

    @GetMapping
    public RepresentationModel<?> getRoot() {
        RepresentationModel<?> rootModel = new RepresentationModel<>();
        rootModel.add(
                linkTo(methodOn(HotelController.class).getHotels(null, null, null)).withRel("hotels"),
                linkTo(methodOn(RoomController.class).getRooms(null, null, 0, 10)).withRel("rooms"),
                linkTo(methodOn(RoomController.class).getBookings(null, null, 0, 10)).withRel("bookings")
        );
        rootModel.add(Link.of("/swagger-ui.html","documentation"));
        return rootModel;
    }
}
