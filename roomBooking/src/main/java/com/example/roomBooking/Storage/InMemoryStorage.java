package com.example.roomBooking.Storage;

import jakarta.annotation.PostConstruct;
import org.example.hotelbookingapi.DTO.BookRoomResponse;
import org.example.hotelbookingapi.DTO.HotelResponse;
import org.example.hotelbookingapi.DTO.RoomResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {

    public final Map<Long, HotelResponse> hotels = new ConcurrentHashMap<>();
    public final Map<Long, RoomResponse> rooms = new ConcurrentHashMap<>();
    public final Map<Long, BookRoomResponse> bookRooms = new ConcurrentHashMap<>();
    public final AtomicLong hotelSequence = new AtomicLong(0);
    public final AtomicLong roomSequence = new AtomicLong(0);
    public final AtomicLong bookingSequence = new AtomicLong(0);

    @PostConstruct
    public void init() {
        HotelResponse hotel1 = new HotelResponse(
                hotelSequence.incrementAndGet(), "Пятизвездочный", 5,
                "г.Москва, ул.Образцова, д.9", new ArrayList<>());
        HotelResponse hotel2 = new HotelResponse(
                hotelSequence.incrementAndGet(), "Воронцовский", 4,
                "г.Крым, ул.Грибоедова, д.9", new ArrayList<>());

        hotels.put(hotel1.getId(), hotel1);
        hotels.put(hotel2.getId(), hotel2);

        RoomResponse room1 = new RoomResponse(
                15.0, roomSequence.incrementAndGet(), hotel1, 2, "23к1",
                new ArrayList<>(), null);

        RoomResponse room2 = new RoomResponse(
                17.0, roomSequence.incrementAndGet(), hotel1, 4, "34к1",
                new ArrayList<>(), null);

        RoomResponse room3 = new RoomResponse(
                20.0, roomSequence.incrementAndGet(), hotel2, 10, "210",
                new ArrayList<>(), null);

        rooms.put(room1.getId(), room1);
        rooms.put(room2.getId(), room2);
        rooms.put(room3.getId(), room3);

        hotel1.setRoomResponses(new ArrayList<>(List.of(room1, room2)));
        hotel2.setRoomResponses(new ArrayList<>(List.of(room3)));

        BookRoomResponse bookRoom1 = new BookRoomResponse(
                LocalDateTime.now(),
                bookingSequence.incrementAndGet(),
                1L,
                hotel1.getId(),
                "12345678 0000",
                "Алесандр",
                "Алесандрович",
                "+7900010000",
                "Россия",
                LocalDateTime.of(2025, 1, 15, 14, 0),
                LocalDateTime.of(2025, 1, 20, 12, 0)
        );
        BookRoomResponse bookRoom2 = new BookRoomResponse(
                LocalDateTime.now(),
                bookingSequence.incrementAndGet(),
                1L,
                hotel1.getId(),
                "12345678 0000",
                "Иван",
                "Иванов",
                "+7900000000",
                "Россия",
                LocalDateTime.of(2025, 2, 15, 14, 0),
                LocalDateTime.of(2025, 2, 20, 12, 0)
        );
        bookRooms.put(bookRoom2.getId(), bookRoom2);
        bookRooms.put(bookRoom1.getId(), bookRoom1);
        room1.setBooks(new ArrayList<>(List.of(bookRoom2, bookRoom1)));

        BookRoomResponse bookRoom3 = new BookRoomResponse(
                LocalDateTime.now(),
                bookingSequence.incrementAndGet(),
                2L,
                hotel1.getId(),
                "987654 0000",
                "Илья",
                "Ильич",
                "+7900000010",
                "Грузия",
                LocalDateTime.of(2025, 2, 15, 14, 0),
                LocalDateTime.of(2025, 2, 20, 12, 0)
        );
        bookRooms.put(bookRoom3.getId(), bookRoom3);
        room2.setBooks(new ArrayList<>(List.of(bookRoom3)));

    }
}