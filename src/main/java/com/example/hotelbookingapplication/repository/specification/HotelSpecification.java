package com.example.hotelbookingapplication.repository.specification;

import com.example.hotelbookingapplication.model.Hotel;
import com.example.hotelbookingapplication.validation.filter.HotelValidatorFilter;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public interface HotelSpecification {

   static Specification<Hotel> withFilter(HotelValidatorFilter filter){
       return Specification.where(byId(filter.getId()))
               .and(byName(filter.getName()))
               .and(byTitle(filter.getTitle()))
               .and(byCity(filter.getCity()))
               .and(byAddress(filter.getAddress()))
               .and(byDistanceCenterGreaterAndEqual(filter.getDistanceGreater()))
               .and(byDistanceCenterLessAndEqual(filter.getDistanceLess()))
               .and(byRatingGreaterAndEqual(filter.getRatingGreater()))
               .and(byRatingLessAndEqual(filter.getRatingLees()))
               .and(byNumberOfRating(filter.getNumberOfRating()));
   }

    static Specification<Hotel> byId(Integer id) {
        return (root, query, cb) -> {
            if (id == null) {
                return null;
            }
            return cb.equal(root.get("id"), id);
        };
   }

    static Specification<Hotel> byName(String name) {
        return (root, query, cb) -> {
            if (name == null) {
                return null;
            }
            return cb.equal(root.get("name"), name);
        };
    }

    static Specification<Hotel> byTitle(String title) {
        return (root, query, cb) -> {
            if (title == null) {
                return null;
            }
            return cb.equal(root.get("title"), title);
        };
    }

    static Specification<Hotel> byCity(String city) {
        return (root, query, cb) -> {
            if (city == null) {
                return null;
            }
            return cb.equal(root.get("city"), city);
        };
    }

    static Specification<Hotel> byAddress(String address) {
        return (root, query, cb) -> {
            if (address == null) {
                return null;
            }
            return cb.equal(root.get("address"), address);
        };
    }

    static Specification<Hotel> byDistanceCenterGreaterAndEqual(Double distanceFromCenterCity) {
        return (root, query, cb) -> {
            if (distanceFromCenterCity == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get("distanceFromCenterCity"), distanceFromCenterCity);
        };
    }

    static Specification<Hotel> byDistanceCenterLessAndEqual(Double distanceFromCenterCity) {
        return (root, query, cb) -> {
            if (distanceFromCenterCity == null) {
                return null;
            }
            return cb.lessThanOrEqualTo(root.get("distanceFromCenterCity"), distanceFromCenterCity);
        };
    }

    static Specification<Hotel> byRatingGreaterAndEqual(BigDecimal rating) {
        return (root, query, cb) -> {
            if (rating == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get("rating"), rating);
        };
    }

    static Specification<Hotel> byRatingLessAndEqual(BigDecimal rating) {
        return (root, query, cb) -> {
            if (rating == null) {
                return null;
            }
            return cb.lessThanOrEqualTo(root.get("rating"), rating);
        };
    }

    static Specification<Hotel> byNumberOfRating(Integer numberOfRating) {
        return (root, query, cb) -> {
            if (numberOfRating == null) {
                return null;
            }
            return cb.equal(root.get("numberOfRating"), numberOfRating);
        };
    }

}
