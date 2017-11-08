package com.amunga.david.diabeat;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.liuguangqiang.cookie.CookieBar;
import com.liuguangqiang.cookie.OnActionClickListener;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    CardView cardGraph, card_hba, card_todayStats, card_averageStats, card_last_entry;
    TextView txtBloodSugar, txtDate, txtTime, txtDuration, txtHba;

    RelativeLayout relMain;

    private List<String> dates = new ArrayList<>();
    private List<String> entryKeys = new ArrayList<>();
    List<PointValue> values;

    boolean isSugarFree = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graphInit();
        initViews();


        cardGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });
        card_hba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSugarFree) {
                    new CookieBar.Builder(MainActivity.this)
                            .setTitle("Health Status: Warning!")
                            .setIcon(R.drawable.ic_action_warning)
                            .setMessage("Please ensure your sugar levels are low")
                            .setBackgroundColor(R.color.colorRed)
                            .setTitleColor(R.color.colorWhite)
                            .setMessageColor(R.color.colorWhite)
                            .show();

                } else {
                    new CookieBar.Builder(MainActivity.this)
                            .setTitle("Health Status: Info!")
                            .setIcon(R.drawable.ic_action_like)
                            .setMessage("Your levels are just about right!")
                            .setBackgroundColor(R.color.colorAccent)
                            .setTitleColor(R.color.colorWhite)
                            .setMessageColor(R.color.colorWhite)
                            .show();
                }


            }
        });


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Entry");

        Query query = ref.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String bs = postSnapshot.child("bloodSugar").getValue().toString();
                    String date = postSnapshot.child("date").getValue().toString();
                    String time = postSnapshot.child("time").getValue().toString();
                    String hb = postSnapshot.child("hb").getValue().toString();

                    txtBloodSugar.setText(bs);
                    txtDate.setText(date);
                    txtTime.setText(time);
                    txtHba.setText(hb + "%");
                    checkLevels();

                    setDuration(date, time);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void checkLevels() {
        String hba = txtHba.getText().toString().trim();
        hba = hba.replace("%", "");
        Double hb = Double.valueOf(hba);
        Double hbStandard = 6.5;
        Log.d(TAG, "Hb " + hb.toString());
        if (hb > hbStandard) {
            Log.d(TAG, "Health Status Bad");
            isSugarFree = false;
            txtHba.setTextColor(getResources().getColor(R.color.colorRed));
        } else {
            Log.d(TAG, "Health Status Good");
            txtHba.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        String bs = txtBloodSugar.getText().toString().trim();
        Double bsa = Double.valueOf(bs);

//        if(bsa<200)
//        {
//            txtBloodSugar.setTextColor(getResources().getColor(R.color.colorPrimary));
//        }
//        else
//        {
//            txtBloodSugar.setTextColor(getResources().getColor(R.color.colorRed));
//        }


    }

    private void setDuration(String entryDate, String entryTime) {
        String dateStr = entryDate + " " + entryTime;
        Log.d(TAG, "DateStr " + dateStr);
        SimpleDateFormat simpDate = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = null;

        try {
            date = simpDate.parse(dateStr);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        long nowTime = System.currentTimeMillis();
        long diff = nowTime - date.getTime();
        Log.d(TAG, "Time :" + String.valueOf(diff));

        if (diff >= 1000 && diff <= 60000) {
            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diff);
            txtDuration.setText(String.valueOf(diffInSec) + " seconds ago");
            Log.d(TAG, "Seconds");
        } else if (diff >= 60000 && diff <= 3600000) {

            long diffInMin = TimeUnit.MILLISECONDS.toMinutes(diff);
            txtDuration.setText(String.valueOf(diffInMin) + " minutes ago");
        } else if (diff >= 3600000 && diff <= 86400000) {

            long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);
            txtDuration.setText(String.valueOf(diffInHours) + " hours ago");
        } else if (diff >= 86400000) {
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diff);
            txtDuration.setText(String.valueOf(diffInDays) + " days ago");
        }

    }

    private void initViews() {
        cardGraph = (CardView) findViewById(R.id.cardGraph);
        card_hba = (CardView) findViewById(R.id.card_hba);
        card_last_entry = (CardView) findViewById(R.id.card_last_entry);

        txtBloodSugar = (TextView) findViewById(R.id.txtBloodSugar);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtDuration = (TextView) findViewById(R.id.txtDuration);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtHba = (TextView) findViewById(R.id.txtHba);

        relMain = (RelativeLayout) findViewById(R.id.rel_main);


    }


    public void graphInit() {
        final LineChartView chart = (LineChartView) findViewById(R.id.chart);
        chart.setInteractive(true);
        values = new ArrayList<PointValue>();


        class StringDateComparator implements Comparator<String> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            public int compare(String lhs, String rhs) {
                try {
                    return dateFormat.parse(lhs).compareTo(dateFormat.parse(rhs));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        }


//Set Graph
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Entry");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String date = postSnapshot.child("date").getValue().toString();
                    dates.add(date);
                    String key = postSnapshot.getKey();
                    entryKeys.add(key);


                }
                Collections.sort(dates, new StringDateComparator());
                Log.d(TAG, "Dates" + dates);
                Log.d(TAG, "Key" + entryKeys);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference refBs = FirebaseDatabase.getInstance().getReference("Entry");
        refBs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < entryKeys.size(); i++) {
                    String bs = dataSnapshot.child(entryKeys.get(i)).child("bloodSugar").getValue().toString();
                    String date = dataSnapshot.child(entryKeys.get(i)).child("date").getValue().toString();

                    float bsa = Float.valueOf(bs);


                    Log.d(TAG, "Graph Sugar :" + bs + " - " + date);


                    values.add(new PointValue(Float.valueOf(i), bsa));


                }
                Log.d(TAG, "Values " + values);
                Line line = new Line(values).setColor(Color.GREEN).setCubic(true);
                line.setFilled(true);
                line.setHasLabels(true);
                line.setCubic(true);
                line.setShape(ValueShape.CIRCLE);
                line.setHasLabelsOnlyForSelected(true);
                line.setHasLines(true);
                line.setHasPoints(true);

                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);


                axisX.setName("Entries");

                axisY.setName("Blood Sugar");
                List<Line> lines = new ArrayList<Line>();
                lines.add(line);

                LineChartData data = new LineChartData();

                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
                data.setBaseValue(Float.NEGATIVE_INFINITY);

                data.setLines(lines);

                chart.setLineChartData(data);
                chart.setScrollEnabled(true);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cardGraph) {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            intent.putExtra("graph", "yes");
            startActivity(intent);

        }

        if (v.getId() == R.id.card_hba) {


        }

        if (v.getId() == R.id.card_last_entry) {

            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            intent.putExtra("lastEntry", "yes");
            startActivity(intent);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.entry_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
