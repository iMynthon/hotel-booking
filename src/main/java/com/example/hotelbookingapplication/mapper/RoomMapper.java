package com.example.hotelbookingapplication.mapper;

import com.example.hotelbookingapplication.dto.request.UpsertRoomRequest;
import com.example.hotelbookingapplication.dto.response.RoomResponse;
import com.example.hotelbookingapplication.model.Room;
import com.example.hotelbookingapplication.service.impl.HotelService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {HotelMapper.class,HotelService.class})
public interface RoomMapper {

    @Mapping(target = "hotel",qualifiedByName = "findByName")
    Room requestToRoom(UpsertRoomRequest request);

    @Mapping(target = "hotel",qualifiedByName = "findByName")
    Room requestToRoom(Integer id,UpsertRoomRequest request);

    @Mapping(target = "hotel",source = "hotel.name")
    RoomResponse roomToResponse(Room room);

    @Mapping(target = "hotel",ignore = true)
    void update(@MappingTarget Room destination,Room root);
}
