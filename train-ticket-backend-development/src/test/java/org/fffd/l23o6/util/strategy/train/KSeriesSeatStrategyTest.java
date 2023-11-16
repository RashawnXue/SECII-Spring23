package org.fffd.l23o6.util.strategy.train;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class KSeriesSeatStrategyTest {
    int stationCount=10;
    boolean[][] testMap;
    Map<String,Integer> testedSeats=new HashMap<>(){{
        put("软卧1号上铺",0);put("硬卧5号中铺",12);put("2车8座",35);put("3车3座",38);
    }};

    @Test
    //初始值测试
    void GetLeftSeatCountTest1(){
        testMap=KSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        Map<TrainSeatStrategy.SeatType,Integer> seatCountMap=KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(0,stationCount-1,testMap);
        assertEquals(seatCountMap.get(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT),8);
        assertEquals(seatCountMap.get(KSeriesSeatStrategy.KSeriesSeatType.HARD_SLEEPER_SEAT),12);
        assertEquals(seatCountMap.get(KSeriesSeatStrategy.KSeriesSeatType.SOFT_SEAT),16);
        assertEquals(seatCountMap.get(KSeriesSeatStrategy.KSeriesSeatType.HARD_SEAT),20);
    }
    @Test
    //初始分配测试
    void allocSeatTest1(){
        testMap=KSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        assertEquals("软卧1号上铺",KSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(0,stationCount-1, KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT,testMap));
        assertEquals("硬卧1号上铺",KSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(3,stationCount-1, KSeriesSeatStrategy.KSeriesSeatType.HARD_SLEEPER_SEAT,testMap));
        assertEquals("1车1座",KSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(stationCount-2,stationCount-1, KSeriesSeatStrategy.KSeriesSeatType.SOFT_SEAT,testMap));
        assertEquals("3车1座",KSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(stationCount-2,stationCount-1, KSeriesSeatStrategy.KSeriesSeatType.HARD_SEAT,testMap));
    }
    @Test
    //无座测试
    void allocSeatTest2(){
        testMap=KSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        assertEquals("无座",KSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(0,stationCount-1, KSeriesSeatStrategy.KSeriesSeatType.NO_SEAT,testMap));
    }
    @Test
    //占用后的分配测试
    void allocSeatTest3(){
        testMap=KSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        testMap[0][0]=true;
        testMap[1][1]=true;

        int startStation1=0;
        int endStation1=1;
        int startStation2=0;
        int endStation2=3;

        assertEquals("软卧2号下铺",KSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(startStation1,endStation1, KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT,testMap));
        assertEquals("软卧3号上铺",KSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(startStation2,endStation2, KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT,testMap));
    }
    @Test
    //空值测试
    void allocSeatTest4(){
        testMap=KSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        for(int i=0;i<stationCount-1;i++){
            for(int j=0;j<8;j++)testMap[i][j]=true;
        }
        assertNull(KSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(0,stationCount-1, KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT,testMap));
    }
    @Test
        //座位图更新测试
    void allocSeatTest5(){
        // 座位图初始化
        testMap=KSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        testMap[0][0]=true;
        testMap[1][1]=true;

        //第一次分配测试
        int startStation1=0;
        int endStation1=1;

        KSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(startStation1,endStation1, KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT,testMap);
        boolean[][] resultMap1=testMap.clone();
        for(int i=startStation1;i<endStation1;i++){
            testMap[i][1]=true;
        }
        assertArrayEquals(resultMap1,testMap);

        //第二次分配测试
        int startStation2=0;
        int endStation2=3;
        KSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(startStation2,endStation2, KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT,testMap);
        boolean[][] resultMap2=resultMap1.clone();
        for(int i=startStation2;i<endStation2;i++){
            resultMap2[i][2]=true;
        }
        assertArrayEquals(resultMap2,testMap);
    }
    @Test
    //座位图撤回更新测试1
    void getRevokedSeatMapTest1(){
        boolean[][] oldMap=KSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);

        for(int i=0;i<stationCount-2;i++){
            for(int j:testedSeats.values()){
                oldMap[i][j]=true;
            }
        }
        boolean[][] newMap=KSeriesSeatStrategy.INSTANCE.getRevokedSeatMap(0,stationCount-2,oldMap,"软卧1号上铺");
        for(int i=0;i<stationCount-2;i++){
            oldMap[i][testedSeats.get("软卧1号上铺")]=false;
        }
        assertArrayEquals(oldMap,newMap);
    }
    @Test
    //座位图撤回更新测试2
    void getRevokedSeatMapTest2(){
        boolean[][] oldMap=KSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);

        for(int i=0;i<stationCount-2;i++){
            for(int j:testedSeats.values()){
                oldMap[i][j]=true;
            }
        }
        boolean[][] newMap=KSeriesSeatStrategy.INSTANCE.getRevokedSeatMap(0,stationCount-2,oldMap,"2车8座");
        for(int i=0;i<stationCount-2;i++){
            oldMap[i][testedSeats.get("2车8座")]=false;
        }
        assertArrayEquals(oldMap,newMap);
    }
    @Test
    //空值测试
    void getRevokedSeatMapTest3(){
        boolean[][] oldMap=KSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        assertNull(KSeriesSeatStrategy.INSTANCE.getRevokedSeatMap(0,stationCount-1,oldMap,"not a seat"));
    }
    @Test
    void calcPriceTest(){
        assertEquals(10,KSeriesSeatStrategy.INSTANCE.calcPrice(0,1, KSeriesSeatStrategy.KSeriesSeatType.NO_SEAT));
        assertEquals(20,KSeriesSeatStrategy.INSTANCE.calcPrice(2,1, KSeriesSeatStrategy.KSeriesSeatType.HARD_SEAT));
        assertEquals(60,KSeriesSeatStrategy.INSTANCE.calcPrice(3,1, KSeriesSeatStrategy.KSeriesSeatType.SOFT_SEAT));
        assertEquals(120,KSeriesSeatStrategy.INSTANCE.calcPrice(4,1, KSeriesSeatStrategy.KSeriesSeatType.HARD_SLEEPER_SEAT));
        assertEquals(200,KSeriesSeatStrategy.INSTANCE.calcPrice(5,1, KSeriesSeatStrategy.KSeriesSeatType.SOFT_SLEEPER_SEAT));
    }
}
