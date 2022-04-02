package mosis.elfak.basketscheduling;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import mosis.elfak.basketscheduling.contracts.BasketballEvent;
import mosis.elfak.basketscheduling.contracts.Constants;
import mosis.elfak.basketscheduling.databinding.ActivityMainBinding;
import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;
import mosis.elfak.basketscheduling.firebase.repository.BasketballEventRepository;

public class MainActivity extends AppCompatActivity implements
        BasketballEventRepository.BasketballEventListener,
        EventListAdapter.EventsImagesEventListener {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private FirebaseServices _firebaseServices;
    private FirebaseAuthClient _firebaseAuthClient;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private ListView eventsListView;
    private EventListAdapter eventListAdapter;
    private ProgressBar progressBar;
    private CheckBox checkBox;
    private ArrayList<BasketballEvent> events;

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        //initializeListView();
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
            return super.onOptionsItemSelected(item);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private void initialize(){
        ArrayList<BasketballEvent> events = new ArrayList<BasketballEvent>();
        _firebaseServices = FirebaseServices.getInstance(MainActivity.this);
        _firebaseAuthClient = _firebaseServices.firebaseAuthClient;
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
        progressBar = findViewById(R.id.progressBar_main);
        checkBox = findViewById(R.id.checkBox_main_show_only_your_events);
        progressBar.setVisibility(View.GONE);
        checkBox.setChecked(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeListView()
    {
        try
        {
            ArrayList<BasketballEvent> _userEvents = new ArrayList<BasketballEvent>();
            events = _firebaseRealtimeDatabaseClient
                    .basketballEventRepository
                    .getAllBasketballEvents();

            if (checkBox.isChecked()) {
                for (int i = 0; i < events.size(); i++){
                    BasketballEvent _event = _firebaseRealtimeDatabaseClient.basketballEventRepository.getEvent(i);
                    if (_firebaseAuthClient.getAutheticatedUserId().equals(_event.getCreatedBy())){
                        _userEvents.add(_event);
                    }
                }
                events = _userEvents;
            }


            eventsListView = (ListView) findViewById(R.id.main_events_list);
            if (events.size() > 1) {
                eventsListView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
            eventListAdapter = new EventListAdapter(MainActivity.this, events);
            eventsListView.setAdapter(eventListAdapter);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }

    private void InitializeListeners(){
        _firebaseRealtimeDatabaseClient.basketballEventRepository.setEventListenerForBasketballEvent(this);
        EventListAdapter.setEventListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void checkBox_onClick(View view){
        initializeListView();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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