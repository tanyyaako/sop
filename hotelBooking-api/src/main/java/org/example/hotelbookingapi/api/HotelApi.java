package org.example.hotelbookingapi.api;

import io.swagger.v3.oas.annotations.OpenAPI31;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Hotel", description = "API для работы с отелями")
@RequestMapping("/api/hotels")
public interface HotelApi {
    @Operation(summary = "Получить отели с фильтрацией и пагинацией")
    @ApiResponse(responseCode = "200", description = "Отели успешно найдены")
    @GetMapping()
    CollectionModel<EntityModel<HotelResponse>> getHotels(
            @Parameter(description = "Фильтр по названию отеля") @RequestParam(required = false) String hotelName,
            @Parameter(description = "Фильтр по адресу")@RequestParam(required = false) String address,
            @Parameter(description = "Фильтр по количеству звезд") @RequestParam(required = false) String grade);

    @Operation(summary = "Получить отель по Id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отель найден"),
            @ApiResponse(responseCode = "404", description = "Отель не найден", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StatusResponse.class))
            })
    })
    @GetMapping(value = "/{id}")
    EntityModel<HotelResponse> getHotel(@PathVariable String id);

    @Operation(summary = "Создать новый отель")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Отель успешно создан"),
            @ApiResponse(responseCode = "400", description = "Невалидный запрос",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class))),
            @ApiResponse(responseCode = "409", description = "Отель с таким адресом уже существует",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class)))

    })
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<HotelResponse>> createHotel(@Valid @RequestBody HotelRequest hotelRequest);

    @Operation(summary = "Обновить отель")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отель успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Отель не найден",
                    content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<HotelResponse> updateHotel(@PathVariable String id,@Valid @RequestBody HotelRequest hotelRequest);

    @Operation(summary = "Удалить отель по ID")
    @ApiResponse(responseCode = "204", description = "Отель успешно удален")
    @ApiResponse(responseCode = "404", description = "Отель не найден")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteHotel(@PathVariable String id);
}
