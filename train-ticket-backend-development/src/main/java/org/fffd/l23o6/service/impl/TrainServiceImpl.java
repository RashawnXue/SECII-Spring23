package org.fffd.l23o6.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.fffd.l23o6.util.context.train.TrainSeatContext;
import org.fffd.l23o6.util.strategy.train.TrainSeatStrategy;
import org.fffd.l23o6.service.UserService;
import org.springframework.data.util.Pair;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.mapper.TrainMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.train.AdminTrainVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.pojo.vo.train.TicketInfo;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.service.TrainService;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {
    private final UserService userService;
    private final TrainDao trainDao;
    private final RouteDao routeDao;

    @Override
    public TrainDetailVO getTrain(Long trainId) {
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        return TrainDetailVO.builder().id(trainId).date(train.getDate()).name(train.getName())
                .stationIds(route.getStationIds()).arrivalTimes(train.getArrivalTimes())
                .departureTimes(train.getDepartureTimes()).extraInfos(train.getExtraInfos()).build();
    }

    @Override
    public List<TrainVO> listTrains(Long startStationId, Long endStationId, String date) {
        System.out.println("date: "+date);

        if (startStationId == null || endStationId == null || date == null) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS);
        }

        // 获取所有包含乘车区间的路线
        List<RouteEntity> routes = routeDao.findAll();
        List<RouteEntity> filteredRoutes = routes.stream()
                .filter(route -> {
                    List<Long> stationIds = route.getStationIds();
                    if (stationIds.contains(startStationId) && stationIds.contains(endStationId)) {
                        int startIndex = stationIds.indexOf(startStationId);
                        int endIndex = stationIds.indexOf(endStationId);
                        return startIndex < endIndex;
                    }
                    return false;
                })
                .toList();

        // 获取当天的还未发车的此路线对应的所有车次
        List<TrainEntity> trains = trainDao.findAll();
        List<TrainEntity> filteredTrains = trains.stream()
                .filter(train -> filteredRoutes.stream()
                        .anyMatch(route -> Objects.equals(route.getId(), train.getRouteId())))
                .filter(train -> filterByDate(train, startStationId, date))
                .filter(train -> isTrainComing(train,startStationId))
                .toList();
        List<TrainVO> filteredTrainsVO = filteredTrains.stream().map(trainEntity -> generateTrainVO(trainEntity, startStationId, endStationId))
                .toList();

        return filteredTrainsVO;
    }


    @Override
    public List<AdminTrainVO> listTrainsAdmin() {
        // 检查用户权限
        userService.checkAuthority();
        return trainDao.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(TrainMapper.INSTANCE::toAdminTrainVO).collect(Collectors.toList());
    }

    @Override
    public void addTrain(String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
            List<Date> departureTimes) {
        // 检查用户权限
        userService.checkAuthority();
        TrainEntity entity = TrainEntity.builder().name(name).routeId(routeId).trainType(type)
                .date(date).arrivalTimes(arrivalTimes).departureTimes(departureTimes).build();
        RouteEntity route = routeDao.findById(routeId).get();
        if (route.getStationIds().size() != entity.getArrivalTimes().size()
                || route.getStationIds().size() != entity.getDepartureTimes().size()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列表长度错误");
        }
        entity.setExtraInfos(new ArrayList<String>(Collections.nCopies(route.getStationIds().size(), "预计正点")));

        TrainSeatStrategy seatStrategy = TrainSeatContext.getInstance().getStrategy(entity.getTrainType());
        entity.setSeats(seatStrategy.initSeatMap(route.getStationIds().size()));

        trainDao.save(entity);
    }

    @Override
    public void changeTrain(Long id, String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
                            List<Date> departureTimes) {
        // 检查用户权限
        userService.checkAuthority();
        Optional<TrainEntity> optionalTrain = trainDao.findById(id);
        TrainEntity entity;
        if (optionalTrain.isPresent()) {
            entity = optionalTrain.get();
        } else {
            // 处理TrainEntity对象为空的情况
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS);
        }
        // 对存在的TrainEntity对象进行操作
        entity.setName(name).setRouteId(routeId).setTrainType(type).setDate(date).setArrivalTimes(arrivalTimes).setDepartureTimes(departureTimes);
        RouteEntity route = routeDao.findById(routeId).get();
        if (route.getStationIds().size() != entity.getArrivalTimes().size()
                || route.getStationIds().size() != entity.getDepartureTimes().size()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列表长度错误");
        }
        //entity.setExtraInfos(new ArrayList<String>(Collections.nCopies(route.getStationIds().size(), "预计正点")));

        TrainSeatStrategy seatStrategy = TrainSeatContext.getInstance().getStrategy(entity.getTrainType());
        entity.setSeats(seatStrategy.initSeatMap(route.getStationIds().size()));

        trainDao.save(entity);

    }

    @Override
    public void deleteTrain(Long id) {
        userService.checkAuthority();
        trainDao.deleteById(id);
    }

    /**
     * 由对应的车次实体类对象生成TrainVO类型对象
     * @param train 车次实体
     * @param startStationId 出发站ID
     * @param endStationId 目的站ID
     * @return TrainVO对象
     */
    private TrainVO generateTrainVO(TrainEntity train, Long startStationId, Long endStationId) {
        Pair<Date, Date> times = getTrainTimes(train, startStationId, endStationId);
        List<TicketInfo> ticketInfo = getTicketInfo(train,startStationId,endStationId);
        return TrainVO.builder()
                .id(train.getId())
                .name(train.getName())
                .startStationId(startStationId)
                .endStationId(endStationId)
                .departureTime(times.getFirst())
                .arrivalTime(times.getSecond())
                .ticketInfo(ticketInfo)
                .build();
    }


    /**
     * 获得对应的不同车票类型的列表
     * @param train 车次实体
     * @param startStationId 出发站ID
     * @param endStationId 目的站ID
     * @return 不同车票类型组成的列表
     */
    private List<TicketInfo> getTicketInfo(TrainEntity train, Long startStationId, Long endStationId) {
        List<TicketInfo> ticketInfos = new ArrayList<>();
        TrainType type = train.getTrainType();
        boolean[][] seats = train.getSeats();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = route.getStationIds().indexOf(startStationId);
        int endStationIndex = route.getStationIds().indexOf(endStationId);

        // 使用对应策略
        TrainSeatStrategy seatStrategy = TrainSeatContext.getInstance().getStrategy(type);

        for(TrainSeatStrategy.SeatType seatType:seatStrategy.getSeatTypes()){
            TicketInfo ticket = new TicketInfo(seatType.getText(),
                    seatStrategy.getLeftSeatCount(startStationIndex,endStationIndex,seats).get(seatType),
                    seatStrategy.calcPrice(startStationIndex,endStationIndex,seatType));
            ticketInfos.add(ticket);
        }

        return ticketInfos;
    }

    /**
     * 获取车次在路线中某一段上的发车和到达时间
     * @param train 车次实体
     * @param startStationId 出发站ID
     * @param endStationId 目的站ID
     * @return 对应站点的发车和到达时间
     */
    private Pair<Date, Date> getTrainTimes(TrainEntity train, Long startStationId, Long endStationId) {
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        // 对应station的id在station_ids中的index
        int startStationIdId = route.getStationIds().indexOf(startStationId);
        int endStationIdId = route.getStationIds().indexOf(endStationId);
        Date departureTime = train.getDepartureTimes().get(startStationIdId);
        Date arrivalTime = train.getArrivalTimes().get(endStationIdId);

        return Pair.of(departureTime, arrivalTime);
    }

    /**
     * 判断车次是否已发车
     * @param train 车次实体
     * @param startStationId 出发站ID
     * @return 真当且仅当该车次在该站点还未发车
     */
    private boolean isTrainComing(TrainEntity train, Long startStationId) {
        Optional<RouteEntity> routeOptional = routeDao.findById(train.getRouteId());
        if (routeOptional.isEmpty()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS);
        }
        RouteEntity route = routeOptional.get();
        List<Long> stationIds = route.getStationIds();
        int startStationIndex = stationIds.indexOf(startStationId);
        List<Date> departureTimes = train.getDepartureTimes();
        Date departureTime = departureTimes.get(startStationIndex);
        Date now = new Date();

        // 获取时间戳并转换为分钟精度
        double timestamp1 = Math.floor(now.getTime() / 60000);
        double timestamp2 = Math.floor(departureTime.getTime() / 60000);

        if (timestamp1 < timestamp2) {
            return true;
        } else return !(timestamp1 > timestamp2);
    }

    /**
     * 根据发车时间进行过滤
     * @param train 车次实体
     * @param startStationId 出发站ID
     * @param date 期望发车时间
     * @return 真当且仅当车次在该站点发车时间与期望相符
     */
    private boolean filterByDate(TrainEntity train, Long startStationId, String date){
        Optional<RouteEntity> routeOptional = routeDao.findById(train.getRouteId());
        if (routeOptional.isEmpty()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS);
        }
        RouteEntity route = routeOptional.get();
        List<Long> stationIds = route.getStationIds();
        int startStationIndex = stationIds.indexOf(startStationId);
        List<Date> departureTimes = train.getDepartureTimes();
        Date departureTime = departureTimes.get(startStationIndex);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(departureTime);

        int departTime_year = calendar.get(Calendar.YEAR);
        int departureTime_month = calendar.get(Calendar.MONTH)+1;
        int departureTime_day = calendar.get(Calendar.DAY_OF_MONTH);

        int date_year = Integer.parseInt(date.substring(0,4));
        int date_month = Integer.parseInt(date.substring(5,7));
        int date_day = Integer.parseInt(date.substring(8,10));

        System.out.println(departureTime);
        System.out.println(date_year + " " + date_month + " " + date_day);
        System.out.println(departTime_year + " " + departureTime_month + " " + departureTime_day);


        return (date_day == departureTime_day) && (date_month == departureTime_month) && (date_year == departTime_year);
    }
}
