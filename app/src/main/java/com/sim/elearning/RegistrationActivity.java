package com.sim.elearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final String TAG = "Elerningo";

    Button signinButton;
    RadioGroup radioGroup1;
    EditText fullNameText;
    EditText emailText;
    EditText pwdText;
    EditText pwdConfirmText;
    ProgressBar progressBar;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        signinButton = findViewById(R.id.siginupButton);
        radioGroup1 = findViewById(R.id.radioGroup1);
        fullNameText = findViewById(R.id.fullNameText);
        emailText = findViewById(R.id.emailText);
        pwdText = findViewById(R.id.pwdText);
        pwdConfirmText = findViewById(R.id.pwdConfirmText);
        progressBar = findViewById(R.id.progressBar);


    }

    String userType = "student";

    public void checkButton(View view) {
        int checkedButtonRadio = radioGroup1.getCheckedRadioButtonId();

        RadioButton rb = findViewById(checkedButtonRadio);
        userType = rb.getText().toString().toLowerCase();
    }

    public void setSigninfun() {
        Intent signinIntent = new Intent(this, MainActivity.class);
        startActivity(signinIntent);
        finish();
    }

    public void toSigninForm(View view) {
        setSigninfun();
    }

    public void updateUI(FirebaseUser user) {
        setSigninfun();
    }


    public void signup(View view) {

        final String fullName = fullNameText.getText().toString();
        final String email = emailText.getText().toString().trim();
        String password = pwdText.getText().toString();


        if (!validateForm()) {
            return;
        }


        progressBar.setVisibility(View.VISIBLE);


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            final FirebaseUser user = mAuth.getCurrentUser();


                            // Create a new user with a first and last name
                            Map<String, Object> usernew = new HashMap<>();
                            usernew.put("fullname", fullName);
                            usernew.put("usertype", userType);
                            usernew.put("email", email);
                            usernew.put("modules",new ArrayList<String>());
                            //usernew.put("sessionsIDs",new ArrayList<String>());

                            // Add a new document with a user ID
                            DocumentReference df=fstore.collection("users").document(user.getUid());
                            df.set(usernew).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + user.getUid() );
                                    mAuth.signOut();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegistrationActivity.this, "Account successfully created",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                    Toast.makeText(RegistrationActivity.this, "Error creating user",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });


                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            //updateUI(null);
                        }

                    }
                });
    }


    private boolean validateForm() {
        boolean isvalid = true;

        String email = emailText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailText.setError("Required.");
            isvalid = false;
        } else {
            emailText.setError(null);
        }
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!email.matches(emailPattern)) {
            emailText.setError("Email is not correct.");
            isvalid = false;
        } else {
            emailText.setError(null);
        }

        String password = pwdText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            pwdText.setError("Required.");
            isvalid = false;
        } else {
            pwdText.setError(null);
        }
        if (password.length() < 6) {
            pwdText.setError("Password must be at least 6 characters long.");
            isvalid = false;
        } else {
            pwdText.setError(null);
        }

        String passwordConfirm = pwdConfirmText.getText().toString();
        if (!TextUtils.equals(password, passwordConfirm)) {
            pwdConfirmText.setError("Password doesn't match.");
            isvalid = false;
        } else {
            pwdConfirmText.setError(null);
        }


        return isvalid;
    }
}
