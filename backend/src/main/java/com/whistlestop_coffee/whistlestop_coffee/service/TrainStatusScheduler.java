package com.whistlestop_coffee.whistlestop_coffee.service;

import com.whistlestop_coffee.whistlestop_coffee.model.Order;
import com.whistlestop_coffee.whistlestop_coffee.model.Train;
import com.whistlestop_coffee.whistlestop_coffee.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainStatusScheduler {

    private final OrderRepository orderRepository;
    private final TrainService trainService;

    public TrainStatusScheduler(OrderRepository orderRepository, TrainService trainService) {
        this.orderRepository = orderRepository;
        this.trainService = trainService;
    }

    // Run every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void updatePickupTimesBasedOnTrainDelays() {
        // Find all active orders that are associated with a train
        List<Order> activeOrders = orderRepository.findAll().stream()
                .filter(o -> !o.isArchived() && !"CANCELLED".equals(o.getStatus()))
                .filter(o -> o.getTrainId() != null && !o.getTrainId().isEmpty())
                .toList();

        for (Order order : activeOrders) {
            Train train = trainService.getTrainStatus(order.getTrainId());
            if (train != null) {
                String estimatedArrivalTime = withExistingDate(order.getEstimatedArrivalTime(), train.getEstimatedArrivalTime());
                // If the train's estimated arrival time is different from the order's estimated arrival time, update the order
                if (!estimatedArrivalTime.equals(order.getEstimatedArrivalTime())) {
                    System.out.println("Train " + train.getTrainId() + " delayed! Updating order #" + order.getId() + " pickup time to " + estimatedArrivalTime);
                    order.setEstimatedArrivalTime(estimatedArrivalTime);
                    order.setPickupTime(estimatedArrivalTime);
                    orderRepository.save(order);
                }
            }
        }
    }

    private String withExistingDate(String existingDateTime, String newTime) {
        if (newTime == null || newTime.isBlank()) {
            return existingDateTime;
        }
        if (newTime.contains(" ")) {
            return newTime;
        }
        if (existingDateTime != null && existingDateTime.contains(" ")) {
            return existingDateTime.split(" ")[0] + " " + newTime;
        }
        return newTime;
    }
}
