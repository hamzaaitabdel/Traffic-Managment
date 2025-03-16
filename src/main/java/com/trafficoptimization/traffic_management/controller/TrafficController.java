package com.trafficoptimization.traffic_management.controller;

import com.trafficoptimization.traffic_management.model.TrafficData;
import com.trafficoptimization.traffic_management.service.TrafficService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/traffic")
public class TrafficController {
    
    private final TrafficService trafficService;
    
    @Autowired
    public TrafficController(TrafficService trafficService) {
        this.trafficService = trafficService;
    }
    
    @PostMapping
    public ResponseEntity<TrafficData> addTrafficData(@Valid @RequestBody TrafficData trafficData) {
        TrafficData savedData = trafficService.saveTrafficData(trafficData);
        return new ResponseEntity<>(savedData, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<TrafficData>> getAllTrafficData() {
        List<TrafficData> trafficDataList = trafficService.getAllTrafficData();
        return new ResponseEntity<>(trafficDataList, HttpStatus.OK);
    }
    
    @GetMapping("/road-segment/{id}")
    public ResponseEntity<List<TrafficData>> getTrafficDataByRoadSegment(@PathVariable("id") String roadSegmentId) {
        List<TrafficData> trafficDataList = trafficService.getTrafficDataByRoadSegment(roadSegmentId);
        return new ResponseEntity<>(trafficDataList, HttpStatus.OK);
    }
    
    @GetMapping("/time-range")
    public ResponseEntity<List<TrafficData>> getTrafficDataByTimeRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<TrafficData> trafficDataList = trafficService.getTrafficDataByTimeRange(start, end);
        return new ResponseEntity<>(trafficDataList, HttpStatus.OK);
    }
    
    @GetMapping("/congestion/{id}")
    public ResponseEntity<List<TrafficData>> getCongestionPoints(
            @PathVariable("id") String roadSegmentId,
            @RequestParam(value = "threshold", defaultValue = "0.7") Double congestionThreshold) {
        List<TrafficData> congestionPoints = trafficService.getCongestionPoints(roadSegmentId, congestionThreshold);
        return new ResponseEntity<>(congestionPoints, HttpStatus.OK);
    }
    
    @GetMapping("/average-congestion/{id}")
    public ResponseEntity<Double> getAverageCongestion(@PathVariable("id") String roadSegmentId) {
        Double averageCongestion = trafficService.calculateAverageCongestion(roadSegmentId);
        return new ResponseEntity<>(averageCongestion, HttpStatus.OK);
    }
}
