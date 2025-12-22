package com.example.roomBooking.Service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.pricingservice.grpc.PriceRequest;
import org.example.pricingservice.grpc.PriceResponse;
import org.example.pricingservice.grpc.PricingServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PricingServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(PricingServiceClient.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @GrpcClient("pricing-service")
    private PricingServiceGrpc.PricingServiceBlockingStub pricingServiceStub;

    @Value("${pricing.fallback.enabled:true}")
    private boolean fallbackEnabled;

    @Value("${pricing.fallback.base-price:1000.0}")
    private double fallbackBasePrice;

    /**
     * Рассчитывает цену для бронирования через gRPC
     * @param roomId ID номера
     * @param hotelId ID отеля
     * @param dateFrom Дата заезда
     * @param dateTo Дата выезда
     * @param basePrice Базовая цена номера
     * @param occupancyRate Процент загруженности (0.0 - 1.0)
     * @return Рассчитанная цена или fallback цена при ошибке
     */
    public double calculatePrice(Long roomId, Long hotelId, LocalDate dateFrom, LocalDate dateTo,
                                 Double basePrice, double occupancyRate) {
        try {
            logger.info("Requesting price calculation from pricing-service for room_id: {}, hotel_id: {}, dates: {} - {}",
                    roomId, hotelId, dateFrom, dateTo);

            PriceRequest request = PriceRequest.newBuilder()
                    .setRoomId(roomId)
                    .setHotelId(hotelId)
                    .setDateFrom(dateFrom.format(DATE_FORMATTER))
                    .setDateTo(dateTo.format(DATE_FORMATTER))
                    .setBasePrice(basePrice != null ? basePrice : fallbackBasePrice)
                    .setOccupancyRate(occupancyRate)
                    .build();

            PriceResponse response = pricingServiceStub.calculatePrice(request);

            double calculatedPrice = response.getPrice();
            logger.info("Price calculated successfully: {} {} for room_id: {} ",
                    calculatedPrice, response.getCurrency(), roomId);

            return calculatedPrice;

        } catch (Exception e) {
            logger.error("Error calling pricing-service for room_id: {}", roomId, e);

            if (fallbackEnabled) {
                double fallbackPrice = basePrice != null ? basePrice : fallbackBasePrice;
                logger.warn("Using fallback price: {} for room_id: {}", fallbackPrice, roomId);
                return fallbackPrice;
            } else {
                throw new RuntimeException("Pricing service unavailable and fallback disabled", e);
            }
        }
    }

    /**
     * Рассчитывает процент загруженности номера на основе существующих бронирований
     */
    public double calculateOccupancyRate(int totalBookings, int totalDays) {
        if (totalDays == 0) {
            return 0.0;
        }
        // Упрощенный расчет: количество бронирований / общее количество дней
        // В реальном приложении здесь была бы более сложная логика
        return Math.min(1.0, (double) totalBookings / totalDays);
    }
}

