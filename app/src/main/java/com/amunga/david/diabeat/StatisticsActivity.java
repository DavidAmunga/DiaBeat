package com.amunga.david.diabeat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private static final String TAG = "StatisticsActivity";

    AppCompatSpinner spinnerTime, spinnerCategory;
    ImageView imageViewTime, imageViewCategory;

    ValueLineChart mCubicValueLineChart;
    ValueLineSeries series;

    ScrollView scroll1;

    private List<String> timeList = new ArrayList<>();
    private List<String> categoryList = new ArrayList<>();

    private List<String> bloodSugarList = new ArrayList<>();
    private List<String> hbList = new ArrayList<>();
    private List<String> activityList = new ArrayList<>();

    private List<String> dates = new ArrayList<>();

    //private long bs=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Statistics");

        initViews();


    }

    private void initViews() {
        spinnerCategory = (AppCompatSpinner) findViewById(R.id.spinnerCategory);
        imageViewCategory = (ImageView) findViewById(R.id.imageCategory);
        scroll1 = (ScrollView) findViewById(R.id.scroll1);

        categoryList = Arrays.asList("Blood Sugar", "Activity", "HbA1c");
        timeList = Arrays.asList("Week", "Month", "Year");
        ArrayAdapter categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryList);

        spinnerCategory.setAdapter(categoryAdapter);

        mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);
        series=new ValueLineSeries();
        series.setColor(0xFA56B7B4);

        checkImages();


    }

    private void checkImages() {
        Log.d(TAG, "Spinner: " + spinnerCategory.getSelectedItem().toString());
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getItemAtPosition(position).toString()) {
                    case "Blood Sugar":
                        imageViewCategory.setImageDrawable(getResources().getDrawable(R.drawable.ic_opacityblack));
                        setBloodCard();

                        break;
                    case "Activity":
                        imageViewCategory.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_run_black_24dp));
                        setActivityCard();
                        break;
                    case "HbA1c":
                        imageViewCategory.setImageDrawable(getResources().getDrawable(R.drawable.percent_black));
                        setHbCard();
                        break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void setActivityCard() {
        scroll1.removeAllViews();

        View card1;
        LayoutInflater inflater = LayoutInflater.from(this);
        card1 = inflater.inflate(R.layout.item_statistics_activity, null, true);
        scroll1.addView(card1);

        final TextView txtMinutes=(TextView)card1.findViewById(R.id.txtMinutes);
        final TextView entries=(TextView)card1.findViewById(R.id.txtEntries);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Entry");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double size=Double.valueOf(dataSnapshot.getChildrenCount());
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {

                    String ac=postSnapshot.child("activityDuration").getValue().toString();
                    Log.d(TAG, "Activity Duration:"+ac);
                    hbList.add(ac);

                    String date=postSnapshot.child("date").getValue().toString();
                    if(!dates.contains(date)) {
                        dates.add(date);
                    }


                }
                Log.d(TAG, "Activity list :"+activityList);
                double sum = 0;
                for(int i = 1; i < activityList.size(); i++) {
                    sum += Long.valueOf(activityList.get(i));
                }
                Double ac=sum/size;
                Log.d(TAG, "Average : "+ac);
                Log.d(TAG, "Size : "+size);
                txtMinutes.setText(String.format("%.2f", ac));

                Double avEntry=Double.valueOf(size/dates.size());
                Log.d(TAG, "Dates "+dates+" :Entries:"+String.valueOf(avEntry));

                entries.setText(String.format("%.2f", avEntry));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void setHbCard() {
        scroll1.removeAllViews();

        View card1;
        LayoutInflater inflater = LayoutInflater.from(this);
        card1 = inflater.inflate(R.layout.item_statistics_hb, null, true);
        scroll1.addView(card1);

        final TextView txtHb=(TextView)card1.findViewById(R.id.txtHb);
        final TextView entries=(TextView)card1.findViewById(R.id.txtEntries);


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Entry");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double size=Double.valueOf(dataSnapshot.getChildrenCount());
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {

                    String hb=postSnapshot.child("hb").getValue().toString();
                    Log.d(TAG, "Hb:"+hb);
                    hbList.add(hb);

                    String date=postSnapshot.child("date").getValue().toString();
                    if(!dates.contains(date)) {
                        dates.add(date);
                    }


                }
                Log.d(TAG, "Hb list :"+hbList);
                double sum = 0;
                for(int i = 1; i < hbList.size(); i++) {
                    sum += Long.valueOf(hbList.get(i));
                }
                Double hb=sum/size;
                Log.d(TAG, "Average : "+hb);
                Log.d(TAG, "Size : "+size);
                txtHb.setText(String.format("%.2f", hb));

                Double avEntry=Double.valueOf(size/dates.size());
                Log.d(TAG, "Dates "+dates+" :Entries:"+String.valueOf(avEntry));

                entries.setText(String.format("%.2f", avEntry));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }





    private void setBloodCard() {
        scroll1.removeAllViews();

        View card1;

        LayoutInflater inflater = LayoutInflater.from(this);
        card1 = inflater.inflate(R.layout.item_statistics_blood_sugar, null, true);
        scroll1.addView(card1);

        final TextView txtBs=(TextView)card1.findViewById(R.id.txtMgDl);
        final TextView entries=(TextView)card1.findViewById(R.id.txtEntries);




        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Entry");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double size=Double.valueOf(dataSnapshot.getChildrenCount());
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {

                   String bloodSugar=postSnapshot.child("bloodSugar").getValue().toString();
                   Log.d(TAG, "BS:"+bloodSugar);
                   bloodSugarList.add(bloodSugar);

                    String date=postSnapshot.child("date").getValue().toString();
                    if(!dates.contains(date)) {
                        dates.add(date);
                    }


                }
                Log.d(TAG, "BS list :"+bloodSugarList);
                double sum = 0;
                for(int i = 1; i < bloodSugarList.size(); i++) {
                    sum += Long.valueOf(bloodSugarList.get(i));
                }
                Double bs=sum/size;
                Log.d(TAG, "Average : "+bs);
                Log.d(TAG, "Size : "+size);
                txtBs.setText(String.format("%.2f", bs));

                Double avEntry=Double.valueOf(size/dates.size());
                Log.d(TAG, "Dates "+dates+" :Entries:"+String.valueOf(avEntry));

                entries.setText(String.format("%.2f", avEntry));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
