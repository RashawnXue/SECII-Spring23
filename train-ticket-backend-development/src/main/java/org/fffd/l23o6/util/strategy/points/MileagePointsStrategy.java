package org.fffd.l23o6.util.strategy.points;

import java.math.BigDecimal;

public class MileagePointsStrategy extends PointsStrategy {
    public static final MileagePointsStrategy INSTANCE = new MileagePointsStrategy();

    private static final int[] MILEAGE_POINTS = {1000, 3000, 10000, 20000, 50000};

    private static final BigDecimal[] DISCOUNT_RATES = {
            new BigDecimal("0.1"),
            new BigDecimal("0.15"),
            new BigDecimal("0.2"),
            new BigDecimal("0.25"),
            new BigDecimal("0.3")
    };

    private static final BigDecimal BONUS_RATE = BigDecimal.valueOf(0.2);

    private MileagePointsStrategy(){}

    @Override
    public BigDecimal calcOrderPrice(BigDecimal basePrice, int mileagePoints) {
        BigDecimal discountRate = getDiscountRate(mileagePoints);
        return basePrice.multiply((new BigDecimal("1.0").subtract(discountRate)));
    }

    private BigDecimal getDiscountRate(int mileagePoints) {
        for (int i = MILEAGE_POINTS.length - 1; i >= 0; i--) {
            if (mileagePoints >= MILEAGE_POINTS[i]) {
                return DISCOUNT_RATES[i];
            }
        }
        return new BigDecimal("0.0"); // Default discount rate if mileagePoints is below 1000
    }

    @Override
    public int calcConsumedPoints(int points) {
        for (int i = MILEAGE_POINTS.length - 1; i >= 0; i--) {
            if (points >= MILEAGE_POINTS[i]) {
                return MILEAGE_POINTS[i];
            }
        }
        return 0;
    }

    @Override
    public int calcBonusPoints(BigDecimal price) {
        return price.multiply(BONUS_RATE).intValue();
    }
}
