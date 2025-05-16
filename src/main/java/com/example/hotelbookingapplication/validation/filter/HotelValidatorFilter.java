package com.example.hotelbookingapplication.validation.filter;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class HotelValidatorFilter extends ValidatorFilter {

    @Override
    public void setPageNumber(int pageNumber) {
        super.setPageNumber(pageNumber);
    }

    @Override
    public void setPageSize(int pageSize) {
        super.setPageSize(pageSize);
    }

    private Integer id;

    private String name;

    private String title;

    private String city;

    private String address;

    private Double distanceLess;

    private Double distanceGreater;

    private BigDecimal ratingLees;

    private BigDecimal ratingGreater;

    private Integer numberOfRating;
}
