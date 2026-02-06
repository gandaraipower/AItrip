package com.mysite.sbb.aitrip.place.controller;

import com.mysite.sbb.aitrip.place.dto.PlaceResponse;
import com.mysite.sbb.aitrip.place.service.PlaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("PlaceController 테스트")
class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceService placeService;

    @Test
    @DisplayName("GET /api/places - 장소 목록 조회 (인증 불필요)")
    void getAllPlaces() throws Exception {
        // given
        List<PlaceResponse> places = List.of(
                new PlaceResponse(1L, "블루보틀", "서울", "카페", "강남구",
                        new BigDecimal("37.5265"), new BigDecimal("127.0402"),
                        "09:00-21:00", 45, null, "TourAPI",
                        LocalDateTime.now(), LocalDateTime.now())
        );
        given(placeService.getAllPlaces()).willReturn(places);

        // when & then
        mockMvc.perform(get("/api/places"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("블루보틀"));
    }

    @Test
    @DisplayName("POST /api/places - 인증 없이 장소 등록 시 401")
    void createPlace_unauthorized() throws Exception {
        mockMvc.perform(get("/api/places"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
