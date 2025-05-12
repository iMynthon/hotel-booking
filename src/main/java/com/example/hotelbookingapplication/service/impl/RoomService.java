package com.example.hotelbookingapplication.service.impl;

import com.example.hotelbookingapplication.exception.EntityNotFoundException;
import com.example.hotelbookingapplication.mapper.RoomMapper;
import com.example.hotelbookingapplication.model.Room;
import com.example.hotelbookingapplication.repository.RoomRepository;
import com.example.hotelbookingapplication.service.HotelBookingService;
import com.example.hotelbookingapplication.validation.PaginationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService implements HotelBookingService<Room> {

    private final RoomRepository roomRepository;

    private final RoomMapper roomMapper;

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room findById(Integer id) {
        return roomRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException(String.format("Room под id:{%s} не найдена",id)));
    }

    @Override
    public Room save(Room entity) {
        return roomRepository.save(entity);
    }

    @Override
    public Room update(Room entity) {
        Room exists = findById(entity.getId());
        roomMapper.update(exists,entity);
        return roomRepository.save(entity);
    }

    @Override
    public void deleteById(Integer id) {
       if(!roomRepository.existsById(id)){
           throw new EntityNotFoundException(String.format("Room под id:{%s} не найдена",id));
       }
       roomRepository.deleteById(id);
    }
}
