package org.example.pricingservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;

@Service
public class PricingCalculationService {

    @Value("${pricing.base-price:1000.0}")
    private double basePrice;

    @Value("${pricing.occupancy-threshold-high:0.8}")
    private double occupancyThresholdHigh;

    @Value("${pricing.occupancy-threshold-low:0.3}")
    private double occupancyThresholdLow;

    @Value("${pricing.price-multiplier-high:1.5}")
    private double priceMultiplierHigh;

    @Value("${pricing.price-multiplier-low:0.8}")
    private double priceMultiplierLow;

    @Value("${pricing.seasonal-multiplier-summer:1.2}")
    private double seasonalMultiplierSummer;

    @Value("${pricing.seasonal-multiplier-winter:0.9}")
    private double seasonalMultiplierWinter;

    /**
     * Рассчитывает цену на основе базовой цены, загруженности и сезона
     */
    public double calculatePrice(double basePrice, double occupancyRate, LocalDate dateFrom, LocalDate dateTo) {
        double price = basePrice;

        // Применяем коэффициент загруженности
        if (occupancyRate >= occupancyThresholdHigh) {
            // Высокая загруженность - увеличиваем цену
            price *= priceMultiplierHigh;
        } else if (occupancyRate <= occupancyThresholdLow) {
            // Низкая загруженность - снижаем цену
            price *= priceMultiplierLow;
        }

        // Применяем сезонный коэффициент
        double seasonalMultiplier = getSeasonalMultiplier(dateFrom, dateTo);
        price *= seasonalMultiplier;

        return Math.round(price * 100.0) / 100.0; // Округляем до 2 знаков
    }

    /**
     * Определяет сезонный коэффициент на основе дат
     */
    private double getSeasonalMultiplier(LocalDate dateFrom, LocalDate dateTo) {
        // Летний сезон: июнь, июль, август
        Month fromMonth = dateFrom.getMonth();
        Month toMonth = dateTo.getMonth();

        boolean isSummer = (fromMonth == Month.JUNE || fromMonth == Month.JULY || fromMonth == Month.AUGUST) ||
                          (toMonth == Month.JUNE || toMonth == Month.JULY || toMonth == Month.AUGUST);

        boolean isWinter = (fromMonth == Month.DECEMBER || fromMonth == Month.JANUARY || fromMonth == Month.FEBRUARY) ||
                          (toMonth == Month.DECEMBER || toMonth == Month.JANUARY || toMonth == Month.FEBRUARY);

        if (isSummer) {
            return seasonalMultiplierSummer;
        } else if (isWinter) {
            return seasonalMultiplierWinter;
        }

        return 1.0; // Обычный сезон
    }

}

