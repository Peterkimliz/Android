package com.example.i_explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.viewholder> implements Filterable {
    private ArrayList<User> list;
    private ArrayList<User> fulllist;
    private Context context;

    public Adapter(ArrayList<User>list, Context context){
        this.list=list;
        fulllist= new ArrayList<>(list);
    }

    @NonNull
    @Override
    public Adapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.viewholder holder, int position) {
        User user = list.get(position);
        holder.textViewActivity.setText(user.getActivityName());
        holder.textViewDate.setText(user.getDate());
        holder.textViewLocation.setText(user.getLocation());
        holder.textViewTime.setText(user.getTime());
        holder.textViewReporter.setText(user.getReporter());

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }
    public Filter search_filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<User> filtered_list = new ArrayList<>();
            if (constraint==null || constraint.length()==0){
                filtered_list.addAll(fulllist);
            }else{
                String filter_pattern = constraint.toString().toLowerCase().trim();
                for(User item:fulllist){
                    if(item.getActivityName().toUpperCase().contains(filter_pattern)){
                        filtered_list.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filtered_list;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            

        }
    };
    public class viewholder extends RecyclerView.ViewHolder{
        TextView textViewActivity,textViewDate,textViewTime,textViewLocation,textViewReporter;
        ImageButton imageButton;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            textViewActivity=(TextView)itemView.findViewById(R.id.activityName);
            textViewDate=(TextView)itemView.findViewById(R.id.dateName);
            textViewLocation=(TextView)itemView.findViewById(R.id.locationName);
            textViewTime=(TextView)itemView.findViewById(R.id.timeName);
            textViewReporter=(TextView)itemView.findViewById(R.id.reporterName);
            imageButton=(ImageButton)itemView.findViewById(R.id.viewbutton);
        }

    }
}


