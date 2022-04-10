package mosis.elfak.basketscheduling.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import mosis.elfak.basketscheduling.R;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;
import mosis.elfak.basketscheduling.firebase.repository.UserRepository;
import mosis.elfak.basketscheduling.services.LocationService;

public class LoginActivity extends AppCompatActivity implements
        FirebaseAuthClient.UserAuthenticationEventListener,
        UserRepository.CurrentUserEventListener {

    private static final String TAG = "LoginActivity";
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSION_ACCESS_BACKGROUND_LOCATION = 2;
    private FirebaseServices _firebaseServices;
    private FirebaseAuthClient _firebaseAuthClient;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private String email;
    private String password;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onRestart() {
        try
        {
            super.onRestart();
            checkIsUserAlreadyLoggedIn();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onUserSignInSuccess() {
        try
        {
            _firebaseRealtimeDatabaseClient
                    .userRepository
                    .setEventListenerForCurrentUser(this)
                    .setCurrentUser(_firebaseAuthClient.getAutheticatedUserId(), LoginActivity.class.getName());
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
            progressBar.setVisibility(View.GONE);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUserCreatedSuccess() {

    }

    @Override
    public void onUserCreatedFailure() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCurrentUserSet() {
        requestPermissionForLocationTracking();
    }

    @Override
    public String getInvokerName() {
        return LoginActivity.class.getName();
    }

    public void login_btn_onClick(View view){
        try
        {
            progressBar.setVisibility(View.VISIBLE);
            if (validateUserInput()) {
                _firebaseAuthClient
                        .signInWithEmailAndPassword(email, password, LoginActivity.class.getName())
                        .setEventListener(LoginActivity.this);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            progressBar.setVisibility(View.GONE);
        }

    }

    public void register_text_onClick(View view){
        try
        {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    private void initialize(){
        _firebaseServices = FirebaseServices.getInstance(LoginActivity.this);
        _firebaseAuthClient = _firebaseServices.firebaseAuthClient;
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
        progressBar = findViewById(R.id.progressBar_login);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private boolean validateUserInput(){
        EditText etEmail = findViewById(R.id.editText_login_email);
        EditText etPassword = findViewById(R.id.editText_login_password);
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return false;
        }else{
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkIsUserAlreadyLoggedIn(){
        if (_firebaseAuthClient.isUserLoggedIn()){
            onUserSignInSuccess();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void requestPermissionForLocationTracking(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            requestPermissionForBackgroundLocationTracking();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void requestPermissionForBackgroundLocationTracking(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_ACCESS_BACKGROUND_LOCATION);
        } else {
            enterApplication();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startLocationService(){
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, LocationService.class);
            startForegroundService(serviceIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String perminssions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, perminssions, grantResults);

        switch (requestCode){
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    requestPermissionForBackgroundLocationTracking();
                }else{
                    _firebaseAuthClient.signOut();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "You are signed out! If you want to use application you must grant location tracking!", Toast.LENGTH_SHORT).show();                }
                return;
            case PERMISSION_ACCESS_BACKGROUND_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    enterApplication();
                }else{
                    _firebaseAuthClient.signOut();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "You are signed out! If you want to use application you must grant location tracking!", Toast.LENGTH_SHORT).show();                }
                return;
        }
    }

    private boolean isLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)){
            if (LocationService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterApplication(){
        startLocationService();
        Intent i = new Intent(this, MainActivity.class);
        progressBar.setVisibility(View.GONE);
        startActivity(i);
    }
}