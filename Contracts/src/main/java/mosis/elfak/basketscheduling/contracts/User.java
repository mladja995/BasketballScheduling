package mosis.elfak.basketscheduling.contracts;

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

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setPoints(int points) {
        this.points += points;
    }

    @Override
    public String toString(){
        return this.username;
    }
}
