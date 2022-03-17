package mosis.elfak.basketscheduling;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import mosis.elfak.basketscheduling.firebase.FirebaseServices;
import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;

public class MainActivity extends AppCompatActivity implements FirebaseAuthClient.UserEventListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String email = "mladen.mladenovic95@yahoo.com";
        String password = "mladen1995";

        FirebaseServices.getInstance(MainActivity.this)
                .firebaseAuthClient
                .signInWithEmailAndPassword(email, password, MainActivity.class.getName())
                .setEventListener(MainActivity.this);
    }

    @Override
    public void onUserSignUpSuccess() {
        Toast.makeText(this, "User created!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserSignUpFailure() {
        Toast.makeText(this, "User not created!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserSignInSuccess() {
        Toast.makeText(this, "User logged in!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserSignInFailure() {
        Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getInvokerName() {
        return MainActivity.class.getName();
    }
}