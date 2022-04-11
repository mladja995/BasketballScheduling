package mosis.elfak.basketscheduling.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mosis.elfak.basketscheduling.R;
import mosis.elfak.basketscheduling.contracts.Constants;
import mosis.elfak.basketscheduling.contracts.User;
import mosis.elfak.basketscheduling.databinding.ActivityRankingsBinding;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;
import mosis.elfak.basketscheduling.firebase.repository.UserRepository;
import mosis.elfak.basketscheduling.internals.UserListAdapter;

public class RankingsActivity extends AppCompatActivity implements UserRepository.UsersEventListener, UserListAdapter.UsersImagesEventListener {

    private static final String TAG = "RankingsActivity";
    private ActivityRankingsBinding binding;
    private FirebaseServices _firebaseServices;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private ListView usersListView;
    private UserListAdapter userListAdapter;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_rankings);
            binding = ActivityRankingsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setSupportActionBar(binding.toolbarRankings);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            initialize();
            initializeListView();
            InitializeListeners();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initializeListView()
    {
        try
        {
            usersListView = (ListView) findViewById(R.id.rankings_user_list);
            usersListView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            ArrayList<User> users = _firebaseRealtimeDatabaseClient.userRepository.getAllUsers();
            Comparator<User> userComparator = (u1, u2) -> (int) (u1.getPoints() - u2.getPoints());
            users.sort(Collections.reverseOrder(userComparator));
            userListAdapter = new UserListAdapter(RankingsActivity.this, _firebaseRealtimeDatabaseClient.userRepository.getAllUsers());
            usersListView.setAdapter(userListAdapter);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        try
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_rankings, menu);
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
        _firebaseServices = FirebaseServices.getInstance(RankingsActivity.this);
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
        progressBar = findViewById(R.id.progressBar_rankings);
        progressBar.setVisibility(View.GONE);
    }

    private void InitializeListeners(){
        _firebaseRealtimeDatabaseClient.userRepository.setEventListenerForUsers(this);
        UserListAdapter.setEventListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onUsersListUpdated() {
        initializeListView();
    }

    @Override
    public String getInvokerName() {
        return RankingsActivity.class.getName();
    }

    @Override
    public void onUsersImagesDownloaded() {
        progressBar.setVisibility(View.GONE);
        usersListView.setVisibility(View.VISIBLE);
    }
}