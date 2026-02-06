package com.mysite.sbb.aitrip.trip.controller;

import com.mysite.sbb.aitrip.trip.service.TripService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TripController 테스트")
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TripService tripService;

    @Test
    @DisplayName("GET /api/trips - 인증 없이 접근 시 401")
    void getMyTrips_unauthorized() throws Exception {
        mockMvc.perform(get("/api/trips"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
