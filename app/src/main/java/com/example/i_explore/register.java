package com.example.i_explore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {
    EditText email, password, confirm_password;
    Button Register;
    TextView Login;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        email = (EditText) findViewById(R.id.et_email_register);
        password = (EditText) findViewById(R.id.et_Password_register);
        confirm_password = (EditText) findViewById(R.id.et_ConfirmPassword_register);
        Register = (Button) findViewById(R.id.button_sign_up);
        Login = (TextView) findViewById(R.id.textViewLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email = email.getText().toString().trim();
                String user_password = password.getText().toString().trim();
                String user_confirm_password = confirm_password.getText().toString().trim();

                if (user_email.equals("") || user_password.equals("") || user_confirm_password.equals("")) {
                    Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_SHORT).show();
                } else if (!user_confirm_password.equals(user_password) ) {
                    Toast.makeText(getApplicationContext(), "password mismatched ", Toast.LENGTH_SHORT).show();

                } else {
                    progressDialog.setTitle("Creating your Account");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("please wait while we are creating your account...");
                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        Intent intentHome = new Intent(register.this, home.class);
                                        startActivity(intentHome);
                                        finish();
                                        progressDialog.dismiss();

                                    } else {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(register.this, message, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();


                                    }
                                }
                            });


                }


            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_intent = new Intent(register.this, login.class);
                startActivity(login_intent);
            }
        });
    }
}