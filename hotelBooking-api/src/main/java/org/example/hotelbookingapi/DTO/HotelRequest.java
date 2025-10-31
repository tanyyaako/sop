package org.example.hotelbookingapi.DTO;

import jakarta.validation.constraints.NotBlank;

public record HotelRequest(
        @NotBlank(message = "Название отеля не может быть пустым") String hotelName,
        @NotBlank(message = "Адрес отеля не может быть пустым") String address,
        @NotBlank(message = "Количество звезд отеля не может быть пустым") String grade

) {
}
