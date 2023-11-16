package org.fffd.l23o6.util.context.train;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import org.fffd.l23o6.util.strategy.train.*;
import org.fffd.l23o6.pojo.enum_.TrainType;

public class TrainSeatContext {
    private static final TrainSeatContext INSTANCE=new TrainSeatContext();
    private TrainSeatContext(){}

    public static TrainSeatContext getInstance(){
        return INSTANCE;
    }

    /**
     * 根据列车类型获取对应的座位策略
     * @param trainType 列车类型
     * @return 座位策略, 列车类型不存在则抛出异常
     */
    public TrainSeatStrategy getStrategy(TrainType trainType){
        switch (trainType){
            case HIGH_SPEED:
                return GSeriesSeatStrategy.INSTANCE;
            case NORMAL_SPEED:
                return KSeriesSeatStrategy.INSTANCE;
            default:
                throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列车类型错误");
        }
    }
}
