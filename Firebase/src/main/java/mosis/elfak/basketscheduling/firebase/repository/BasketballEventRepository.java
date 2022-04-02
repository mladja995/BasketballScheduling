package mosis.elfak.basketscheduling.firebase.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import mosis.elfak.basketscheduling.contracts.BasketballEvent;
import mosis.elfak.basketscheduling.contracts.User;

public class BasketballEventRepository {

    private static final String TAG = "EventRepository";
    private DatabaseReference tableRef;
    private static final String FIREBASE_CHILD = "BasketballEvents";
    private HashMap<String, BasketballEventRepository.BasketballEventListener> basketballEventListeners;
    private HashMap<String, BasketballEventRepository.UserBasketballEventListener> userBasketballEventListeners;
    private HashMap<String, Integer> basketballEventsKeyIndexMapping;
    private ArrayList<BasketballEvent> basketballEvents;

    public BasketballEventRepository(DatabaseReference dbRef){
        tableRef = dbRef.child(FIREBASE_CHILD);
        basketballEventListeners = new HashMap<String, BasketballEventRepository.BasketballEventListener>();
        userBasketballEventListeners = new HashMap<String, BasketballEventRepository.UserBasketballEventListener>();
        basketballEventsKeyIndexMapping = new HashMap<String, Integer>();
        basketballEvents = new ArrayList<BasketballEvent>();
        initializeTableListeners();
    }

    public interface BasketballEventListener {
        void onBasketballEventsListUpdated();
        String getInvokerName();
    }

    public interface UserBasketballEventListener {
        void onUserBasketballEventCreatedSuccess(BasketballEvent userBasketballEvent);
        void onUserBasketballEventCreatedFailure();
        String getInvokerName();
    }

    public BasketballEventRepository setEventListenerForBasketballEvent(BasketballEventRepository.BasketballEventListener listener){
        BasketballEventRepository.BasketballEventListener _listener = getBasketballEventListener(listener.getInvokerName());
        if (_listener == null) {
            basketballEventListeners.put(listener.getInvokerName(), listener);
        }
        return this;
    }

    private BasketballEventRepository.BasketballEventListener getBasketballEventListener(String key){
        if (basketballEventListeners.containsKey(key)){
            return basketballEventListeners.get(key);
        }else{
            return null;
        }
    }

    public BasketballEventRepository setEventListenerForUserBasketballEvent(BasketballEventRepository.UserBasketballEventListener listener){
        BasketballEventRepository.UserBasketballEventListener _listener = getUserBasketballEventListener(listener.getInvokerName());
        if (_listener == null) {
            userBasketballEventListeners.put(listener.getInvokerName(), listener);
        }
        return this;
    }

    private BasketballEventRepository.UserBasketballEventListener getUserBasketballEventListener(String key){
        if (userBasketballEventListeners.containsKey(key)){
            return userBasketballEventListeners.get(key);
        }else{
            return null;
        }
    }

    private void initializeTableListeners(){
        tableRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String eventKey = snapshot.getKey();
                if (!basketballEventsKeyIndexMapping.containsKey(eventKey)){
                    BasketballEvent event = snapshot.getValue(BasketballEvent.class);
                    basketballEvents.add(event);
                    basketballEventsKeyIndexMapping.put(eventKey, basketballEvents.size()-1);
                }
                for (Map.Entry<String, BasketballEventRepository.BasketballEventListener> entry : basketballEventListeners.entrySet()) {
                    String k = entry.getKey();
                    BasketballEventRepository.BasketballEventListener v = entry.getValue();
                    v.onBasketballEventsListUpdated();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String eventKey = snapshot.getKey();
                BasketballEvent event = snapshot.getValue(BasketballEvent.class);
                if (basketballEventsKeyIndexMapping.containsKey(eventKey)){
                    int index = basketballEventsKeyIndexMapping.get(eventKey);
                    basketballEvents.set(index, event);
                }
                else{
                    basketballEvents.add(event);
                    basketballEventsKeyIndexMapping.put(eventKey, basketballEvents.size()-1);
                }
                for (Map.Entry<String, BasketballEventRepository.BasketballEventListener> entry : basketballEventListeners.entrySet()) {
                    String k = entry.getKey();
                    BasketballEventRepository.BasketballEventListener v = entry.getValue();
                    v.onBasketballEventsListUpdated();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String eventKey = snapshot.getKey();
                if(basketballEventsKeyIndexMapping.containsKey(eventKey)){
                    int index = basketballEventsKeyIndexMapping.get(eventKey);
                    basketballEvents.remove(index);
                    recreateKeyIndexMapping();
                    for (Map.Entry<String, BasketballEventRepository.BasketballEventListener> entry : basketballEventListeners.entrySet()) {
                        String k = entry.getKey();
                        BasketballEventRepository.BasketballEventListener v = entry.getValue();
                        v.onBasketballEventsListUpdated();
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
                for (Map.Entry<String, BasketballEventRepository.BasketballEventListener> entry : basketballEventListeners.entrySet()) {
                    String k = entry.getKey();
                    BasketballEventRepository.BasketballEventListener v = entry.getValue();
                    v.onBasketballEventsListUpdated();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private void recreateKeyIndexMapping(){
        basketballEventsKeyIndexMapping.clear();
        for (int i = 0; i < basketballEvents.size(); i++){
            basketballEventsKeyIndexMapping.put(basketballEvents.get(i).getEventId(), i);
        }
    }

    public ArrayList<BasketballEvent> getAllBasketballEvents(){
        return this.basketballEvents;
    }

    public BasketballEvent getEvent(int index){
        return basketballEvents.get(index);
    }

    public void createNewBasketballEvent(BasketballEvent event, String invokerName){
        if (event != null) {
            tableRef.child(event.getEventId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    BasketballEvent _event = snapshot.getValue(BasketballEvent.class);
                    BasketballEventRepository.UserBasketballEventListener listener = getUserBasketballEventListener(invokerName);
                    if (listener != null){
                        listener.onUserBasketballEventCreatedSuccess(_event);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, error.getMessage());
                    BasketballEventRepository.UserBasketballEventListener listener = getUserBasketballEventListener(invokerName);
                    if (listener != null){
                        listener.onUserBasketballEventCreatedFailure();
                    }
                }
            });
            tableRef.child(event.getEventId()).setValue(event);
        }
    }
}
