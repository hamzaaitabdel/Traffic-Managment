package com.trafficoptimization.traffic_management.service;

import com.trafficoptimization.traffic_management.model.TrafficData;
import java.time.LocalDateTime;
import java.util.List;

public interface TrafficService {
    
    TrafficData saveTrafficData(TrafficData trafficData);
    
    List<TrafficData> getAllTrafficData();
    
    List<TrafficData> getTrafficDataByRoadSegment(String roadSegmentId);
    
    List<TrafficData> getTrafficDataByTimeRange(LocalDateTime start, LocalDateTime end);
    
    List<TrafficData> getCongestionPoints(String roadSegmentId, Double congestionThreshold);
    
    Double calculateAverageCongestion(String roadSegmentId);
}
