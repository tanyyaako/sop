package com.example.roomBooking.Service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.pricingservice.grpc.PriceRequest;
import org.example.pricingservice.grpc.PriceResponse;
import org.example.pricingservice.grpc.PricingServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PricingServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(PricingServiceClient.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final double DEFAULT_BASE_PRICE = 1000.0;

    @GrpcClient("pricing-service")
    private PricingServiceGrpc.PricingServiceBlockingStub pricingServiceStub;

    public double calculatePrice(Long roomId, Long hotelId, LocalDate dateFrom, LocalDate dateTo,
                                 Double basePrice, double occupancyRate) {
        try {
            PriceRequest request = PriceRequest.newBuilder()
                    .setRoomId(roomId)
                    .setHotelId(hotelId)
                    .setDateFrom(dateFrom.format(DATE_FORMATTER))
                    .setDateTo(dateTo.format(DATE_FORMATTER))
                    .setBasePrice(basePrice != null ? basePrice : DEFAULT_BASE_PRICE)
                    .setOccupancyRate(occupancyRate)
                    .build();

            PriceResponse response = pricingServiceStub.calculatePrice(request);

            double calculatedPrice = response.getPrice();
            logger.info("Price calculated successfully: {} {} for room_id: {} ",
                    calculatedPrice, response.getCurrency(), roomId);
            return calculatedPrice;

        } catch (Exception e) {
            logger.error("Error calling pricing-service for room_id: {}", roomId, e);
            double price = basePrice != null ? basePrice : DEFAULT_BASE_PRICE;
            return price;
        }
    }
    public double calculateOccupancyRate(int totalBookings, int totalDays) {
        if (totalDays == 0) {
            return 0.0;
        }
        return Math.min(1.0, (double) totalBookings / totalDays);
    }
}

