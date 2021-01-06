package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface ShipService {
    Ship getShipById(String id);
    void deleteShipByOrder(String id);
    List<Ship> getAllShips(String name, String planet, ShipType shipType,
                           Long after, Long before, Boolean isUsed, Double minSpeed,
                           Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                           Double minRating, Double maxRating, ShipOrder order,
                           Integer pageNumber, Integer pageSize);
    List<Ship> getShipListByPage(List<Ship> ships, ShipOrder order, Integer pageNumber, Integer pageSize);
    Ship editShip(Ship ship, Long id);
    Ship create (Ship ship);
    double getRaitng(Double speed, Boolean isUsed, Date proDate);
}
