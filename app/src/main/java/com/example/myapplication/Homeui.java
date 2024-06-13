package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Homeui extends AppCompatActivity {


    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    Chatsfragment chatsfragment = new Chatsfragment();
    Contactsfragment contactsfragment = new Contactsfragment();
    Groupsfragment groupsfragment = new Groupsfragment();
    Requestfragment requestfragment = new Requestfragment();

    private String username;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeui);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.homeui_frame, chatsfragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.chats) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.homeui_frame, chatsfragment).commit();
                    return true;
                } else if (itemId == R.id.groups) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.homeui_frame, groupsfragment).commit();
                    return true;
                } else if (itemId == R.id.contacts) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.homeui_frame, contactsfragment).commit();
                    return true;
                } else if (itemId == R.id.request) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.homeui_frame, requestfragment).commit();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null){
            SendUsertoLoginActivity();
        }
        else{
            VerifyUserExistance();
        }
    }

    public void VerifyUserExistance(){
        String currentUserID = mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(Homeui.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SendUsertoSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.settings){
            SendUsertoSettingsActivity();
        } else if(id == R.id.camera){
            Intent cam = new Intent();
            cam.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(cam);
        }else if(id == R.id.logout) {
            mAuth.signOut();
            SendUsertoLoginActivity();
        }else if(id == R.id.findfriends){
            Intent cam = new Intent();
            cam.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(cam);
        }else if(id == R.id.create_groups){
            RequestNewGroup();
        }
        return true;
    }

    private void RequestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Homeui.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(Homeui.this);
        groupNameField.setHint("Group Name");
        builder.setView(groupNameField);

        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String groupName = groupNameField.getText().toString();

                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(Homeui.this, "Input Group Name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                }
            }
        });

        builder.show();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }
    private void CreateNewGroup(String groupName) {
        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful()){
                            Toast.makeText(Homeui.this, groupName + " is Created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void SendUsertoLoginActivity(){
        Intent intent = new Intent(Homeui.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void SendUsertoSettingsActivity(){
        Intent settings = new Intent(this, Settings.class);
        settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settings);
        finish();
    }

}