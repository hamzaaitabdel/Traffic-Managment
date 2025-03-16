package com.trafficoptimization.traffic_management.services;

import com.trafficoptimization.traffic_management.model.TrafficData;
import com.trafficoptimization.traffic_management.repository.TrafficDataRepository;
import com.trafficoptimization.traffic_management.service.TrafficServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrafficServiceImplTest {
    
    @Mock
    private TrafficDataRepository trafficDataRepository;
    
    @InjectMocks
    private TrafficServiceImpl trafficService;
    
    private TrafficData trafficData1;
    private TrafficData trafficData2;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        trafficData1 = new TrafficData(1L, LocalDateTime.now(), "A8-KM125-126", 85, 70.5, 0.65);
        trafficData2 = new TrafficData(2L, LocalDateTime.now(), "A8-KM125-126", 120, 45.2, 0.85);
    }
    
    @Test
    void saveTrafficData_WithoutCongestionLevel_ShouldCalculateAndSave() {
        // Given
        TrafficData inputData = new TrafficData(null, LocalDateTime.now(), "A8-KM125-126", 85, 70.5, null);
        TrafficData savedData = new TrafficData(1L, inputData.getTimestamp(), inputData.getRoadSegmentId(), 
                inputData.getVehicleCount(), inputData.getAverageSpeed(), 0.65);
        
        when(trafficDataRepository.save(any(TrafficData.class))).thenReturn(savedData);
        
        // When
        TrafficData result = trafficService.saveTrafficData(inputData);
        
        // Then
        assertNotNull(result.getCongestionLevel());
        verify(trafficDataRepository, times(1)).save(any(TrafficData.class));
    }
    
    @Test
    void getAllTrafficData_ShouldReturnAllData() {
        // Given
        List<TrafficData> expectedData = Arrays.asList(trafficData1, trafficData2);
        when(trafficDataRepository.findAll()).thenReturn(expectedData);
        
        // When
        List<TrafficData> result = trafficService.getAllTrafficData();
        
        // Then
        assertEquals(2, result.size());
        verify(trafficDataRepository, times(1)).findAll();
    }
    
    @Test
    void getTrafficDataByRoadSegment_ShouldReturnFilteredData() {
        // Given
        String roadSegmentId = "A8-KM125-126";
        List<TrafficData> expectedData = Arrays.asList(trafficData1, trafficData2);
        when(trafficDataRepository.findByRoadSegmentId(roadSegmentId)).thenReturn(expectedData);
        
        // When
        List<TrafficData> result = trafficService.getTrafficDataByRoadSegment(roadSegmentId);
        
        // Then
        assertEquals(2, result.size());
        verify(trafficDataRepository, times(1)).findByRoadSegmentId(roadSegmentId);
    }
    
    @Test
    void calculateAverageCongestion_ShouldReturnCorrectAverage() {
        // Given
        String roadSegmentId = "A8-KM125-126";
        List<TrafficData> dataList = Arrays.asList(trafficData1, trafficData2);
        when(trafficDataRepository.findByRoadSegmentId(roadSegmentId)).thenReturn(dataList);
        
        // Expected average: (0.65 + 0.85) / 2 = 0.75
        double expectedAverage = 0.75;
        
        // When
        Double result = trafficService.calculateAverageCongestion(roadSegmentId);
        
        // Then
        assertEquals(expectedAverage, result, 0.001);
        verify(trafficDataRepository, times(1)).findByRoadSegmentId(roadSegmentId);
    }
}
