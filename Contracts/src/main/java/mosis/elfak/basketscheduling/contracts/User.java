package mosis.elfak.basketscheduling.contracts;

import java.util.ArrayList;

public class User {
    private String userId;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String imageURL;
    private String latitude;
    private String longitude;
    private int points;
    private ArrayList<String> friends;
    private ArrayList<FriendRequest> friendsRequests;

    public User() {
    }

    public User(String userId, String username, String firstname, String lastname, String email, String phone, String imageURL) {
        this.userId = userId;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.imageURL = imageURL;
        this.latitude = "";
        this.longitude = "";
        this.points = 0;
        this.friends = new ArrayList<String>();
        this.friendsRequests = new ArrayList<FriendRequest>();
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getPoints() { return points; }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void removePoints(int points){
        this.points -= points;
    }

    public ArrayList<String> getFriends() {
        if (friends != null) {
            return friends;
        }else {
            return friends = new ArrayList<String>();
        }
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public ArrayList<FriendRequest> getFriendsRequests() {
        if (friendsRequests != null) {
            return friendsRequests;
        }else{
            return friendsRequests = new ArrayList<FriendRequest>();
        }
    }

    public void setFriendsRequests(ArrayList<FriendRequest> friendsRequests) {
        this.friendsRequests = friendsRequests;
    }

    public void addFriendRequest(FriendRequest friendRequest){
        getFriendsRequests().add(friendRequest);
    }

    public void removeFriendRequest(FriendRequest friendRequest){
        getFriendsRequests().remove(friendRequest);
    }

    @Override
    public String toString(){
        return this.username;
    }
}
