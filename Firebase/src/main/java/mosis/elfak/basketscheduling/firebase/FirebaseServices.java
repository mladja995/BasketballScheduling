package mosis.elfak.basketscheduling.firebase;

public class FirebaseServices {

    public FirebaseAuthClient firebaseAuthClient;
    public FirebaseRealtimeDatabaseClient firebaseRealtimeDatabaseClient;
    public FirebaseStorageClient firebaseStorageClient;

    private FirebaseServices(){
        firebaseAuthClient = FirebaseAuthClient.getInstance();
        firebaseRealtimeDatabaseClient = FirebaseRealtimeDatabaseClient.getInstance();
        firebaseStorageClient = FirebaseStorageClient.getInstance();
    }

    private static class SingletonHolder{
        public static final FirebaseServices instance = new FirebaseServices();
    }

    public static FirebaseServices getInstance() {
        return FirebaseServices.SingletonHolder.instance;
    }

}
