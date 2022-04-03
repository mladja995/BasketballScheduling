package mosis.elfak.basketscheduling;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import mosis.elfak.basketscheduling.contracts.BasketballEvent;


public class EventListAdapter extends ArrayAdapter<BasketballEvent> {

    private static final String TAG = "EventListAdapter";
    private static EventsImagesEventListener _listener;

    public EventListAdapter(Context context, ArrayList<BasketballEvent> eventsArrayList){
        super(context, R.layout.list_view_item1, eventsArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        try {
            BasketballEvent event = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item1, parent, false);
            }

            ImageView imageView = convertView.findViewById(R.id.list_view_item_event_image);
            TextView eventDescription = convertView.findViewById(R.id.list_view_item_event_description);
            TextView beginsAt = convertView.findViewById(R.id.list_view_item_begins_at);
            TextView endsOn = convertView.findViewById(R.id.list_view_item_ends_on);
            TextView maxNumOfPlayers = convertView.findViewById(R.id.list_view_item_max_num_of_players);
            TextView currentNumOfPlayers = convertView.findViewById(R.id.list_view_item_current_num_of_players);


            Picasso.with(getContext())
                    .load(event.getImageURL())
                    .fit()
                    .into(imageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    if (_listener != null) {
                        _listener.onEventsImagesDownloaded();
                    }
                }

                @Override
                public void onError() {

                }
            });

            eventDescription.setText(event.getEventDescription());
            beginsAt.setText(event.getBeginsAt());
            endsOn.setText(event.getEndsOn());
            maxNumOfPlayers.setText(Integer.toString(event.getMaxNumOfPlayers()));
            currentNumOfPlayers.setText(Integer.toString(event.getCurrentNumOfPlayers()));

            return convertView;
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
            return convertView;
        }
    }

    public interface EventsImagesEventListener {
        void onEventsImagesDownloaded();
    }

    public static void setEventListener(EventsImagesEventListener listener){
        _listener = listener;
    }
}
