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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    EditText password_create,username_create,email_create;
    Button createBtn;


    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //FireStore Connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        createBtn = findViewById(R.id.signUpBtn);
        password_create = findViewById(R.id.signUpPasswordTxt);
        username_create = findViewById(R.id.signUpUsernameTxt);
        email_create = findViewById(R.id.signUpEmailTxt);

        //Firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Listening for changes in authentication
        //state and responds accordingly when the state changes

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                //Check if the user is Logged In or not
                if(currentUser != null){
                    //user already logged in
                }else{
                    //user signed out
                }
            }
        };

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = email_create.getText().toString().trim();
                String username = username_create.getText().toString().trim();
                String password = password_create.getText().toString().trim();

                if (TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(username)) {
                    Toast.makeText(SignUpActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                } else if((!TextUtils.isEmpty(email) && TextUtils.isEmpty(username ) && TextUtils.isEmpty(password)) ||
                        (!TextUtils.isEmpty(password) && TextUtils.isEmpty(email ) && TextUtils.isEmpty(username)) ||
                        (!TextUtils.isEmpty(username) && TextUtils.isEmpty(email ) && TextUtils.isEmpty(password))){
                    Toast.makeText(SignUpActivity.this, "No empty fields are allowed!", Toast.LENGTH_SHORT).show();
                }else {
                    // All fields are filled, proceed with account creation
                    createUserEmailAccount(email, password, username);
                }

            }
        });

    }

    private void createUserEmailAccount(String email,String pass,String username){
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(username)){

            firebaseAuth.createUserWithEmailAndPassword(email,
                    pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isComplete()){
                        //The user is created successfully
                        Toast.makeText(SignUpActivity.this, "Account created successfully",
                                Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(SignUpActivity.this, AddJournalActivity.class);
                        startActivity(i);
                        finish();

                    }else{
                        Toast.makeText(SignUpActivity.this, "Something went wrong: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }
}