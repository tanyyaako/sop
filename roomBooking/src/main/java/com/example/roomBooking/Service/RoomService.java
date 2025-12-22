package com.example.roomBooking.Service;

import com.example.roomBooking.Storage.InMemoryStorage;
import com.example.roomBooking.config.RabbitMQConfig;
import com.example.roomBooking.Service.PricingServiceClient;
import org.example.events.RoomBookedEvent;
import org.example.events.RoomUnbookedEvent;
import org.example.hotelbookingapi.DTO.*;
import org.example.hotelbookingapi.exception.ResourceNotFoundException;
import org.example.hotelbookingapi.exception.RoomAlreadyBookedException;
import org.example.hotelbookingapi.exception.RoomAlreadyExistsException;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

@Service
public class RoomService {
    private final InMemoryStorage storage;
    private final HotelService hotelService;
    private final RabbitTemplate rabbitTemplate;
    private final PricingServiceClient pricingServiceClient;

    public RoomService(InMemoryStorage storage, HotelService hotelService, RabbitTemplate rabbitTemplate, PricingServiceClient pricingServiceClient) {
        this.storage = storage;
        this.hotelService = hotelService;
        this.rabbitTemplate = rabbitTemplate;
        this.pricingServiceClient = pricingServiceClient;
    }
    public RoomResponse findRoomById(Long id) {
        return Optional.ofNullable(storage.rooms.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Room", id));
    }
    public PagedResponse<RoomResponse> findAllRooms(Long hotelId,String area, int page, int size) {
        Stream<RoomResponse> roomsStream = storage.rooms.values().stream()
                .sorted(Comparator.comparing(RoomResponse::getId));

        if (hotelId != null) {
            roomsStream = roomsStream.filter(room -> room.getHotel() != null && room.getHotel().getId().equals(hotelId));
        }
        if (area != null ) {
            roomsStream = roomsStream.filter(r -> r.getArea() != null && r.getArea().equals(Double.valueOf(area)));
        }
        List<RoomResponse> allRooms = roomsStream.toList();

        int totalElements = allRooms.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<RoomResponse> pageContent = (fromIndex > toIndex) ? List.of() : allRooms.subList(fromIndex, toIndex);

        return new PagedResponse<>(pageContent, page, size, totalElements, totalPages, page >= totalPages - 1);
    }
    public RoomResponse createBook(RoomRequest request) {
        HotelResponse hotel = hotelService.findById(Long.valueOf(request.hotelId()));
        validateRoom(request.hotelId(), request.numberRoom(), null);

        long id = storage.roomSequence.incrementAndGet();
        var room = new RoomResponse(
                Double.valueOf(request.area()),
                id,
                hotel,
                request.floor(),
                request.numberRoom(),
                null,
                null
        );
        storage.rooms.put(id, room);
        return room;
    }
    public RoomResponse updateBook(Long id, UpdateRoomRequest request) {
        RoomResponse existingRoom = findRoomById(id);
        validateRoom(null, request.numberRoom(), id.toString());

        var updatedRoom = new RoomResponse(
                Double.valueOf(request.area()),
                id,
                existingRoom.getHotel(),
                request.floor(),
                request.numberRoom(),
                null,
                null
        );
        storage.rooms.put(id, updatedRoom);
        return updatedRoom;
    }
    public void deleteBook(Long id) {
        RoomResponse room = findRoomById(id);
        if (room.getHotel() != null && room.getHotel().getRoomResponses() != null) {
            room.getHotel().getRoomResponses().removeIf(r -> r.getId().equals(id));
        }
        storage.rooms.remove(id);
    }
    public void deleteRoomsByHotelId(Long hotelId) {
        List<Long> roomIdsToDelete = storage.rooms.values().stream()
                .filter(room ->  room.getHotel().getId().equals(hotelId))
                .map(RoomResponse::getId)
                .toList();

        roomIdsToDelete.forEach(storage.rooms::remove);
    }
    public BookRoomResponse bookRoom(BookRoomRequest request) {
        var room = storage.rooms.get(Long.valueOf(request.roomId()));
        if (room == null) {
            throw new ResourceNotFoundException("Room", request.roomId());
        }

        LocalDateTime from;
        LocalDateTime to;
        try {
            from = LocalDate.parse(request.dateFrom()).atTime(14, 0);
            to = LocalDate.parse(request.dateTo()).atTime(12, 0);
        } catch (Exception e){
            throw new IllegalArgumentException("Неверный формат даты. Используйте YYYY-MM-DD");
        }

        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("Дата выезда должна быть после даты заезда");
        }

        boolean overlaps = room.getBooks() != null && room.getBooks().stream()
                .anyMatch(b -> datesOverlap(b.getFrom(), b.getTo(), from, to));
        if (overlaps) {
            throw new RoomAlreadyBookedException(from, to, request.roomId());
        }

        // Рассчитываем цену через pricing-service
        LocalDate dateFrom = from.toLocalDate();
        LocalDate dateTo = to.toLocalDate();
        double basePrice = room.getBasePrice() != null ? room.getBasePrice() : 1000.0;
        
        // Рассчитываем процент загруженности на основе существующих бронирований
        long totalDays = ChronoUnit.DAYS.between(dateFrom, dateTo) + 1;
        int existingBookings = room.getBooks() != null ? room.getBooks().size() : 0;
        double occupancyRate = pricingServiceClient.calculateOccupancyRate(existingBookings, (int) totalDays);
        
        // Получаем цену от pricing-service
        double calculatedPrice = pricingServiceClient.calculatePrice(
                room.getId(),
                room.getHotel().getId(),
                dateFrom,
                dateTo,
                basePrice,
                occupancyRate
        );

        LocalDateTime bookedAt = LocalDateTime.now();
        LocalDateTime priceCalculatedAt = LocalDateTime.now();
        Long bookingId = storage.bookingSequence.incrementAndGet();
        BookRoomResponse booking = new BookRoomResponse(
                bookedAt,
                bookingId,
                Long.valueOf(request.roomId()),
                room.getHotel().getId(),
                request.userName(),
                request.userSurname(),
                request.userNumber(),
                request.documentNumber(),
                request.userCountry(),
                from,
                to,
                calculatedPrice
        );
        if (room.getBooks() == null) {
            room.setBooks(new ArrayList<>());
        }
        room.getBooks().add(booking);
        storage.bookRooms.put(bookingId, booking);
        
        // Отправляем событие RoomBookedEvent с рассчитанной ценой в Fanout exchange
        RoomBookedEvent event = new RoomBookedEvent(
                booking.getId(),
                booking.getRoomId(),
                booking.getHotelId(),
                booking.getNumberDocument(),
                booking.getUserName(),
                booking.getUserSurname(),
                booking.getFrom().toString(),
                booking.getTo().toString(),
                bookedAt.toString(),
                calculatedPrice,
                "RUB"
        );
        
        // Отправляем в Fanout exchange для рассылки всем подписчикам
        rabbitTemplate.convertAndSend(RabbitMQConfig.ROOM_BOOKED_FANOUT_EXCHANGE, "", event);
        
        // Также отправляем в Topic exchange для обратной совместимости
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_ROOM_BOOKED, event);
        return booking;
    }
    public UnbookRoomResponse unbookRoom(UnbookRoomRequest request) {

        BookRoomResponse booking = storage.bookRooms.get(Long.valueOf(request.bookingId()));
        if (booking == null) {
            throw new ResourceNotFoundException("BookingID", request.bookingId());
        }

        RoomResponse room = storage.rooms.get(booking.getRoomId());
        if (room == null) {
            throw new ResourceNotFoundException("Room", booking.getRoomId());
        }
        if (room.getBooks() != null) {
            room.getBooks().removeIf(b -> b.getId().equals(booking.getId()));
        }

        storage.bookRooms.remove(booking.getId());
        LocalDateTime unBookedAt = LocalDateTime.now();
        RoomUnbookedEvent event = new RoomUnbookedEvent(booking.getId(), unBookedAt.toString());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_ROOM_UNBOOKED, event);
        return new UnbookRoomResponse(
                booking.getId(),
                booking.getHotelId(),
                booking.getRoomId(),
                booking.getNumberDocument(),
                booking.getFrom(),
                booking.getTo(),
                unBookedAt
        );
    }
    public PagedResponse<BookRoomResponse> findBookingsWithPagination(Long roomId, String documentNumber, int page, int size) {
        List<BookRoomResponse> allBookings = findBookings(roomId, documentNumber);

        int totalElements = allBookings.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<BookRoomResponse> pageContent = (fromIndex > toIndex) ? List.of() : allBookings.subList(fromIndex, toIndex);

        return new PagedResponse<>(pageContent, page, size, totalElements, totalPages, page >= totalPages - 1);
    }
    public List<BookRoomResponse> findBookings(Long roomId, String documentNumber) {

        Stream<BookRoomResponse> bookingsStream = storage.bookRooms.values().stream()
                .sorted((b1, b2) -> b1.getId().compareTo(b2.getId()));

        if (roomId != null) {
            bookingsStream = bookingsStream.filter(b -> b.getRoomId() != null && b.getRoomId().equals(roomId));
        }

        if (documentNumber != null && !documentNumber.trim().isEmpty()) {
            bookingsStream = bookingsStream.filter(b -> b.getNumberDocument() != null &&
                    b.getNumberDocument().equals(documentNumber));
        }
        return bookingsStream.toList();
    }
    private boolean datesOverlap(LocalDateTime start1, LocalDateTime end1,
                                 LocalDateTime start2, LocalDateTime end2) {
        return !(end1.isBefore(start2) || end2.isBefore(start1));
    }
    private void validateRoom(String hotelId, String roomNumber, String id) {
        boolean exists = storage.rooms.values().stream()
                .anyMatch(room -> Objects.equals(room.getHotel().toString(), hotelId)
                        && room.getNumberRoom().equalsIgnoreCase(roomNumber)
                        && (id == null || !room.getId().toString().equalsIgnoreCase(id)));

        if (exists) {
            throw new RoomAlreadyExistsException(roomNumber, hotelId);
        }
    }
}
