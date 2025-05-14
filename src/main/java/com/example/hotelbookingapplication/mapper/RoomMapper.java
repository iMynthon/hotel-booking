package com.example.hotelbookingapplication.mapper;

import com.example.hotelbookingapplication.dto.request.UpsertRoomRequest;
import com.example.hotelbookingapplication.dto.response.RoomResponse;
import com.example.hotelbookingapplication.model.Room;
import com.example.hotelbookingapplication.service.impl.HotelService;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {HotelMapper.class,HotelService.class})
public interface RoomMapper {

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Mapping(target = "hotel",source = "request.hotel", qualifiedByName = "findByName")
    Room requestToRoom(UpsertRoomRequest request);

    @Mapping(target = "hotel",source = "request.hotel",qualifiedByName = "findByName")
    Room requestToRoom(Integer id,UpsertRoomRequest request);

    @Mapping(target = "hotel",source = "hotel.name")
    @Mapping(target = "unavailableDate",qualifiedByName = "parseLocalDateToString")
    RoomResponse roomToResponse(Room room);

    @Mapping(target = "hotel",ignore = true)
    void update(@MappingTarget Room destination,Room root);

    default List<LocalDate> parseStringToLocalDate(List<String> unavailableDate){
        return unavailableDate.stream().map(date -> LocalDate.parse(date,DATE_FORMATTER)).collect(Collectors.toList());
    }
    @Named("parseLocalDateToString")
    default List<String> parseLocalDateToString(List<LocalDate> unavailableDate){
        return unavailableDate.stream().map(date -> date.format(DATE_FORMATTER)).toList();
    }
}
