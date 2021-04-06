package com.example.i_explore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.Filter;
import android.widget.Filterable;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class view extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String currentUser;
    AlertDialog.Builder builder;
    FirebaseRecyclerOptions<User> options;
    FirebaseRecyclerAdapter<User, Holder> adapters;
    ProgressDialog progressDialog;
    EditText taskEditText;
    String message, reporting;
    ArrayList<User> list;
    Adapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);


        builder = new AlertDialog.Builder(this);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser);
        progressDialog = new ProgressDialog(this);

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        displayData();


    }

    private void displayData() {

        options = new FirebaseRecyclerOptions.Builder<User>().setQuery(databaseReference, User.class).build();
        adapters = new FirebaseRecyclerAdapter<User, Holder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull User model) {
                final String postkey = getRef(position).getKey();
                holder.textViewActivity.setText("Event : " + model.getActivityName());
                holder.textViewLocation.setText("Location: " + model.getLocation());
                holder.textViewDate.setText("Date: " + model.getDate());
                holder.textViewTime.setText("Time: " + model.getTime());
                holder.textViewReporter.setText("Reporter: " + model.getReporter());
                holder.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(view.this, holder.imageButton);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.add:
                                        addReport(postkey);
                                        break;
                                    case R.id.view:
                                        viewReport(postkey);

                                        break;
                                    case R.id.delete:

                                        deleteActivity(postkey);
                                        break;
                                }
                                return true;
                            }
                        });

                        popup.show();
                    }
                });


            }

            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.data, parent, false);

                return new Holder(v);
            }
        };
        adapters.startListening();
        recyclerView.setAdapter(adapters);
    }

    private void viewReport(String postkey) {
        databaseReference.child(postkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("reports")) {
                    reporting = snapshot.child("reports").getValue().toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.this);
                    builder.setTitle("Report")
                            .setMessage(reporting)
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    Toast.makeText(view.this, "No report to show", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addReport(String postkey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        taskEditText = new EditText(this);
        builder.setView(taskEditText);
        LinearLayout layoutName = new LinearLayout(this);
        layoutName.setOrientation(LinearLayout.VERTICAL);
        layoutName.addView(taskEditText); // displays the user input bar
        builder.setView(layoutName);


        builder.setTitle("Add Report")
                .setMessage("What do you want to Add ?")

                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        message = taskEditText.getText().toString();
                        if (message.isEmpty()) {
                            Toast.makeText(view.this, "please enter a report", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.setTitle("Uploading data");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setMessage("please wait while we are uploading your data...");
                            progressDialog.show();
                            putDataToFirebase(postkey, message);

                        }
                        dialog.cancel();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Entered Data");
        alert.show();
    }

    private void putDataToFirebase(String postkey, String message) {
        HashMap hashMap = new HashMap();
        hashMap.put("reports", message);
        databaseReference.child(postkey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(view.this, "Report added successfully", Toast.LENGTH_SHORT).show();

                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(view.this, message, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }

            }
        });

    }


    private void deleteActivity(String keys) {


        builder.setMessage("Do you want to delete data")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deletingFromFirebase();
                        databaseReference.child(keys).removeValue();

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
        alert.setTitle("Delete Data");
        alert.show();


    }

    private void deletingFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
   public boolean onCreateOptionsMenu(Menu menu) {

       MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searching, menu);

       MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return true;
            }
        });
        return true;
    }

    }






