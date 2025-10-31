package org.example.hotelbookingapi.DTO;

import java.util.List;

public record UpdateRoomRequest(
        String area,
        Integer floor,
        String numberRoom,
        List<String> roomPhotoUrl
) {
}
