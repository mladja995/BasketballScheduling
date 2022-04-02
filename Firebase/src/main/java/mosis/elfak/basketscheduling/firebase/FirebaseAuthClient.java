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
    private HashMap<String, UserAuthenticationEventListener> userAuthenticationEventListeners;
    FirebaseUser authUser;


    private FirebaseAuthClient(){
        mAuth = FirebaseAuth.getInstance();
        userAuthenticationEventListeners = new HashMap<String, UserAuthenticationEventListener>();
    }

    private static class SingletonHolder{
        public static final FirebaseAuthClient instance = new FirebaseAuthClient();
    }

    public static FirebaseAuthClient getInstance(){
        return SingletonHolder.instance;
    }

    public interface UserAuthenticationEventListener {
        void onUserSignUpSuccess();
        void onUserSignUpFailure();
        void onUserSignInSuccess();
        void onUserSignInFailure();
        String getInvokerName();
    }

    public void setEventListener(UserAuthenticationEventListener listener){
        UserAuthenticationEventListener _listener = getListener(listener.getInvokerName());
        if (_listener == null) {
            userAuthenticationEventListeners.put(listener.getInvokerName(), listener);
        }
    }

    private UserAuthenticationEventListener getListener(String key){
        if (userAuthenticationEventListeners.containsKey(key)){
            return userAuthenticationEventListeners.get(key);
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
                            Log.i(TAG, "createUserWithEmail:success" + " " + email);
                            authUser = mAuth.getCurrentUser();
                            UserAuthenticationEventListener listener = getListener(invokerName);
                            if (listener != null){
                                listener.onUserSignUpSuccess();
                            }
                        }
                        else
                        {
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            UserAuthenticationEventListener listener = getListener(invokerName);
                            if (listener != null){
                                listener.onUserSignUpFailure();
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
                            Log.i(TAG, "signInWithEmail:success" + " " + email);
                            authUser = mAuth.getCurrentUser();
                            UserAuthenticationEventListener listener = getListener(invokerName);
                            if (listener != null){
                                listener.onUserSignInSuccess();
                            }
                        }
                        else
                        {
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                            UserAuthenticationEventListener listener = getListener(invokerName);
                            if (listener != null){
                                listener.onUserSignInFailure();
                            }
                        }
                    }
                });

        return SingletonHolder.instance;
    }

    public void signOut(){
        mAuth.signOut();
        userAuthenticationEventListeners.clear();
        authUser = null;
    }

    public String getAutheticatedUserId(){
        if (authUser != null){
            return authUser.getUid();
        }else{
            return null;
        }
    }

    public String getAutheticatedUserEmail(){
        if (authUser != null){
            return authUser.getEmail();
        }else{
            return null;
        }
    }

    public boolean isUserLoggedIn(){
        if (authUser != null){
            return true;
        }else{
            return false;
        }
    }
}
