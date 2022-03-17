package mosis.elfak.basketscheduling.firebase;

class FirebaseStorageClient {

    private static class SingletonHolder{
        public static final FirebaseStorageClient instance = new FirebaseStorageClient();
    }

    public static FirebaseStorageClient getInstance(){
        return FirebaseStorageClient.SingletonHolder.instance;
    }
}
