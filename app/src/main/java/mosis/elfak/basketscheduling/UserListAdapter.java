package mosis.elfak.basketscheduling;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import mosis.elfak.basketscheduling.contracts.User;

public class UserListAdapter extends ArrayAdapter<User> {

    private static UsersImagesEventListener _listener;
    private ListView usersListView;

    public UserListAdapter(Context context, ArrayList<User> userArrayList){
        super(context, R.layout.list_view_item, userArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        User user = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.list_view_item_user_image);
        TextView username = convertView.findViewById(R.id.list_view_item_username);
        TextView fullname = convertView.findViewById(R.id.list_view_item_fullname);
        TextView points = convertView.findViewById(R.id.list_view_item_points);

        Picasso.with(getContext()).load(user.getImageURL()).fit().into(imageView, new com.squareup.picasso.Callback(){

            @Override
            public void onSuccess() {
                _listener.onUsersImagesDownloaded();
            }

            @Override
            public void onError() {

            }
        });

        username.setText(user.getUsername());
        fullname.setText(user.getFirstname() + " " + user.getLastname());
        points.setText(String.valueOf(user.getPoints()));

        return convertView;
    }

    public interface UsersImagesEventListener {
        void onUsersImagesDownloaded();
    }

    public static void setEventListener(UsersImagesEventListener listener){
        _listener = listener;
    }
}
