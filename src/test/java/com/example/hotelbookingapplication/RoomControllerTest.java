package com.example.hotelbookingapplication;

import com.example.hotelbookingapplication.model.Hotel;
import com.example.hotelbookingapplication.model.Room;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
public class RoomControllerTest extends AbstractTest{

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Тестовый поиск Room по id")
    public void testFindById() throws Exception{
        Room luxuryRoom = roomRepository.findByName("Luxury Room").orElseThrow();
        Hotel grandPlaza = hotelRepository.findByName("Grand Plaza").orElseThrow();
        mockMvc.perform(get("/api/room/{id}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(luxuryRoom.getName()))
                .andExpect(jsonPath("$.unavailableRoomInDate.length()").value(3))
                .andExpect(jsonPath("$.hotel").value(grandPlaza.getName()));

    }
}
