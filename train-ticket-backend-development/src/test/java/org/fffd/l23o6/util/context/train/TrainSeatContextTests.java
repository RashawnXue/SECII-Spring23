package org.fffd.l23o6.util.context.train;

import org.fffd.l23o6.util.strategy.train.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.fffd.l23o6.pojo.enum_.TrainType.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TrainSeatContextTests {
    @Test
    void test() {
        assertEquals(TrainSeatContext.getInstance().getStrategy(HIGH_SPEED), GSeriesSeatStrategy.INSTANCE);
        assertEquals(TrainSeatContext.getInstance().getStrategy(NORMAL_SPEED), KSeriesSeatStrategy.INSTANCE);
    }
}
