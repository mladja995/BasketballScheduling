package mosis.elfak.basketscheduling.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

class FirebaseAuthClient {

    private static final String TAG = "FirebaseAuthClient";
    private boolean ret = false;
    public FirebaseAuth mAuth;
    public FirebaseUser authUser;


    private FirebaseAuthClient(){
        try
        {
            mAuth = FirebaseAuth.getInstance();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    private static class SingletonHolder{
        public static final FirebaseAuthClient instance = new FirebaseAuthClient();
    }

    public static FirebaseAuthClient getInstance(){
        return SingletonHolder.instance;
    }

    public boolean createUserWithEmailAndPassword(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            authUser = mAuth.getCurrentUser();
                            ret = true;
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            ret = false;
                        }
                    }
                });

        return ret;
    }
}
