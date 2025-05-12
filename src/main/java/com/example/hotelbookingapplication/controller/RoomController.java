package com.example.hotelbookingapplication.controller;

import com.example.hotelbookingapplication.dto.request.UpsertRoomRequest;
import com.example.hotelbookingapplication.dto.response.RoomResponse;
import com.example.hotelbookingapplication.mapper.RoomMapper;
import com.example.hotelbookingapplication.service.impl.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @GetMapping("/{id}")
    public RoomResponse findById(@PathVariable Integer id){
        return roomMapper.roomToResponse(roomService.findById(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RoomResponse save(@RequestBody @Valid UpsertRoomRequest request){
        return roomMapper.roomToResponse(roomService.save(roomMapper.requestToRoom(request)));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public RoomResponse update(@PathVariable Integer id,@RequestBody @Valid UpsertRoomRequest request){
        return roomMapper.roomToResponse(roomService.save(roomMapper.requestToRoom(id,request)));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id){
        roomService.deleteById(id);
    }

}
