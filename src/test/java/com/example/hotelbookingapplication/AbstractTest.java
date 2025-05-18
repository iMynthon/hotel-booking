package com.example.hotelbookingapplication;

import com.example.hotelbookingapplication.model.jpa.*;
import com.example.hotelbookingapplication.model.mongodb.BookingRegistrationStats;
import com.example.hotelbookingapplication.model.mongodb.RegistrationUserStats;
import com.example.hotelbookingapplication.repository.jpa.*;
import com.example.hotelbookingapplication.repository.mongodb.BookingRegistrationStatsRepository;
import com.example.hotelbookingapplication.repository.mongodb.RegistrationUserStatsRepository;
import com.example.hotelbookingapplication.service.mongodb.RegistrationUserStatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(AbstractTest.class);
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
    protected RegistrationUserStatsRepository registrationUserStatsRepository;

    @Autowired
    protected BookingRegistrationStatsRepository bookingRegistrationStatsRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;



    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17")
            .withReuse(true);

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.8")
            .withReuse(true);

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withReuse(true);


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(postgreSQLContainer, mongoDBContainer).join();
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers",kafkaContainer::getBootstrapServers);
    }

    @BeforeEach
    @Transactional
    void setUp(){
        hotelRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        bookingRepository.deleteAllInBatch();
        authorityRepository.deleteAllInBatch();

        Room standardRoom = Room.builder()
                .name("Standard Room")
                .description("Уютный номер с видом во двор")
                .number(109)
                .price(500)
                .numberOfPeople(2)
                .build();

        Room luxuryRoom = Room.builder()
                .name("Luxury Room")
                .number(103)
                .description("Номер с видом на море и мини-баром")
                .price(400)
                .numberOfPeople(5)
                .build();

        Room availableRoom = Room.builder()
                .name("Available Room")
                .number(104)
                .description("Номер с видом на пляж")
                .price(300)
                .numberOfPeople(3)
                .build();

        Room defaultRoom = Room.builder()
                .name("Default Room")
                .number(101)
                .description("Оьбычная комната")
                .price(200)
                .numberOfPeople(2)
                .build();

        Hotel testHotel = Hotel.builder()
                .name("Grand Plaza")
                .title("Роскошный отель в центре города")
                .city("Москва")
                .address("ул. Тверская, 10")
                .distanceFromCenterCity(0.5)
                .rating(new BigDecimal(4))
                .numberOfRating(120)
                .rooms(new ArrayList<>(List.of(standardRoom,luxuryRoom)))
                .build();

        Hotel testHotel2 = Hotel.builder()
                .name("Moscow City")
                .title("Отель в центре Москвы")
                .city("Москва")
                .address("ул. Ленина, 50")
                .distanceFromCenterCity(1.0)
                .rating(new BigDecimal(3))
                .numberOfRating(250)
                .rooms(new ArrayList<>(List.of(defaultRoom,availableRoom)))
                .build();

        standardRoom.setHotel(testHotel);
        luxuryRoom.setHotel(testHotel);
        defaultRoom.setHotel(testHotel2);
        availableRoom.setHotel(testHotel2);

        User user = User.builder()
                .username("Пользователь системы")
                .email("user@mail.ru")
                .password("12345")
                .build();

        User user2 = User.builder()
                .username("Пользователь системы 2")
                .email("user2@mail.ru")
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

        Authority userAuth2 = Authority.builder()
                .role(RoleType.ROLE_USER)
                .user(user2)
                .build();

        Authority adminAuth = Authority.builder()
                .role(RoleType.ROLE_ADMIN)
                .user(admin)
                .build();

        user.getRoles().add(userAuth);
        user2.getRoles().add(userAuth2);
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
        standardRoom.getUnavailableDate().addAll(new ArrayList<>(
                List.of(booking.getArrivalDate(),booking.getDepartureDate())));

        admin.getBookings().add(booking1);
        luxuryRoom.getBookings().add(booking1);
        luxuryRoom.getUnavailableDate().addAll(new ArrayList<>(
                List.of(booking1.getArrivalDate(),booking1.getDepartureDate())));

        user.getBookings().add(booking2);
        availableRoom.getBookings().add(booking2);
        availableRoom.getUnavailableDate().addAll(new ArrayList<>(
                List.of(booking2.getArrivalDate(),booking2.getDepartureDate())));

        userRepository.saveAll(new ArrayList<>(List.of(user,user2,admin)));
        saveInMongoUserStats(user,user2,admin);
        saveInMongoBookingStats(booking,booking1,booking2);


    }

    private void saveInMongoBookingStats(Booking... booking) {
        bookingRegistrationStatsRepository.deleteAll();
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
        registrationUserStatsRepository.deleteAll();
        Arrays.stream(user)
                .forEach(currentUser -> registrationUserStatsRepository.save(RegistrationUserStats.builder()
                                .id(currentUser.getId())
                                .registeredTime(LocalDateTime.now())
                                .user(currentUser)
                        .build()));
    }
}
