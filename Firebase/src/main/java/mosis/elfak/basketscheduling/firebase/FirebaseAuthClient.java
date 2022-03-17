package mosis.elfak.basketscheduling.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class FirebaseAuthClient {

    private static final String TAG = "FirebaseAuthClient";
    private FirebaseAuth mAuth;
    private HashMap<String, UserEventListener> userCreatedEventListeners;
    FirebaseUser authUser;


    private FirebaseAuthClient(){
        mAuth = FirebaseAuth.getInstance();
        userCreatedEventListeners = new HashMap<String, UserEventListener>();
    }

    private static class SingletonHolder{
        public static final FirebaseAuthClient instance = new FirebaseAuthClient();
    }

    public static FirebaseAuthClient getInstance(){
        return SingletonHolder.instance;
    }

    public interface UserEventListener {
        void onUserSignUpSuccess();
        void onUserSignUpFailure();
        void onUserSignInSuccess();
        void onUserSignInFailure();
        String getInvokerName();
    }

    public void setEventListener(UserEventListener listener){
        UserEventListener _listener = getListener(listener.getInvokerName());
        if (_listener == null) {
            userCreatedEventListeners.put(listener.getInvokerName(), listener);
        }
    }

    private UserEventListener getListener(String key){
        if (userCreatedEventListeners.containsKey(key)){
            return userCreatedEventListeners.get(key);
        }else{
            return null;
        }
    }

    public FirebaseAuthClient createUserWithEmailAndPassword(String email, String password, String invokerName){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            authUser = mAuth.getCurrentUser();
                            UserEventListener listener = getListener(invokerName);
                            if (listener != null){
                                userCreatedEventListeners.get(invokerName).onUserSignUpSuccess();
                            }
                        }
                        else
                        {
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            UserEventListener listener = getListener(invokerName);
                            if (listener != null){
                                userCreatedEventListeners.get(invokerName).onUserSignInFailure();
                            }
                        }
                    }
                });

        return SingletonHolder.instance;
    }

    public FirebaseAuthClient signInWithEmailAndPassword(String email, String password, String invokerName){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            authUser = mAuth.getCurrentUser();
                            UserEventListener listener = getListener(invokerName);
                            if (listener != null){
                                userCreatedEventListeners.get(invokerName).onUserSignInSuccess();
                            }
                        }
                        else
                        {
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                            UserEventListener listener = getListener(invokerName);
                            if (listener != null){
                                userCreatedEventListeners.get(invokerName).onUserSignUpFailure();
                            }
                        }
                    }
                });

        return SingletonHolder.instance;
    }

    public void signOut(){
        mAuth.signOut();
        userCreatedEventListeners.clear();
        authUser = null;
    }
}
