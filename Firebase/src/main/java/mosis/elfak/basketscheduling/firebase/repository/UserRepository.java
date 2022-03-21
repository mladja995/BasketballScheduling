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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mosis.elfak.basketscheduling.contracts.User;

public class UserRepository {

    private static final String TAG = "UserRepository";
    private DatabaseReference tableRef;
    private static final String FIREBASE_CHILD = "Users";
    private HashMap<String, CurrentUserEventListener> currentUserEventListeners;
    private HashMap<String, UsersEventListener> usersEventListeners;
    private HashMap<String, Integer> usersKeyIndexMapping;
    private User currentUser;
    private ArrayList<User> users;

    public UserRepository(DatabaseReference dbRef){
        tableRef = dbRef.child(FIREBASE_CHILD);
        currentUserEventListeners = new HashMap<String, CurrentUserEventListener>();
        usersEventListeners = new HashMap<String, UsersEventListener>();
        users = new ArrayList<User>();
        usersKeyIndexMapping = new HashMap<String, Integer>();
        initializeTableListeners();
    }

    public interface UsersEventListener {
        void OnUsersListUpdated();
        String getInvokerName();
    }

    public interface CurrentUserEventListener {
        void onUserCreatedSuccess();
        void onUserCreatedFailure();
        String getInvokerName();
    }

    public UserRepository setEventListenerForCurrentUser(CurrentUserEventListener listener){
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

    public UserRepository setEventListenerForUsers(UsersEventListener listener){
        UsersEventListener _listener = getUsersEventListener(listener.getInvokerName());
        if (_listener == null) {
            usersEventListeners.put(listener.getInvokerName(), listener);
        }
        return this;
    }

    private UsersEventListener getUsersEventListener(String key){
        if (usersEventListeners.containsKey(key)){
            return usersEventListeners.get(key);
        }else{
            return null;
        }
    }

    private void initializeTableListeners(){
        tableRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String userKey = snapshot.getKey();
                if (!usersKeyIndexMapping.containsKey(userKey)){
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                    usersKeyIndexMapping.put(userKey, users.size()-1);
                }
                for (Map.Entry<String, UsersEventListener> entry : usersEventListeners.entrySet()) {
                    String k = entry.getKey();
                    UsersEventListener v = entry.getValue();
                    v.OnUsersListUpdated();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String userKey = snapshot.getKey();
                User user = snapshot.getValue(User.class);
                if (usersKeyIndexMapping.containsKey(userKey)){
                    int index = usersKeyIndexMapping.get(userKey);
                    users.set(index, user);
                }
                else{
                    users.add(user);
                    usersKeyIndexMapping.put(userKey, users.size()-1);
                }
                for (Map.Entry<String, UsersEventListener> entry : usersEventListeners.entrySet()) {
                    String k = entry.getKey();
                    UsersEventListener v = entry.getValue();
                    v.OnUsersListUpdated();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String userKey = snapshot.getKey();
                if(usersKeyIndexMapping.containsKey(userKey)){
                    int index = usersKeyIndexMapping.get(userKey);
                    users.remove(index);
                    recreateKeyIndexMapping();
                    for (Map.Entry<String, UsersEventListener> entry : usersEventListeners.entrySet()) {
                        String k = entry.getKey();
                        UsersEventListener v = entry.getValue();
                        v.OnUsersListUpdated();
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });

        tableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (Map.Entry<String, UsersEventListener> entry : usersEventListeners.entrySet()) {
                    String k = entry.getKey();
                    UsersEventListener v = entry.getValue();
                    v.OnUsersListUpdated();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private void recreateKeyIndexMapping(){
        usersKeyIndexMapping.clear();
        for (int i = 0; i < users.size(); i++){
            usersKeyIndexMapping.put(users.get(i).getUserId(), i);
        }
    }

    public void createNewUser(User user, String invokerName){
        if (currentUser == null) {
            user.setPoints(3);
            tableRef.child(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currentUser = snapshot.getValue(User.class);
                    CurrentUserEventListener listener = getCurrentUserListener(invokerName);
                    if (listener != null){
                        listener.onUserCreatedSuccess();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage());
                    CurrentUserEventListener listener = getCurrentUserListener(invokerName);
                    if (listener != null){
                        listener.onUserCreatedFailure();
                    }
                }
            });
            tableRef.child(user.getUserId()).setValue(user);
        }
    }

    public void setLocationForCurrentUser(String latitude, String longitude){
        if (currentUser != null)
        {
            currentUser.setLatitude(latitude);
            currentUser.setLongitude(longitude);
            tableRef.child(currentUser.getUserId()).setValue(currentUser);
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

    public User getCurrentUser(){
        return this.currentUser;
    }

    public ArrayList<User> getAllUsers(){
        return this.users;
    }

    public void addPointsToCurrentUser(int points){
        if (currentUser != null){
            currentUser.setPoints(points);
            tableRef.child(currentUser.getUserId()).setValue(currentUser);
        }
    }
}
