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

public class login extends AppCompatActivity {
    EditText email , password;
    Button login;
    TextView sign_Up;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        email=(EditText)findViewById(R.id.et_Email_login);
        password=(EditText)findViewById(R.id.et_password_login);
        login= findViewById(R.id.button_Login);
        sign_Up= findViewById(R.id.textView_sign_up);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email = email.getText().toString();
                String user_password = password.getText().toString();
                if(user_email.equals("")|| user_password.equals("")){
                    Toast.makeText(getApplicationContext(),"Fields are empty",Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.setTitle("Signing to your  Account");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("please wait while we are signing you...");
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(user_email,user_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        Intent intentHome = new Intent(login.this, home.class);
                                        startActivity(intentHome);
                                        finish();
                                        progressDialog.dismiss();

                                    } else {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(login.this, message, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();


                                    }
                                }
                            });

                }
            }
        });

        sign_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register_intent = new Intent(login.this,register.class);
                startActivity(register_intent);
            }
        });
    }
}