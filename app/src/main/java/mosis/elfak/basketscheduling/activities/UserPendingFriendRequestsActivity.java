package mosis.elfak.basketscheduling.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import mosis.elfak.basketscheduling.R;
import mosis.elfak.basketscheduling.contracts.Constants;
import mosis.elfak.basketscheduling.contracts.FriendRequest;
import mosis.elfak.basketscheduling.contracts.FriendRequestStatus;
import mosis.elfak.basketscheduling.contracts.User;
import mosis.elfak.basketscheduling.databinding.ActivityUserPendingFriendRequestsBinding;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;
import mosis.elfak.basketscheduling.firebase.repository.UserRepository;
import mosis.elfak.basketscheduling.internals.PendingFriendRequestsListAdapter;

public class UserPendingFriendRequestsActivity extends AppCompatActivity implements
        UserRepository.UsersEventListener, PendingFriendRequestsListAdapter.FriendsImagesEventListener {

    private static final String TAG = "UserPendingFriendRequestsActivity";
    private ActivityUserPendingFriendRequestsBinding binding;
    private FirebaseServices _firebaseServices;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private ListView pendingFriendRequestsListView;
    private PendingFriendRequestsListAdapter pendingFriendRequestsListAdapter;
    private ProgressBar progressBar;
    private ArrayList<User> _friends;
    private HashMap<Integer, String> contexMenuItemPositionUserMap;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_pending_friend_requests);
            binding = ActivityUserPendingFriendRequestsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setSupportActionBar(binding.toolbarUserPendingFriendsRequests);
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
    @Override
    protected void onResume() {
        super.onResume();
        initializeListView();
    }

    @SuppressLint("LongLogTag")
    public boolean onCreateOptionsMenu(Menu menu) {
        try
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_user_pending_friend_requests, menu);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try
        {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            if (id == R.id.action_show_map)
            {
                Bundle stateBundle = new Bundle();
                stateBundle.putInt("state", Constants.SHOW_MAP);
                Intent i = new Intent(this, MapsActivity.class);
                i.putExtras(stateBundle);
                startActivity(i);
            }
            if (id == R.id.action_create_event)
            {
                Intent i = new Intent(this, CreateBasketballEventActivity.class);
                startActivity(i);
            }
            else if (id == android.R.id.home)
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
        _firebaseServices = FirebaseServices.getInstance(UserPendingFriendRequestsActivity.this);
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
        progressBar = findViewById(R.id.progressBar_user_pending_friends_requests);
        progressBar.setVisibility(View.GONE);
    }

    @SuppressLint("LongLogTag")
    public void initializeListView()
    {
        try
        {
            contexMenuItemPositionUserMap = new HashMap<Integer, String>();
            pendingFriendRequestsListView = (ListView) findViewById(R.id.user_pending_friends_requests_list);
            _friends = new ArrayList<User>();
            ArrayList<FriendRequest> currentUserFriendRequests = _firebaseRealtimeDatabaseClient.userRepository.getCurrentUser().getFriendsRequests();
            int _position = 0;
            for (int i = 0; i < currentUserFriendRequests.size(); i++){
                if (currentUserFriendRequests.get(i).getStatus().equals(FriendRequestStatus.Pending.toString())) {
                    String _friendKey = currentUserFriendRequests.get(i).getUserId();
                    User _friend = _firebaseRealtimeDatabaseClient.userRepository.getUser(_friendKey);
                    _friends.add(_friend);
                    contexMenuItemPositionUserMap.put(_position, _friend.getUserId());
                    _position++;
                }
            }

            if (_friends.size() > 1){
                pendingFriendRequestsListView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }

            pendingFriendRequestsListAdapter = new PendingFriendRequestsListAdapter(UserPendingFriendRequestsActivity.this, _friends);
            pendingFriendRequestsListView.setAdapter(pendingFriendRequestsListAdapter);
            InitializeContextMenuListener();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    private void InitializeListeners(){
        _firebaseRealtimeDatabaseClient.userRepository.setEventListenerForUsers(this);
        PendingFriendRequestsListAdapter.setEventListener(this);
    }

    private void InitializeContextMenuListener(){

        pendingFriendRequestsListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo){
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
                User _friend = _friends.get(info.position);
                contextMenu.setHeaderTitle(_friend.getUsername());
                contextMenu.add(0, 1, 1, "Accept");
                contextMenu.add(0, 2, 2, "Decline");
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        if (item.getItemId() == 1){
            FriendRequest _friendRequest = getFriendRequest(info.position);
            _friendRequest.setStatus(FriendRequestStatus.Accepted.toString());
            acceptFriendRequest(_friendRequest);
            initializeListView();
        }
        else if (item.getItemId() == 2){
            FriendRequest _friendRequest = getFriendRequest(info.position);
            _friendRequest.setStatus(FriendRequestStatus.Declined.toString());
            declineFriendRequest(_friendRequest);
            initializeListView();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onUsersListUpdated() {
        initializeListView();
    }

    @SuppressLint("LongLogTag")
    private void acceptFriendRequest(FriendRequest friendRequest){
        try{
            _firebaseRealtimeDatabaseClient
                    .userRepository
                    .updateFriendRequestStatus(friendRequest);
            Toast.makeText(UserPendingFriendRequestsActivity.this, "Friend request accepted!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(UserPendingFriendRequestsActivity.this, "Ops! Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage());
        }
    }

    @SuppressLint("LongLogTag")
    private void declineFriendRequest(FriendRequest friendRequest){
        try{
            _firebaseRealtimeDatabaseClient
                    .userRepository
                    .updateFriendRequestStatus(friendRequest);
            Toast.makeText(UserPendingFriendRequestsActivity.this, "Friend request declined!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(UserPendingFriendRequestsActivity.this, "Ops! Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage());
        }
    }

    private FriendRequest getFriendRequest(Integer contextMenuItemPosition){
        FriendRequest _pendingFriendRequest = null;
        String friendUserKey = contexMenuItemPositionUserMap.get(contextMenuItemPosition);
        if (!friendUserKey.isEmpty() && friendUserKey != null){
            for (int i = 0; i < _firebaseRealtimeDatabaseClient.userRepository.getCurrentUser().getFriendsRequests().size(); i++){
                if (_firebaseRealtimeDatabaseClient.userRepository.getCurrentUser().getFriendsRequests().get(i).getUserId().equals(friendUserKey)
                    && _firebaseRealtimeDatabaseClient.userRepository.getCurrentUser().getFriendsRequests().get(i).getStatus().equals(FriendRequestStatus.Pending.toString())){
                    _pendingFriendRequest = _firebaseRealtimeDatabaseClient.userRepository.getCurrentUser().getFriendsRequests().get(i);
                    break;
                }
            }
        }
        return _pendingFriendRequest;
    }

    @Override
    public String getInvokerName() {
        return UserPendingFriendRequestsActivity.class.getName();
    }

    @Override
    public void onFriendsImagesDownloaded() {
        progressBar.setVisibility(View.GONE);
        pendingFriendRequestsListView.setVisibility(View.VISIBLE);
    }
}