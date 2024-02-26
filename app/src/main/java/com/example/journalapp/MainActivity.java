package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText emailTxt, passwordTxt;
    private Button loginBtn, createAccountBtn;


    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createAccountBtn = findViewById(R.id.accountBtn);

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });


        loginBtn  = findViewById(R.id.loginBtn);
        emailTxt = findViewById(R.id.emailTxt);
        passwordTxt = findViewById(R.id.passwordTxt);


        //Firebase Authentication
        firebaseAuth =  FirebaseAuth.getInstance();



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginEmailPassUser(emailTxt.getText().toString().trim()
                        ,passwordTxt.getText().toString().trim());
            }
        });

    }

    private void loginEmailPassUser(String email,String pass){
        if(!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(pass)){
            firebaseAuth.signInWithEmailAndPassword(email,
                    pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (task.isSuccessful()) {
                        // Sign in success
                        Intent i = new Intent(MainActivity.this, JournalActivityList.class);
                        startActivity(i);
                        finish();
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "Please enter correct ID or Password!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "No ID or Password entered!", Toast.LENGTH_SHORT).show();
        }
    }
}