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
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView titleTextView, registerTextView, forgotTextView;
    private ImageView logoImageView;
    private Button loginButton;
    private EditText usernameEditedText, passwordEditText;
    private FirebaseUser currentUser;
    private ProgressBar progressbar;

    private final String TAG = "Elerningo";

    private FirebaseAuth mAuth;
    private FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        titleTextView = findViewById(R.id.title_textView);
        registerTextView = findViewById(R.id.register_textView);
        forgotTextView = findViewById(R.id.forgot_textView);

        progressbar = findViewById(R.id.progressBar);

        logoImageView = findViewById(R.id.log_imageView);

        loginButton = findViewById(R.id.login_button);

        usernameEditedText = findViewById(R.id.username_editText);
        passwordEditText = findViewById(R.id.password_editText);


        // Check if user is signed in (non-null) and update UI accordingly.
        //currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        //if (currentUser != null) {
        //}


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditedText.getText().toString();
                String password = passwordEditText.getText().toString();


                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter username and password", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    progressbar.setVisibility(View.VISIBLE);
                }

                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressbar.setVisibility(View.INVISIBLE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                }


                            }
                        });

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();


    }


    public void toProfilActivity(FirebaseUser user) {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        profileIntent.putExtra("email", user.getEmail());
        startActivity(profileIntent);
        finish();
    }


    public void updateUI(final FirebaseUser fuser) {
        //toProfilActivity(user);

        DocumentReference userRef = fstore.collection("users").document(fuser.getUid());
        final User user = new User();

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String userid = fuser.getUid();
                            user.setId(userid);
                            user.setFullname(documentSnapshot.getString("fullname"));
                            user.setEmail(documentSnapshot.getString("email"));
                            user.setType(documentSnapshot.getString("usertype"));
                            user.setModules((ArrayList<String>) documentSnapshot.get("modules"));

                            if (user.getType().compareTo("teacher") == 0) {
                                Log.i(TAG, "It is a teacher");
                                toProfHomeActivity(user);
                            } else {
                                // go to student profile
                                Log.i(TAG, "It is a student");
                                toStudentActivity(user);
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error with user data", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void toProfHomeActivity(User teacher) {
        Intent profHomeIntent = new Intent(this, ProfHomeActivity.class);
        profHomeIntent.putExtra("teacher", teacher);
        startActivity(profHomeIntent);
        finish();
    }

    private void toStudentActivity(User student) {
        Intent studentHomeActivity = new Intent(this, StudentHomeActivity.class);
        studentHomeActivity.putExtra("student", student);
        startActivity(studentHomeActivity);
        finish();
    }

    public void toRegisterForm(View view) {
        Intent regitrationIntent = new Intent(this, RegistrationActivity.class);
        startActivity(regitrationIntent);
        finish();
    }
}
