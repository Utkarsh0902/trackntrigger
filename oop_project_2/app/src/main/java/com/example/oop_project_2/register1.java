package com.example.oop_project_2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class register1 extends AppCompatActivity {
    EditText mEmail,mPassword,mConfirmPassword,mUsername,mPhone;
    Button mSignupnow;
    TextView mLogin;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    ProgressBar progressBar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        mEmail = findViewById(R.id.Remail);
        mPassword = findViewById(R.id.Rpassword);
        mConfirmPassword = findViewById(R.id.Rconfirm_password);
        mSignupnow = findViewById(R.id.RSingupbutton);
        mLogin = findViewById(R.id.RLogin);
        mUsername = findViewById(R.id.rusername);
        mPhone = findViewById(R.id.rphone);
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.RprogressBar);

        if(fAuth.getCurrentUser()!=null && fAuth.getCurrentUser().isEmailVerified() == true)
        {
            Toast.makeText(register1.this, fAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),Dashboard.class));
        }
        mSignupnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmpassword = mConfirmPassword.getText().toString().trim();
                String username = mUsername.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("Email Required");
                    return;
                }
                if(Pattern.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$",email) == false)
                {
                    mEmail.setError("Enter Valid Email-ID");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    mPassword.setError("Password Required");
                    return;
                }
                if(Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",password) == false)
                {
                    mPassword.setError("Password must contain Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character");
                    return;
                }
                if(TextUtils.isEmpty(confirmpassword) || password.equals(confirmpassword) == false)
                {
                    mConfirmPassword.setError("Password and Confirm Password do not Match");
                    return;
                }
                if(TextUtils.isEmpty(username))
                {
                    mUsername.setError("Username Required");
                    return;
                }
                if(Pattern.matches("^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$",username) == false)
                {
                 mUsername.setError("Username must contain only alphanumeric letters,dots,underscores,hyphens and must be 5-20 characters long");
                 return;
                }
                if(TextUtils.isEmpty(phone))
                {
                    mPhone.setError("Phone No. Required");
                    return;
                }
                if(Pattern.matches("[0-9]{10}",phone) == false)
                {
                    mPhone.setError("Enter correct 10 digit phone number");
                    return;
                }
                DocumentReference documentReference = fstore.collection("users").document(email);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.exists())
                                {
                                    mEmail.setError("Email already exits");
                                    return;
                                }
                                else
                                {
                                    Map<String,Object> user = new HashMap<>();
                                    Uri path = Uri.parse("android.resource://your.package.name/" + R.drawable.profile_image);
                                    String imgPath = path.toString();
                                    user.put("Username",username);
                                    user.put("Email",email);
                                    user.put("Phone",phone);
                                    user.put("Profile_Image",imgPath);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG","OnSuccess : userprofile is created for " + username);

                                        }
                                    });
                                    progressBar.setVisibility(View.VISIBLE);
                                    fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()) {
                                                fAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                            Toast.makeText(register1.this, "Registered Successfully Please check your email for verification", Toast.LENGTH_LONG).show();
                                                            mEmail.setText("");
                                                            mPassword.setText("");
                                                            mConfirmPassword.setText("");
                                                            mPhone.setText("");
                                                            mUsername.setText("");
                                                        } else {
                                                            Toast.makeText(register1.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                });
                                            }



                                            else
                                            {
                                                Toast.makeText(register1.this,task.getException().getMessage() ,Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                                }
                            }
                            else
                            {
                                Log.d("Username", "Error = "+ task.getException().getMessage());
                            }
                    }
                });

                return;


            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}