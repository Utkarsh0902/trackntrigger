package com.example.oop_project_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dashboard extends AppCompatActivity {

    RecyclerView cRecyclerView;
    private CategoryListAdapter cAdapter;
    public static LinkedList<String> categoryList = new LinkedList<>();

    ImageView profile;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    DocumentReference documentReference;
    StorageReference mStorageRef;
    DocumentReference documentReference1, documentReference2;
    Button Todo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        profile = findViewById(R.id.profile_button);
        fAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        fstore = FirebaseFirestore.getInstance();
        Todo = findViewById(R.id.todo_button);
        documentReference1 = fstore.collection("users").document(fAuth.getCurrentUser().getEmail());
        documentReference2 = fstore.collection("users").document(fAuth.getCurrentUser().getEmail() + "1234");
        documentReference2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HashMap <String,Object> map3 = (HashMap<String, Object>) documentSnapshot.getData();
                try {
                    List<String> categories = (List<String>) map3.get("Categories");

                    //Set<String> categories = new HashSet<>();

                    if (categories != null) {
                        for (String category : categories) {
                            categoryList.add(category);
                            int categoryLastSize = categoryList.size();
                            cRecyclerView.getAdapter().notifyItemInserted(categoryLastSize);
                            // Scroll to the bottom.
                            cRecyclerView.smoothScrollToPosition(categoryLastSize);

                        }
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }

        });

        FloatingActionButton categoryFAB = findViewById(R.id.category_fab);
        categoryFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int categoryLastSize = categoryList.size();
                categoryList.add(new String());
                // Notify the adapter, that the data has changed.
                cRecyclerView.getAdapter().notifyItemInserted(categoryLastSize);
                // Scroll to the bottom.
                cRecyclerView.smoothScrollToPosition(categoryLastSize);
            }
        });

        Todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),TodoListMain2.class));
            }
        });

        try {
            File file = File.createTempFile("Profile_Image","jpg");
            StorageReference reference = mStorageRef.child("images/" + fAuth.getCurrentUser().getEmail() + "/Profile_image.jpg");
            reference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    profile.setImageURI(Uri.fromFile(file));
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Dashboard.this,e.getMessage(),Toast.LENGTH_LONG);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UserProfile.class));
            }
        });

        // Get a handle to the RecyclerView.
        cRecyclerView = findViewById(R.id.category_recycler_view);
        // Create an adapter and supply the data to be displayed.
        cAdapter = new CategoryListAdapter(this,categoryList);
        // Connect the adapter with the RecyclerView.
        cRecyclerView.setAdapter(cAdapter);
        // Give the RecyclerView a default layout manager.
        cRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }
}