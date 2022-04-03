package mosis.elfak.basketscheduling.contracts;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BasketballEvent {
    private String eventId;
    private String createdAt;
    private String beginsAt;
    private String endsOn;
    private String createdBy;
    private int maxNumOfPlayers;
    private int currentNumOfPlayers;
    private String eventDescription;
    private String latitude;
    private String longitude;
    private String imageURL;
    private ArrayList<String> joinedUsers;

    public BasketballEvent() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public BasketballEvent(String beginsAt, String endsOn, String createdBy, int maxNumOfPlayers, int currentNumOfPlayers,
                           String eventDescription, String latitude, String longitude, String imageURL) {
        this.beginsAt = beginsAt;
        this.endsOn = endsOn;
        this.createdBy = createdBy;
        this.maxNumOfPlayers = maxNumOfPlayers;
        this.currentNumOfPlayers = currentNumOfPlayers;
        this.eventDescription = eventDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageURL = imageURL;
        this.joinedUsers = new ArrayList<String>();
        joinedUsers.add(createdBy);
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")).toString();

    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getBeginsAt() {
        return beginsAt;
    }

    public void setBeginsAt(String beginsAt) {
        this.beginsAt = beginsAt;
    }

    public String getEndsOn() {
        return endsOn;
    }

    public void setEndsOn(String endsOn) {
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

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescriptionDescription(String locationDescription) {
        this.eventDescription = locationDescription;
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

    public ArrayList<String> getJoinedUsers() {
        return joinedUsers;
    }

    public void setJoinedUsers(ArrayList<String> joinedUsers) {
        this.joinedUsers = joinedUsers;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void addUserToEvent(String userId){
        this.joinedUsers.add(userId);
        this.currentNumOfPlayers += 1;
    }

    public void removeUserFromEvent(String userId){
        for (int i = 0; i < joinedUsers.size(); i++){
            if (joinedUsers.get(i).equals(userId)){
                joinedUsers.remove(i);
                currentNumOfPlayers -= 1;
            }
        }
    }
}
