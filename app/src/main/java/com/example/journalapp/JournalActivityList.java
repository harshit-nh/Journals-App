package com.example.journalapp;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class JournalActivityList extends AppCompatActivity {

    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;


    //Firebase FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference =
            db.collection("Journal");

    //Firebase Storage
    private StorageReference storageReference;

    //List of Journals
    private List<Journal> journalList;

    //RecyclerView
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;

    //Widgets
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        //Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //Widgets
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JournalActivityList.this, AddJournalActivity.class);
                startActivity(i);
            }
        });

        //Posts list
        journalList = new ArrayList<>();



        //Exit Confirmation dialog
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmationDialog();
            }
        };

        getOnBackPressedDispatcher().addCallback(this,callback);

    }


    //Adding a menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if(itemId == R.id.action_add){
            if(user != null && firebaseAuth != null){
                Intent i = new Intent(JournalActivityList.this,
                        AddJournalActivity.class);
                startActivity(i);
            }
        }
        else if(itemId == R.id.action_signOut){
            if(user != null && firebaseAuth!= null){
                firebaseAuth.signOut();
                Intent i = new Intent(JournalActivityList.this,
                        MainActivity.class);
                startActivity(i);
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {

            //QueryDocumentSnapshot: It is an object that represents
            // a single document retrieved from a FireStore query

            for(QueryDocumentSnapshot journals: queryDocumentSnapshots){
                Journal journal = journals.toObject(Journal.class);

                journalList.add(journal);
            }

            //RecyclerView
            myAdapter = new MyAdapter(JournalActivityList.this,
                    journalList);
            recyclerView.setAdapter(myAdapter);

            myAdapter.notifyDataSetChanged();

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "OOPS! Something went wrong!", Toast.LENGTH_SHORT).show();
        });
    }


    //Exit confirmation dialog function
    private void showExitConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit the app?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    
}