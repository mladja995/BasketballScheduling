package mosis.elfak.basketscheduling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import mosis.elfak.basketscheduling.contracts.User;
import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;
import mosis.elfak.basketscheduling.firebase.repository.UserRepository;

public class RegisterActivity extends AppCompatActivity implements UserRepository.UserEventListener, FirebaseAuthClient.UserAuthenticationEventListener {

    private static final String TAG = "RegisterActivity";
    private FirebaseServices _firebaseServices;
    private FirebaseAuthClient _firebaseAuthClient;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private String userId;
    private String email;
    private String password;
    private String username;
    private String firstname;
    private String lastname;
    private String phone;
    private String imageURL;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
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
    public void onUserAddedSuccess() {
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
    public void onUserChangedSuccess() {

    }

    @Override
    public void onUserRemovedSuccess() {

    }

    @Override
    public void onUserActionFailure() {
        try
        {
            Toast.makeText(RegisterActivity.this, "Ops! Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUserSignUpSuccess() {
        try
        {
            userId = _firebaseAuthClient.getAutheticatedUserId();
            User user = new User(userId, username, firstname, lastname, email, phone, imageURL);
            _firebaseRealtimeDatabaseClient
                    .userRepository
                    .addNewUser(user)
                    .setEventListener(RegisterActivity.this);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUserSignUpFailure() {
        try
        {
            Toast.makeText(RegisterActivity.this, "Ops! Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUserSignInSuccess() {

    }

    @Override
    public void onUserSignInFailure() {

    }

    @Override
    public String getInvokerName() {
        return RegisterActivity.class.getName();
    }

    public void register_btn_onClick(View view){
        try
        {
            progressBar.setVisibility(View.VISIBLE);
            if (validateUserInput()) {
                _firebaseAuthClient
                        .createUserWithEmailAndPassword(email, password, RegisterActivity.class.getName())
                        .setEventListener(RegisterActivity.this);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            progressBar.setVisibility(View.GONE);
        }

    }

    private void initialize(){
        _firebaseServices = FirebaseServices.getInstance(RegisterActivity.this);
        _firebaseAuthClient = _firebaseServices.firebaseAuthClient;
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
        progressBar = findViewById(R.id.progressBar_register);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private boolean validateUserInput(){
        // TODO: Add logic for image
        EditText etEmail = findViewById(R.id.editText_register_email);
        EditText etPassword = findViewById(R.id.editText_register_password);
        EditText etUsername = findViewById(R.id.editText_register_username);
        EditText etFirstname = findViewById(R.id.editText_register_firstname);
        EditText etLastname = findViewById(R.id.editText_register_lastname);
        EditText etPhone = findViewById(R.id.editText_register_phone);
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        username = etUsername.getText().toString();
        firstname = etFirstname.getText().toString();
        lastname = etLastname.getText().toString();
        phone = etPhone.getText().toString();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || phone.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return false;
        }else{
            return true;
        }
    }

    private void checkIsUserAlreadyLoggedIn(){
        if (_firebaseAuthClient.isUserLoggedIn()){
            onUserAddedSuccess();
        }
    }
}