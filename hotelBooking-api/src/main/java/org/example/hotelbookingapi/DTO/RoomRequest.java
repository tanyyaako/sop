package org.example.hotelbookingapi.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RoomRequest(
        @NotBlank(message = "ID отеля не может быть пустым") String hotelId,
        @NotBlank(message = "Площадь номера не может быть пустым") String area,
        @NotNull(message = "Этаж номера не может быть пустым") Integer floor,
        @NotBlank(message = "Номер комнаты не может быть пустым") String numberRoom,
        List<String> roomPhotoUrl
) {
}
