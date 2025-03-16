package com.trafficoptimization.traffic_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trafficoptimization.traffic_management.model.TrafficData;
import com.trafficoptimization.traffic_management.service.TrafficService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrafficController.class)
class TrafficControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TrafficService trafficService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private TrafficData trafficData1;
    private TrafficData trafficData2;
    
    @BeforeEach
    void setUp() {
        trafficData1 = new TrafficData(1L, LocalDateTime.now(), "A8-KM125-126", 85, 70.5, 0.65);
        trafficData2 = new TrafficData(2L, LocalDateTime.now(), "A8-KM125-126", 120, 45.2, 0.85);
    }
    
    @Test
    void addTrafficData_ShouldReturnCreatedData() throws Exception {
        // Given
        when(trafficService.saveTrafficData(any(TrafficData.class))).thenReturn(trafficData1);
        
        // When & Then
        mockMvc.perform(post("/api/traffic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trafficData1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roadSegmentId").value("A8-KM125-126"))
                .andExpect(jsonPath("$.vehicleCount").value(85))
                .andExpect(jsonPath("$.congestionLevel").value(0.65));
    }
    
    @Test
    void getAllTrafficData_ShouldReturnAllData() throws Exception {
        // Given
        List<TrafficData> trafficDataList = Arrays.asList(trafficData1, trafficData2);
        when(trafficService.getAllTrafficData()).thenReturn(trafficDataList);
        
        // When & Then
        mockMvc.perform(get("/api/traffic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].roadSegmentId").value("A8-KM125-126"))
                .andExpect(jsonPath("$[1].roadSegmentId").value("A8-KM125-126"));
    }
    
    @Test
    void getTrafficDataByRoadSegment_ShouldReturnFilteredData() throws Exception {
        // Given
        String roadSegmentId = "A8-KM125-126";
        List<TrafficData> trafficDataList = Arrays.asList(trafficData1, trafficData2);
        when(trafficService.getTrafficDataByRoadSegment(roadSegmentId)).thenReturn(trafficDataList);
        
        // When & Then
        mockMvc.perform(get("/api/traffic/road-segment/{id}", roadSegmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].roadSegmentId").value(roadSegmentId))
                .andExpect(jsonPath("$[1].roadSegmentId").value(roadSegmentId));
    }
    
    @Test
    void getAverageCongestion_ShouldReturnCorrectValue() throws Exception {
        // Given
        String roadSegmentId = "A8-KM125-126";
        Double averageCongestion = 0.75;
        when(trafficService.calculateAverageCongestion(roadSegmentId)).thenReturn(averageCongestion);
        
        // When & Then
        mockMvc.perform(get("/api/traffic/average-congestion/{id}", roadSegmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0.75));
    }
}
