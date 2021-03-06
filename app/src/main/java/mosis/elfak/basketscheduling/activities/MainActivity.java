package mosis.elfak.basketscheduling.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import mosis.elfak.basketscheduling.R;
import mosis.elfak.basketscheduling.contracts.BasketballEvent;
import mosis.elfak.basketscheduling.contracts.Constants;
import mosis.elfak.basketscheduling.databinding.ActivityMainBinding;
import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;
import mosis.elfak.basketscheduling.firebase.repository.BasketballEventRepository;
import mosis.elfak.basketscheduling.internals.EventListAdapter;
import mosis.elfak.basketscheduling.internals.EventsFilter;

public class MainActivity extends AppCompatActivity implements
        BasketballEventRepository.BasketballEventListener,
        EventListAdapter.EventsImagesEventListener {

    private static final String TAG = "MainActivity";
    ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityMainBinding binding;
    private FirebaseServices _firebaseServices;
    private FirebaseAuthClient _firebaseAuthClient;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private ListView eventsListView;
    private EventListAdapter eventListAdapter;
    private ProgressBar progressBar;
    private CheckBox checkBox;
    private ArrayList<BasketballEvent> events;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setSupportActionBar(binding.toolbarMain);
            initialize();
            InitializeListeners();
            initializeListView();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        initializeListView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        try
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
            else if (id == R.id.action_ranking)
            {
                Intent i = new Intent(this, RankingsActivity.class);
                startActivity(i);
            }
            else if (id == R.id.action_singOut)
            {
                _firebaseAuthClient.signOut();
                _firebaseRealtimeDatabaseClient.userRepository.InvalidateCurrentUser();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
            }
            else if (id == R.id.action_create_event)
            {
                Intent i = new Intent(this, CreateBasketballEventActivity.class);
                startActivity(i);
            }
            else if (id == R.id.action_friend_requests)
            {
                Intent i = new Intent(this, UserPendingFriendRequestsActivity.class);
                startActivity(i);
            }
            else if (id == R.id.action_filter)
            {
                Intent i = new Intent(this, FilterActivity.class);
                startActivity(i);
            }
            else if (id == R.id.action_services)
            {
                Intent i = new Intent(this, ServicesActivity.class);
                startActivity(i);
            }
            else if (id == R.id.action_profile)
            {
                Intent i = new Intent(this, ViewUserProfileActivity.class);
                Bundle positionBundle = new Bundle();
                int position = _firebaseRealtimeDatabaseClient.userRepository.getUserIndex(_firebaseAuthClient.getAutheticatedUserId());
                positionBundle.putInt("position", position);
                i.putExtras(positionBundle);
                startActivity(i);
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
        events = new ArrayList<BasketballEvent>();
        _firebaseServices = FirebaseServices.getInstance(MainActivity.this);
        _firebaseAuthClient = _firebaseServices.firebaseAuthClient;
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
        eventsListView = (ListView) findViewById(R.id.main_events_list);
        progressBar = findViewById(R.id.progressBar_main);
        checkBox = findViewById(R.id.checkBox_main_show_only_your_events);
        progressBar.setVisibility(View.GONE);
        checkBox.setChecked(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeListView()
    {
        try
        {
            ArrayList<BasketballEvent> _filteredEvents = EventsFilter.getInstance().getFilteredEvents();;
            events = _firebaseRealtimeDatabaseClient
                    .basketballEventRepository
                    .getAllBasketballEvents();

            if (!checkBox.isChecked()){
                ArrayList<BasketballEvent> _notUserEvents = new ArrayList<BasketballEvent>();
                if (!EventsFilter.getInstance().isFilterActive()) {
                    for (int i = 0; i < events.size(); i++) {
                        BasketballEvent _event = _firebaseRealtimeDatabaseClient.basketballEventRepository.getEvent(i);
                        if (!_firebaseAuthClient.getAutheticatedUserId().equals(_event.getCreatedBy())) {
                            _notUserEvents.add(_event);
                        }
                    }
                }else{
                    for (int i = 0; i < _filteredEvents.size(); i++) {
                        BasketballEvent _event = _filteredEvents.get(i);
                        if (!_firebaseAuthClient.getAutheticatedUserId().equals(_event.getCreatedBy())) {
                            _notUserEvents.add(_event);
                        }
                    }
                }
                events = _notUserEvents;
            }

            if (checkBox.isChecked()) {
                ArrayList<BasketballEvent> _userEvents = new ArrayList<BasketballEvent>();
                for (int i = 0; i < events.size(); i++){
                    BasketballEvent _event = _firebaseRealtimeDatabaseClient.basketballEventRepository.getEvent(i);
                    if (_firebaseAuthClient.getAutheticatedUserId().equals(_event.getCreatedBy())){
                        _userEvents.add(_event);
                    }
                }
                events = _userEvents;
            }

            if (events.size() > 1) {
                eventsListView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
            eventListAdapter = new EventListAdapter(MainActivity.this, events);
            eventsListView.setAdapter(eventListAdapter);
            InitializeContextMenuListener();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    private void InitializeListeners(){
        _firebaseRealtimeDatabaseClient.basketballEventRepository.setEventListenerForBasketballEvent(this);
        EventListAdapter.setEventListener(this);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Constants.RESULT_JOIN_EVENT)
                    {
                        Toast.makeText(MainActivity.this, "Great! You've just joined to event!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void InitializeContextMenuListener(){

        eventsListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo){
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
                BasketballEvent _event = events.get(info.position);
                contextMenu.setHeaderTitle(_event.getEventDescription());
                contextMenu.add(0, 1, 1, "View event");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Bundle positionBundle = new Bundle();
        if (!EventsFilter.getInstance().isFilterActive()) {
            positionBundle.putString("eventKey", events.get(info.position).getEventId());
        }else{
            positionBundle.putString("eventKey", EventsFilter.getInstance().getFilteredEvents().get(info.position).getEventId());
        }
        if (checkBox.isChecked()){
            positionBundle.putBoolean("isCurrentUserEvent", true);
        }else{
            positionBundle.putBoolean("isCurrentUserEvent", false);
        }
        Intent i = null;

        if (item.getItemId() == 1){
            i = new Intent(this, ViewBasketballEventActivity.class);
            i.putExtras(positionBundle);
            activityResultLauncher.launch(i);
        }

        return super.onContextItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkBox_onClick(View view){
        initializeListView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBasketballEventsListUpdated() {
        initializeListView();
    }

    @Override
    public String getInvokerName() {
        return MainActivity.class.getName();
    }

    @Override
    public void onEventsImagesDownloaded() {
        try {
            progressBar.setVisibility(View.GONE);
            eventsListView.setVisibility(View.VISIBLE);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }
}