package com.example.hotelbookingapplication.service.impl;

import com.example.hotelbookingapplication.exception.DuplicateDataException;
import com.example.hotelbookingapplication.exception.EntityNotFoundException;
import com.example.hotelbookingapplication.mapper.BookingMapper;
import com.example.hotelbookingapplication.model.jpa.Booking;
import com.example.hotelbookingapplication.model.jpa.Room;
import com.example.hotelbookingapplication.model.kafka.BookingEvent;
import com.example.hotelbookingapplication.model.mongodb.BookingRegistrationStats;
import com.example.hotelbookingapplication.repository.jpa.BookingRepository;
import com.example.hotelbookingapplication.service.HotelBookingService;
import com.example.hotelbookingapplication.service.mongodb.BookingRegistrationStatService;
import com.example.hotelbookingapplication.validation.filter.HotelValidatorFilter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService implements HotelBookingService<Booking> {

    private final BookingRegistrationStatService bookingRegistrationStatService;

    private final BookingRepository bookingRepository;

    private final RoomService roomService;

    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    @Setter(onMethod_ = @Autowired)
    private BookingMapper bookingMapper;

    public List<Booking> findAll(HotelValidatorFilter filter){
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

    @Transactional
    @Override
    public Booking save(Booking entity) {
        Booking saveEntity = roomLinkingBooking(entity);
        saveEntity = bookingRepository.save(saveEntity);
        createSendMessageBooking(saveEntity);
        return saveEntity;
    }

    @Transactional
    @Override
    public Booking update(Booking entity) {
        Booking exists = findById(entity.getId());
        Booking newBooking = changeDateBooking(exists,entity);
        return bookingRepository.save(newBooking);
    }

    @Override
    public void deleteById(Integer id) {
        if(!bookingRepository.existsById(id)){
            throw new EntityNotFoundException(String.format("Бронь по id:{%s} - не найдена",id));
        }
        Booking booking = findById(id);
        BookingRegistrationStats bookingRegistrationStats = bookingRegistrationStatService
                .findByBookingDate(booking.getArrivalDate(),booking.getDepartureDate());
        bookingRepository.deleteById(id);
        bookingRegistrationStatService.deleteById(bookingRegistrationStats.getId());
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

    private Booking roomLinkingBooking(Booking booking){
        Room room = roomService.findByNumber(booking.getRoom().getNumber());
        checkFreeDate(room.getUnavailableDate(),booking);
        room.getUnavailableDate().addAll(
                List.of(booking.getArrivalDate(),booking.getDepartureDate()));
        room.getBookings().add(booking);
        booking.setRoom(room);
        return booking;
    }

    private Booking changeDateBooking(Booking exists,Booking update){
        Room room = exists.getRoom();
        room.getUnavailableDate().removeIf(date -> date.isEqual(exists.getArrivalDate()) ||
                date.isEqual(exists.getDepartureDate()));
        if(update.getRoom().getNumber().equals(room.getNumber())){
            checkFreeDate(room.getUnavailableDate(),update);
            room.getUnavailableDate().addAll(
                    List.of(update.getArrivalDate(),update.getDepartureDate()));
            room.getBookings().add(exists);
            exists.setRoom(room);
        } else {
            Room updateRoom = roomService.findByNumber(update.getRoom().getNumber());
            checkFreeDate(updateRoom.getUnavailableDate(),update);
            updateRoom.getUnavailableDate().addAll(
                    List.of(update.getArrivalDate(),update.getDepartureDate()));
            updateRoom.getBookings().add(exists);
            exists.setRoom(updateRoom);
        }
        bookingMapper.update(exists,update);
        return exists;
    }

    private void createSendMessageBooking(Booking booking){
        String hotelBookingTopic = "hotel_booking";
        kafkaTemplate.send(hotelBookingTopic,"hotel_booking: " + System.currentTimeMillis(),createBookingEvent(booking));
    }

    private BookingEvent createBookingEvent(Booking booking){
        return BookingEvent.builder()
                .userId(booking.getUser().getId())
                .arrivalDate(booking.getArrivalDate())
                .departureDate(booking.getDepartureDate())
                .registrationBooking(LocalDateTime.now())
                .build();
    }
}
