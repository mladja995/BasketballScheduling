package mosis.elfak.basketscheduling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import mosis.elfak.basketscheduling.firebase.FirebaseServices;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            FirebaseServices fb = FirebaseServices.getInstance();
        }catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }
}