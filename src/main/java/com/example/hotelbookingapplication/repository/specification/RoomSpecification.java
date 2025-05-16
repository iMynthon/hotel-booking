package com.example.hotelbookingapplication.repository.specification;

import com.example.hotelbookingapplication.model.Room;
import com.example.hotelbookingapplication.validation.filter.RoomValidatorFilter;
import org.springframework.data.jpa.domain.Specification;

public interface RoomSpecification {

    static Specification<Room> withFilter(RoomValidatorFilter filter){
        return Specification.where(byId(filter.getId()))
                .and(byName(filter.getName()))
                .and(byMinPrice(filter.getMinPrice()))
                .and(byMaxPrice(filter.getMaxPrice()))
                .and(byArrivalAndDepartureDates(filter.getArrivalDate(), filter.getDepartureDate()))
                .and(byHotelId(filter.getHotel_id()));
    }

    static Specification<Room> byId(Integer id){
        return (root, query, cb) -> {
            if (id == null) {
                return null;
            }
            return cb.equal(root.get("id"), id);
        };
    }

    static Specification<Room> byName(String name){
        return (root, query, cb) -> {
            if (name == null) {
                return null;
            }
            return cb.equal(root.get("name"), name);
        };
    }

    static Specification<Room> byMinPrice(Integer price){
        return (root, query, cb) -> {
            if (price == null) {
                return null;
            }
            return cb.lessThan(root.get("id"), price);
        };
    }

    static Specification<Room> byMaxPrice(Integer price){
        return (root, query, cb) -> {
            if (price == null) {
                return null;
            }
            return cb.greaterThan(root.get("id"), price);
        };
    }

    static Specification<Room> byArrivalAndDepartureDates(String arrival,String departure){
        return (root, query, cb) -> {
            if (arrival == null || departure == null) {
                return null;
            }
            return cb.between(root.get("price"), arrival,departure).not();
        };
    }


    static Specification<Room> byHotelId(Integer hotelId){
        return (root, query, cb) -> {
            if (hotelId == null) {
                return null;
            }
            return cb.equal(root.get("hotelId"), hotelId);
        };
    }
}
