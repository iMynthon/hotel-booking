package com.example.hotelbookingapplication;

import com.example.hotelbookingapplication.model.Hotel;
import com.example.hotelbookingapplication.model.Room;
import com.example.hotelbookingapplication.repository.HotelRepository;
import com.example.hotelbookingapplication.repository.RoomRepository;
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
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
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

        Room standardRoom = Room.builder()
                .name("Standard Room")
                .description("Уютный номер с видом во двор")
                .number(101)
                .numberOfPeople(2)
                .unavailableRoomInDate(List.of(
                        LocalDateTime.of(2025, 6, 15, 0, 0),
                        LocalDateTime.of(2025, 6, 20, 0, 0)
                )).build();

        Room luxuryRoom = Room.builder()
                .name("Luxury Room")
                .number(103)
                .description("Номер с видом на море и мини-баром")
                .unavailableRoomInDate(List.of(
                        LocalDateTime.of(2025, 6, 10, 0, 0),
                        LocalDateTime.of(2025, 6, 11, 0, 0),
                        LocalDateTime.of(2025, 6, 12, 0, 0)
                )).build();

        Room availableRoom = Room.builder()
                .name("Available Room")
                .number(104)
                .unavailableRoomInDate(List.of())  // Пустой список
                .build();

        Room defaultRoom = Room.builder()
                .name("Standard Room")
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
                .rooms(List.of(standardRoom,luxuryRoom))
                .build();

        Hotel testHotel2 = Hotel.builder()
                .name("Moscow City")
                .title("Отель в центре Москвы")
                .city("Москва")
                .address("ул. Ленина, 50")
                .distanceFromCenterCity(1.0)
                .rating((float) 3)
                .numberOfRating(250)
                .rooms(List.of(defaultRoom,availableRoom))
                .build();

        hotelRepository.saveAll(List.of(testHotel,testHotel2));
    }
}
