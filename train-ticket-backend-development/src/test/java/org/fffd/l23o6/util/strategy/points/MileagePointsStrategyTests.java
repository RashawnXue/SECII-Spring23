package org.fffd.l23o6.util.strategy.points;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MileagePointsStrategyTests {
    @Test
    void calcOrderPriceTest(){
        BigDecimal basePrice = new BigDecimal("1000.0");
        int[] mileagePoints = {0, 500, 1500, 3500, 10500, 20500, 50500};
        BigDecimal[] ansPrice = {
                new BigDecimal("1000.0"),
                new BigDecimal("1000.0"),
                new BigDecimal("900.0"),
                new BigDecimal("850.0"),
                new BigDecimal("800.0"),
                new BigDecimal("750.0"),
                new BigDecimal("700.0")
        };
        for(int i = 0;i < mileagePoints.length;i++ ){
            assertEquals(MileagePointsStrategy.INSTANCE.calcOrderPrice(basePrice, mileagePoints[i]).setScale(2), ansPrice[i].setScale(2));
        }
    }
    @Test
    void calcConsumedPointsTest(){
        int[] mileagePoints = {0, 500, 1000, 1500, 3000, 3500, 10000, 10500, 20000, 20500, 50000, 50500, 99999};
        int[] consumedPoints = {0, 0, 1000, 1000, 3000, 3000, 10000, 10000, 20000, 20000, 50000, 50000, 50000};
        for(int i = 0;i < mileagePoints.length;i++ ){
            assertEquals(MileagePointsStrategy.INSTANCE.calcConsumedPoints(mileagePoints[i]), consumedPoints[i]);
        }
    }

    @Test
    void calcBonusPointsTest(){
        BigDecimal[] basePrices = {
                new BigDecimal("0.0"),
                new BigDecimal("100.0"),
                new BigDecimal("105.0"),
                new BigDecimal("99999.0"),
        };
        int[] bonusPoints = {0, 20, 21, 19999};
        for(int i = 0;i < basePrices.length;i++ ){
            assertEquals(MileagePointsStrategy.INSTANCE.calcBonusPoints(basePrices[i]), bonusPoints[i]);
        }
    }
}
