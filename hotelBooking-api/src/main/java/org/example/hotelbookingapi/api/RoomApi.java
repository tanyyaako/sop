package org.example.hotelbookingapi.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.hotelbookingapi.DTO.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Room", description = "API для работы с номерами отелей")
@RequestMapping("/api/rooms")
public interface RoomApi {
    @Operation(summary = "Получить номера отелей с фильтрацией и пагинацией")
    @ApiResponse(responseCode = "200", description = "Номера успешно найдены")
    @GetMapping()
    PagedModel<EntityModel<RoomResponse>> getRooms(
            @Parameter(description = "Фильтр по ID отеля") @RequestParam(required = false) String hotelId,
            @Parameter(description = "Фильтр по площади номера") @RequestParam(required = false) String area,
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size);

    @Operation(summary = "Получить номер отеля по Id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Номер отеля найден"),
            @ApiResponse(responseCode = "404", description = "Номер отеля не найден", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StatusResponse.class))
            })
    })
    @GetMapping(value = "/{id}")
    EntityModel<RoomResponse> getRoom(@PathVariable String id);

    @Operation(summary = "Добавить новый номер")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Номер успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Невалидный запрос",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "409", description = "Комната с таким номером уже существует", content = @Content(schema = @Schema(implementation = StatusResponse.class)))

    })
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest roomRequest);

    @Operation(summary = "Обновить номер")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Номер успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Номер не найден",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<RoomResponse> updateRoom(@PathVariable String id,@Valid @RequestBody UpdateRoomRequest updateRoomRequest);

    @Operation(summary = "Удалить номер по ID")
    @ApiResponse(responseCode = "204", description = "Номер успешно удален")
    @ApiResponse(responseCode = "404", description = "Номер не найден")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteRoom(@PathVariable String id);

    @Operation(summary = "Забронировать номер")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Номер успешно забронирован"),
            @ApiResponse(responseCode = "404", description = "Номер не найден",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    })
    @PostMapping(value = "/book", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<EntityModel<BookRoomResponse>> bookRoom( @Valid @RequestBody BookRoomRequest bookRoomRequest);

    @Operation(summary = "Отменить бронирование")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Бронь успешно отменена"),
            @ApiResponse(responseCode = "404", description = "Номер не найден",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    })
    @PostMapping(value = "/unbook")
    ResponseEntity<EntityModel<UnbookRoomResponse>> unbookRoom(@Valid @RequestBody UnbookRoomRequest unbookRoomRequest);

    @Operation(summary = "Получить бронирования с фильтрацией и пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бронирования успешно найдены"),
    })
    @GetMapping("/bookings")
    PagedModel<EntityModel<BookRoomResponse>> getBookings(
            @Parameter(description = "Фильтр по ID комнаты") @RequestParam(required = false) String roomId,
            @Parameter(description = "Фильтр по номеру документа") @RequestParam(required = false) String documentNumber,
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size);

}
