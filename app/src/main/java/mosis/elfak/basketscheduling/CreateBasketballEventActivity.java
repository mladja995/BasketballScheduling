package mosis.elfak.basketscheduling;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mosis.elfak.basketscheduling.contracts.BasketballEvent;
import mosis.elfak.basketscheduling.contracts.Constants;
import mosis.elfak.basketscheduling.databinding.ActivityCreateBasketballEventBinding;
import mosis.elfak.basketscheduling.databinding.ActivityMainBinding;
import mosis.elfak.basketscheduling.firebase.FirebaseAuthClient;
import mosis.elfak.basketscheduling.firebase.FirebaseRealtimeDatabaseClient;
import mosis.elfak.basketscheduling.firebase.FirebaseServices;
import mosis.elfak.basketscheduling.firebase.FirebaseStorageClient;
import mosis.elfak.basketscheduling.firebase.repository.BasketballEventRepository;

public class CreateBasketballEventActivity extends AppCompatActivity implements
        FirebaseStorageClient.ImageEventListener,
        BasketballEventRepository.UserBasketballEventListener{

    private static final String TAG = "CreateBasketballEvent";
    private ActivityCreateBasketballEventBinding binding;
    private FirebaseServices _firebaseServices;
    private FirebaseAuthClient _firebaseAuthClient;
    private FirebaseRealtimeDatabaseClient _firebaseRealtimeDatabaseClient;
    private FirebaseStorageClient _firebaseStorageClient;
    private ActivityResultLauncher<String> activityResultLauncher;
    private String eventId;
    private String beginsAt;
    private String endsOn;
    private String maxNumOfPlayer;
    private String currentNumOfPlayers;
    private String eventDescription;
    private String imageURL;
    private Uri imageURI;
    private String latitude;
    private String longitude;
    private ProgressBar progressBar;
    private Button createEventBtn;
    private Button useMyLocationBtn;
    private Button pickLocationBtn;
    private ImageView eventImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            binding = ActivityCreateBasketballEventBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setSupportActionBar(binding.toolbarCreateBasketballEvent);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            initialize();
            initializeListeners();
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        try
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_create_basketball_event, menu);
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
    public void create_event_btn_onClick(View view){
        try
        {
            if (validateUserInput()) {
                createEventBtn.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                uploadEventImage();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            progressBar.setVisibility(View.GONE);
            createEventBtn.setEnabled(true);
        }

    }

    public void eventImage_onClick(View view){
        activityResultLauncher.launch("image/*");
    }

    private void initialize(){
        _firebaseServices = FirebaseServices.getInstance(CreateBasketballEventActivity.this);
        _firebaseAuthClient = _firebaseServices.firebaseAuthClient;
        _firebaseRealtimeDatabaseClient = _firebaseServices.firebaseRealtimeDatabaseClient;
        _firebaseStorageClient = _firebaseServices.firebaseStorageClient;
        progressBar = findViewById(R.id.progressBar_create_event);
        createEventBtn = findViewById(R.id.button_create_basketball_event_create_event);
        createEventBtn.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        eventImage = findViewById(R.id.imageView_create_basketball_event_image);
    }

    private void initializeListeners()
    {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                eventImage.setImageURI(result);
                eventImage.setScaleType(ImageView.ScaleType.FIT_XY);
                imageURI = result;
            }
        });
    }

    private void uploadEventImage() throws FileNotFoundException {
        eventImage.setDrawingCacheEnabled(true);
        eventImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) eventImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        eventId = _firebaseRealtimeDatabaseClient.getKey();
        _firebaseStorageClient
                .uploadEventImage(eventId, data, CreateBasketballEventActivity.class.getName())
                .setEventListener(CreateBasketballEventActivity.this);
    }

    private boolean validateUserInput(){
        EditText etBeginsAt = findViewById(R.id.editText_create_basketball_event_begins_at);
        EditText etEndsOn = findViewById(R.id.editText_create_basketball_event_ends_on);
        EditText etMaxNumOfPlayers = findViewById(R.id.editText_create_basketball_event_max_num_of_players);
        EditText etCurrentNumOfPlayers = findViewById(R.id.editText_create_basketball_event_current_num_of_players);
        EditText etEventDescription = findViewById(R.id.editText_create_basketball_event_description);
        beginsAt = etBeginsAt.getText().toString();
        endsOn = etEndsOn.getText().toString();
        maxNumOfPlayer = etMaxNumOfPlayers.getText().toString();
        currentNumOfPlayers = etCurrentNumOfPlayers.getText().toString();
        eventDescription = etEventDescription.getText().toString();

        if (beginsAt.isEmpty() || endsOn.isEmpty() || maxNumOfPlayer.isEmpty()
                || currentNumOfPlayers.isEmpty() || eventDescription.isEmpty()
                || latitude == null || longitude == null || imageURI == null){
            Toast.makeText(CreateBasketballEventActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            createEventBtn.setEnabled(true);
            return false;
        }else{
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onUploadImageSuccess(String imageURL) {
        try {
            this.imageURL = imageURL;
            BasketballEvent _event = new BasketballEvent(
                    LocalDateTime.parse(beginsAt),
                    LocalDateTime.parse(endsOn),
                    _firebaseRealtimeDatabaseClient.userRepository.getCurrentUser().getUserId(),
                    Integer.parseInt(maxNumOfPlayer),
                    Integer.parseInt(currentNumOfPlayers),
                    eventDescription,
                    latitude,
                    longitude,
                    imageURL
            );

            _firebaseRealtimeDatabaseClient
                    .basketballEventRepository
                    .setEventListenerForUserBasketballEvent(CreateBasketballEventActivity.this)
                    .createNewBasketballEvent(_event, CreateBasketballEventActivity.class.getName());
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
            progressBar.setVisibility(View.GONE);
            createEventBtn.setEnabled(true);
        }
    }

    @Override
    public void onUploadImageFailure() {
        Toast.makeText(CreateBasketballEventActivity.this, "Ops! Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        createEventBtn.setEnabled(true);
    }

    @Override
    public void onUserBasketballEventsFetchedSuccess(ArrayList<BasketballEvent> userBasketballEvents) {

    }

    @Override
    public void onUserBasketballEventsFetchedFailure(ArrayList<BasketballEvent> userBasketballEvents) {

    }

    @Override
    public void onUserBasketballEventCreatedSuccess(ArrayList<BasketballEvent> userBasketballEvents) {
        progressBar.setVisibility(View.GONE);
        createEventBtn.setEnabled(true);
        Toast.makeText(CreateBasketballEventActivity.this, "Basketball event created successfully!", Toast.LENGTH_SHORT).show();
        _firebaseRealtimeDatabaseClient
                .userRepository
                .addPointsToCurrentUser(5);
        finish();
    }

    @Override
    public void onUserBasketballEventCreatedFailure(ArrayList<BasketballEvent> userBasketballEvents) {
        Toast.makeText(CreateBasketballEventActivity.this, "Ops! Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        createEventBtn.setEnabled(true);
    }

    @Override
    public String getInvokerName() {
        return CreateBasketballEventActivity.class.getName();
    }
}