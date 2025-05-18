package com.example.hotelbookingapplication.service.mongodb;

import com.example.hotelbookingapplication.exception.EntityNotFoundException;
import com.example.hotelbookingapplication.model.jpa.Booking;
import com.example.hotelbookingapplication.model.mongodb.BookingRegistrationStats;
import com.example.hotelbookingapplication.repository.jpa.BookingRepository;
import com.example.hotelbookingapplication.repository.mongodb.BookingRegistrationStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingRegistrationStatService {

    private final BookingRegistrationStatsRepository repository;

    private final BookingRepository bookingRepository;

    private final CSVFormat mongoDBFormat = CSVFormat.DEFAULT.builder()
            .setHeader("id", "user_id","arrival_date","departure_date","registration_date","room_name",
                    "room_price","room_number","hotel_name").build();

    private final Path filePath = Paths.get("data/statistics_bookings_registration.csv");

    public void dataRecordToFile() {
        try(BufferedWriter writer = Files.newBufferedWriter(filePath)){
            CSVPrinter csvPrinter = new CSVPrinter(writer, mongoDBFormat);
            for(BookingRegistrationStats stats : repository.findAll()){
                Booking booking = findByArrivalAndDeparture(stats.getArrivalDate(),stats.getDepartureDate());
                stats.setBooking(booking);
                csvPrinter.printRecord(stats.getId(),stats.getUserId(),stats.getArrivalDate(),
                        stats.getDepartureDate(),stats.getRegisteredTime(), booking.getRoom().getName(),
                        booking.getRoom().getPrice(),booking.getRoom().getNumber(),booking.getRoom().getHotel().getName());
            }
        } catch (IOException ie){
            log.info("Ошибка записи в файл: {} ",ie.getMessage());
        }
    }

    public BookingRegistrationStats findByBookingDate(LocalDate arrival, LocalDate departure){
        return repository.findByArrivalDateAndDepartureDate(arrival,departure).orElseThrow(()->
                new EntityNotFoundException("Запись с таким датами отсутствует в базе данных"));
    }

    public void save(BookingRegistrationStats data){
        repository.save(data);
    }

    public void deleteById(String id){
        if(!repository.existsById(id)){
            throw new EntityNotFoundException(String.format("Запись о бронировании под id:{%s} - в Mongo DB не найдена",id));
        }
        log.info("Удаление записи из Mongo DB");
        repository.deleteById(id);
    }

    private Booking findByArrivalAndDeparture(LocalDate arrivalDate,LocalDate departureDate){
        return bookingRepository.findByArrivalDateAndDepartureDate(arrivalDate,departureDate).orElseThrow(
                ()-> new EntityNotFoundException(String.format("По заданный датам бронь не найдена: {%s} - {%s}",arrivalDate,departureDate)));
    }
}
