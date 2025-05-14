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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public AllHotelResponse findAll(@Valid PaginationFilter filter){
       return hotelMapper.listHotelToListHotelResponse(hotelService.findAll(filter));
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public HotelResponse findById(@PathVariable Integer id){
        return hotelMapper.hotelToResponse(hotelService.findById(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public HotelResponse save(@RequestBody @Valid UpsertHotelRequest request){
        return hotelMapper.hotelToResponse(hotelService.save(hotelMapper.requestToHotel(request)));
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public HotelResponse update(@PathVariable Integer id,@RequestBody @Valid UpsertHotelRequest request){
        return hotelMapper.hotelToResponse(hotelService.update(hotelMapper.requestToHotel(id,request)));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id){
        hotelService.deleteById(id);
    }
}
