package com.example.hotelbookingapplication.service.impl;

import com.example.hotelbookingapplication.exception.DuplicateDataException;
import com.example.hotelbookingapplication.exception.EntityNotFoundException;
import com.example.hotelbookingapplication.mapper.BookingMapper;
import com.example.hotelbookingapplication.model.Booking;
import com.example.hotelbookingapplication.model.Hotel;
import com.example.hotelbookingapplication.model.Room;
import com.example.hotelbookingapplication.repository.BookingRepository;
import com.example.hotelbookingapplication.service.HotelBookingService;
import com.example.hotelbookingapplication.validation.PaginationFilter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService implements HotelBookingService<Booking> {

    private final BookingRepository bookingRepository;

    private final RoomService roomService;

    @Setter(onMethod_ = @Autowired)
    private BookingMapper bookingMapper;

    public List<Booking> findAll(PaginationFilter filter){
        return bookingRepository.findAll(
                        PageRequest.of(
                                filter.getPageNumber(),
                                filter.getPageSize(),
                                Sort.by("id")))
                .getContent();
    }

    @Override
    public Booking findById(Integer id) {
        return bookingRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException(String.format("Бронь по id:{%s} - не найдена",id)));
    }

    @Override
    public Booking save(Booking entity) {
        Room room = roomService.findByNumber(entity.getRoom().getNumber());
        checkFreeDate(room.getUnavailableDate(),entity);
        room.getUnavailableDate().addAll(
                List.of(entity.getArrivalDate(),entity.getDepartureDate()));
        room.getBookings().add(entity);
        entity.setRoom(room);
        return bookingRepository.save(entity);
    }

    @Override
    public Booking update(Booking entity) {
        Room room = roomService.findByNumber(entity.getRoom().getNumber());
        checkFreeDate(room.getUnavailableDate(),entity);
        Booking exists = findById(entity.getId());
        room.getUnavailableDate().addAll(
                List.of(entity.getArrivalDate(),entity.getDepartureDate()));
        bookingMapper.update(exists,entity);
        exists.setRoom(room);
        return bookingRepository.save(exists);
    }

    @Override
    public void deleteById(Integer id) {
        if(bookingRepository.existsById(id)){
            throw new EntityNotFoundException(String.format("Бронь по id:{%s} - не найдена",id));
        }
        bookingRepository.deleteById(id);
    }

    private void checkFreeDate(List<LocalDate> unavailableDate,Booking entity){
        bookingRepository.findAll().stream()
                .filter(booking -> unavailableDate.contains(booking.getArrivalDate()))
                .forEach(booking -> getOccupiedDates(entity.getArrivalDate(),entity.getDepartureDate(),
                        booking.getArrivalDate(),booking.getDepartureDate()));
    }

    private void getOccupiedDates(LocalDate newArrival, LocalDate newDeparture,
                                  LocalDate existsArrival,LocalDate existsDeparture){
        if(!newArrival.isAfter(existsDeparture) && !newDeparture.isBefore(existsArrival)){
            throw new DuplicateDataException(
                    String.format("Забронировать не удалось, конфликт периодов: ваша бронь {%s} - {%s} " +
                            "с актуальная бронь: {%s} - {%s} , загрузите список забронированных дат и сверьтесь с ним"
                            ,newArrival,newDeparture,existsArrival,existsDeparture));
        }
    }
}
