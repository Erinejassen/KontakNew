package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Signup extends AppCompatActivity {
    EditText inputUsername, inputPassword, inputCpassword;
    TextView btnLogin;
    Button btnSignup;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        InitializeFields();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUsertoLoginActivity();
            }
        });
    }

    private void CreateNewAccount() {
        String Username = inputUsername.getText().toString();
        String Password = inputPassword.getText().toString();
        String CPassword = inputCpassword.getText().toString();

        if (TextUtils.isEmpty(Username)) {
            Toast.makeText(this, "Please input username", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Password) || !Password.equals(CPassword)) {
            Toast.makeText(this, "Please input password and make sure passwords match", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Create New Account");
            loadingBar.setMessage("Please Wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(Username, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String currentUserID = mAuth.getCurrentUser().getUid();
                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("uid", currentUserID);
                                userMap.put("username", Username);

                                RootRef.child("Users").child(currentUserID).setValue(userMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    SendUsertoHomeActivity();
                                                    Toast.makeText(Signup.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                } else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(Signup.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(Signup.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void InitializeFields() {
        inputUsername = findViewById(R.id.signupUsername);
        inputPassword = findViewById(R.id.signupPassword);
        inputCpassword = findViewById(R.id.signupCPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        loadingBar = new ProgressDialog(this);
    }

    private void SendUsertoHomeActivity() {
        Intent intent = new Intent(Signup.this, Homeui.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    private void SendUsertoLoginActivity() {
        Intent intent = new Intent(Signup.this, Login.class);
        startActivity(intent);
    }
}