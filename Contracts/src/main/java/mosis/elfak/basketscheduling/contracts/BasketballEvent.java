package mosis.elfak.basketscheduling.contracts;

import java.time.LocalDateTime;

public class BasketballEvent {
    private String eventId;
    private LocalDateTime createdAt;
    private LocalDateTime beginsAt;
    private LocalDateTime endsOn;
    private String createdBy;
    private int maxNumOfPlayers;
    private int currentNumOfPlayers;
    private String locationDescription;
    private String latitude;
    private String longitude;

    public BasketballEvent() {
    }

    public BasketballEvent(LocalDateTime beginsAt, LocalDateTime endsOn, String createdBy, int maxNumOfPlayers, int currentNumOfPlayers, String locationDescription, String latitude, String longitude) {
        this.beginsAt = beginsAt;
        this.endsOn = endsOn;
        this.createdBy = createdBy;
        this.maxNumOfPlayers = maxNumOfPlayers;
        this.currentNumOfPlayers = currentNumOfPlayers;
        this.locationDescription = locationDescription;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getBeginsAt() {
        return beginsAt;
    }

    public void setBeginsAt(LocalDateTime beginsAt) {
        this.beginsAt = beginsAt;
    }

    public LocalDateTime getEndsOn() {
        return endsOn;
    }

    public void setEndsOn(LocalDateTime endsOn) {
        this.endsOn = endsOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getMaxNumOfPlayers() {
        return maxNumOfPlayers;
    }

    public void setMaxNumOfPlayers(int maxNumOfPlayers) {
        this.maxNumOfPlayers = maxNumOfPlayers;
    }

    public int getCurrentNumOfPlayers() {
        return currentNumOfPlayers;
    }

    public void setCurrentNumOfPlayers(int currentNumOfPlayers) {
        this.currentNumOfPlayers = currentNumOfPlayers;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
