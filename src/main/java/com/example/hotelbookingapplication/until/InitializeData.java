package com.example.hotelbookingapplication.until;

import com.example.hotelbookingapplication.model.jpa.*;
import com.example.hotelbookingapplication.model.mongodb.BookingRegistrationStats;
import com.example.hotelbookingapplication.model.mongodb.RegistrationUserStats;
import com.example.hotelbookingapplication.repository.jpa.UserRepository;
import com.example.hotelbookingapplication.repository.mongodb.BookingRegistrationStatsRepository;
import com.example.hotelbookingapplication.repository.mongodb.RegistrationUserStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.init",name = "enabled",havingValue = "true")
public class InitializeData {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final BookingRegistrationStatsRepository bookingRegistrationStatsRepository;

    private final RegistrationUserStatsRepository registrationUserStatsRepository;

    @EventListener
    private void initDB(ApplicationReadyEvent applicationReadyEvent){

        log.info("Сохранение тестовых данных");
        User admin = User.builder()
                .username("admin")
                .email("admin@example.com")
                .password(encoder.encode("123456"))
                .build();

        Authority adminRole = Authority.builder()
                .role(RoleType.ROLE_ADMIN)
                .user(admin)
                .build();

        admin.getRoles().add(adminRole);

        User user1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .password(encoder.encode("454545454"))
                .build();

        Authority userRole1 = Authority.builder()
                .role(RoleType.ROLE_USER)
                .user(user1)
                .build();

        user1.getRoles().add(userRole1);

        User user2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .password(encoder.encode("4343434343"))
                .build();

        Authority userRole2 = Authority.builder()
                .role(RoleType.ROLE_USER)
                .user(user2)
                .build();

        user2.getRoles().add(userRole2);

        Hotel grandHotel = Hotel.builder()
                .name("Grand Hotel")
                .title("Роскошный отель в центре города")
                .city("Москва")
                .address("ул. Тверская, 1")
                .distanceFromCenterCity(0.5)
                .rating(new BigDecimal("4.7"))
                .numberOfRating(125)
                .build();

        Hotel budgetInn = Hotel.builder()
                .name("Budget Inn")
                .title("Дешевый отель на окраине")
                .city("Москва")
                .address("ул. Ленинская, 100")
                .distanceFromCenterCity(10.0)
                .rating(new BigDecimal("3.2"))
                .numberOfRating(87)
                .build();

        Room grandSuite = Room.builder()
                .name("Люкс")
                .description("Просторный номер с видом на город")
                .number(101)
                .price(800000)
                .unavailableDate(new ArrayList<>(List.of(LocalDate.of(2023, 12, 20),
                        LocalDate.of(2023, 12, 24))))
                .numberOfPeople(2)
                .hotel(grandHotel)
                .build();

        Room grandStandard = Room.builder()
                .name("Стандарт")
                .description("Комфортный номер с двуспальной кроватью")
                .number(102)
                .price(550)
                .numberOfPeople(2)
                .hotel(grandHotel)
                .build();

        Room budgetSingle = Room.builder()
                .name("Эконом")
                .description("Малогабаритный номер с одной кроватью")
                .number(201)
                .price(300)
                .unavailableDate(new ArrayList<>(List.of(LocalDate.of(2023, 12, 15),
                        LocalDate.of(2023, 12, 18))))
                .numberOfPeople(1)
                .hotel(budgetInn)
                .build();

        grandHotel.getRooms().addAll(List.of(grandSuite, grandStandard));
        budgetInn.getRooms().add(budgetSingle);

        Booking booking1 = Booking.builder()
                .arrivalDate(LocalDate.of(2023, 12, 20))
                .departureDate(LocalDate.of(2023, 12, 24))
                .room(grandSuite)
                .user(user1)
                .build();

        Booking booking2 = Booking.builder()
                .arrivalDate(LocalDate.of(2023, 12, 15))
                .departureDate(LocalDate.of(2023, 12, 18))
                .room(budgetSingle)
                .user(user2)
                .build();

        user1.getBookings().add(booking1);
        grandSuite.getBookings().add(booking1);

        user2.getBookings().add(booking2);
        budgetSingle.getBookings().add(booking2);

        userRepository.saveAll(new ArrayList<>(List.of(admin,user1,user2)));
        saveInMongoUserStats(admin,user1,user2);
        saveInMongoBookingStats(booking1,booking2);
        log.info("Сохранение завершено");
    }

    private void saveInMongoBookingStats(Booking... booking) {
        Arrays.stream(booking)
                .forEach(currentBooking ->
                        bookingRegistrationStatsRepository
                                .save(BookingRegistrationStats.builder()
                                        .userId(currentBooking.getUser().getId())
                                        .arrivalDate(currentBooking.getArrivalDate())
                                        .departureDate(currentBooking.getDepartureDate())
                                        .registeredTime(LocalDateTime.now())
                                        .booking(currentBooking)
                                        .build()));
    }

    private void saveInMongoUserStats(User... user){
        Arrays.stream(user)
                .forEach(currentUser -> registrationUserStatsRepository.save(RegistrationUserStats.builder()
                        .id(currentUser.getId())
                        .registeredTime(LocalDateTime.now())
                        .user(currentUser)
                        .build()));
    }
}
