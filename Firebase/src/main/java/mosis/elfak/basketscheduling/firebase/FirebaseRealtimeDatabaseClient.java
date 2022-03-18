package mosis.elfak.basketscheduling.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mosis.elfak.basketscheduling.firebase.repository.UserRepository;

public class FirebaseRealtimeDatabaseClient {

    private DatabaseReference _realtimeDatabase;
    public UserRepository userRepository;

    private FirebaseRealtimeDatabaseClient(){
        _realtimeDatabase = FirebaseDatabase.getInstance().getReference();
        userRepository = new UserRepository(_realtimeDatabase);
    }

    private static class SingletonHolder{
        public static final FirebaseRealtimeDatabaseClient instance = new FirebaseRealtimeDatabaseClient();
    }

    public static FirebaseRealtimeDatabaseClient getInstance(){
        return FirebaseRealtimeDatabaseClient.SingletonHolder.instance;
    }
}
