package com.example.spotifyproj2;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PW_RecyclerViewAdapter extends RecyclerView.Adapter<PW_RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<WrappedModel> models;

    public PW_RecyclerViewAdapter(Context context, List<WrappedModel> dates) {
        this.context = context;
        this.models = dates;
    }

    @NonNull
    @Override
    public PW_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.past_wrapped_recycler, parent, false);
        return new PW_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PW_RecyclerViewAdapter.MyViewHolder holder, int position) {
        WrappedModel wrappedModel = models.get(position);
        holder.dateButton.setText(wrappedModel.getDate());  // Set the date text to the button

        // Set click listener on the button
        holder.dateButton.setOnClickListener(v -> {
            // Launch WrappedDetailsActivity with the selected date
            Intent intent = new Intent(context, WrappedDetailsActivity.class);
            intent.putExtra("DATE", wrappedModel.getDate());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        // Returns the number of items
        return models.size();
    }

    // Method to update the data in the adapter and refresh the RecyclerView
    public void updateData(List<WrappedModel> newModels) {
        this.models = newModels;  // Update the existing model list
        notifyDataSetChanged();  // Notify the adapter that the data set has changed
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        Button dateButton;  // Declare Button instead of TextView

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dateButton = itemView.findViewById(R.id.Date); // Assuming you've changed the TextView to Button in your XML with the same ID
        }
    }
}
