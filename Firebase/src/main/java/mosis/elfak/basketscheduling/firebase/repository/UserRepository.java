package mosis.elfak.basketscheduling.firebase.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import mosis.elfak.basketscheduling.contracts.User;

public class UserRepository {

    private static final String TAG = "UserRepository";
    private DatabaseReference tableRef;
    private static final String FIREBASE_CHILD = "Users";
    private HashMap<String, CurrentUserEventListener> currentUserEventListeners;
    private HashMap<String, UsersEventListener> usersEventListeners;
    public User currentUser;

    public UserRepository(DatabaseReference dbRef){
        tableRef = dbRef.child(FIREBASE_CHILD);
        currentUserEventListeners = new HashMap<String, CurrentUserEventListener>();
        usersEventListeners = new HashMap<String, UsersEventListener>();
    }

    public interface UsersEventListener {
        String getInvokerName();
    }

    public interface CurrentUserEventListener {
        void onUserAddedSuccess();
        void onUserAddedFailure();
        String getInvokerName();
    }

    public UserRepository setEventListener(CurrentUserEventListener listener){
        CurrentUserEventListener _listener = getCurrentUserListener(listener.getInvokerName());
        if (_listener == null) {
            currentUserEventListeners.put(listener.getInvokerName(), listener);
        }
        return this;
    }

    private CurrentUserEventListener getCurrentUserListener(String key){
        if (currentUserEventListeners.containsKey(key)){
            return currentUserEventListeners.get(key);
        }else{
            return null;
        }
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public void addNewUser(User user, String invokerName){
        if (currentUser == null) {
            tableRef.child(user.getUserId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currentUser = snapshot.getValue(User.class);
                    CurrentUserEventListener listener = getCurrentUserListener(invokerName);
                    if (listener != null){
                        listener.onUserAddedSuccess();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage());
                    CurrentUserEventListener listener = getCurrentUserListener(invokerName);
                    if (listener != null){
                        listener.onUserAddedFailure();
                    }
                }
            });
            tableRef.child(user.getUserId()).setValue(user);
        }
    }

    public void setCurrentUser(String key){
        if (currentUser == null) {
            tableRef.child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Error getting data", task.getException());
                    }
                    else {
                        Log.d(TAG, String.valueOf(task.getResult().getValue()));
                        currentUser = task.getResult().getValue(User.class);
                    }
                }
            });
        }
    }
}
