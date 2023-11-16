package org.fffd.l23o6.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import cn.dev33.satoken.stp.StpUtil;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.mapper.RouteMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.UserType;
import org.fffd.l23o6.pojo.vo.route.RouteVO;
import org.fffd.l23o6.service.RouteService;
import org.fffd.l23o6.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {
    private final UserService userService;
    private final RouteDao routeDao;
    private final TrainDao trainDao;

    @Override
    public void addRoute(String name, List<Long> stationIds) {
        // 检查用户权限
        userService.checkAuthority();
        RouteEntity entity = routeDao.findByName(name);
        if(entity != null){
            throw new BizException(BizError.ROUTENAME_EXISTS);
        }
        RouteEntity route = RouteEntity.builder().name(name).stationIds(stationIds).build();
        routeDao.save(route);
    }

    @Override
    public List<RouteVO> listRoutes() {
        // 检查用户权限
        userService.checkAuthority();
        return routeDao.findAll(Sort.by(Sort.Direction.ASC, "name")).stream().map(RouteMapper.INSTANCE::toRouteVO).collect(Collectors.toList());
    }

    @Override
    public RouteVO getRoute(Long id) {
        // 检查用户权限
        userService.checkAuthority();
        RouteEntity entity = routeDao.findById(id).get();
        return RouteMapper.INSTANCE.toRouteVO(entity);
    }

    @Override
    public void editRoute(Long id, String name, List<Long> stationIds) {
        // 检查用户权限
        userService.checkAuthority();
        routeDao.save(routeDao.findById(id).get().setStationIds(stationIds).setName(name));
    }

    @Override
    public void deleteRoute(Long id) {
        // 检查用户权限
        userService.checkAuthority();
        List<TrainEntity> trains = trainDao.findAll();
        for(TrainEntity train : trains){
            if(train.getRouteId().equals(id)){
                throw new BizException(BizError.TRAIN_DATA_DEPENDENCY);
            }
        }
        routeDao.deleteById(id);
    }
}
