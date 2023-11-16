package org.fffd.l23o6.service;

import java.util.List;

import org.fffd.l23o6.pojo.vo.station.StationVO;

public interface StationService {
    /**
     * 获取某一站点的详细信息
     * @param id 站点ID
     * @return 站点详细信息
     */
    public StationVO getStation(Long id);

    /**
     * 列出所有站点
     * @return 包含所有站点的列表
     */
    public List<StationVO> listStations();

    /**
     * 添加车站
     * @param name 车站名
     */
    public void addStation(String name);

    /**
     * 修改车站
     * @param id 被修改的车站ID
     * @param name 修改后的车站名
     */
    public void editStation(Long id, String name);

    /**
     * 删除车站
     * @param id 站点ID
     */
    public void deleteStation(Long id);
}
