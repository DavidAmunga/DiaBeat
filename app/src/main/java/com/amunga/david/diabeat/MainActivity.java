package com.amunga.david.diabeat;

import android.content.Intent;
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


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.liuguangqiang.cookie.CookieBar;
import com.liuguangqiang.cookie.OnActionClickListener;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    CardView cardGraph,card_hba,card_todayStats,card_averageStats,card_last_entry;
    TextView txtBloodSugar,txtDate,txtTime,txtDuration,txtHba;

    RelativeLayout relMain;

    boolean isSugarFree=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graphInit();
        initViews();



        cardGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,StatisticsActivity.class);
                startActivity(intent);
            }
        });
        card_hba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSugarFree)
                {
                    new CookieBar.Builder(MainActivity.this)
                            .setTitle("Health Status: Warning!")
                            .setIcon(R.drawable.ic_action_warning)
                            .setMessage("Please ensure your sugar levels are low")
                            .setBackgroundColor(R.color.colorRed)
                            .setTitleColor(R.color.colorWhite)
                            .setMessageColor(R.color.colorWhite)
                            .show();

                }
                else
                {
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



        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Entry");

        Query query = ref.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    String bs=postSnapshot.child("bloodSugar").getValue().toString();
                    String date=postSnapshot.child("date").getValue().toString();
                    String time=postSnapshot.child("time").getValue().toString();
                    String hb=postSnapshot.child("hb").getValue().toString();

                    txtBloodSugar.setText(bs);
                    txtDate.setText(date);
                    txtTime.setText(time);
                    txtHba.setText(hb+"%");
                    checkLevels();

                    setDuration(date,time);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void checkLevels() {
        String hba=txtHba.getText().toString().trim();
        hba=hba.replace("%","");
        Double hb=Double.valueOf(hba);
        Double hbStandard=6.5;
        Log.d(TAG, "Hb "+hb.toString());
        if(hb>hbStandard)
        {
            Log.d(TAG, "Health Status Bad");
            isSugarFree=false;
            txtHba.setTextColor(getResources().getColor(R.color.colorRed));
        }
        else
        {
            Log.d(TAG, "Health Status Good");
            txtHba.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        String bs=txtBloodSugar.getText().toString().trim();
        Double bsa=Double.valueOf(bs);

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
        String dateStr=entryDate+" "+entryTime;
        Log.d(TAG, "DateStr "+dateStr);
        SimpleDateFormat simpDate=new SimpleDateFormat( "dd-MM-yyyy HH:mm");
        Date date=null;

        try {
            date = simpDate.parse(dateStr);

        } catch (ParseException e) {
            e.printStackTrace();
        }



        long nowTime= System.currentTimeMillis();
        long diff=nowTime-date.getTime();
        Log.d(TAG, "Time :"+String.valueOf(diff));

        if(diff>=1000 && diff<=60000)
        {
            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diff);
            txtDuration.setText(String.valueOf(diffInSec)+" seconds ago");
            Log.d(TAG, "Seconds");
        }
        else if(diff>=60000 && diff<=3600000)
        {

            long diffInMin = TimeUnit.MILLISECONDS.toMinutes(diff);
            txtDuration.setText(String.valueOf(diffInMin)+" minutes ago");
        }
        else if(diff>=3600000 && diff<=86400000 )
        {

            long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);
            txtDuration.setText(String.valueOf(diffInHours)+" hours ago");
        }
        else if(diff>=86400000)
        {
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diff);
            txtDuration.setText(String.valueOf(diffInDays)+" days ago");
        }

    }

    private void initViews() {
        cardGraph=(CardView)findViewById(R.id.cardGraph);
        card_averageStats=(CardView)findViewById(R.id.card_averageStats);
        card_hba=(CardView)findViewById(R.id.card_hba);
        card_todayStats=(CardView)findViewById(R.id.card_todayStats);
        card_last_entry=(CardView)findViewById(R.id.card_last_entry);

        txtBloodSugar=(TextView)findViewById(R.id.txtBloodSugar);
        txtDate=(TextView)findViewById(R.id.txtDate);
        txtDuration=(TextView)findViewById(R.id.txtDuration);
        txtTime=(TextView)findViewById(R.id.txtTime);
        txtHba=(TextView)findViewById(R.id.txtHba);

        relMain=(RelativeLayout)findViewById(R.id.rel_main);



    }


    public void graphInit()
    {
        ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFA56B7B4);

        series.addPoint(new ValueLinePoint("Jan", 2.4f));
        series.addPoint(new ValueLinePoint("Feb", 3.4f));
        series.addPoint(new ValueLinePoint("Mar", .4f));
        series.addPoint(new ValueLinePoint("Apr", 1.2f));
        series.addPoint(new ValueLinePoint("Mai", 2.6f));
        series.addPoint(new ValueLinePoint("Jun", 1.0f));
        series.addPoint(new ValueLinePoint("Jul", 3.5f));
        series.addPoint(new ValueLinePoint("Aug", 2.4f));
        series.addPoint(new ValueLinePoint("Sep", 2.4f));
        series.addPoint(new ValueLinePoint("Oct", 3.4f));
        series.addPoint(new ValueLinePoint("Nov", .4f));
        series.addPoint(new ValueLinePoint("Dec", 1.3f));

        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.cardGraph)
        {
            Intent intent=new Intent(MainActivity.this,StatisticsActivity.class);
            intent.putExtra("graph","yes");
            startActivity(intent);

        }
        if(v.getId()==R.id.card_averageStats)
        {
            Intent intent=new Intent(MainActivity.this,StatisticsActivity.class);
            intent.putExtra("averageStats","yes");
            startActivity(intent);
        }
        if(v.getId()==R.id.card_hba)
        {


        }
        if(v.getId()==R.id.card_todayStats)
        {
            Intent intent=new Intent(MainActivity.this,StatisticsActivity.class);
            intent.putExtra("todayStats","yes");
            startActivity(intent);

        }
        if(v.getId()==R.id.card_last_entry)
        {
            Intent intent=new Intent(MainActivity.this,StatisticsActivity.class);
            intent.putExtra("lastEntry","yes");
            startActivity(intent);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.entry_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add:
                Intent intent=new Intent(MainActivity.this,NewEntryActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
