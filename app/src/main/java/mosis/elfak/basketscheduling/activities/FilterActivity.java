package mosis.elfak.basketscheduling.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import mosis.elfak.basketscheduling.R;
import mosis.elfak.basketscheduling.contracts.BasketballEvent;
import mosis.elfak.basketscheduling.contracts.User;
import mosis.elfak.basketscheduling.databinding.ActivityFilterBinding;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;
import mosis.elfak.basketscheduling.firebase.repository.BasketballEventRepository;
import mosis.elfak.basketscheduling.internals.EventsFilter;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FilterActivity extends AppCompatActivity
        implements BasketballEventRepository.BasketballEventListener {

    private static final String TAG = "FilterActivity";
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private ActivityFilterBinding binding;
    private FirebaseServices _firebaseServices;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private EditText etCreatedOn;
    private EditText etBeginsAt;
    private EditText etEndsOn;
    private EditText etMaxNumOfPlayers;
    private EditText etCurrNumOfPlayers;
    private EditText etRadius;
    private Button btnApplyClearFilter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            binding = ActivityFilterBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setSupportActionBar(binding.toolbarFilter);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            initialize();
            initializeListeners();
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        try
        {
            super.onStart();
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
            initialize();
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
            getMenuInflater().inflate(R.menu.menu_filter, menu);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initialize(){
        etCreatedOn = findViewById(R.id.editTextDate_filter_createdOn);
        etBeginsAt = findViewById(R.id.editTextDate_filter_beginsAt);
        etEndsOn = findViewById(R.id.editTextDate_filter_endsOn);
        etMaxNumOfPlayers = findViewById(R.id.editTextDate_filter_maxNumOfPlayers);
        etCurrNumOfPlayers = findViewById(R.id.editTextDate_filter_currNumOfPlayers);
        etRadius = findViewById(R.id.editTextDate_filter_radius);
        btnApplyClearFilter = findViewById(R.id.button_filter_applyClearFilter);
        _firebaseServices = FirebaseServices.getInstance(FilterActivity.this);
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
        if (EventsFilter.getInstance().isFilterActive()){
            btnApplyClearFilter.setText("Clear filter");
            if (EventsFilter.getInstance().get_createdOn() != null){
                etCreatedOn.setText(EventsFilter.getInstance().get_createdOn().toString());
            }
            if (EventsFilter.getInstance().get_beginsAt() != null){
                etBeginsAt.setText(EventsFilter.getInstance().get_beginsAt().toString());
            }
            if (EventsFilter.getInstance().get_endsOn() != null){
                etEndsOn.setText(EventsFilter.getInstance().get_endsOn().toString());
            }
            if (EventsFilter.getInstance().get_maxNumOfPlayers() != -1){
                etMaxNumOfPlayers.setText(Integer.toString(EventsFilter.getInstance().get_maxNumOfPlayers()));
            }
            if (EventsFilter.getInstance().get_currNumOfPlayers() != -1){
                etCurrNumOfPlayers.setText(Integer.toString(EventsFilter.getInstance().get_currNumOfPlayers()));
            }
            if (EventsFilter.getInstance().get_radius() != -1){
                etRadius.setText(Double.toString(EventsFilter.getInstance().get_radius() / 1000));
            }
        } else{
            btnApplyClearFilter.setText("Apply filter");
            etCreatedOn.getText().clear();
            etBeginsAt.getText().clear();
            etEndsOn.getText().clear();
            etMaxNumOfPlayers.getText().clear();
            etCurrNumOfPlayers.getText().clear();
            etRadius.getText().clear();
        }
    }

    private void initializeListeners(){
        _firebaseRealtimeDatabaseClient
                .basketballEventRepository
                .setEventListenerForBasketballEvent(this);
    }

    private void applyFilter(){
        if (validateInput()){
            User currentUser = _firebaseRealtimeDatabaseClient.userRepository.getCurrentUser();
            ArrayList<BasketballEvent> _events = _firebaseRealtimeDatabaseClient.basketballEventRepository.getAllBasketballEvents();
            EventsFilter.getInstance()
                    .setListForFiltering(_events)
                    .filterByCreatedOn(etCreatedOn.getText().toString().isEmpty() ? null : LocalDate.parse(etCreatedOn.getText().toString(), dateFormatter))
                    .filterByBeginsAt(etBeginsAt.getText().toString().isEmpty() ? null : LocalDateTime.parse(etBeginsAt.getText().toString(), dateTimeFormatter))
                    .filterByEndsOn(etEndsOn.getText().toString().isEmpty() ? null : LocalDateTime.parse(etEndsOn.getText().toString(), dateTimeFormatter))
                    .filterByMaxNumOfPlayers(etMaxNumOfPlayers.getText().toString())
                    .filterByCurNumOfPlayers(etCurrNumOfPlayers.getText().toString())
                    .filterByRadius(Double.parseDouble(currentUser.getLatitude()), Double.parseDouble(currentUser.getLongitude()), etRadius.getText().toString());
            ArrayList<BasketballEvent> _filteredEvents = EventsFilter.getInstance().getFilteredEvents();
            Toast.makeText(this, "Filter applied!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void clearFilter(){
        EventsFilter.getInstance().clearFilter();
        initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void applyClearFilter_btn_onClick(View view){
        try{
            if (EventsFilter.getInstance().isFilterActive()){
                clearFilter();
            }else{
                applyFilter();
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(this, "Ops! Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(){
        if (etCreatedOn.getText().toString().isEmpty() && etBeginsAt.getText().toString().isEmpty()
            && etEndsOn.getText().toString().isEmpty() && etMaxNumOfPlayers.getText().toString().isEmpty()
            && etCurrNumOfPlayers.getText().toString().isEmpty() && etRadius.getText().toString().isEmpty()){
            Toast.makeText(this, "You must enter at least one filter!", Toast.LENGTH_SHORT).show();
            return  false;
        }
        if (!etCreatedOn.getText().toString().isEmpty()){
            try{
                LocalDate _date = LocalDate.parse(etCreatedOn.getText().toString(), dateFormatter);
            }catch (Exception e){
                Toast.makeText(this, "Date is in wrong format!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (!etBeginsAt.getText().toString().isEmpty()){
            try{
                LocalDateTime _date = LocalDateTime.parse(etBeginsAt.getText().toString(), dateTimeFormatter);
            }catch (Exception e){
                Toast.makeText(this, "Date is in wrong format!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (!etEndsOn.getText().toString().isEmpty()){
            try{
                LocalDateTime _date = LocalDateTime.parse(etEndsOn.getText().toString(), dateTimeFormatter);
            }catch (Exception e){
                Toast.makeText(this, "Date is in wrong format!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBasketballEventsListUpdated() {

    }

    @Override
    public String getInvokerName() {
        return FilterActivity.class.getName();
    }
}