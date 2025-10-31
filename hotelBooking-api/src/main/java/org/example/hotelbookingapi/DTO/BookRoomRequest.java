package org.example.hotelbookingapi.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

public record BookRoomRequest(
        @NotBlank(message = "ID номера не может быть пустым") String roomId,
        @NotBlank(message = "Имя пользователя не может быть пустым") String userName,
        @NotBlank(message = "Фамилия пользователя не может быть пустым") String userSurname,
        @NotBlank(message = "Номер пользователя не может быть пустым") String userNumber,
        @NotBlank(message = "Номер документа пользователя не может быть пустым") String documentNumber,
        @NotBlank(message = "Страна пользователя не может быть пустым") String userCountry,
        @NotBlank(message = "Дата начала бронирования не может быть пустым") @JsonFormat(pattern = "yyyy-MM-dd") String dateFrom,
        @NotBlank(message = "Дата конца бронирования не может быть пустым") @JsonFormat(pattern = "yyyy-MM-dd") String dateTo
) {
}
