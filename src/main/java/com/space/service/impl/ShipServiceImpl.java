package com.space.service.impl;

import com.space.config.exceptions.BadRequestException;
import com.space.config.exceptions.NotFoundException;
import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipRepository shipRepository;

    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }


    @Override
    public Ship getShipById(String id) {
        if (!isValidId(id)){
            throw new BadRequestException();
        }
        Long idLong = Long.parseLong(id);
        if (!shipRepository.existsById(idLong)){
           throw new com.space.config.exceptions.NotFoundException();
        }

        return shipRepository.findById(idLong).orElse(null);
    }

    @Override
    public void deleteShipByOrder(String id) {
        if (!isValidId(id)){
            throw new BadRequestException();
        }
        Long idLong = Long.parseLong(id);
        if (!shipRepository.existsById(idLong)){
            throw new com.space.config.exceptions.NotFoundException();
        }

        shipRepository.deleteById(idLong);
    }

    @Override
    public List<Ship> getAllShips(String name, String planet, ShipType shipType, Long after,
                                  Long before, Boolean isUsed, Double minSpeed, Double maxSpeed,
                                  Integer minCrewSize, Integer maxCrewSize, Double minRating,
                                  Double maxRating, ShipOrder order, Integer pageNumber, Integer pageSize) {

        List<Ship> shipList = shipRepository.findAll();

        if (name != null){
            shipList = shipList.stream()
                    .filter(ship -> ship.getName().contains(name))
                    .collect(Collectors.toList());
        }

        if (planet != null){
            shipList = shipList.stream()
                    .filter(ship -> ship.getPlanet().contains(planet))
                    .collect(Collectors.toList());
        }

        if (minSpeed != null){
            shipList = shipList.stream()
                    .filter(ship -> ship.getSpeed() >= minSpeed)
                    .collect(Collectors.toList());
        }

        if (maxSpeed !=null){
            shipList = shipList.stream()
                    .filter(ship -> ship.getSpeed() <= maxSpeed)
                    .collect(Collectors.toList());
        }

        if (minRating != null){
            shipList = shipList.stream()
                    .filter(ship -> ship.getRating() >= minRating)
                    .collect(Collectors.toList());
        }

        if (maxRating != null){
            shipList = shipList.stream()
                    .filter(ship -> ship.getRating() <= maxRating)
                    .collect(Collectors.toList());
        }

        if (shipType != null){
            shipList = shipList.stream()
                    .filter(ship -> ship.getShipType().equals(shipType))
                    .collect(Collectors.toList());
        }

        if (after != null){
            shipList = shipList.stream()
                    .filter(ship -> ship.getProdDate().getTime() >= after)
                    .collect(Collectors.toList());
        }

        if (before != null){
            int year = new Date(before).getYear() + 1900;
            shipList = shipList.stream()
                    .filter(ship -> ship.getProdDate().getYear() + 1900 <= year)
                    .collect(Collectors.toList());
        }

        if (minCrewSize != null){
            shipList = shipList.stream()
                    .filter(ship -> ship.getCrewSize() >= minCrewSize)
                    .collect(Collectors.toList());
        }

        if (maxCrewSize != null){
            shipList = shipList.stream()
                    .filter(ship -> ship.getCrewSize() <= maxCrewSize)
                    .collect(Collectors.toList());
        }

        if (isUsed != null){
            shipList = shipList.stream()
                    .filter(ship -> ship.isUsed().equals(isUsed))
                    .collect(Collectors.toList());
        }

        return shipList;
    }


    @Override
    public List<Ship> getShipListByPage(List<Ship> ships, ShipOrder order, Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;

        return ships.stream()
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .sorted(getComparator(order))
                .collect(Collectors.toList());
    }

    @Override
        public Ship editShip(Ship editShip, Long id) {

            if (!isValidId(id.toString())) throw new BadRequestException();

            if (!shipRepository.existsById(id)) throw new NotFoundException();

            Ship oldShip = getShipById(id.toString());

            String name = editShip.getName();

            if (name != null) {
                if (name.length() > 50 || name.isEmpty()) throw new BadRequestException();
                oldShip.setName(name);
            }

            String planet = editShip.getPlanet();

            if (planet != null) {
                if (planet.length() > 50 || planet.isEmpty()) throw new BadRequestException();
                oldShip.setPlanet(planet);
            }

            if (editShip.getShipType() != null)
                oldShip.setShipType(editShip.getShipType());

            if (editShip.isUsed() != null) {
                oldShip.setUsed(editShip.isUsed());
            }

            if (editShip.getProdDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(editShip.getProdDate());
                int prodDate = cal.get(Calendar.YEAR);
                if (prodDate < 2800 || prodDate > 3019) throw new BadRequestException();
                oldShip.setProdDate(editShip.getProdDate());
            }


            Double speed = editShip.getSpeed();

            if (speed != null) {
                if (speed < 0.01d || speed > 0.99d) throw new BadRequestException();
                oldShip.setSpeed(speed);
            }

            Integer crewSize = editShip.getCrewSize();

            if (crewSize != null) {
                if (crewSize < 1 || crewSize > 9999) throw new BadRequestException();

                oldShip.setCrewSize(crewSize);
            }

            oldShip.setRating(getRaitng(oldShip.getSpeed(), oldShip.isUsed(),oldShip.getProdDate()));

            return shipRepository.save(oldShip);
        }


    @Override
    public Ship create(Ship ship) {

        if (!checkValidParams(ship)) {
            throw new BadRequestException();
        }

        if (ship.isUsed() == null){
            ship.setUsed(false);
        }

        Double speed =  Math.round(ship.getSpeed() * 100) * 1.0 /100;

        ship.setRating(getRaitng(speed, ship.isUsed(), ship.getProdDate()));

        return shipRepository.save(ship);
    }

    @Override
    public double getRaitng(Double speed, Boolean isUsed, Date proDate) {

        double k;
        if (isUsed){
            k = 0.5;
        }
        else {
            k = 1;
        }
        double raiting = (80 * speed * k) * 1.0 / (3019 - (proDate.getYear() + 1900) + 1);
        raiting = Math.round(raiting * 100) * 1.0 / 100;
        return raiting;
    }

    private Comparator<Ship> getComparator(ShipOrder order) {
        if (order == null){
            return Comparator.comparing(Ship::getId);
        }

        switch (order.getFieldName()){
            case "id":
                return Comparator.comparing(Ship::getId);
            case "speed":
                return Comparator.comparing(Ship::getSpeed);
            case "prodDate":
                return Comparator.comparing(Ship::getProdDate);
            case "rating":
                return Comparator.comparing(Ship::getRating);
        }
        return null;
    }

    public boolean isValidId(String id){
        Long idLong;
        try {
          idLong = Long.parseLong(id);
          if (idLong <= 0) return false;
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean checkValidParams(Ship ship){
        Calendar calendar1 = new GregorianCalendar();
        Calendar calendar2 = new GregorianCalendar();
        calendar1.set(2800, 0, 1);
        calendar2.set(3019, 11, 31);

        if ( ship == null ||
                ship.getName() == null          ||      ship.getPlanet() == null        ||
                ship.getShipType() == null      ||      ship.getProdDate() == null      ||
                ship.getSpeed() == null         ||      ship.getCrewSize() == null      ||
                ship.getName().length() > 50    ||      ship.getPlanet().length() > 50  ||
                ship.getName().equals("")       ||      ship.getPlanet().equals("")     ||
                ship.getProdDate().before(calendar1.getTime()) ||
                ship.getProdDate().after(calendar2.getTime()) ||
                ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99    ||
                ship.getProdDate().getTime() < 0 ||
                ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)
        {
            return false;
        }
        else return true;

    }


}
