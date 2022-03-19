package mosis.elfak.basketscheduling;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.location.LocationListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.function.Consumer;

import mosis.elfak.basketscheduling.contracts.Constants;
import mosis.elfak.basketscheduling.databinding.ActivityMapsBinding;
import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = "MyPlacesMapsActivity";
    private FirebaseServices _firebaseServices;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private LatLng currentLocation;
    private LocationListener locationListener;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            binding = ActivityMapsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setSupportActionBar(binding.toolbarMaps);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            initialize();
            initializeLocationListener();
        }
        catch (Exception e)
        {
            Log.e(TAG, "onCreate: ", e);
            Toast.makeText(MapsActivity.this, "Ops! Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
            } else {
                mMap.setMyLocationEnabled(true);
                centerMapOnCurrentLocation();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "onMapReady: ", e);
            Toast.makeText(MapsActivity.this, "Ops! Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_create_event){

        }
        else if (id == android.R.id.home)
        {
           finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void centerMapOnCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        }
        else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, null, this.getMainExecutor(), new Consumer<Location>() {
                @Override
                public void accept(Location location) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    persistLocationForCurrentUser();
                }
            });
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
    }

    private void initialize(){
        _firebaseServices = FirebaseServices.getInstance(MapsActivity.this);
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void initializeLocationListener(){
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (currentLocation.latitude != location.getLatitude() || currentLocation.longitude != location.getLongitude())
                {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    centerMapOnCurrentLocation();
                }
            }
        };
    }

    private void persistLocationForCurrentUser(){
        _firebaseRealtimeDatabaseClient
                .userRepository
                .setLocationForCurrentUser(Double.toString(currentLocation.latitude), Double.toString(currentLocation.longitude));
    }
}