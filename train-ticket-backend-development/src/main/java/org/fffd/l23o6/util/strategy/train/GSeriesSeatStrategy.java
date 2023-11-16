package org.fffd.l23o6.util.strategy.train;

import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nullable;


public class GSeriesSeatStrategy extends TrainSeatStrategy {
    public static final GSeriesSeatStrategy INSTANCE = new GSeriesSeatStrategy();
     
    private final Map<Integer, String> BUSINESS_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> FIRST_CLASS_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> SECOND_CLASS_SEAT_MAP = new HashMap<>();

    private final Map<SeatType, Map<Integer, String>> TYPE_MAP = new HashMap<>() {{
        put(GSeriesSeatType.BUSINESS_SEAT, BUSINESS_SEAT_MAP);
        put(GSeriesSeatType.FIRST_CLASS_SEAT, FIRST_CLASS_SEAT_MAP);
        put(GSeriesSeatType.SECOND_CLASS_SEAT, SECOND_CLASS_SEAT_MAP);
    }};

    private final Map<SeatType,Integer> TYPE_MAP_BASE=new HashMap<>();

    private GSeriesSeatStrategy() {

        int counter = 0;

        TYPE_MAP_BASE.put(GSeriesSeatType.BUSINESS_SEAT,counter);
        for (String s : Arrays.asList("1车1A", "1车1C", "1车1F")) {
            BUSINESS_SEAT_MAP.put(counter++, s);
        }

        TYPE_MAP_BASE.put(GSeriesSeatType.FIRST_CLASS_SEAT,counter);
        for (String s : Arrays.asList("2车1A", "2车1C", "2车1D", "2车1F", "2车2A", "2车2C", "2车2D", "2车2F", "3车1A", "3车1C", "3车1D", "3车1F")) {
            FIRST_CLASS_SEAT_MAP.put(counter++, s);
        }

        TYPE_MAP_BASE.put(GSeriesSeatType.SECOND_CLASS_SEAT,counter);
        for (String s : Arrays.asList("4车1A", "4车1B", "4车1C", "4车1D", "4车2F", "4车2A", "4车2B", "4车2C", "4车2D", "4车2F", "4车3A", "4车3B", "4车3C", "4车3D", "4车3F")) {
            SECOND_CLASS_SEAT_MAP.put(counter++, s);
        }
    }

    public enum GSeriesSeatType implements SeatType {
        BUSINESS_SEAT("商务座"), FIRST_CLASS_SEAT("一等座"), SECOND_CLASS_SEAT("二等座"), NO_SEAT("无座");
        private String text;
        GSeriesSeatType(String text){
            this.text=text;
        }
        public String getText() {
            return this.text;
        }
        public static GSeriesSeatType fromString(String text) {
            for (GSeriesSeatType b : GSeriesSeatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @Override
    public @Nullable String allocSeatAndUpdateSeatMap(int startStationIndex, int endStationIndex, SeatType type, boolean[][] seatMap) {
        //endStationIndex - 1 = upper bound
        return super.allocSeatAndUpdateSeatMap(startStationIndex, endStationIndex, type, seatMap, TYPE_MAP,TYPE_MAP_BASE);
    }

    @Override
    public Map<SeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap) {

        return super.getLeftSeatCount(startStationIndex, endStationIndex, seatMap, TYPE_MAP);
    }

    @Override
    public @Nullable boolean[][] getRevokedSeatMap(int startStationIndex, int endStationIndex,boolean[][] oldMap,String seatString){
        return super.getRevokedSeatMap(startStationIndex,endStationIndex,oldMap,seatString,TYPE_MAP);
    }

    @Override
    public boolean[][] initSeatMap(int stationCount) {
        return new boolean[stationCount - 1][BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size() + SECOND_CLASS_SEAT_MAP.size()];
    }

    @Override
    public SeatType[] getSeatTypes(){
        return GSeriesSeatType.values();
    }

    @Override
    public Integer calcPrice(int startStationIndex, int endStationIndex, SeatType type){
        int basePrice = Math.abs(startStationIndex - endStationIndex);
        switch ((GSeriesSeatType)type){
            case NO_SEAT:
                return 15 * basePrice;
            case SECOND_CLASS_SEAT:
                return 25 * basePrice;
            case FIRST_CLASS_SEAT:
                return 35 * basePrice;
            case BUSINESS_SEAT:
                return 45 * basePrice;
            default:
                return null;
        }
    }

    @Override
    public SeatType fromString(String seatType){
        return GSeriesSeatType.fromString(seatType);
    }
}
