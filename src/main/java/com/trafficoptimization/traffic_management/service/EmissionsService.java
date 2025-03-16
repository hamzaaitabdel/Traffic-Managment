package com.trafficoptimization.traffic_management.service;

import com.trafficoptimization.traffic_management.model.EmissionsEstimate;
import com.trafficoptimization.traffic_management.model.TrafficData;
import com.trafficoptimization.traffic_management.repository.TrafficDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalDouble;

@Service
public class EmissionsService {

    private final TrafficDataRepository trafficDataRepository;

    @Autowired
    public EmissionsService(TrafficDataRepository trafficDataRepository) {
        this.trafficDataRepository = trafficDataRepository;
    }

    public EmissionsEstimate estimateEmissionsForRoadSegment(String roadSegmentId) {
        // Get the most recent traffic data for this road segment
        List<TrafficData> recentTrafficData = trafficDataRepository.findByRoadSegmentId(roadSegmentId);
        
        if (recentTrafficData.isEmpty()) {
            return new EmissionsEstimate(roadSegmentId, LocalDateTime.now(), 0.0, 0.0, 0.0, 0, 0.0, 0.0);
        }

        // Calculate averages from the recent data
        int totalVehicles = 0;
        double speedSum = 0;
        double congestionSum = 0;
        int validSpeedRecords = 0;
        int validCongestionRecords = 0;
        LocalDateTime mostRecent = recentTrafficData.get(0).getTimestamp();

        for (TrafficData data : recentTrafficData) {
            if (data.getTimestamp().isAfter(mostRecent)) {
                mostRecent = data.getTimestamp();
            }
            
            totalVehicles += data.getVehicleCount();
            
            if (data.getAverageSpeed() != null) {
                speedSum += data.getAverageSpeed();
                validSpeedRecords++;
            }
            
            if (data.getCongestionLevel() != null) {
                congestionSum += data.getCongestionLevel();
                validCongestionRecords++;
            }
        }

        // Calculate average values
        double avgVehicles = (double) totalVehicles / recentTrafficData.size();
        double avgSpeed = validSpeedRecords > 0 ? speedSum / validSpeedRecords : 0;
        double avgCongestion = validCongestionRecords > 0 ? congestionSum / validCongestionRecords : 0;

        // Calculate emissions based on these metrics
        double co2Emissions = calculateCO2Emissions(avgVehicles, avgSpeed, avgCongestion);
        double noxEmissions = calculateNOxEmissions(avgVehicles, avgSpeed, avgCongestion);
        double pmEmissions = calculateParticulateMatterEmissions(avgVehicles, avgSpeed, avgCongestion);

        return new EmissionsEstimate(
                roadSegmentId,
                mostRecent,
                co2Emissions,
                noxEmissions,
                pmEmissions,
                (int) avgVehicles,
                avgSpeed,
                avgCongestion
        );
    }

    /**
     * Calculate CO2 emissions in kg per hour based on traffic parameters.
     * This uses a simplified model where:
     * - Emissions increase with vehicle count
     * - Emissions are higher at low speeds (stop-and-go traffic) and very high speeds
     * - Emissions increase with congestion (idling vehicles)
     */
    private double calculateCO2Emissions(double vehicleCount, double averageSpeed, double congestionLevel) {
        // Base emissions per vehicle per hour (kg of CO2)
        double baseEmissionsPerVehicle = 2.5;  // Average passenger car emits ~2.5 kg CO2 per hour at optimal speed
        
        // Speed efficiency factor (U-shaped curve: emissions higher at very low and very high speeds)
        double speedFactor;
        if (averageSpeed < 10) {
            // Slow stop-and-go traffic (high emissions)
            speedFactor = 2.0;
        } else if (averageSpeed >= 10 && averageSpeed < 60) {
            // Gradual reduction in emissions as speed increases to optimal range
            speedFactor = 1.5 - (averageSpeed - 10) * 0.01; // Decrease from 1.5 to 1.0
        } else if (averageSpeed >= 60 && averageSpeed < 90) {
            // Optimal speed range
            speedFactor = 1.0;
        } else {
            // Higher speeds increase emissions again
            speedFactor = 1.0 + (averageSpeed - 90) * 0.02; // Increase from 1.0 up
        }
        
        // Congestion factor (higher congestion = more emissions due to inefficient driving)
        double congestionFactor = 1.0 + congestionLevel; // Scale from 1.0 to 2.0
        
        // Total CO2 emissions (kg per hour for the road segment)
        return baseEmissionsPerVehicle * vehicleCount * speedFactor * congestionFactor;
    }

    /**
     * Calculate NOx emissions in grams per hour based on traffic parameters.
     * NOx emissions are especially sensitive to acceleration events common in congested traffic.
     */
    private double calculateNOxEmissions(double vehicleCount, double averageSpeed, double congestionLevel) {
        // Base NOx emissions per vehicle per hour (grams)
        double baseNOxPerVehicle = 1.5;
        
        // Diesel proportion estimate (NOx emissions are higher for diesel vehicles)
        double dieselProportion = 0.4; // Assuming 40% of vehicles are diesel
        
        // Congestion has a stronger effect on NOx than on CO2
        double congestionFactor = 1.0 + (congestionLevel * 1.5);
        
        // Speed factor
        double speedFactor;
        if (averageSpeed < 20) {
            // Low speed, high NOx due to frequent acceleration
            speedFactor = 2.0;
        } else if (averageSpeed < 60) {
            speedFactor = 1.5 - (averageSpeed - 20) * 0.016; // Decrease from 1.5 to 1.0
        } else {
            speedFactor = 1.0;
        }
        
        // Calculate NOx emissions
        double dieselEmissions = baseNOxPerVehicle * dieselProportion * vehicleCount * 3.0; // Diesel emits ~3x more NOx
        double petrolEmissions = baseNOxPerVehicle * (1 - dieselProportion) * vehicleCount;
        
        return (dieselEmissions + petrolEmissions) * speedFactor * congestionFactor;
    }

    /**
     * Calculate particulate matter (PM) emissions in grams per hour.
     * PM is especially relevant for diesel vehicles and in congested urban areas.
     */
    private double calculateParticulateMatterEmissions(double vehicleCount, double averageSpeed, double congestionLevel) {
        // Base PM emissions per vehicle per hour (grams)
        double basePMPerVehicle = 0.1;
        
        // Diesel proportion (PM emissions are much higher for diesel vehicles)
        double dieselProportion = 0.4;
        
        // Urban factor (assuming this is an urban road if congestion is high)
        double urbanFactor = congestionLevel > 0.5 ? 1.5 : 1.0;
        
        // Speed factor (PM emissions are higher at lower speeds)
        double speedFactor = averageSpeed < 30 ? 1.8 : 1.0;
        
        // Calculate PM emissions
        double dieselEmissions = basePMPerVehicle * dieselProportion * vehicleCount * 5.0; // Diesel emits ~5x more PM
        double petrolEmissions = basePMPerVehicle * (1 - dieselProportion) * vehicleCount;
        
        return (dieselEmissions + petrolEmissions) * speedFactor * urbanFactor;
    }
}