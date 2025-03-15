package com.trafficoptimization.traffic_management.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.trafficoptimization.traffic_management.model.TrafficData;
import com.trafficoptimization.traffic_management.repository.TrafficDataRepository;

@Service
public class TrafficServiceImpl implements TrafficService{


    private final TrafficDataRepository trafficDataRepository;

    @Autowired
    public TrafficServiceImpl(TrafficDataRepository trafficDataRepository){
        this.trafficDataRepository = trafficDataRepository;
    }

    @Override
    public TrafficData saveTrafficData(TrafficData trafficData) {
        if (trafficData.getCongestionLevel() == null && trafficData.getVehicleCount() != null 
                && trafficData.getAverageSpeed() != null) {
            // Simple algorithm: higher vehicle count and lower speed = higher congestion
            double normalizedCount = Math.min(1.0, trafficData.getVehicleCount() / 100.0);
            double normalizedSpeed = Math.max(0.0, 1.0 - (trafficData.getAverageSpeed() / 130.0));
            trafficData.setCongestionLevel((normalizedCount + normalizedSpeed) / 2.0);
        }
        
        return trafficDataRepository.save(trafficData);
    }

    @Override
    public List<TrafficData> getAllTrafficData() {
        return trafficDataRepository.findAll();
    }

    @Override
    public List<TrafficData> getTrafficDataByRoadSegment(String roadSegmentId) {
        return trafficDataRepository.findByRoadSegmentId(roadSegmentId);
    }

    @Override
    public List<TrafficData> getTrafficDataByTimeRange(LocalDateTime start, LocalDateTime end) {
        return trafficDataRepository.findByTimestampBetween(start, end);
    }

    @Override
    public List<TrafficData> getCongestionPoints(String roadSegmentId, Double congestionThreshold) {
        return trafficDataRepository.findCongestionPoints(roadSegmentId, congestionThreshold);
    }

    @Override
    public Double calculateAverageCongestion(String roadSegmentId) {
        List<TrafficData> trafficDataList = trafficDataRepository.findByRoadSegmentId(roadSegmentId);
        
        if (trafficDataList.isEmpty()) {
            return 0.0;
        }
        
        return trafficDataList.stream()
                .filter(data -> data.getCongestionLevel() != null)
                .mapToDouble(TrafficData::getCongestionLevel)
                .average()
                .orElse(0.0);
    }
    
}
