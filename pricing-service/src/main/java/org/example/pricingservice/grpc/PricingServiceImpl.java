package org.example.pricingservice.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Month;

@GrpcService
public class PricingServiceImpl extends PricingServiceGrpc.PricingServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(PricingServiceImpl.class);
    
    private static final double OCCUPANCY_THRESHOLD_HIGH = 0.8;
    private static final double OCCUPANCY_THRESHOLD_LOW = 0.3;
    private static final double PRICE_MULTIPLIER_HIGH = 1.5;
    private static final double PRICE_MULTIPLIER_LOW = 0.8;
    private static final double SEASONAL_MULTIPLIER_SUMMER = 1.2;
    private static final double SEASONAL_MULTIPLIER_WINTER = 0.9;

    @Override
    public void calculatePrice(PriceRequest request, StreamObserver<PriceResponse> responseObserver) {
        logger.info("Received price calculation request for room_id: {}, hotel_id: {}, dates: {} - {}",
                request.getRoomId(), request.getHotelId(), request.getDateFrom(), request.getDateTo());

        try {
            LocalDate dateFrom = LocalDate.parse(request.getDateFrom());
            LocalDate dateTo = LocalDate.parse(request.getDateTo());

            double basePrice = request.getBasePrice() > 0 ? request.getBasePrice() : 1000.0;
            double occupancyRate = request.getOccupancyRate() >= 0 ? request.getOccupancyRate() : 0.5;

            double calculatedPrice = calculatePrice(basePrice, occupancyRate, dateFrom, dateTo);

            PriceResponse response = PriceResponse.newBuilder()
                    .setPrice(calculatedPrice)
                    .setCurrency("RUB")
                    .build();

            logger.info("Calculated price: {} {} for room_id: {}", calculatedPrice, response.getCurrency(), request.getRoomId());
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            logger.error("Error calculating price", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error calculating price: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private double calculatePrice(double basePrice, double occupancyRate, LocalDate dateFrom, LocalDate dateTo) {
        double price = basePrice;

        if (occupancyRate >= OCCUPANCY_THRESHOLD_HIGH) {
            price *= PRICE_MULTIPLIER_HIGH;
        } else if (occupancyRate <= OCCUPANCY_THRESHOLD_LOW) {
            price *= PRICE_MULTIPLIER_LOW;
        }

        double seasonalMultiplier = getSeasonalMultiplier(dateFrom, dateTo);
        price *= seasonalMultiplier;

        return Math.round(price * 100.0) / 100.0;
    }

    private double getSeasonalMultiplier(LocalDate dateFrom, LocalDate dateTo) {
        Month fromMonth = dateFrom.getMonth();
        Month toMonth = dateTo.getMonth();

        boolean isSummer = (fromMonth == Month.JUNE || fromMonth == Month.JULY || fromMonth == Month.AUGUST) ||
                          (toMonth == Month.JUNE || toMonth == Month.JULY || toMonth == Month.AUGUST);

        boolean isWinter = (fromMonth == Month.DECEMBER || fromMonth == Month.JANUARY || fromMonth == Month.FEBRUARY) ||
                          (toMonth == Month.DECEMBER || toMonth == Month.JANUARY || toMonth == Month.FEBRUARY);

        if (isSummer) {
            return SEASONAL_MULTIPLIER_SUMMER;
        } else if (isWinter) {
            return SEASONAL_MULTIPLIER_WINTER;
        }

        return 1.0;
    }
}

