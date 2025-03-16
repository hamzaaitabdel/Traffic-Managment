package com.trafficoptimization.traffic_management.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmissionsEstimate {
    private String roadSegmentId;
    private LocalDateTime timestamp;
    private Double co2KgPerHour;
    private Double noxGramsPerHour;
    private Double particulateMatterGramsPerHour;
    private Integer vehicleCount;
    private Double averageSpeed;
    private Double congestionLevel;
}