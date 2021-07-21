package com.example.oop_project_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 111;
    //CallbackManager mCallbackManager;
    EditText mEmail,mPassword;
    Button mLogin,mSignUpNow;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    //LoginButton loginButton;
    GoogleSignInClient mGoogleSignInClient;
    ImageView Image;
    private FirebaseFirestore fstore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         mEmail = findViewById(R.id.email);
         mPassword =findViewById(R.id.password);
         mLogin = findViewById(R.id.button);
         mSignUpNow = findViewById(R.id.signup);
        // progressBar = findViewById(R.id.progressBar2);
         fAuth = FirebaseAuth.getInstance();
         Image = findViewById(R.id.GoogleSignIn);
         fstore = FirebaseFirestore.getInstance();
//         mCallbackManager = CallbackManager.Factory.create();
//         loginButton = findViewById(R.id.login_button);
//         loginButton.setPermissions("email");

        if(fAuth.getCurrentUser()!=null && fAuth.getCurrentUser().isEmailVerified() == true)
        {
            Toast.makeText(MainActivity.this, fAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),Dashboard.class));
        }
       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }

        });




                mLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = mEmail.getText().toString().trim();
                        String password = mPassword.getText().toString().trim();
                        if (TextUtils.isEmpty(email)) {
                            mEmail.setError("Email Required");
                            return;
                        }
                        if (TextUtils.isEmpty(password)) {
                            mPassword.setError("Password Required");
                            return;
                        }
                       // progressBar.setVisibility(View.VISIBLE);

                        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (fAuth.getCurrentUser().isEmailVerified()) {
                                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                    } else {
                                        Toast.makeText(MainActivity.this, "Please Verify Your Email to proceed", Toast.LENGTH_LONG).show();
                                       // progressBar.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                   // progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                });

         mSignUpNow.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(getApplicationContext(),register1.class));
             }
         });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("GoogleLogin", "Google sign in failed", e);
                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            String email = user.getEmail().toString();
                            DocumentReference documentReference = fstore.collection("users").document(email);

                            Map <String,Object> User = new HashMap<>();
                            User.put("Email",email);
                            documentReference.get();
                            documentReference.set(User).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   // Log.d("TAG","OnSuccess : userprofile is created for " + User);

                                }
                            });
                            startActivity(new Intent(getApplicationContext(),Dashboard.class));
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("GoogleSignIn", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG);
                            //Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                });
    }



}