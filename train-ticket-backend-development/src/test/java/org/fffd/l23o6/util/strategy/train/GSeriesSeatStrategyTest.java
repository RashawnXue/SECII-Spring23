package org.fffd.l23o6.util.strategy.train;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GSeriesSeatStrategyTest {

    int stationCount=10;
    boolean[][] testMap;
    Map<String,Integer> testedSeats=new HashMap<>(){{
        put("1车1A",0);put("2车1C",4);put("4车1C",17);
    }};

    @Test
    //初始值测试
    void GetLeftSeatCountTest1(){
        testMap=GSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        Map<TrainSeatStrategy.SeatType,Integer> seatCountMap=GSeriesSeatStrategy.INSTANCE.getLeftSeatCount(0,stationCount-1,testMap);
        assertEquals(seatCountMap.get(GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT),3);
        assertEquals(seatCountMap.get(GSeriesSeatStrategy.GSeriesSeatType.FIRST_CLASS_SEAT),12);
        assertEquals(seatCountMap.get(GSeriesSeatStrategy.GSeriesSeatType.SECOND_CLASS_SEAT),15);
    }
    @Test
    //每个座位类型的第一次分配测试
    void allocSeatTest1(){
        testMap=GSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        assertEquals("1车1A",GSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(0,stationCount-1, GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT,testMap));
        assertEquals("2车1A",GSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(0,stationCount-1, GSeriesSeatStrategy.GSeriesSeatType.FIRST_CLASS_SEAT,testMap));
        assertEquals("4车1A",GSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(0,stationCount-1, GSeriesSeatStrategy.GSeriesSeatType.SECOND_CLASS_SEAT,testMap));
    }
    @Test
    //无座测试
    void allocSeatTest2(){
        testMap=GSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        assertEquals("无座",GSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(0,stationCount-1, GSeriesSeatStrategy.GSeriesSeatType.NO_SEAT,testMap));
    }
    @Test
    //已被占用的座位图测试
    void allocSeatTest3(){
        testMap=GSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        testMap[0][0]=true;
        testMap[1][1]=true;
        assertEquals("1车1C",GSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(0,1, GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT,testMap));
        assertEquals("1车1F",GSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(0,3, GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT,testMap));
    }
    @Test
    //空值测试
    void allocSeatTest4(){
        testMap=GSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        for(int i=0;i<stationCount-1;i++){
            for(int j=0;j<3;j++)testMap[i][j]=true;
        }
        assertNull(GSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(0,stationCount-1, GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT,testMap));
    }
    @Test
    //座位图更新测试
    void allocSeatTest5(){
        // 座位图初始化
        testMap=GSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        testMap[0][0]=true;
        testMap[1][1]=true;

        //第一次分配测试
        int startStation1=0;
        int endStation1=1;

        GSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(startStation1,endStation1, GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT,testMap);
        boolean[][] resultMap1=testMap.clone();
        for(int i=startStation1;i<endStation1;i++){
            testMap[i][1]=true;
        }
        assertArrayEquals(resultMap1,testMap);

        //第二次分配测试
        int startStation2=0;
        int endStation2=3;
        GSeriesSeatStrategy.INSTANCE.allocSeatAndUpdateSeatMap(startStation2,endStation2, GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT,testMap);
        boolean[][] resultMap2=resultMap1.clone();
        for(int i=startStation2;i<endStation2;i++){
            resultMap2[i][2]=true;
        }
        assertArrayEquals(resultMap2,testMap);
    }
    @Test
    //座位图撤回更新测试1
    void getRevokedSeatMapTest1(){
        boolean[][] oldMap=GSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);

        for(int i=0;i<stationCount-2;i++){
            for(int j:testedSeats.values()){
                oldMap[i][j]=true;
            }
        }
        boolean[][] newMap=GSeriesSeatStrategy.INSTANCE.getRevokedSeatMap(0,stationCount-2,oldMap,"1车1A");
        for(int i=0;i<stationCount-2;i++){
            oldMap[i][testedSeats.get("1车1A")]=false;
        }
        assertArrayEquals(oldMap,newMap);
    }
    @Test
    //座位图撤回更新测试2
    void getRevokedSeatMapTest2(){
        boolean[][] oldMap=GSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);

        for(int i=0;i<stationCount-2;i++){
            for(int j:testedSeats.values()){
                oldMap[i][j]=true;
            }
        }
        boolean[][] newMap=GSeriesSeatStrategy.INSTANCE.getRevokedSeatMap(0,stationCount-2,oldMap,"4车1C");
        for(int i=0;i<stationCount-2;i++){
            oldMap[i][testedSeats.get("4车1C")]=false;
        }
        assertArrayEquals(oldMap,newMap);
    }
    @Test
    //空值测试
    void getRevokedSeatMapTest3(){
        boolean[][] oldMap=GSeriesSeatStrategy.INSTANCE.initSeatMap(stationCount);
        assertNull(GSeriesSeatStrategy.INSTANCE.getRevokedSeatMap(0,stationCount-1,oldMap,"not a seat"));
    }
    @Test
    void calcPriceTest(){
        assertEquals(45,GSeriesSeatStrategy.INSTANCE.calcPrice(0,1, GSeriesSeatStrategy.GSeriesSeatType.BUSINESS_SEAT));
        assertEquals(35,GSeriesSeatStrategy.INSTANCE.calcPrice(1,2, GSeriesSeatStrategy.GSeriesSeatType.FIRST_CLASS_SEAT));
        assertEquals(50,GSeriesSeatStrategy.INSTANCE.calcPrice(3,1, GSeriesSeatStrategy.GSeriesSeatType.SECOND_CLASS_SEAT));
        assertEquals(60,GSeriesSeatStrategy.INSTANCE.calcPrice(4,0, GSeriesSeatStrategy.GSeriesSeatType.NO_SEAT));
    }
}
