package com.amunga.david.diabeat;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amunga.david.diabeat.model.Entry;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.Calendar;

public class NewEntryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener, com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "NewEntryActivity";

    private CardView cardBlood,cardHb,cardActivity;
    private ImageView cancel1,cancel2,cancel3;
    private TextView txtTime,txtDate,txtReminder;
    private EditText edtNote,edtBs,edtHb,edtAc;

    private LinearLayout line1;

    private View card1,card2,card3;

    private String alarmTime;

    private DatabaseReference mDatabase;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Entry");

        progressDialog=new ProgressDialog(this);

        mDatabase= FirebaseDatabase.getInstance().getReference("Entry");

        line1=(LinearLayout)findViewById(R.id.lineCards);

//        Inflate Views
//        Blood Sugar



        LayoutInflater inflater = LayoutInflater.from(this);
        card1= inflater.inflate(R.layout.item_blood_sugar, null, true);
        cardBlood=(CardView)card1.findViewById(R.id.card_blood);
        cardBlood=(CardView)card1.findViewById(R.id.card_blood);
        line1.addView(card1);

//        Hb
        LayoutInflater inflater1 = LayoutInflater.from(this);
        card2= inflater1.inflate(R.layout.item_hb, null, true);

        cardHb=(CardView)card2.findViewById(R.id.card_hb);
        line1.addView(card2);

//       Activity
        LayoutInflater inflater2 = LayoutInflater.from(this);
        card3= inflater2.inflate(R.layout.item_activity, null, true);
        cardActivity=(CardView)card3.findViewById(R.id.card_activity);

        line1.addView(card3);

        cancel1=(ImageView)cardBlood.findViewById(R.id.action_cancel);
        cancel2=(ImageView)cardActivity.findViewById(R.id.action_cancel);
        cancel3=(ImageView)cardHb.findViewById(R.id.action_cancel);

        initViews();

        cancelCard();

        Calendar now=Calendar.getInstance();
        String year=String.valueOf(now.get(Calendar.YEAR));
        String month=String.valueOf(now.get(Calendar.MONTH));
        int newMonth=Integer.parseInt(month)+1;
        String day=String.valueOf(now.get(Calendar.DAY_OF_MONTH));
        txtDate.setText(day+"-"+newMonth+"-"+year);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 50, 0, 50);
        cardBlood.setLayoutParams(layoutParams);
        cardHb.setLayoutParams(layoutParams);
        cardActivity.setLayoutParams(layoutParams);


//        Set Time and Date Dialog
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now=Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog= com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                        NewEntryActivity.this,
                        now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.setTitle("Pick a Date");
                datePickerDialog.show(getFragmentManager(),"DatePicker");
            }
        });
        String hour=String.valueOf(now.get(Calendar.HOUR_OF_DAY));
        String minute=String.valueOf(now.get(Calendar.MINUTE));
        txtTime.setText(hour+":"+minute);
        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now=Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog= com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                        NewEntryActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                ); //True is 24 Hrs , False is 12 hours
                timePickerDialog.setTitle("Pick a time");
                timePickerDialog.show(getFragmentManager(),"Time Picker");
            }
        });
        txtReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LovelyTextInputDialog(NewEntryActivity.this)
                        .setTopColorRes(R.color.colorAccent)
                        .setTitle("Reminder")
                        .setMessage("Remind me next entry after")
                        .setIcon(R.drawable.ic_action_alarm)
                        .setInitialInput("20")
                        .setHint("Enter Minutes")
                        .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                            @Override
                            public void onTextInputConfirmed(String text) {
                                alarmTime=text;
                                if(text.length()<=0)
                                {
                                    txtReminder.setText("No Reminder");
                                }
                                else
                                {
                                    txtReminder.setText("Reminder in "+text+" min");
                                }


                            }
                        })
                        .show();
            }
        });






    }

    private void save() {




        String note=edtNote.getText().toString().trim();
        String reminder=alarmTime;
        String time=txtTime.getText().toString().trim();
        String date=txtDate.getText().toString().trim();
        String bs=edtBs.getText().toString().trim();
        String ac=edtAc.getText().toString().trim();
        String hb=edtHb.getText().toString().trim();


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Entry").push();
        Entry entry=new Entry(date,time,reminder,note,bs,ac,hb);
        ref.setValue(entry);




        Toast.makeText(this, "Entry Added!", Toast.LENGTH_SHORT).show();


    }







    private void initViews() {
        txtDate=(TextView)findViewById(R.id.txtDate);
        txtTime=(TextView)findViewById(R.id.txtTime);
        edtNote=(EditText)findViewById(R.id.edtNote);
        txtReminder=(TextView) findViewById(R.id.txtReminder);

        edtAc=(EditText)cardActivity.findViewById(R.id.edt_min);
        edtBs=(EditText)cardBlood.findViewById(R.id.edt_bs);
        edtHb=(EditText)cardHb.findViewById(R.id.edt_hb);

    }

    private void cancelCard() {
        cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                line1.removeView(cardBlood);
            }
        });
        cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                line1.removeView(cardActivity);
            }
        });
        cancel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                line1.removeView(cardHb);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.new_entry_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.accept:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month=monthOfYear+1;
        Log.d(TAG, "month "+month);
        String date=dayOfMonth+"-"+month+"-"+year;
        Log.d(TAG, "onDateSet: "+date);
        txtDate.setText(date);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time=hourOfDay+":"+minute;
        Log.d(TAG, "onTimeSet: "+time);
        txtTime.setText(time);
    }
}
