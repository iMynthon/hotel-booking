package com.example.hotelbookingapplication.validation.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomValidatorFilter extends ValidatorFilter {

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

    private Integer minPrice;

    private Integer maxPrice;

    private Integer numberOfPeople;

    private String arrivalDate;

    private String departureDate;

    private Integer hotel_id;
}
