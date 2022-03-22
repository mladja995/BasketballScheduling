package mosis.elfak.basketscheduling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import mosis.elfak.basketscheduling.contracts.Constants;
import mosis.elfak.basketscheduling.databinding.ActivityMainBinding;
import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private FirebaseServices _firebaseServices;
    private FirebaseAuthClient _firebaseAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setSupportActionBar(binding.toolbarMain);
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
        _firebaseServices = FirebaseServices.getInstance(MainActivity.this);
        _firebaseAuthClient = _firebaseServices.firebaseAuthClient;
    }
}