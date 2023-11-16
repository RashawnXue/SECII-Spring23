package org.fffd.l23o6.service;

import java.util.Date;
import java.util.List;

import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.train.AdminTrainVO;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;

public interface TrainService {
    /**
     * 获取某一车次详细信息
     * @param trainId 车次ID
     * @return 车次详细信息
     */
    public TrainDetailVO getTrain(Long trainId);

    /**
     * 列出符合条件的所有车次
     * @param startStationId 出发站ID
     * @param endStationId 目的站ID
     * @param date 出发时间
     * @return 包含所有符合条件的车次的列表
     */
    public List<TrainVO> listTrains(Long startStationId, Long endStationId, String date);

    /**
     * 为管理员列出所有车次的信息
     * @return 包含所有车次管理信息的列表
     */
    public List<AdminTrainVO> listTrainsAdmin();

    /**
     * 添加车次
     * @param name 车次名
     * @param routeId 路线ID
     * @param type 车次类型
     * @param date 车次时间
     * @param arrivalTimes 车次到达各个站点的时间
     * @param departureTimes 车次从各个站点出发的时间
     */
    public void addTrain(String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
                         List<Date> departureTimes);

    /**
     * 修改车次
     * @param trainId 被修改的车次ID
     * @param name 修改后的车次名
     * @param routeId 修改后的路线ID
     * @param type 修改后的车次类型
     * @param date 修改后的车次时间
     * @param arrivalTimes 修改后的车次到达各个站点的时间
     * @param departureTimes 修改后的车次离开各个站点的时间
     */
    public void changeTrain(Long trainId, String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
            List<Date> departureTimes);

    /**
     * 删除车次
     * @param trainId 车次ID
     */
    public void deleteTrain(Long trainId);
}
