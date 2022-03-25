package mosis.elfak.basketscheduling.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mosis.elfak.basketscheduling.firebase.repository.BasketballEventRepository;
import mosis.elfak.basketscheduling.firebase.repository.UserRepository;

public class FirebaseRealtimeDatabaseClient implements IFirebaseKey{

    private DatabaseReference _realtimeDatabase;
    public UserRepository userRepository;
    public BasketballEventRepository basketballEventRepository;

    private FirebaseRealtimeDatabaseClient(){
        _realtimeDatabase = FirebaseDatabase.getInstance().getReference();
        userRepository = new UserRepository(_realtimeDatabase);
        basketballEventRepository = new BasketballEventRepository(_realtimeDatabase);
    }

    @Override
    public String getKey() {
        return _realtimeDatabase.push().getKey();
    }

    private static class SingletonHolder{
        public static final FirebaseRealtimeDatabaseClient instance = new FirebaseRealtimeDatabaseClient();
    }

    public static FirebaseRealtimeDatabaseClient getInstance(){
        return FirebaseRealtimeDatabaseClient.SingletonHolder.instance;
    }
}
