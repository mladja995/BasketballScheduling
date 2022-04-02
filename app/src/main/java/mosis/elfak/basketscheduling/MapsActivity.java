package mosis.elfak.basketscheduling;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.LocationListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import mosis.elfak.basketscheduling.contracts.Constants;
import mosis.elfak.basketscheduling.contracts.User;
import mosis.elfak.basketscheduling.databinding.ActivityMapsBinding;
import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;
import mosis.elfak.basketscheduling.firebase.repository.UserRepository;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, UserRepository.UsersEventListener {

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = "MyPlacesMapsActivity";
    private FirebaseServices _firebaseServices;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private LatLng currentLocation;
    private LocationListener locationListener;
    private boolean showUsers = false;
    private HashMap<Marker, Integer> markerUserIdMap;
    private int state = 0;
    private boolean selCoorsEnabled = false;


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
            processIntent();
            initialize();
            initializeListeners();
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
            if (mMap == null){
                mMap = googleMap;
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
                onMapReady(mMap);
            } else {
                mMap.setMyLocationEnabled(true);
                centerMapOnCurrentLocation();
                if (state == Constants.SELECT_LOCATION){
                    setOnMapClickListener();
                }else {
                    initializeMarkers();
                }
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
        if (state == Constants.SELECT_LOCATION && !selCoorsEnabled){
            menu.add(0, 1, 1, "Select Coordinates");
            menu.add(0, 2, 2, "Cancel");

        }
        else {
            getMenuInflater().inflate(R.menu.menu_maps, menu);
            if (!showUsers) {
                menu.getItem(1).setTitle("Show users");
            } else {
                menu.getItem(1).setTitle("Hide users");
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (state == Constants.SELECT_LOCATION && !selCoorsEnabled){
            if (id == 1){
                selCoorsEnabled = true;
                Toast.makeText(this, "Select coordinates", Toast.LENGTH_SHORT).show();
                item.setEnabled(false);
            }else if (id == 2){
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }
        else {
            if (id == R.id.action_create_event) {
                Intent i = new Intent(this, CreateBasketballEventActivity.class);
                startActivity(i);
            } else if (id == R.id.action_show_hide_users) {
                if (!showUsers) {
                    item.setTitle("Hide users");
                    showUsers = true;
                    showUsersOnMap();
                } else {
                    item.setTitle("Show users");
                    showUsers = false;
                    hideUsersOnMap();
                }
            } else if (id == android.R.id.home) {
                finish();
            }
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

    private void processIntent(){
        Intent mapIntent = getIntent();
        Bundle mapBundle = mapIntent.getExtras();

        if (mapBundle != null){
            state = mapBundle.getInt("state");
        }
    }

    private void initialize(){
        _firebaseServices = FirebaseServices.getInstance(MapsActivity.this);
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
        _firebaseRealtimeDatabaseClient.userRepository.setEventListenerForUsers(MapsActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void initializeListeners(){
        initializeLocationListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void initializeLocationListener(){
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

    private void initializeMarkers(){
        ArrayList<User> users = _firebaseRealtimeDatabaseClient.userRepository.getAllUsers();
        if (markerUserIdMap != null) {
            for (Map.Entry<Marker, Integer> entry : markerUserIdMap.entrySet()) {
                Marker k = entry.getKey();
                k.remove();
            }
        }
        markerUserIdMap = new HashMap<Marker, Integer>((int)((double)users.size()*1.2));
        for (int i = 0; i < users.size(); i++)
        {
            User user = users.get(i);
            String lat = user.getLatitude();
            String lon = user.getLongitude();
            if (!lat.isEmpty() && !lon.isEmpty()) {
                LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(loc);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_basketball_player_30));
                markerOptions.title(user.getUsername());
                if (!showUsers) {
                    markerOptions.visible(false);
                } else {
                    markerOptions.visible(true);
                }
                if (user.getUserId() != _firebaseRealtimeDatabaseClient.userRepository.getCurrentUser().getUserId()) {
                    Marker marker = mMap.addMarker(markerOptions);
                    markerUserIdMap.put(marker, i);
                }
            }
        }
    }

    private void setOnMapClickListener(){
        if (mMap != null){
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    if (state == Constants.SELECT_LOCATION && selCoorsEnabled)
                    {
                        String lat = Double.toString(latLng.latitude);
                        String lon = Double.toString(latLng.longitude);
                        Intent locationIntent = new Intent();
                        locationIntent.putExtra("lat", lat);
                        locationIntent.putExtra("lon", lon);
                        setResult(Constants.RESULT_SELECT_LOCATION, locationIntent);
                        finish();
                    }
                }
            });
        }
    }

    private void persistLocationForCurrentUser(){
        _firebaseRealtimeDatabaseClient
                .userRepository
                .setLocationForCurrentUser(Double.toString(currentLocation.latitude), Double.toString(currentLocation.longitude));
    }

    @Override
    public void onUsersListUpdated() {
        initializeMarkers();
    }

    @Override
    public String getInvokerName() {
        return MapsActivity.class.getName();
    }

    private void showUsersOnMap(){
        for (Map.Entry<Marker, Integer> entry : markerUserIdMap.entrySet()) {
            Marker k = entry.getKey();
            k.setVisible(true);
        }
    }

    private void hideUsersOnMap(){
        for (Map.Entry<Marker, Integer> entry : markerUserIdMap.entrySet()) {
            Marker k = entry.getKey();
            k.setVisible(false);
        }
    }
}