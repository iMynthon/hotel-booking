package com.example.hotelbookingapplication.service.impl;

import com.example.hotelbookingapplication.exception.EntityNotFoundException;
import com.example.hotelbookingapplication.mapper.HotelMapper;
import com.example.hotelbookingapplication.model.Hotel;
import com.example.hotelbookingapplication.repository.HotelRepository;
import com.example.hotelbookingapplication.service.HotelBookingService;
import com.example.hotelbookingapplication.validation.PaginationFilter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService implements HotelBookingService<Hotel> {

    private final HotelRepository hotelRepository;

    @Setter(onMethod_ = @Autowired)
    private HotelMapper hotelMapper;

    public List<Hotel> findAll(PaginationFilter filter) {
        return hotelRepository.findAll(
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

    @Override
    public Hotel save(Hotel entity) {
        return hotelRepository.save(entity);
    }

    @Override
    public Hotel update(Hotel entity) {
        Hotel exists = findById(entity.getId());
        hotelMapper.update(exists,entity);
        return exists;
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
        return hotelRepository.findByName(name).orElseThrow(()->
                new EntityNotFoundException(String.format("Hotel с name:{%s} - не найден",name)));
    }
}
