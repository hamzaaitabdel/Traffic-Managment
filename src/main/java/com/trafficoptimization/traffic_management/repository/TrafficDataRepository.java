package com.trafficoptimization.traffic_management.repository;

import com.trafficoptimization.traffic_management.model.TrafficData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrafficDataRepository extends JpaRepository<TrafficData, Long> {
    
    List<TrafficData> findByRoadSegmentId(String roadSegmentId);
    
    List<TrafficData> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM TrafficData t WHERE t.roadSegmentId = ?1 AND t.congestionLevel > ?2")
    List<TrafficData> findCongestionPoints(String roadSegmentId, Double congestionThreshold);
}
