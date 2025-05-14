package com.example.hotelbookingapplication;

import com.example.hotelbookingapplication.model.*;
import com.example.hotelbookingapplication.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class AbstractTest {

    @Autowired
    protected HotelRepository hotelRepository;

    @Autowired
    protected RoomRepository roomRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AuthorityRepository authorityRepository;

    @Autowired
    protected BookingRepository bookingRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp(){
        hotelRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
        authorityRepository.deleteAll();

        Room standardRoom = Room.builder()
                .name("Standard Room")
                .description("Уютный номер с видом во двор")
                .number(109)
                .numberOfPeople(2)
                .build();

        Room luxuryRoom = Room.builder()
                .name("Luxury Room")
                .number(103)
                .description("Номер с видом на море и мини-баром")
                .build();

        Room availableRoom = Room.builder()
                .name("Available Room")
                .number(104)
                .build();

        Room defaultRoom = Room.builder()
                .name("Default Room")
                .number(101)
                .build();

        Hotel testHotel = Hotel.builder()
                .name("Grand Plaza")
                .title("Роскошный отель в центре города")
                .city("Москва")
                .address("ул. Тверская, 10")
                .distanceFromCenterCity(0.5)
                .rating((float) 4)
                .numberOfRating(120)
                .rooms(new ArrayList<>(List.of(standardRoom,luxuryRoom)))
                .build();

        Hotel testHotel2 = Hotel.builder()
                .name("Moscow City")
                .title("Отель в центре Москвы")
                .city("Москва")
                .address("ул. Ленина, 50")
                .distanceFromCenterCity(1.0)
                .rating((float) 3)
                .numberOfRating(250)
                .rooms(new ArrayList<>(List.of(defaultRoom,availableRoom)))
                .build();
        standardRoom.setHotel(testHotel);
        luxuryRoom.setHotel(testHotel);
        defaultRoom.setHotel(testHotel2);
        availableRoom.setHotel(testHotel2);

        hotelRepository.saveAll(new ArrayList<>(List.of(testHotel,testHotel2)));

        User user = User.builder()
                .username("Пользователь системы")
                .email("user@mail.ru")
                .password("12345")
                .build();

        User admin = User.builder()
                .username("Администратор системы")
                .email("admin@mail.ru")
                .password("1290")
                .build();

        Authority userAuth = Authority.builder()
                .role(RoleType.ROLE_USER)
                .user(user)
                .build();

        Authority adminAuth = Authority.builder()
                .role(RoleType.ROLE_ADMIN)
                .user(admin)
                .build();

        user.getRoles().add(userAuth);
        admin.getRoles().add(adminAuth);


        Booking booking = Booking.builder()
                .arrivalDate(LocalDate.of(2025,1,10))
                .departureDate(LocalDate.of(2025,1,20))
                .room(standardRoom)
                .user(user)
                .build();

        Booking booking1 = Booking.builder()
                .arrivalDate(LocalDate.of(2025,3,2))
                .departureDate(LocalDate.of(2025,3,25))
                .room(luxuryRoom)
                .user(admin)
                .build();

        Booking booking2 = Booking.builder()
                .arrivalDate(LocalDate.of(2025,5,14))
                .departureDate(LocalDate.of(2025,5,29))
                .room(availableRoom)
                .user(user)
                .build();

        user.getBookings().add(booking);
        standardRoom.getBookings().add(booking);
        admin.getBookings().add(booking1);
        luxuryRoom.getBookings().add(booking1);
        user.getBookings().add(booking2);
        availableRoom.getBookings().add(booking2);
        userRepository.saveAll(new ArrayList<>(List.of(user,admin)));
    }

    protected List<LocalDate> createDate(LocalDate... dates) {
        List<LocalDate> result = new ArrayList<>();
        Collections.addAll(result, dates);
        return result;
    }
}
