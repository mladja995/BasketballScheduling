package mosis.elfak.basketscheduling.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.HashMap;

public class FirebaseStorageClient {

    private static final String TAG = "FirebaseStorageClient";
    private HashMap<String, ProfileImageEventListener> profileImageEventListeners;
    private static final String FIREBASE_CHILD_PROFILE_IMAGES = "profile_images";
    private FirebaseStorage _storage;
    private StorageReference _profile_images;

    private FirebaseStorageClient(){
        _storage = FirebaseStorage.getInstance();
        _profile_images = _storage.getReference(FIREBASE_CHILD_PROFILE_IMAGES);
        profileImageEventListeners = new HashMap<String, ProfileImageEventListener>();
    }

    private static class SingletonHolder{
        public static final FirebaseStorageClient instance = new FirebaseStorageClient();
    }

    public static FirebaseStorageClient getInstance(){
        return FirebaseStorageClient.SingletonHolder.instance;
    }

    public interface ProfileImageEventListener {
        void onUploadProfileImageSuccess(String imageURL);
        void onUploadProfileImageFailure();
        String getInvokerName();
    }

    public void setEventListener(ProfileImageEventListener listener){
        ProfileImageEventListener _listener = getListener(listener.getInvokerName());
        if (_listener == null) {
            profileImageEventListeners.put(listener.getInvokerName(), listener);
        }
    }

    private ProfileImageEventListener getListener(String key){
        if (profileImageEventListeners.containsKey(key)){
            return profileImageEventListeners.get(key);
        }else{
            return null;
        }
    }

    public FirebaseStorageClient uploadProfileImage(String fileName, byte[] bytes, String invokerName){
        StorageReference fileRef = _profile_images.child(fileName);
        UploadTask uploadTask = fileRef.putBytes(bytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "uploadProfileImage:failure", e);
                ProfileImageEventListener listener = getListener(invokerName);
                if (listener != null){
                    profileImageEventListeners.get(invokerName).onUploadProfileImageFailure();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "uploadProfileImage:success");
                ProfileImageEventListener listener = getListener(invokerName);
                if (listener != null){
                    profileImageEventListeners.get(invokerName).onUploadProfileImageSuccess(fileRef.getDownloadUrl().toString());
                }
            }
        });

        return this;
    }
}
