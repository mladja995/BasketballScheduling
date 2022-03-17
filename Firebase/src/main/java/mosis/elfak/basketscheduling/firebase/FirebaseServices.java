package mosis.elfak.basketscheduling.firebase;

import android.content.Context;

import com.google.firebase.FirebaseApp;

public class FirebaseServices {

    public FirebaseAuthClient firebaseAuthClient;
    public FirebaseRealtimeDatabaseClient firebaseRealtimeDatabaseClient;
    public FirebaseStorageClient firebaseStorageClient;
    private static FirebaseApp firebaseApp;

    private FirebaseServices(){
        firebaseAuthClient = FirebaseAuthClient.getInstance();
        firebaseRealtimeDatabaseClient = FirebaseRealtimeDatabaseClient.getInstance();
        firebaseStorageClient = FirebaseStorageClient.getInstance();
    }

    private static class SingletonHolder{
        public static final FirebaseServices instance = new FirebaseServices();
    }

    public static FirebaseServices getInstance(Context context) {
        if (firebaseApp == null){
            firebaseApp = FirebaseApp.initializeApp(context);
        }
        return FirebaseServices.SingletonHolder.instance;
    }


}
