package com.example.hotelbookingapplication;
import com.example.hotelbookingapplication.dto.request.UpsertBookingRequest;
import com.example.hotelbookingapplication.dto.request.UpsertUserRequest;
import com.example.hotelbookingapplication.model.Booking;
import com.example.hotelbookingapplication.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
public class BookingControllerTest extends AbstractTest{

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Тестовое получение всех броней")
    @WithMockUser(username = "user",roles = "USER")
    public void testGetByIdBooking() throws Exception{

        mockMvc.perform(get("/api/booking"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.bookingList.length()").value(3));
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
}
