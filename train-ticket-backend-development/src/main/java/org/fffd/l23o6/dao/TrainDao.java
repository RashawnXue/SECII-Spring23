package org.fffd.l23o6.dao;

import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainDao extends JpaRepository<TrainEntity, Long>{
    List<TrainEntity> findByDate(String date);

    //List<TrainEntity> findByRouteId(Long RouteId);
}
