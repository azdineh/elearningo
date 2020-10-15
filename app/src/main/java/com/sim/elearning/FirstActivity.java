package com.sim.elearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FirstActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore fstore;
    private ProgressBar progressBar;

    private final String TAG = "Elerningo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        progressBar=findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            progressBar.setVisibility(View.VISIBLE);
            updateUI(currentUser);
        }
        else {
            Intent loginIntent = new Intent(this, MainActivity.class);
            startActivity(loginIntent);
            finish();
        }
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

                            progressBar.setVisibility(View.INVISIBLE);
                            if (user.getType().compareTo("teacher") == 0) {
                                Log.i(TAG, "It is a teacher");
                                toProfHomeActivity(user);
                            } else {
                                // go to student profile
                                Log.i(TAG, "It is a student");
                                toStudentActivity(user);
                            }

                        } else {
                            Toast.makeText(FirstActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FirstActivity.this, "Error with user data", Toast.LENGTH_SHORT).show();
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
}
