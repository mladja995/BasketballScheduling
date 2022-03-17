package mosis.elfak.basketscheduling;

import androidx.appcompat.app.AppCompatActivity;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;

public class LoginActivity extends AppCompatActivity implements FirebaseAuthClient.UserEventListener{

    private static final String TAG = "LoginActivity";
    private FirebaseServices _firebaseServices;
    private FirebaseAuthClient _firebaseAuthClient;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            initialize();
            checkIsUserAlreadyLoggedIn();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        try
        {
            super.onStart();
            checkIsUserAlreadyLoggedIn();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        try
        {
            super.onResume();
            checkIsUserAlreadyLoggedIn();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUserSignUpSuccess() {
    }

    @Override
    public void onUserSignUpFailure() {

    }

    @Override
    public void onUserSignInSuccess() {
        try
        {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUserSignInFailure() {
        try
        {
            Toast.makeText(LoginActivity.this, "Ops! Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public String getInvokerName() {
        return LoginActivity.class.getName();
    }

    public void login_btn_onClick(View view){
        if (validateUserInput()) {
            _firebaseAuthClient
                    .signInWithEmailAndPassword(email, password, LoginActivity.class.getName())
                    .setEventListener(LoginActivity.this);
        }
    }

    private void initialize(){
        _firebaseServices = FirebaseServices.getInstance(LoginActivity.this);
        _firebaseAuthClient = _firebaseServices.firebaseAuthClient;
    }

    private boolean validateUserInput(){
        EditText etEmail = findViewById(R.id.editText_login_email);
        EditText etPassword = findViewById(R.id.editText_login_password);
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private void checkIsUserAlreadyLoggedIn(){
        if (_firebaseAuthClient.isUserLoggedIn()){
            onUserSignInSuccess();
        }
    }
}