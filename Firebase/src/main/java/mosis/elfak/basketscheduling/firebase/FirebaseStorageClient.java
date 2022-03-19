package mosis.elfak.basketscheduling.firebase;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    ProfileImageEventListener listener = getListener(invokerName);
                    if (listener != null) {
                        profileImageEventListeners.get(invokerName).onUploadProfileImageSuccess(downloadUri.toString());
                    }
                } else {
                    Log.e(TAG, "uploadProfileImage:failure", task.getException());
                }
            }
        });

        return this;
    }
}
