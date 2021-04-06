package com.example.i_explore;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holder extends RecyclerView.ViewHolder {
    TextView textViewActivity,textViewDate,textViewTime,textViewLocation,textViewReporter;
    ImageButton imageButton;
    public Holder(@NonNull View itemView) {
        super(itemView);
        textViewActivity=(TextView)itemView.findViewById(R.id.activityName);
        textViewDate=(TextView)itemView.findViewById(R.id.dateName);
        textViewLocation=(TextView)itemView.findViewById(R.id.locationName);
        textViewTime=(TextView)itemView.findViewById(R.id.timeName);
        textViewReporter=(TextView)itemView.findViewById(R.id.reporterName);
        imageButton=(ImageButton)itemView.findViewById(R.id.viewbutton);

    }
}
