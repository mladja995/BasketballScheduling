package mosis.elfak.basketscheduling.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;

import mosis.elfak.basketscheduling.R;
import mosis.elfak.basketscheduling.contracts.FriendRequest;
import mosis.elfak.basketscheduling.contracts.FriendRequestStatus;
import mosis.elfak.basketscheduling.contracts.User;
import mosis.elfak.basketscheduling.databinding.ActivityViewUserProfileBinding;
import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;

public class ViewUserProfileActivity extends AppCompatActivity {

    private static final String TAG = "ViewUserProfileActivity";
    private ActivityViewUserProfileBinding binding;
    private FirebaseServices _firebaseServices;
    private FirebaseAuthClient _firebaseAuthClient;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private ProgressBar progressBar;
    private User _user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            binding = ActivityViewUserProfileBinding.inflate(getLayoutInflater());

            initialize();
            processIntent();
            _setContentView();
            InitializeListeners();
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        try
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_view_user_profile, menu);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try
        {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            if (id == android.R.id.home)
            {
                finish();
            }
            return super.onOptionsItemSelected(item);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private void initialize(){
        _firebaseServices = FirebaseServices.getInstance(ViewUserProfileActivity.this);
        _firebaseAuthClient = _firebaseServices.firebaseAuthClient;
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
    }

    private void InitializeListeners(){
    }

    private void processIntent()
    {
        try{
            Intent intent = getIntent();
            Bundle positionBundle = intent.getExtras();
            Integer index = positionBundle.getInt("position");
            _user = _firebaseRealtimeDatabaseClient.userRepository.getUser(index);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private void _setContentView(){
        if (isUserFriend() || _user.getUserId().equals(_firebaseAuthClient.getAutheticatedUserId())){
            setContentView(R.layout.activity_view_user_profile);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_user_profile);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            progressBar = findViewById(R.id.progressBar_view_user_profile);
            progressBar.setVisibility(View.GONE);
            initializeUserProfile();
        } else{
            setContentView(R.layout.activity_view_user_profile_unknown);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_user_profile_unknown);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            initializeUserProfileUnknown();
        }
    }

    private boolean isUserFriend(){
        User _currentUser = _firebaseRealtimeDatabaseClient.userRepository.getCurrentUser();
        for (int i = 0; i < _currentUser.getFriends().size(); i++){
            if (_user.getUserId().equals(_currentUser.getFriends().get(i))){
                return true;
            }
        }
        return false;
    }

    private void initializeUserProfile(){
        ImageView ivUserImage = findViewById(R.id.imageView_view_user_profile_profile_image);
        TextView tvUsername = findViewById(R.id.textView_view_user_profile_username);
        TextView tvFullName = findViewById(R.id.textView_view_user_profile_fullname_value);
        TextView tvMail = findViewById(R.id.textView_view_user_profile_mail_value);
        TextView tvPhone = findViewById(R.id.textView_view_user_profile_phone_value);
        TextView tvPoints = findViewById(R.id.textView_view_user_profile_points_value);

        ivUserImage.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Picasso.with(this)
                .load(_user.getImageURL())
                .fit()
                .into(ivUserImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "onImageLoad: success");
                        ivUserImage.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError() {
                        Log.e(TAG, "onImageLoad: failure");
                    }
                });

        tvUsername.setText(_user.getUsername());
        tvFullName.setText(_user.getFirstname() + " " + _user.getLastname());
        tvMail.setText(_user.getEmail());
        tvPhone.setText(_user.getPhone());
        tvPoints.setText(Integer.toString(_user.getPoints()));
    }

    @SuppressLint("ResourceAsColor")
    private void initializeUserProfileUnknown(){
        Button btnSendFriendRequest = findViewById(R.id.button_view_user_profile_send_friend_request);
        if (isFriendRequestAlreadySent()){
            btnSendFriendRequest.setBackgroundColor(R.color.light_gray);
        }
    }

    private boolean isFriendRequestAlreadySent(){
        for (int i = 0; i < _user.getFriendsRequests().size(); i++){
            FriendRequest _friendRequest = _user.getFriendsRequests().get(i);
            if (_friendRequest.getUserId().equals(_firebaseAuthClient.getAutheticatedUserId()) && _friendRequest.getStatus().equals(FriendRequestStatus.Pending.toString())){
                return true;
            }
        }
        return false;
    }

    @SuppressLint("ResourceAsColor")
    public void send_friend_request_onClick(View view){
        if (isFriendRequestAlreadySent()){
            Toast.makeText(this, "You've already sent friend request!", Toast.LENGTH_SHORT).show();
        }
        else{
            FriendRequest _friendRequest = new FriendRequest(_firebaseAuthClient.getAutheticatedUserId(), FriendRequestStatus.Pending);
            _firebaseRealtimeDatabaseClient
                    .userRepository
                    .sendFriendRequest(_user, _friendRequest);
            Button btnSendFriendRequest = findViewById(R.id.button_view_user_profile_send_friend_request);
            btnSendFriendRequest.setBackgroundColor(R.color.light_gray);
            Toast.makeText(this, "Friend request sent!", Toast.LENGTH_SHORT).show();
        }
    }
}