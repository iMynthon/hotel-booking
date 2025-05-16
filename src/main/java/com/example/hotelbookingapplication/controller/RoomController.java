package com.example.hotelbookingapplication.controller;

import com.example.hotelbookingapplication.dto.request.UpsertRoomRequest;
import com.example.hotelbookingapplication.dto.response.AllRoomResponse;
import com.example.hotelbookingapplication.dto.response.RoomResponse;
import com.example.hotelbookingapplication.mapper.RoomMapper;
import com.example.hotelbookingapplication.service.impl.RoomService;
import com.example.hotelbookingapplication.validation.filter.RoomValidatorFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room")
@Validated
public class RoomController {

    private final RoomService roomService;

    private final RoomMapper roomMapper;

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public AllRoomResponse findAll(@Valid RoomValidatorFilter roomFilter){
        return roomMapper.roomListToResponseList(roomService.findAll(roomFilter));
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public RoomResponse findById(@PathVariable Integer id){
        return roomMapper.roomToResponse(roomService.findById(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public RoomResponse save(@RequestBody @Valid UpsertRoomRequest request){
        return roomMapper.roomToResponse(roomService.save(roomMapper.requestToRoom(request)));
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public RoomResponse update(@PathVariable Integer id,@RequestBody @Valid UpsertRoomRequest request){
        return roomMapper.roomToResponse(roomService.update(roomMapper.requestToRoom(id,request)));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id){
        roomService.deleteById(id);
    }

}
