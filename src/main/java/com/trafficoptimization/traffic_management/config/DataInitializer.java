package com.trafficoptimization.traffic_management.config;

import com.trafficoptimization.traffic_management.model.TrafficData;
import com.trafficoptimization.traffic_management.repository.TrafficDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final TrafficDataRepository trafficDataRepository;
    
    @Autowired
    public DataInitializer(TrafficDataRepository trafficDataRepository) {
        this.trafficDataRepository = trafficDataRepository;
    }
    
    @Override
    public void run(String... args) {
        // Initialize with sample data
        TrafficData data1 = new TrafficData(null, LocalDateTime.now().minusHours(2), "A8-KM125-126", 85, 70.5, 0.65);
        TrafficData data2 = new TrafficData(null, LocalDateTime.now().minusHours(1), "A8-KM125-126", 120, 45.2, 0.85);
        TrafficData data3 = new TrafficData(null, LocalDateTime.now(), "A8-KM125-126", 65, 95.0, 0.3);
        
        TrafficData data4 = new TrafficData(null, LocalDateTime.now().minusHours(2), "B27-KM45-46", 40, 85.3, 0.25);
        TrafficData data5 = new TrafficData(null, LocalDateTime.now().minusHours(1), "B27-KM45-46", 75, 60.8, 0.55);
        TrafficData data6 = new TrafficData(null, LocalDateTime.now(), "B27-KM45-46", 110, 30.5, 0.9);
        
        trafficDataRepository.saveAll(Arrays.asList(data1, data2, data3, data4, data5, data6));
    }
}
