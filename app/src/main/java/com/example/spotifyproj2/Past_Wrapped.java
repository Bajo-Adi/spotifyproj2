package com.example.spotifyproj2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class Past_Wrapped extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_wrapped);


        RecyclerView recyclerView = findViewById(R.id.lalala);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//      right now I hard coded the dates in, but as long as backend has it so dates are added do dates array, then it will work.
        List<WrappedModel> dates = new ArrayList<>();
        dates.add(new WrappedModel("2021-01-01"));
        dates.add(new WrappedModel("2021-01-02"));
        dates.add(new WrappedModel("2021-01-03"));


        PW_RecyclerViewAdapter adapter = new PW_RecyclerViewAdapter(this, dates);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


}