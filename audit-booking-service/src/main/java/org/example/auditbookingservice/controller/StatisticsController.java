package org.example.auditbookingservice.controller;

import org.example.auditbookingservice.storage.InMemoryStorage;
import org.example.events.RoomBookedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    
    private final InMemoryStorage storage;
    
    public StatisticsController(InMemoryStorage storage) {
        this.storage = storage;
    }
    
    @GetMapping("/foreigners")
    public ResponseEntity<Map<String, Object>> getForeignerStatistics() {
        List<RoomBookedEvent> foreignerBookings = new ArrayList<>(storage.foreignerBookings.values());
        Map<String, Object> statistics = Map.of(
            "foreignerCount", storage.foreignerBookings.size(),
            "totalBookings", storage.bookRooms.size(),
            "foreignerBookings", foreignerBookings
        );
        return ResponseEntity.ok(statistics);
    }
}

