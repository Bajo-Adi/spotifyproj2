package com.example.spotifyproj2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PW_RecyclerViewAdapter  extends RecyclerView.Adapter<PW_RecyclerViewAdapter.MyViewHolder> {

    Context context;
    public List<WrappedModel> models;
    public PW_RecyclerViewAdapter(Context context, List<WrappedModel> dates) {
        this.context = context;
        this.models = dates;

    }


    @NonNull
    @Override
    public PW_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        inflating layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.past_wrapped_recycler, parent, false);
        return new PW_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PW_RecyclerViewAdapter.MyViewHolder holder, int position) {
//        assigning values to rows as they recycle back on the screen

        holder.tvDate.setText(models.get(position).getDate());

    }

    @Override
    public int getItemCount() {
//        returns the number of rows
        return models.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.Date);
        }
    }
}

