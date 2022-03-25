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

import java.util.HashMap;

public class FirebaseStorageClient {

    private static final String TAG = "FirebaseStorageClient";
    private HashMap<String, ImageEventListener> imageEventListeners;
    private static final String FIREBASE_CHILD_PROFILE_IMAGES = "profile_images";
    private static final String FIREBASE_CHILD_EVENTS_IMAGES = "events_images";
    private FirebaseStorage _storage;
    private StorageReference _profile_images;
    private StorageReference _events_images;

    private FirebaseStorageClient(){
        _storage = FirebaseStorage.getInstance();
        _profile_images = _storage.getReference(FIREBASE_CHILD_PROFILE_IMAGES);
        _events_images = _storage.getReference(FIREBASE_CHILD_EVENTS_IMAGES);
        imageEventListeners = new HashMap<String, ImageEventListener>();
    }

    private static class SingletonHolder{
        public static final FirebaseStorageClient instance = new FirebaseStorageClient();
    }

    public static FirebaseStorageClient getInstance(){
        return FirebaseStorageClient.SingletonHolder.instance;
    }

    public interface ImageEventListener {
        void onUploadImageSuccess(String imageURL);
        void onUploadImageFailure();
        String getInvokerName();
    }

    public void setEventListener(ImageEventListener listener){
        ImageEventListener _listener = getListener(listener.getInvokerName());
        if (_listener == null) {
            imageEventListeners.put(listener.getInvokerName(), listener);
        }
    }

    private ImageEventListener getListener(String key){
        if (imageEventListeners.containsKey(key)){
            return imageEventListeners.get(key);
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
                Log.e(TAG, "uploadImage:failure", e);
                ImageEventListener listener = getListener(invokerName);
                if (listener != null){
                    imageEventListeners.get(invokerName).onUploadImageFailure();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "uploadImage:success");
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
                    ImageEventListener listener = getListener(invokerName);
                    if (listener != null) {
                        imageEventListeners.get(invokerName).onUploadImageSuccess(downloadUri.toString());
                    }
                } else {
                    Log.e(TAG, "uploadImage:failure", task.getException());
                }
            }
        });

        return this;
    }

    public FirebaseStorageClient uploadEventImage(String fileName, byte[] bytes, String invokerName){
        StorageReference fileRef = _events_images.child(fileName);
        UploadTask uploadTask = fileRef.putBytes(bytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "uploadImage:failure", e);
                ImageEventListener listener = getListener(invokerName);
                if (listener != null){
                    imageEventListeners.get(invokerName).onUploadImageFailure();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "uploadImage:success");
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
                    ImageEventListener listener = getListener(invokerName);
                    if (listener != null) {
                        imageEventListeners.get(invokerName).onUploadImageSuccess(downloadUri.toString());
                    }
                } else {
                    Log.e(TAG, "uploadImage:failure", task.getException());
                }
            }
        });

        return this;
    }

}
