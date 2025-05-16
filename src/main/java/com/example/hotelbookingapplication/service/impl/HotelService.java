package com.example.hotelbookingapplication.service.impl;

import com.example.hotelbookingapplication.dto.request.HotelEstimateRequest;
import com.example.hotelbookingapplication.dto.response.HotelRatingResponse;
import com.example.hotelbookingapplication.exception.EntityNotFoundException;
import com.example.hotelbookingapplication.mapper.HotelMapper;
import com.example.hotelbookingapplication.model.Hotel;
import com.example.hotelbookingapplication.repository.HotelRepository;
import com.example.hotelbookingapplication.repository.specification.HotelSpecification;
import com.example.hotelbookingapplication.service.HotelBookingService;
import com.example.hotelbookingapplication.validation.filter.HotelValidatorFilter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService implements HotelBookingService<Hotel> {

    private final HotelRepository hotelRepository;

    @Setter(onMethod_ = @Autowired)
    private HotelMapper hotelMapper;

    public List<Hotel> findAll(HotelValidatorFilter filter) {
        return hotelRepository.findAll(
                        HotelSpecification.withFilter(filter),
                PageRequest.of(
                        filter.getPageNumber(),
                        filter.getPageSize(),
                        Sort.by("id")))
                .getContent();
    }

    @Override
    public Hotel findById(Integer id) {
        return hotelRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException(String.format("Hotel под id:{%s} - не найден",id)));
    }

    public HotelRatingResponse changesRatingToHotel(HotelEstimateRequest request) {
        Hotel changeHotels = findByName(request.name());
        return hotelMapper.hotelToRatingResponse(hotelRepository.save(ratingCalculator(changeHotels, request.newMark())));
    }

    @Override
    public Hotel save(Hotel entity) {
        return hotelRepository.save(entity);
    }

    @Override
    public Hotel update(Hotel entity) {
        Hotel exists = findById(entity.getId());
        hotelMapper.update(exists,entity);
        return hotelRepository.save(exists);
    }

    @Override
    public void deleteById(Integer id) {
        if(!hotelRepository.existsById(id)){
            throw new EntityNotFoundException(
                    String.format("Hotel под id:{%s} - не найден",id));
        }
         hotelRepository.deleteById(id);
    }

    @Named("findByName")
    public Hotel findByName(String name){
        return hotelRepository.findByNameIgnoreCase(name).orElseThrow(()->
                new EntityNotFoundException(String.format("Hotel с name:{%s} - не найден",name)));
    }

    private Hotel ratingCalculator(Hotel changeHotels,BigDecimal newMark){
        BigDecimal currentRating = changeHotels.getRating();
        int numberOfRating = changeHotels.getNumberOfRating();
        BigDecimal totalRating = currentRating.multiply(new BigDecimal(numberOfRating));
        totalRating = totalRating.add(newMark);
        numberOfRating++;
        BigDecimal newRating = totalRating.divide(new BigDecimal(numberOfRating),new MathContext(3, RoundingMode.HALF_UP));
        changeHotels.setRating(newRating);
        changeHotels.setNumberOfRating(numberOfRating);
        return changeHotels;
    }
}
