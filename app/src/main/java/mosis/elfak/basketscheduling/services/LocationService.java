package mosis.elfak.basketscheduling.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.model.LatLng;
import java.util.function.Consumer;

import mosis.elfak.basketscheduling.R;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private LocationListener locationListener;
    private LatLng currentLocation;
    private FirebaseServices _firebaseServices;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // This method is called every time you start a service
        initialize();
        initializeLocationListener();
        requestLocationUpdates();

        final String CHANNELID = "Location Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("This app is tracking your location...")
                .setContentTitle("Location service enabled")
                .setSmallIcon(R.drawable.basketball);
        startForeground(1001, notification.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initialize(){
        _firebaseServices = FirebaseServices.getInstance(LocationService.this);
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, null, this.getMainExecutor(), new Consumer<Location>() {
                @Override
                public void accept(Location location) {
                    if (location != null) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        persistLocationForCurrentUser();
                        Log.i(TAG, "Current location: " + "Latitude - " + currentLocation.latitude + " " + "Longitude - " + currentLocation.longitude);
                    }
                }
            });
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void initializeLocationListener(){
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if ( currentLocation == null || (currentLocation.latitude != location.getLatitude() || currentLocation.longitude != location.getLongitude()))
                {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    persistLocationForCurrentUser();
                    Log.i(TAG, "Current location: " + "Latitude - " + currentLocation.latitude + " " + "Longitude - " + currentLocation.longitude);
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
