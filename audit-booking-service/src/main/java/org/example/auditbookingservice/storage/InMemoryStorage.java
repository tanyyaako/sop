package org.example.auditbookingservice.storage;

import org.example.events.RoomBookedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {
    public final Map<Long, RoomBookedEvent> bookRooms = new ConcurrentHashMap<>();
    public final AtomicLong bookingSequence = new AtomicLong(0);
}
