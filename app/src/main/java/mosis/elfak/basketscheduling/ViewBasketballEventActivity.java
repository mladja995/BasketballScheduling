package mosis.elfak.basketscheduling;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mosis.elfak.basketscheduling.contracts.BasketballEvent;
import mosis.elfak.basketscheduling.contracts.Constants;
import mosis.elfak.basketscheduling.databinding.ActivityViewBasketballEventBinding;
import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;

public class ViewBasketballEventActivity extends AppCompatActivity {

    private static final String TAG = "ViewEventActivity";
    private ActivityViewBasketballEventBinding binding;
    private FirebaseServices _firebaseServices;
    private FirebaseAuthClient _firebaseAuthClient;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private BasketballEvent _event;
    private boolean isCurrentUserEvent = false;
    private String _eventKey;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            binding = ActivityViewBasketballEventBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setSupportActionBar(binding.toolbarViewBasketballEvent);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            initialize();
            InitializeListeners();
            processIntent();
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
            getMenuInflater().inflate(R.menu.menu_view_basketball_event, menu);
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
        _firebaseServices = FirebaseServices.getInstance(ViewBasketballEventActivity.this);
        _firebaseAuthClient = _firebaseServices.firebaseAuthClient;
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
        progressBar = findViewById(R.id.progressBar_view_basketball_event);
        progressBar.setVisibility(View.GONE);
    }

    private void InitializeListeners(){
    }

    private void processIntent()
    {
        try{
            Intent intent = getIntent();
            Bundle positionBundle = intent.getExtras();
            _eventKey = positionBundle.getString("eventKey");
            isCurrentUserEvent = positionBundle.getBoolean("isCurrentUserEvent");
            initializeEvent(_eventKey);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    @SuppressLint("ResourceAsColor")
    private void initializeEvent(String key){
        if (key.isEmpty()){
            finish();
        }
        _event = _firebaseRealtimeDatabaseClient.basketballEventRepository.getEvent(key);

        ImageView ivEventImage = findViewById(R.id.imageView_view_basketball_event_image);
        TextView tvEventDescription = findViewById(R.id.textView_view_basketball_event_description);
        TextView tvBeginsAt = findViewById(R.id.textView_view_basketball_event_beginsAt_value);
        TextView tvEndsOn = findViewById(R.id.textView_view_basketball_event_endsOn_value);
        TextView tvMaxNumOfPlayers = findViewById(R.id.textView_view_basketball_event_max_num_of_players_value);
        TextView tvCurrNumOfPlayers = findViewById(R.id.textView_view_basketball_event_curr_num_of_players_value);
        TextView tvEventCreatedOn = findViewById(R.id.textView_view_basketball_event_createdOn_value);
        Button btnShowOnMap = findViewById(R.id.button_view_basketball_event_show_on_map);
        Button btnJoinEvent = findViewById(R.id.button_view_basketball_event_join_event);

        ivEventImage.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Picasso.with(this)
                .load(_event.getImageURL())
                .fit()
                .into(ivEventImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "onImageLoad: success");
                        ivEventImage.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError() {
                        Log.e(TAG, "onImageLoad: failure");
                    }
                });
        tvEventDescription.setText(_event.getEventDescription());
        tvBeginsAt.setText(_event.getBeginsAt());
        tvEndsOn.setText(_event.getEndsOn());
        tvMaxNumOfPlayers.setText(Integer.toString(_event.getMaxNumOfPlayers()));
        tvCurrNumOfPlayers.setText(Integer.toString(_event.getCurrentNumOfPlayers()));
        tvEventCreatedOn.setText(_event.getCreatedAt());

        if (isUserAlreadyJoinToEvent()){
            btnJoinEvent.setBackgroundColor(R.color.light_gray);
        }
    }

    public void show_on_map_onClick(View view){
        // TODO: Implement
    }

    public void join_event_onClick(View view){
        if (isUserAlreadyJoinToEvent()){
            Toast.makeText(this, "You've already join to this event!", Toast.LENGTH_SHORT).show();
        }
        else{
            if (_event.getMaxNumOfPlayers() <= _event.getCurrentNumOfPlayers()){
                Toast.makeText(this, "Sorry, you can't join. Max number of players reached!", Toast.LENGTH_SHORT).show();
            }
            else {
                _firebaseRealtimeDatabaseClient
                        .basketballEventRepository
                        .addUserToEvent(_event);
                Toast.makeText(this, "Great! You've just joined to event!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isUserAlreadyJoinToEvent(){
        boolean ret = false;
        if (_firebaseAuthClient.getAutheticatedUserId().equals(_event.getCreatedBy())){
            ret = true;
            return ret;
        }

        for (int i = 0; i < _event.getJoinedUsers().size(); i++){
            if (_firebaseAuthClient.getAutheticatedUserId().equals(_event.getJoinedUsers().get(i))){
                ret = true;
            }
        }

        return ret;
    }
}