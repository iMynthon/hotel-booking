package com.example.hotelbookingapplication;
import com.example.hotelbookingapplication.dto.request.UpsertBookingRequest;
import com.example.hotelbookingapplication.model.jpa.Booking;
import com.example.hotelbookingapplication.model.jpa.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class BookingControllerTest extends AbstractTest{

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    @Test
    @DisplayName("Тестовое получение всех броней админом")
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetFindAll() throws Exception {

        mockMvc.perform(get("/api/booking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingList.length()").value(3));
    }

    @Test
    @DisplayName("Тестовое получение всех броней другой ролью")
    @WithMockUser(username = "user", roles = "USER")
    public void testGetFindAllAnyRoles() throws Exception {

        mockMvc.perform(get("/api/booking"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Тестовое сохранение Booking и проверка на свободные даты")
    @WithMockUser(username = "Пользователь системы",roles = "USER")
    public void testSave_CheckFreeDate() throws Exception{
        UpsertBookingRequest request = UpsertBookingRequest.builder()
                .arrivalDate("15.06.2025")
                .departureDate("28.06.2025")
                .roomNumber(101)
                .build();

        mockMvc.perform(post("/api/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomNumber").value(101));

        User user = userRepository.findByUsernameIgnoreCase("Пользователь системы").orElseThrow();
        List<Booking> booking = bookingRepository.findByUserId(user.getId()).orElseThrow();

        assertTrue(booking.stream().anyMatch(b -> b.getUser().getUsername().equals(user.getUsername())));
        assertTrue(booking.stream().anyMatch(b -> b.getRoom().getNumber().equals(101)));

        UpsertBookingRequest newRequest = UpsertBookingRequest.builder()
                .arrivalDate("15.06.2025")
                .departureDate("28.06.2025")
                .roomNumber(101)
                .build();

        mockMvc.perform(post("/api/booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тестовое обновление брони, смена дат")
    @WithMockUser(username = "admin",roles = "ADMIN")
    public void testUpdateBooking() throws Exception{
        User user = userRepository.findByUsernameIgnoreCase("Пользователь системы").orElseThrow();
        List<Booking> bookingList = bookingRepository.findByUserId(user.getId()).orElseThrow();
        Booking booking = bookingList.getFirst();

        UpsertBookingRequest request = UpsertBookingRequest.builder()
                .arrivalDate("13.04.2025")
                .departureDate("20.04.2025")
                .roomNumber(101)
                .build();

        mockMvc.perform(put("/api/booking/{id}",booking.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Booking updateBooking = bookingRepository.findById(booking.getId()).orElseThrow();

        assertEquals(updateBooking.getArrivalDate(), LocalDate.parse(request.getArrivalDate(),DATE_FORMATTER));
        assertEquals(updateBooking.getDepartureDate(),LocalDate.parse(request.getDepartureDate(),DATE_FORMATTER));
        assertEquals(101, updateBooking.getRoom().getNumber());
    }

    @Test
    @DisplayName("Тестовое удаление брони")
    @WithMockUser(username = "admin",roles = "ADMIN")
    public void testDeleteById() throws Exception{
        User user = userRepository.findByUsernameIgnoreCase("Пользователь системы").orElseThrow();
        List<Booking> bookingList = bookingRepository.findByUserId(user.getId()).orElseThrow();

        Booking booking = bookingList.getFirst();

        assertEquals(3,bookingRepository.count());
        assertEquals(3,bookingRegistrationStatsRepository.count());

        mockMvc.perform(delete("/api/booking/{id}",booking.getId()))
                .andExpect(status().isNoContent());

        assertEquals(2,bookingRepository.count());
        assertEquals(2,bookingRegistrationStatsRepository.count());
    }

    @Test
    @DisplayName("Тестовое удаление брони другой ролью")
    @WithMockUser(username = "user",roles = "USER")
    public void testDeleteByIdAnyRole() throws Exception{
        User user = userRepository.findByUsernameIgnoreCase("Пользователь системы").orElseThrow();
        List<Booking> bookingList = bookingRepository.findByUserId(user.getId()).orElseThrow();

        Booking booking = bookingList.getFirst();

        assertEquals(3,bookingRepository.count());

        mockMvc.perform(delete("/api/booking/{id}",booking.getId()))
                .andExpect(status().isForbidden());

        assertEquals(3,bookingRepository.count());
    }
}
