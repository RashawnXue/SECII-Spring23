package org.fffd.l23o6.service;

import org.fffd.l23o6.pojo.vo.route.RouteVO;

import java.util.List;

public interface RouteService {
    /**
     * 添加路线
     * @param name 路线名
     * @param stationIds 路线所包含的站点列表
     */
    void addRoute(String name, List<Long> stationIds);

    /**
     * 列出所有路线
     * @return 包含所有路线的列表
     */
    List<RouteVO> listRoutes();

    /**
     * 获取某一路线的详细信息
     * @param id 路线ID
     * @return 路线详细信息
     */
    RouteVO getRoute(Long id);

    /**
     * 编辑某一路线
     * @param id 某修改的路线ID
     * @param name 修改后的路线名
     * @param stationIds 修改后的站点列表
     */
    void editRoute(Long id, String name, List<Long> stationIds);

    /**
     * 删除路线
     * @param id 路线ID
     */
    void deleteRoute(Long id);
}
