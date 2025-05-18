package com.example.hotelbookingapplication.repository.jpa.specification;

import com.example.hotelbookingapplication.model.jpa.Booking;
import com.example.hotelbookingapplication.model.jpa.Room;
import com.example.hotelbookingapplication.validation.filter.RoomValidatorFilter;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public interface RoomSpecification {

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    static Specification<Room> withFilter(RoomValidatorFilter filter){
        return Specification.where(byId(filter.getId()))
                .and(byName(filter.getName()))
                .and(byMinPrice(filter.getMinPrice()))
                .and(byMaxPrice(filter.getMaxPrice()))
                .and(byArrivalAndDepartureDates(filter.getArrivalDate(), filter.getDepartureDate()))
                .and(byHotelId(filter.getHotelId()));
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
            return cb.greaterThanOrEqualTo(root.get("price"),price);
        };
    }

    static Specification<Room> byMaxPrice(Integer price){
        return (root, query, cb) -> {
            if (price == null) {
                return null;
            }
            return cb.lessThanOrEqualTo(root.get("price"), price);
        };
    }

    static Specification<Room> byArrivalAndDepartureDates(String arrival,String departure){
        return (root, query, cb) -> {
            if (arrival == null || departure == null) {
                return null;
            }
            Subquery<Integer> bookingSubquery = Objects.requireNonNull(query).subquery(Integer.class);
            Root<Booking> bookingRoot = bookingSubquery.from(Booking.class);
            Predicate dataOverlap = cb.and(
                    cb.lessThan(bookingRoot.get("arrivalDate"), LocalDate.parse(departure,DATE_FORMATTER)),
                    cb.greaterThan(bookingRoot.get("departureDate"),LocalDate.parse(arrival,DATE_FORMATTER)));
            bookingSubquery.select(bookingRoot.get("room").get("id"))
                    .where(cb.and(
                            dataOverlap,
                            cb.equal(bookingRoot.get("room"),root)
                    ));
            return cb.not(cb.exists(bookingSubquery));
        };
    }


    static Specification<Room> byHotelId(Integer hotelId){
        return (root, query, cb) -> {
            if (hotelId == null) {
                return null;
            }
            return cb.equal(root.get("hotel").get("id"), hotelId);
        };
    }
}
