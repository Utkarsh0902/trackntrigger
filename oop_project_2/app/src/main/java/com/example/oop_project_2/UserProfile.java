package com.example.oop_project_2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UserProfile extends AppCompatActivity {
    private static final int REQUEST_GET_SINGLE_FILE = 11;
    EditText mEmail,mPhone,mProfession;
TextView lEmail,lPhone,lProfession;
TextView username;
ImageView bEmail,bPhone,bProfession,BackButton,Profile_Image;
FirebaseAuth fAuth;
FirebaseFirestore fstore;
Button Logout;
Button Save,Cancel;
String Susername,Sphone,Semail;
DocumentReference documentReference;
StorageReference mStorageRef;
    Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mEmail = findViewById(R.id.email_show);
        mPhone = findViewById(R.id.phone_show);
        //mProfession = findViewById(R.id.profession);
        username = findViewById(R.id.name);
        BackButton = findViewById(R.id.BackButton);
        Logout = findViewById(R.id.logout_Button);
        //Save = findViewById(R.id.Save_Button);
        Cancel = findViewById(R.id.cancel_button);
        Profile_Image = findViewById(R.id.profile_image);
        //bProfession = findViewById(R.id.profession_button);
        lEmail = findViewById(R.id.email_text_view);
        lPhone = findViewById(R.id.phone_textView);
        //lProfession = findViewById(R.id.profession_textView);
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        documentReference = fstore.collection("users").document(fAuth.getCurrentUser().getEmail());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Susername = documentSnapshot.getString("Username");
                    Semail = documentSnapshot.getString("Email");
                    Sphone = documentSnapshot.getString("Phone");
                    username.setText(Susername + "'s Profile");
                    mEmail.setText(Semail);
                    mPhone.setText(Sphone);
                } else {
                    Toast.makeText(UserProfile.this, "No user registered with this email", Toast.LENGTH_LONG).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        try {
            File file = File.createTempFile("Profile_Image","jpg");
            StorageReference reference = mStorageRef.child("images/" + fAuth.getCurrentUser().getEmail() + "/Profile_image.jpg");
            reference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Profile_Image.setImageURI(Uri.fromFile(file));
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfile.this,e.getMessage(),Toast.LENGTH_LONG);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }



        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
            }
        });


        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = fstore.collection("users").document(fAuth.getCurrentUser().getEmail());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Map <String, Object> map = documentSnapshot.getData();
                            if(map.containsKey("Phone")  ){
                                FirebaseAuth.getInstance().signOut();
                            }
                            else {
                                GoogleSignInOptions gso = new GoogleSignInOptions.
                                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                                        build();

                                GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(UserProfile.this, gso);
                                googleSignInClient.signOut();
                            }
                        }
                        else {

                        }
                    }
                });
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });




//        bProfession.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mProfession.setEnabled(true);
//                BackButton.setVisibility(View.INVISIBLE);
//                Logout.setVisibility(View.INVISIBLE);
//                mEmail.setVisibility(View.INVISIBLE);
//                mPhone.setVisibility(View.INVISIBLE);
//                bPhone.setVisibility(View.INVISIBLE);
//                bProfession.setVisibility(View.INVISIBLE);
//                lEmail.setVisibility(View.INVISIBLE);
//                lPhone.setVisibility(View.INVISIBLE);
//               // Save.setVisibility(View.VISIBLE);
//                Cancel.setVisibility(View.VISIBLE);
//            }
//        });


//        Save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String profession_local = mProfession.getText().toString().trim();
//                mProfession.setText(profession_local);
//                mPhone.setEnabled(false);
//                mProfession.setEnabled(false);
//                BackButton.setVisibility(View.VISIBLE);
//                Logout.setVisibility(View.VISIBLE);
//                mEmail.setVisibility(View.VISIBLE);
//                mProfession.setVisibility(View.VISIBLE);
//                lEmail.setVisibility(View.VISIBLE);
//                lProfession.setVisibility(View.VISIBLE);
//                lPhone.setVisibility(View.VISIBLE);
//                bPhone.setVisibility(View.VISIBLE);
//                bProfession.setVisibility(View.VISIBLE);
//                Save.setVisibility(View.INVISIBLE);
//                Cancel.setVisibility(View.INVISIBLE);
//
//            }
//        });


//        Cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPhone.setEnabled(false);
//                mProfession.setEnabled(false);
//                BackButton.setVisibility(View.VISIBLE);
//                Logout.setVisibility(View.VISIBLE);
//                mEmail.setVisibility(View.VISIBLE);
//                mProfession.setVisibility(View.VISIBLE);
//                bPhone.setVisibility(View.VISIBLE);
//                bProfession.setVisibility(View.VISIBLE);
//                Save.setVisibility(View.INVISIBLE);
//                Cancel.setVisibility(View.INVISIBLE);
//            }
//        });

        Profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_SINGLE_FILE);
            }
        });
    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);
            try {
                if (resultCode == RESULT_OK) {
                    if (requestCode == REQUEST_GET_SINGLE_FILE) {
                       selectedImageUri = data.getData();
                        // Get the path from the Uri
                        final String path = getPathFromURI(selectedImageUri);
                        if (path != null) {
                            File f = new File(path);
                            selectedImageUri = Uri.fromFile(f);
                        }

                        documentReference = fstore.collection("users").document(fAuth.getCurrentUser().getEmail());
                        StorageReference reference = mStorageRef.child("images/" + fAuth.getCurrentUser().getEmail() + "/Profile_image.jpg");
                        reference.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Set the image in ImageView
                                Profile_Image.setImageURI(selectedImageUri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserProfile.this,e.getMessage(),Toast.LENGTH_LONG);
                            }
                        });



                    }
                }
            } catch (Exception e) {
                Log.e("FileSelectorActivity", "File select error", e);
            }
        }
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}