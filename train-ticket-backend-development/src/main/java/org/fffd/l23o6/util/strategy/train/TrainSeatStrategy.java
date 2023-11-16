package org.fffd.l23o6.util.strategy.train;

import jakarta.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class TrainSeatStrategy {
    /**
     * 分配两车站间的第一个空余席位，并更新座位图
     * @param startStationIndex 开始车站的索引
     * @param endStationIndex 到达车站的索引
     * @param type 列车类型
     * @param seatMap 列车的座位图，若成功分配则更新
     * @return 座位描述字符串，没有空余席位时返回null
     */
    public abstract @Nullable String allocSeatAndUpdateSeatMap(int startStationIndex, int endStationIndex, SeatType type, boolean[][] seatMap);

    /**
     * 获取两车站间每种列车的剩余座位数
     * @param startStationIndex 开始车站的索引
     * @param endStationIndex 到达车站的索引
     * @param seatMap   列车的座位图
     * @return Map，key为座位类型，value为剩余座位数
     */
    public abstract Map<SeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap);

    /**
     * 撤回列车上的一个席位，并更新座位图
     * @param startStationIndex 开始车站的索引
     * @param endStationIndex 到达车站的索引
     * @param oldMap 原来的座位图
     * @param seatString 需要撤回的座位描述字符串
     * @return 更新后的座位图，字符串描述的座位不存在时返回null
     */
    public abstract @Nullable boolean[][] getRevokedSeatMap(int startStationIndex, int endStationIndex,boolean[][] oldMap,String seatString);
    /**
     * 初始化座位图
     * @param stationCount 车站数量
     * @return 二维布尔数组，第一维表示下标车站与下一站，第二维表示座位
     */
    public abstract boolean[][] initSeatMap(int stationCount);

    /**
     * 获取列车的座位类型
     * @return 座位类型数组
     */
    public abstract SeatType[] getSeatTypes();

    /**
     * 计算两车站间的票价
     * @param startStationIndex 开始车站的索引
     * @param endStationIndex 到达车站的索引
     * @param type 座位类型
     * @return 票价，若不存在该类型座位则返回null
     */
    public abstract Integer calcPrice(int startStationIndex, int endStationIndex, SeatType type);

    /**
     * 将座位描述字符串转换为座位类型
     * @param seatType 座位描述字符串
     * @return 座位类型，不存在该类型座位时返回null
     */
    public abstract SeatType fromString(String seatType);

    protected boolean[][] getRevokedSeatMap(int startStationIndex, int endStationIndex, boolean[][]oldMap, String seatString, Map<SeatType,Map<Integer,String>> typeMap){
        if(seatString.equals("无座")){
            //不需要撤回
            return oldMap;
        }
        //初始化新座位图
        boolean[][] newMap=oldMap.clone();
        //找到座位的索引
        int index=-1;
        for(SeatType type:typeMap.keySet()){
            if(typeMap.get(type).containsValue(seatString)){
                for(int i:typeMap.get(type).keySet()){
                    if(typeMap.get(type).get(i).equals(seatString)){
                        index=i;
                        break;
                    }
                }
            }
        }
        if(index==-1){
            //该座位描述不存在
            return null;
        }

        //将该座位从座位图中撤回
        for(int i=startStationIndex;i<endStationIndex;i++){
            newMap[i][index]=false;
        }
        return newMap;
    }
    protected String allocSeatAndUpdateSeatMap(int startStationIndex, int endStationIndex, SeatType type, boolean[][] seatMap, Map<SeatType, Map<Integer, String>> typeMap, Map<SeatType,Integer> typeMapBase){
        //"无座" 总会返回 "无座"
        if(type.getText().equals("无座")){
            return "无座";
        }
        //找出该类型座位的第一个空余座位
        boolean isAvailable;
        for(int i=typeMapBase.get(type);i-typeMapBase.get(type)<typeMap.get(type).size();i++){
            isAvailable=true;
            for(int j=startStationIndex;j<endStationIndex;j++){
                if(seatMap[j][i]){
                    isAvailable=false;
                    break;
                }
            }
            if(isAvailable){
                //更改传入的seatMap
                for(int j=startStationIndex;j<endStationIndex;j++){
                    seatMap[j][i]=true;
                }
                return typeMap.get(type).get(i);
            }
        }

        //如果没有空余座位，返回null
        return null;
    }
    protected Map<SeatType,Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap,Map<SeatType, Map<Integer, String>> typeMap){

        Map<SeatType,Integer> map=new HashMap<>();
        int base=0;     //策略类座位总图中每种座位类型的基数
        int counter=0;  //访问座位总图下标的计数器
        int count=0;    //每个座位类型的剩余座位数
        boolean isAvailable=true;

        for(SeatType type:typeMap.keySet()){
            for(count=0; counter-base<typeMap.get(type).size(); counter++){
                isAvailable=true;
                for(int i=startStationIndex;i<endStationIndex;i++){
                    if(seatMap[i][counter]){
                        isAvailable=false;
                        break;
                    }
                }
                if(isAvailable){ count++;}
            }
            map.put(type, count);
            base+=typeMap.get(type).size();
        }
        return map;
    }
    public interface SeatType {
        public String getText();
    }
    
}
