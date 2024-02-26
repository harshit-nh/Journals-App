package com.example.journalapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class AddJournalActivity extends AppCompatActivity {

    private TextView addPost;
    private ImageView imageView,  addPhotoBtn;
    private EditText titleEditTxt, thoughtsEditTxt;
    private ProgressBar progressBar;
    private Button saveButton;

    //Firebase(FireStore)
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Journal");

    //Firebase Storage
    private StorageReference storageReference;


    //Firebase Auth
    private String currentUserId;
    private String currentUserName;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;


    //Using Activity Result Launcher
    ActivityResultLauncher<String> mTakePhoto;

    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        imageView = findViewById(R.id.post_imageView);
        titleEditTxt = findViewById(R.id.post_title_et);
        thoughtsEditTxt = findViewById(R.id.post_description_et);
        progressBar = findViewById(R.id.post_progressBar);
        saveButton = findViewById(R.id.post_save_journal_button);
        addPhotoBtn= findViewById(R.id.postCameraButton);
        addPost = findViewById(R.id.add_post_txt);

        progressBar.setVisibility(View.INVISIBLE);
        //Firebase
        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        if(user != null){
            currentUserId = user.getUid();
            currentUserName = user.getDisplayName();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveJournal();
            }
        });

        mTakePhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        imageView.setImageURI(result);
                        imageUri = result;
                    }
                });


        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Getting the image from the gallery
                mTakePhoto.launch("image/*");
            }
        });


    }

    private void saveJournal() {
        String title = titleEditTxt.getText().toString().trim();
        String thoughts = thoughtsEditTxt.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri!= null){

            //The saving path of the images in the Firebase Storage
            final StorageReference filePath = storageReference
                    .child("journal_images")
                    .child("my_image_"+ Timestamp.now().getSeconds());

            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    //Creating a Journal Object
                                    Journal journal = new Journal();
                                    journal.setTitle(title);
                                    journal.setThoughts(thoughts);
                                    journal.setImageUrl(imageUrl);

                                    journal.setTimeAdded(new Timestamp(new Date()));
                                    journal.setUserName(currentUserName);
                                    journal.setUserId(currentUserId);

                                    collectionReference.add(journal)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    progressBar.setVisibility(View.INVISIBLE);

                                                    Intent i = new Intent(AddJournalActivity.this, JournalActivityList.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(AddJournalActivity.this, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddJournalActivity.this, "Failed !", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user= firebaseAuth.getCurrentUser();
    }
}