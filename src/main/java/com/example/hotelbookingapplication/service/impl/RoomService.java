package com.example.hotelbookingapplication.service.impl;

import com.example.hotelbookingapplication.exception.EntityNotFoundException;
import com.example.hotelbookingapplication.mapper.HotelMapper;
import com.example.hotelbookingapplication.mapper.RoomMapper;
import com.example.hotelbookingapplication.model.Room;
import com.example.hotelbookingapplication.repository.RoomRepository;
import com.example.hotelbookingapplication.service.HotelBookingService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService implements HotelBookingService<Room> {

    private final RoomRepository roomRepository;

    @Setter(onMethod_ = @Autowired)
    private RoomMapper roomMapper;

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room findById(Integer id) {
        return roomRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException(String.format("Room под id:{%s} не найдена",id)));
    }

    public Room findByNumber(Integer number){
        return roomRepository.findByNumber(number).orElseThrow(
                ()-> new EntityNotFoundException(String.format("Room под number:{%s} не найдена",number)));
    }

    @Override
    public Room save(Room entity) {
        return roomRepository.save(entity);
    }

    @Override
    public Room update(Room entity) {
        Room exists = findById(entity.getId());
        roomMapper.update(exists,entity);
        return roomRepository.save(exists);
    }

    @Override
    public void deleteById(Integer id) {
       if(!roomRepository.existsById(id)){
           throw new EntityNotFoundException(String.format("Room под id:{%s} не найдена",id));
       }
       roomRepository.deleteById(id);
    }
}
