package com.example.i_explore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class home extends AppCompatActivity {
    EditText activity, location, date, time, reporter;
    Button submit;
    String activityname, locationEntered, dateEntered, timeEntered, reporterName;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    AwesomeValidation awesomeValidation;
    AlertDialog.Builder builder;
    ProgressDialog progressDialog;
    DatePickerDialog datePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = findViewById(R.id.et_activityname);
        location = findViewById(R.id.et_location);
        date = findViewById(R.id.et_date);
        time = findViewById(R.id.et_time);
        reporter = findViewById(R.id.et_reportername);
        submit = findViewById(R.id.btn_submit);
        builder = new AlertDialog.Builder(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        progressDialog = new ProgressDialog(this);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.et_activityname,
                RegexTemplate.NOT_EMPTY, R.string.activityname);

        awesomeValidation.addValidation(this, R.id.et_date,
                RegexTemplate.NOT_EMPTY, R.string.date);

        awesomeValidation.addValidation(this, R.id.et_reportername,
                RegexTemplate.NOT_EMPTY, R.string.reportername);

        date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showDateDialog(date);

            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(time);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!awesomeValidation.validate()) {

                } else {

                    showdialogpop();


                }
            }
        });
    }
    private void showTimeDialog(final EditText time_in) {
        final Calendar calendar=Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm");
                time_in.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new TimePickerDialog(home.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
    }
    private void showDateDialog(final EditText date_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd");
                date_in.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new DatePickerDialog(home.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showdialogpop() {
        activityname = activity.getText().toString();
        locationEntered = location.getText().toString();
        dateEntered = date.getText().toString();
        timeEntered = time.getText().toString();
        reporterName = reporter.getText().toString();

        StringBuffer buffer = new StringBuffer();
        buffer.append("Event Name:" + activityname + " \n");
        buffer.append("Location:" + locationEntered + " \n");
        buffer.append("Date:" + dateEntered + " \n");
        buffer.append("Time:" + timeEntered + " \n");
        buffer.append("Reporter:" + reporterName + " \n");
        builder.setMessage(buffer.toString())
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        registerData(activityname, locationEntered, dateEntered, timeEntered, reporterName);
                        dialog.cancel();


                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Entered Data");
        alert.show();


    }

    private void registerData(String actName, String loc, String dat, String tim, String rep) {
        progressDialog.setTitle("Uploading data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("please wait while we are uploading your data...");
        progressDialog.show();
        final String currentuserId = firebaseAuth.getCurrentUser().getUid();
        HashMap hashMap = new HashMap();
        hashMap.put("activityName", actName);
        hashMap.put("location", loc);
        hashMap.put("date", dat);
        hashMap.put("time", tim);
        hashMap.put("reporter", rep);

        databaseReference.child(currentuserId).child(actName).updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(home.this, "data successfully uploaded", Toast.LENGTH_SHORT).show();
                            cleardata();
                            progressDialog.dismiss();

                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(home.this, message, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();


                        }
                    }
                });

    }

    protected void cleardata() {
        location.setText("");
        date.setText("");
        time.setText("");
        activity.setText("");
        reporter.setText("");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intentLogin = new Intent(home.this, login.class);
            startActivity(intentLogin);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menus,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                firebaseAuth.signOut();
                Intent intentLogin = new Intent(home.this, login.class);
                startActivity(intentLogin);
                finish();
                break;

            case R.id.view:
                Intent intentview = new Intent(home.this, view.class);
                startActivity(intentview);
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}