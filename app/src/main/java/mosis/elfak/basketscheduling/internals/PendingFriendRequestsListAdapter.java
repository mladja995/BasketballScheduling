package mosis.elfak.basketscheduling.internals;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mosis.elfak.basketscheduling.R;
import mosis.elfak.basketscheduling.contracts.FriendRequestStatus;
import mosis.elfak.basketscheduling.contracts.User;

public class PendingFriendRequestsListAdapter extends ArrayAdapter<User> {

    private static final String TAG = "PendingFriendRequestsListAdapter";
    private static PendingFriendRequestsListAdapter.FriendsImagesEventListener _listener;

    public PendingFriendRequestsListAdapter(Context context, ArrayList<User> userArrayList){
        super(context, R.layout.list_view_item, userArrayList);
    }

    @SuppressLint("LongLogTag")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        try {
            User user = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item2, parent, false);
            }

            ImageView imageView = convertView.findViewById(R.id.list_view_item_friend_image);
            TextView username = convertView.findViewById(R.id.list_view_item_friend_username);
            TextView status = convertView.findViewById(R.id.list_view_item_friend_status);

            Picasso.with(getContext()).load(user.getImageURL()).fit().into(imageView, new com.squareup.picasso.Callback() {

                @Override
                public void onSuccess() {
                    _listener.onFriendsImagesDownloaded();
                }

                @Override
                public void onError() {

                }
            });

            username.setText(user.getUsername());
            status.setText(FriendRequestStatus.Pending.toString());

            return convertView;
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
            return convertView;
        }
    }

    public interface FriendsImagesEventListener {
        void onFriendsImagesDownloaded();
    }

    public static void setEventListener(PendingFriendRequestsListAdapter.FriendsImagesEventListener listener){
        _listener = listener;
    }
}
