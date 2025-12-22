package org.example.pricingservice.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.pricingservice.service.PricingCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@GrpcService
public class PricingServiceImpl extends PricingServiceGrpc.PricingServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(PricingServiceImpl.class);
    private final PricingCalculationService pricingCalculationService;

    public PricingServiceImpl(PricingCalculationService pricingCalculationService) {
        this.pricingCalculationService = pricingCalculationService;
    }

    @Override
    public void calculatePrice(PriceRequest request, StreamObserver<PriceResponse> responseObserver) {
        logger.info("Received price calculation request for room_id: {}, hotel_id: {}, dates: {} - {}",
                request.getRoomId(), request.getHotelId(), request.getDateFrom(), request.getDateTo());

        try {
            LocalDate dateFrom = LocalDate.parse(request.getDateFrom());
            LocalDate dateTo = LocalDate.parse(request.getDateTo());

            // Используем базовую цену из запроса, если она указана, иначе используем значение по умолчанию
            double basePrice = request.getBasePrice() > 0 ? request.getBasePrice() : 1000.0;
            double occupancyRate = request.getOccupancyRate() >= 0 ? request.getOccupancyRate() : 0.5;

            // Рассчитываем цену
            double calculatedPrice = pricingCalculationService.calculatePrice(
                    basePrice, occupancyRate, dateFrom, dateTo);

            // Формируем ответ
            PriceResponse response = PriceResponse.newBuilder()
                    .setPrice(calculatedPrice)
                    .setCurrency("RUB")
                    .build();

            logger.info("Calculated price: {} {} for room_id: {}", calculatedPrice, response.getCurrency(), request.getRoomId());

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            logger.error("Error calculating price", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Error calculating price: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}

