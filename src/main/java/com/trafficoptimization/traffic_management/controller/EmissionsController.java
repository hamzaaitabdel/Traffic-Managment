package com.trafficoptimization.traffic_management.controller;

import com.trafficoptimization.traffic_management.model.EmissionsEstimate;
import com.trafficoptimization.traffic_management.service.EmissionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/traffic")
public class EmissionsController {

    private final EmissionsService emissionsService;

    @Autowired
    public EmissionsController(EmissionsService emissionsService) {
        this.emissionsService = emissionsService;
    }

    @GetMapping("/emissions/estimate/{roadSegmentId}")
    public ResponseEntity<EmissionsEstimate> getEmissionsEstimate(@PathVariable String roadSegmentId) {
        EmissionsEstimate emissions = emissionsService.estimateEmissionsForRoadSegment(roadSegmentId);
        return ResponseEntity.ok(emissions);
    }
}