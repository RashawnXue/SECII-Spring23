package org.fffd.l23o6.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import cn.dev33.satoken.stp.StpUtil;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.StationDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.mapper.StationMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.StationEntity;
import org.fffd.l23o6.pojo.enum_.UserType;
import org.fffd.l23o6.pojo.vo.station.StationVO;
import org.fffd.l23o6.service.StationService;
import org.fffd.l23o6.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService{
    private final UserService userService;
    private final StationDao stationDao;
    private final RouteDao routeDao;

    @Override
    public StationVO getStation(Long id){
        return StationMapper.INSTANCE.toStationVO(stationDao.findById(id).get());
    }

    @Override
    public List<StationVO> listStations(){
        return stationDao.findAll(Sort.by(Sort.Direction.ASC, "name")).stream().map(StationMapper.INSTANCE::toStationVO).collect(Collectors.toList());
    }

    @Override
    public void addStation(String name){
        // 检查用户权限
        userService.checkAuthority();
        StationEntity entity = stationDao.findByName(name);
        if(entity != null){
            throw new BizException(BizError.STATIONNAME_EXISTS);
        }
        stationDao.save(StationEntity.builder().name(name).build());
    }

    @Override
    public void editStation(Long id, String name){
        // 检查用户权限
        userService.checkAuthority();
        StationEntity entity = stationDao.findById(id).get();
        entity.setName(name);
        stationDao.save(entity);
    }

    @Override
    public void deleteStation(Long id) {
        // 检查用户权限
        userService.checkAuthority();
        List<RouteEntity> routes = routeDao.findAll();
        for(RouteEntity route : routes){
            if(route.getStationIds().contains(id)){
                throw new BizException(BizError.ROUTE_DATA_DEPENDENCY);
            }
        }
        stationDao.deleteById(id);
    }
}
