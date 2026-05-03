package com.whistlestop_coffee.whistlestop_coffee.model;

public class Train {
    private String trainId;
    private String origin;
    private String destination;
    private String scheduledArrivalTime;
    private String estimatedArrivalTime;
    private String status; // "On time", "Delayed", "Cancelled"

    public Train() {}

    public Train(String trainId, String origin, String destination, String scheduledArrivalTime, String estimatedArrivalTime, String status) {
        this.trainId = trainId;
        this.origin = origin;
        this.destination = destination;
        this.scheduledArrivalTime = scheduledArrivalTime;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.status = status;
    }

    public String getTrainId() { return trainId; }
    public void setTrainId(String trainId) { this.trainId = trainId; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getScheduledArrivalTime() { return scheduledArrivalTime; }
    public void setScheduledArrivalTime(String scheduledArrivalTime) { this.scheduledArrivalTime = scheduledArrivalTime; }

    public String getEstimatedArrivalTime() { return estimatedArrivalTime; }
    public void setEstimatedArrivalTime(String estimatedArrivalTime) { this.estimatedArrivalTime = estimatedArrivalTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
