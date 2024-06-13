package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {


    private FirebaseAuth mAuth;
    EditText loginusername, loginpassword;
    TextView btnSignup, forgotPass;
    Button btnlogin;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        InitializeFields();

        btnlogin.setOnClickListener(v -> {
            AllowUserLogin();
        });
        btnSignup.setOnClickListener(v -> {
            SendUsertoSignupActivity();
        });
    }
    private void InitializeFields(){
        loginusername = findViewById(R.id.loginEmail);
        loginpassword = findViewById(R.id.loginPassword);
        forgotPass = findViewById(R.id.forgetpass);
        btnSignup = findViewById(R.id.btnsignup);
        btnlogin = findViewById(R.id.btnLogin);
    }
    //Go to Home
    private void AllowUserLogin(){
        String Username = loginusername.getText().toString();
        String Password = loginpassword.getText().toString();

        if(TextUtils.isEmpty(Username)){
            Toast.makeText(this, "Please input username", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(Password)){
            Toast.makeText(this, "Please input password", Toast.LENGTH_SHORT).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(Username,Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                SendUsertoHomeActivity();
                                Toast.makeText(Login.this, "Logged In",Toast.LENGTH_SHORT).show();
                            }else{
                                String message = task.getException().toString();
                                Toast.makeText(Login.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    //To Signup
    private void SendUsertoSignupActivity(){
        Intent intent = new Intent(Login.this, Signup.class);
        startActivity(intent);
    }
    //To Home
    private void SendUsertoHomeActivity(){
        Intent intent = new Intent(Login.this, Homeui.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }
}