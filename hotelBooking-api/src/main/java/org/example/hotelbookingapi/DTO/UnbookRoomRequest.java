package org.example.hotelbookingapi.DTO;

import jakarta.validation.constraints.NotBlank;

public record UnbookRoomRequest(
        @NotBlank(message = "ID бронирования не может быть пустым") String bookingId
){
}
