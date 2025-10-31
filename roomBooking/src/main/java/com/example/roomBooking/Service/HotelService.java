package com.example.roomBooking.Service;

import com.example.roomBooking.Storage.InMemoryStorage;
import org.example.hotelbookingapi.DTO.HotelRequest;
import org.example.hotelbookingapi.DTO.HotelResponse;
import org.example.hotelbookingapi.exception.ResourceNotFoundException;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelService {
    private final InMemoryStorage storage;

    public HotelService(InMemoryStorage storage) {
        this.storage = storage;
    }

    public List<HotelResponse> findAll() {
        return storage.hotels.values().stream().toList();
    }

    public HotelResponse findById(Long id) {
        return Optional.ofNullable(storage.hotels.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", id));
    }

    public HotelResponse create(HotelRequest request) {
        long id = storage.hotelSequence.incrementAndGet();
        HotelResponse hotel = new HotelResponse(id, request.hotelName(), Integer.parseInt(request.grade()), request.address(), null);
        storage.hotels.put(id, hotel);
        return hotel;
    }

    public HotelResponse update(Long id, HotelRequest request) {
        findById(id);
        HotelResponse updatedHotel = new HotelResponse(id, request.hotelName(), Integer.getInteger(request.grade()), request.address(), null);
        storage.hotels.put(id, updatedHotel);
        return updatedHotel;
    }

    public void delete(Long id) {
        storage.hotels.remove(id);
    }

}
