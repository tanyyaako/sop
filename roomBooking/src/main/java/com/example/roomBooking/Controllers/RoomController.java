package com.example.roomBooking.Controllers;

import com.example.roomBooking.Service.RoomService;
import com.example.roomBooking.assemblers.BookRoomModelAssembler;
import com.example.roomBooking.assemblers.RoomModelAssembler;
import com.example.roomBooking.assemblers.UnbookRoomModelAssembler;
import jakarta.validation.Valid;
import org.example.hotelbookingapi.DTO.*;
import org.example.hotelbookingapi.api.RoomApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController implements RoomApi {
    private final RoomService roomService;
    private final RoomModelAssembler roomModelAssembler;
    private final PagedResourcesAssembler<?> pagedResourcesAssembler;
    private final BookRoomModelAssembler bookRoomModelAssembler;
    private final UnbookRoomModelAssembler unbookRoomModelAssembler;


    public RoomController(BookRoomModelAssembler bookRoomModelAssembler,
                          RoomService roomService,
                          RoomModelAssembler roomModelAssembler,
                          PagedResourcesAssembler<?> pagedResourcesAssembler,
                          UnbookRoomModelAssembler unbookRoomModelAssembler) {
        this.bookRoomModelAssembler = bookRoomModelAssembler;
        this.roomService = roomService;
        this.roomModelAssembler = roomModelAssembler;
        this.unbookRoomModelAssembler = unbookRoomModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    public PagedModel<EntityModel<RoomResponse>> getRooms(String hotelId, String area, int page, int size) {
        Long hotelIdLong = hotelId != null ? Long.valueOf(hotelId) : null;
        PagedResponse<RoomResponse> pagedResponse = roomService.findAllRooms(hotelIdLong, area, page, size);
        Page<RoomResponse> roomPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );
        return ((PagedResourcesAssembler<RoomResponse>) pagedResourcesAssembler).toModel(roomPage, roomModelAssembler);
    }

    @Override
    public EntityModel<RoomResponse> getRoom(String id) {
        RoomResponse roomResponse = roomService.findRoomById(Long.valueOf(id));
        return roomModelAssembler.toModel(roomResponse);
    }

    @Override
    public ResponseEntity<EntityModel<RoomResponse>> createRoom(@Valid RoomRequest roomRequest) {
        RoomResponse roomResponse = roomService.createBook(roomRequest);
        EntityModel<RoomResponse> entityModel = roomModelAssembler.toModel(roomResponse);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<RoomResponse> updateRoom(String id, @Valid UpdateRoomRequest updateRoomRequest) {
        RoomResponse roomResponse = roomService.updateBook(Long.valueOf(id), updateRoomRequest);
        return roomModelAssembler.toModel(roomResponse);
    }

    @Override
    public void deleteRoom(String id) {
        roomService.deleteBook(Long.valueOf(id));
    }

    @Override
    public ResponseEntity<EntityModel<BookRoomResponse>> bookRoom(@Valid BookRoomRequest bookRoomRequest) {
        BookRoomResponse bookRoomResponse = roomService.bookRoom(bookRoomRequest);
        EntityModel<BookRoomResponse> entityModel = bookRoomModelAssembler.toModel(bookRoomResponse);

        return ResponseEntity.ok(entityModel);
    }

    @Override
    public ResponseEntity<EntityModel<UnbookRoomResponse>> unbookRoom(@Valid UnbookRoomRequest unbookRoomRequest) {
        UnbookRoomResponse unbookRoomResponse = roomService.unbookRoom(unbookRoomRequest);
        EntityModel<UnbookRoomResponse> entityModel = unbookRoomModelAssembler.toModel(unbookRoomResponse);

        return ResponseEntity.ok(entityModel);
    }

    @Override
    public PagedModel<EntityModel<BookRoomResponse>> getBookings(String roomId, String documentNumber, int page, int size) {
        Long roomIdLong = roomId != null ? Long.valueOf(roomId) : null;
        PagedResponse<BookRoomResponse> pagedResponse = roomService.findBookingsWithPagination(roomIdLong, documentNumber, page, size);
        Page<BookRoomResponse> bookingPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );
        return ((PagedResourcesAssembler<BookRoomResponse>) pagedResourcesAssembler).toModel(bookingPage, bookRoomModelAssembler);
    }
}
