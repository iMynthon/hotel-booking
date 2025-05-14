package com.example.hotelbookingapplication.mapper;

import com.example.hotelbookingapplication.dto.request.UpsertBookingRequest;
import com.example.hotelbookingapplication.dto.response.AllBookingResponse;
import com.example.hotelbookingapplication.dto.response.BookingResponse;
import com.example.hotelbookingapplication.model.Booking;
import com.example.hotelbookingapplication.service.impl.UserService;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {RoomMapper.class, UserService.class})
public interface BookingMapper {

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Mapping(target = "room.number",source = "request.roomNumber")
    @Mapping(target = "user",qualifiedByName = "findByUsername",source = "username")
    @Mapping(target = "arrivalDate",qualifiedByName = "stringToLocalDate",source = "request.arrivalDate")
    @Mapping(target = "departureDate",qualifiedByName = "stringToLocalDate",source = "request.departureDate")
    Booking requestToBooking(String username,UpsertBookingRequest request);

    @Mapping(target = "room.number",source = "request.roomNumber")
    @Mapping(target = "arrivalDate",qualifiedByName = "stringToLocalDate",source = "request.arrivalDate")
    @Mapping(target = "departureDate",qualifiedByName = "stringToLocalDate",source = "request.departureDate")
    Booking requestToBooking(Integer id,UpsertBookingRequest request);

    @Mapping(target = "roomNumber",source = "room.number")
    @Mapping(target = "arrivalDate",qualifiedByName = "localDateToString",source = "booking.arrivalDate")
    @Mapping(target = "departureDate",qualifiedByName = "localDateToString",source = "booking.departureDate")
    BookingResponse bookingToResponse(Booking booking);

    default AllBookingResponse bookingListToResponseList(List<Booking> bookings){
        return new AllBookingResponse(bookings.stream().map(this::bookingToResponse).toList());
    }

    @Mapping(target = "user",ignore = true)
    void update(@MappingTarget Booking destination,Booking root);

    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String date){
        return LocalDate.parse(date,DATE_FORMATTER);
    }

    @Named("localDateToString")
    default String localDateToString(LocalDate date){
        return date.format(DATE_FORMATTER);
    }


}
