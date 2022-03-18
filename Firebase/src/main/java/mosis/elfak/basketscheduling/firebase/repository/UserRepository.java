package mosis.elfak.basketscheduling.firebase.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    private HashMap<String, UserEventListener> userEventListeners;
    public User currentUser;

    public UserRepository(DatabaseReference dbRef){
        tableRef = dbRef.child(FIREBASE_CHILD);
        tableRef.addChildEventListener(childEventListener);
        tableRef.addListenerForSingleValueEvent(parentEventListener);
        userEventListeners = new HashMap<String, UserEventListener>();
    }

    public interface UserEventListener {
        void onUserAddedSuccess();
        void onUserChangedSuccess();
        void onUserRemovedSuccess();
        void onUserActionFailure();
        String getInvokerName();
    }

    public void setEventListener(UserEventListener listener){
        UserEventListener _listener = getListener(listener.getInvokerName());
        if (_listener == null) {
            userEventListeners.put(listener.getInvokerName(), listener);
        }
    }

    private UserEventListener getListener(String key){
        if (userEventListeners.containsKey(key)){
            return userEventListeners.get(key);
        }else{
            return null;
        }
    }

    ValueEventListener parentEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: ");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            currentUser = snapshot.getValue(User.class);
            for (Map.Entry<String, UserEventListener> entry : userEventListeners.entrySet()) {
                String key = entry.getKey();
                UserEventListener value = entry.getValue();
                value.onUserAddedSuccess();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            currentUser = snapshot.getValue(User.class);
            for (Map.Entry<String, UserEventListener> entry : userEventListeners.entrySet()) {
                String key = entry.getKey();
                UserEventListener value = entry.getValue();
                value.onUserChangedSuccess();
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            currentUser = null;
            for (Map.Entry<String, UserEventListener> entry : userEventListeners.entrySet()) {
                String key = entry.getKey();
                UserEventListener value = entry.getValue();
                value.onUserRemovedSuccess();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e(TAG, error.getMessage());
            for (Map.Entry<String, UserEventListener> entry : userEventListeners.entrySet()) {
                String key = entry.getKey();
                UserEventListener value = entry.getValue();
                value.onUserActionFailure();
            }
        }
    };

    public UserRepository addNewUser(User user){
        if (currentUser == null) {
            tableRef.child(user.getUserId()).setValue(user);
        }
        return this;
    }
}
