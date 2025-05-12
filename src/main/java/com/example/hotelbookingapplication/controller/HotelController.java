package com.example.hotelbookingapplication.controller;

import com.example.hotelbookingapplication.dto.request.UpsertHotelRequest;
import com.example.hotelbookingapplication.dto.response.AllHotelResponse;
import com.example.hotelbookingapplication.dto.response.HotelResponse;
import com.example.hotelbookingapplication.mapper.HotelMapper;
import com.example.hotelbookingapplication.service.impl.HotelService;
import com.example.hotelbookingapplication.validation.PaginationFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotel")
@Validated
public class HotelController {

    private final HotelService hotelService;

    private final HotelMapper hotelMapper;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public AllHotelResponse findAll(@Valid PaginationFilter filter){
       return hotelMapper.listHotelToListHotelResponse(hotelService.findAll(filter));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public HotelResponse findById(@PathVariable Integer id){
        return hotelMapper.hotelToResponse(hotelService.findById(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public HotelResponse save(@RequestBody @Valid UpsertHotelRequest request){
        return hotelMapper.hotelToResponse(hotelService.save(hotelMapper.requestToHotel(request)));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public HotelResponse update(@PathVariable Integer id,@RequestBody @Valid UpsertHotelRequest request){
        return hotelMapper.hotelToResponse(hotelService.save(hotelMapper.requestToHotel(id,request)));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id){
        hotelService.deleteById(id);
    }
}
