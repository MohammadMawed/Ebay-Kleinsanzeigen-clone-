package com.mawed.firebaselogin;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class savedItemAdapter extends RecyclerView.ViewHolder {
    ImageView imageView;
    ImageView profilePic;
    TextView category;
    TextView priceView;
    TextView textView;
    TextView time;
    TextView location;
    TextView username;
    View itemViewView;
    public savedItemAdapter(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image_single_view);
        textView = itemView.findViewById(R.id.title_single_View);
        priceView = itemView.findViewById(R.id.price);
        location = itemView.findViewById(R.id.location);
        time = itemView.findViewById(R.id.time_single_view);
        itemViewView = itemView;
    }
}
