package mosis.elfak.basketscheduling.contracts;

public class FriendRequest {

    private String userId;
    private String status;

    public FriendRequest(){

    }

    public FriendRequest(String userId, FriendRequestStatus status) {
        this.userId = userId;
        this.status = status.toString();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }
}
