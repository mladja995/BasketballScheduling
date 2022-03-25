package mosis.elfak.basketscheduling;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;
import mosis.elfak.basketscheduling.contracts.User;
import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;
import mosis.elfak.basketscheduling.firebase.FirebaseStorageClient;
import mosis.elfak.basketscheduling.firebase.repository.UserRepository;

public class RegisterActivity extends AppCompatActivity implements UserRepository.CurrentUserEventListener, FirebaseAuthClient.UserAuthenticationEventListener, FirebaseStorageClient.ImageEventListener {

    private ActivityResultLauncher<String> activityResultLauncher;
    private static final String TAG = "RegisterActivity";
    private static final int REQUEST_LOAD_IMAGE = 1;
    private FirebaseServices _firebaseServices;
    private FirebaseAuthClient _firebaseAuthClient;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private FirebaseStorageClient _firebaseStorageClient;
    private String userId;
    private String email;
    private String password;
    private String username;
    private String firstname;
    private String lastname;
    private String phone;
    private String imageURL;
    private Uri imageURI;
    private ProgressBar progressBar;
    private Button registerBtn;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            initialize();
            initializeListeners();
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
    public void onUserCreatedSuccess() {
        try
        {
            progressBar.setVisibility(View.GONE);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUserCreatedFailure() {
        try
        {
            Toast.makeText(RegisterActivity.this, "Ops! Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            registerBtn.setEnabled(true);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUserSignUpSuccess() {
        try
        {
            userId = _firebaseAuthClient.getAutheticatedUserId();
            uploadProfileImage();
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
            progressBar.setVisibility(View.GONE);
            registerBtn.setEnabled(true);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUserSignInSuccess() {

    }

    @Override
    public void onUserSignInFailure() {

    }

    @Override
    public void onUploadImageSuccess(String imageURL) {
        this.imageURL = imageURL;
        User user = new User(userId, username, firstname, lastname, email, phone, this.imageURL);
        _firebaseRealtimeDatabaseClient
                .userRepository
                .setEventListenerForCurrentUser(RegisterActivity.this)
                .createNewUser(user, RegisterActivity.class.getName());
    }

    @Override
    public void onUploadImageFailure() {
        Toast.makeText(RegisterActivity.this, "Ops! Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        registerBtn.setEnabled(true);
    }

    @Override
    public String getInvokerName() {
        return RegisterActivity.class.getName();
    }

    public void register_btn_onClick(View view){
        try
        {
            progressBar.setVisibility(View.VISIBLE);
            registerBtn.setEnabled(false);
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
            registerBtn.setEnabled(true);
        }

    }

    public void profileImage_onClick(View view){
        activityResultLauncher.launch("image/*");
    }

    private void initialize(){
        _firebaseServices = FirebaseServices.getInstance(RegisterActivity.this);
        _firebaseAuthClient = _firebaseServices.firebaseAuthClient;
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
        _firebaseStorageClient = _firebaseServices.firebaseStorageClient;
        progressBar = findViewById(R.id.progressBar_register);
        registerBtn = findViewById(R.id.button_register_register);
        registerBtn.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
        profileImage = findViewById(R.id.imageView_register_profile_image);
    }


    private void initializeListeners()
    {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                profileImage.setImageURI(result);
                imageURI = result;
            }
        });
    }

    private void uploadProfileImage() throws FileNotFoundException {
        try {
            profileImage.setDrawingCacheEnabled(true);
            profileImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            _firebaseStorageClient
                    .uploadProfileImage(userId, data, RegisterActivity.class.getName())
                    .setEventListener(RegisterActivity.this);
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private boolean validateUserInput(){
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

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()
                || firstname.isEmpty() || lastname.isEmpty() || phone.isEmpty()
                || imageURI == null){
            Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            registerBtn.setEnabled(true);
            return false;
        }else{
            return true;
        }
    }

    private void checkIsUserAlreadyLoggedIn(){
        if (_firebaseAuthClient.isUserLoggedIn()){
            onUserCreatedSuccess();
        }
    }
}