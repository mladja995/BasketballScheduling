package mosis.elfak.basketscheduling.internals;

import java.util.HashMap;

import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.repository.BasketballEventRepository;
import mosis.elfak.basketscheduling.firebase.repository.UserRepository;

public class ServicesStates {

    private HashMap<String, Boolean> _servicesStates;

    private ServicesStates(){
        _servicesStates = new HashMap<String, Boolean>();
    }

    private static class SingletonHolder{
        public static final ServicesStates instance = new ServicesStates();
    }

    public static ServicesStates getInstance(){
        return ServicesStates.SingletonHolder.instance;
    }

    public void registerServiceState(String serviceName, Boolean state){
        if (_servicesStates.containsKey(serviceName)){
            Boolean _state = _servicesStates.get(serviceName);
            _state = state;
        }else{
            _servicesStates.put(serviceName, state);
        }
    }

    public boolean getServiceState(String serviceName){
        if (_servicesStates.containsKey(serviceName)){
            return _servicesStates.get(serviceName);
        }
        return false;
    }
}
