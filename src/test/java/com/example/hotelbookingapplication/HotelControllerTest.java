package com.example.hotelbookingapplication;

import com.example.hotelbookingapplication.dto.request.UpsertHotelRequest;
import com.example.hotelbookingapplication.model.Hotel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
public class HotelControllerTest extends AbstractTest{

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Тестовый поиск всех отелей")
    public void testFindAll() throws Exception{

        mockMvc.perform(get("/api/hotel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotels.length()").value(2));
    }

    @Test
    @DisplayName("тестовый поиск отеля по id")
    public void testFindById() throws Exception{
        Hotel expectedHotel = hotelRepository.findByName("Grand Plaza").orElseThrow();
        assertNotNull(expectedHotel);
        mockMvc.perform(get("/api/hotel/{id}",expectedHotel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(expectedHotel.getName()))
                .andExpect(jsonPath("$.id").value(expectedHotel.getId()));
    }

    @Test
    @DisplayName("Тестовое сохранение сущности Hotel")
    public void testSaveHotel() throws Exception{
        UpsertHotelRequest request = UpsertHotelRequest.builder()
                .name("save hotel")
                .title("save title")
                .address("save address")
                .city("save city")
                .distanceFromCenterCity((float) 0.5)
                .build();

        assertEquals(2,hotelRepository.count());

        mockMvc.perform(post("/api/hotel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.id").isNotEmpty());

        assertEquals(3,hotelRepository.count());
    }

    @Test
    @DisplayName("Тестовое обновление сущности Hotel")
    public void testUpdateHotel() throws Exception{
        UpsertHotelRequest request = UpsertHotelRequest.builder()
                .name("update hotel")
                .title("update title")
                .address("update address")
                .city("update city")
                .distanceFromCenterCity((float) 0.8)
                .build();

        Hotel deprecatedHotel = hotelRepository.findByName("Grand Plaza").orElseThrow();

        assertEquals(2,hotelRepository.count());

        mockMvc.perform(put("/api/hotel/{id}",deprecatedHotel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.id").value(deprecatedHotel.getId()));

        assertEquals(2,hotelRepository.count());
    }

    @Test
    @DisplayName("Тестовое удаление сущности Hotel")
    public void testDeleteById() throws Exception{
        Hotel hotel = hotelRepository.findByName("Grand Plaza").orElseThrow();

        assertEquals(2,hotelRepository.count());

        mockMvc.perform(delete("/api/hotel/{id}",hotel.getId()))
                .andExpect(status().isNoContent());

        assertEquals(1,hotelRepository.count());
    }
}
