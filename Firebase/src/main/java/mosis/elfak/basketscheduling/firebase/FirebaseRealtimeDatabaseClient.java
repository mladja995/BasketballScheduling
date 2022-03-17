package mosis.elfak.basketscheduling.firebase;

class FirebaseRealtimeDatabaseClient {

    private static class SingletonHolder{
        public static final FirebaseRealtimeDatabaseClient instance = new FirebaseRealtimeDatabaseClient();
    }

    public static FirebaseRealtimeDatabaseClient getInstance(){
        return FirebaseRealtimeDatabaseClient.SingletonHolder.instance;
    }
}
