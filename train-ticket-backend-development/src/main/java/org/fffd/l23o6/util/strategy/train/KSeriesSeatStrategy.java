package org.fffd.l23o6.util.strategy.train;

import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nullable;



public class KSeriesSeatStrategy extends TrainSeatStrategy {
    public static final KSeriesSeatStrategy INSTANCE = new KSeriesSeatStrategy();
     
    private final Map<Integer, String> SOFT_SLEEPER_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> HARD_SLEEPER_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> SOFT_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> HARD_SEAT_MAP = new HashMap<>();

    private final Map<SeatType, Map<Integer, String>> TYPE_MAP = new HashMap<>() {{
        put(KSeriesSeatType.SOFT_SLEEPER_SEAT, SOFT_SLEEPER_SEAT_MAP);
        put(KSeriesSeatType.HARD_SLEEPER_SEAT, HARD_SLEEPER_SEAT_MAP);
        put(KSeriesSeatType.SOFT_SEAT, SOFT_SEAT_MAP);
        put(KSeriesSeatType.HARD_SEAT, HARD_SEAT_MAP);
    }};

    private final Map<SeatType,Integer> TYPE_MAP_BASE=new HashMap<>();

    private KSeriesSeatStrategy() {

        int counter = 0;
        TYPE_MAP_BASE.put(KSeriesSeatType.SOFT_SLEEPER_SEAT,counter);
        for (String s : Arrays.asList("软卧1号上铺", "软卧2号下铺", "软卧3号上铺", "软卧4号上铺", "软卧5号上铺", "软卧6号下铺", "软卧7号上铺", "软卧8号上铺")) {
            SOFT_SLEEPER_SEAT_MAP.put(counter++, s);
        }
        TYPE_MAP_BASE.put(KSeriesSeatType.HARD_SLEEPER_SEAT,counter);
        for (String s : Arrays.asList("硬卧1号上铺", "硬卧2号中铺", "硬卧3号下铺", "硬卧4号上铺", "硬卧5号中铺", "硬卧6号下铺", "硬卧7号上铺", "硬卧8号中铺", "硬卧9号下铺", "硬卧10号上铺", "硬卧11号中铺", "硬卧12号下铺")) {
            HARD_SLEEPER_SEAT_MAP.put(counter++, s);
        }
        TYPE_MAP_BASE.put(KSeriesSeatType.SOFT_SEAT,counter);
        for (String s : Arrays.asList("1车1座", "1车2座", "1车3座", "1车4座", "1车5座", "1车6座", "1车7座", "1车8座", "2车1座", "2车2座", "2车3座", "2车4座", "2车5座", "2车6座", "2车7座", "2车8座")) {
            SOFT_SEAT_MAP.put(counter++, s);
        }
        TYPE_MAP_BASE.put(KSeriesSeatType.HARD_SEAT,counter);
        for (String s : Arrays.asList("3车1座", "3车2座", "3车3座", "3车4座", "3车5座", "3车6座", "3车7座", "3车8座", "3车9座", "3车10座", "4车1座", "4车2座", "4车3座", "4车4座", "4车5座", "4车6座", "4车7座", "4车8座", "4车9座", "4车10座")) {
            HARD_SEAT_MAP.put(counter++, s);
        }
    }

    public enum KSeriesSeatType implements SeatType {
        SOFT_SLEEPER_SEAT("软卧"), HARD_SLEEPER_SEAT("硬卧"), SOFT_SEAT("软座"), HARD_SEAT("硬座"), NO_SEAT("无座");
        private String text;
        KSeriesSeatType(String text){
            this.text=text;
        }
        public String getText() {
            return this.text;
        }
        public static KSeriesSeatType fromString(String text) {
            for (KSeriesSeatType b : KSeriesSeatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    public @Nullable String allocSeatAndUpdateSeatMap(int startStationIndex, int endStationIndex, SeatType type, boolean[][] seatMap) {
        //endStationIndex - 1 = upper bound
        return super.allocSeatAndUpdateSeatMap(startStationIndex, endStationIndex, type, seatMap, TYPE_MAP, TYPE_MAP_BASE);
    }

    public Map<SeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap) {
        return super.getLeftSeatCount(startStationIndex, endStationIndex, seatMap, TYPE_MAP);
    }

    @Override
    public @Nullable boolean[][] getRevokedSeatMap(int startStationIndex, int endStationIndex, boolean[][] oldMap, String seatString){
        return super.getRevokedSeatMap(startStationIndex, endStationIndex, oldMap, seatString, TYPE_MAP);
    }

    @Override
    public boolean[][] initSeatMap(int stationCount) {
        return new boolean[stationCount - 1][SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size() + HARD_SEAT_MAP.size()];
    }

    @Override
    public SeatType[] getSeatTypes(){
        return KSeriesSeatType.values();
    }

    @Override
    public Integer calcPrice(int startStationIndex, int endStationIndex, SeatType type){
        int basePrice = Math.abs(startStationIndex - endStationIndex);
        switch ((KSeriesSeatType)type){
            case NO_SEAT:
                return 10 * basePrice;
            case HARD_SEAT:
                return 20 * basePrice;
            case SOFT_SEAT:
                return 30 * basePrice;
            case HARD_SLEEPER_SEAT:
                return 40 * basePrice;
            case SOFT_SLEEPER_SEAT:
                return 50 * basePrice;
            default:
                return null;
        }
    }

    @Override
    public SeatType fromString(String seatType){
        return KSeriesSeatType.fromString(seatType);
    }
}
