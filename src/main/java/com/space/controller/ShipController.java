package com.space.controller;

import com.space.config.exceptions.BadRequestException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.impl.ShipServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/rest/ships")
public class ShipController {
    private ShipServiceImpl shipServiceImpl;

    @Autowired
    public ShipController(ShipServiceImpl shipServiceImpl) {
        this.shipServiceImpl = shipServiceImpl;
    }


    @GetMapping
    public List<Ship> getAllShips(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) String planet,
                                  @RequestParam(required = false) ShipType shipType,
                                  @RequestParam(required = false) Long after,
                                  @RequestParam(required = false) Long before,
                                  @RequestParam(required = false) Boolean isUsed,
                                  @RequestParam(required = false) Double minSpeed,
                                  @RequestParam(required = false) Double maxSpeed,
                                  @RequestParam(required = false) Integer minCrewSize,
                                  @RequestParam(required = false) Integer maxCrewSize,
                                  @RequestParam(required = false) Double minRating,
                                  @RequestParam(required = false) Double maxRating,
                                  @RequestParam(required = false) ShipOrder order,
                                  @RequestParam(required = false) Integer pageNumber,
                                  @RequestParam(required = false) Integer pageSize){

        List<Ship> shipList = shipServiceImpl.getAllShips(name, planet, shipType, after, before,
                isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating,
                maxRating, order, pageNumber, pageSize);

        return shipServiceImpl.getShipListByPage(shipList, order, pageNumber, pageSize);
    }

    @GetMapping("/count")
    public int shipCount(@RequestParam(required = false) String name,
                         @RequestParam(required = false) String planet,
                         @RequestParam(required = false) ShipType shipType,
                         @RequestParam(required = false) Long after,
                         @RequestParam(required = false) Long before,
                         @RequestParam(required = false) Boolean isUsed,
                         @RequestParam(required = false) Double minSpeed,
                         @RequestParam(required = false) Double maxSpeed,
                         @RequestParam(required = false) Integer minCrewSize,
                         @RequestParam(required = false) Integer maxCrewSize,
                         @RequestParam(required = false) Double minRating,
                         @RequestParam(required = false) Double maxRating,
                         @RequestParam(required = false) ShipOrder order,
                         @RequestParam(required = false) Integer pageNumber,
                         @RequestParam(required = false) Integer pageSize){
        return shipServiceImpl.getAllShips(name, planet, shipType, after, before,
                isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating,
                maxRating, order, pageNumber, pageSize).size();

    }

    @GetMapping("/{id}")
    public Ship getShipById(@PathVariable String id) {
        return shipServiceImpl.getShipById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id){
        shipServiceImpl.deleteShipByOrder(id);
    }


    @PostMapping
    public Ship createNewShip(@RequestBody Ship newShip){
        return shipServiceImpl.create(newShip);
    }

    @PostMapping("/{id}")
    public Ship update(@RequestBody Ship editShip,
                       @PathVariable Long id){
            return shipServiceImpl.editShip(editShip, id);
    }
}
