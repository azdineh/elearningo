package com.sim.elearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SessionsActivity extends AppCompatActivity implements SessionAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SessionAdapter sessionAdapter;

    private CollectionReference sessionsRef;
    private FirebaseFirestore firestore;
    private final String TAG = "Elerningo";
    private List<Session> sessions;
    private User teacher;
    private DocumentReference userRef;
    private TextView textView;
    private String activitysender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);


        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        textView = findViewById(R.id.textView4);


        teacher = (User) getIntent().getSerializableExtra("teacher");
        activitysender = (String) getIntent().getSerializableExtra("activitysender");
        if (activitysender == null) {
            activitysender = "teacherprofil";
        }

        setTitle("Sessions of Prof.:" + teacher.getFullname());


        sessions = new ArrayList<>();


        firestore = FirebaseFirestore.getInstance();
        sessionsRef = firestore.collection("sessions");

        sessionAdapter = new SessionAdapter(SessionsActivity.this, sessions);
        recyclerView.setAdapter(sessionAdapter);

        sessionAdapter.setOnItemClickListener(this);


        //sessions = db.collection("users").document(teacher.getId());

    }


    @Override
    protected void onResume() {
        super.onResume();
        sessions.clear();
        sessionsRef
                .whereEqualTo("teacherid", teacher.getId())
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() == 0) {
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        for (QueryDocumentSnapshot qDS : queryDocumentSnapshots) {
                            Session s = qDS.toObject(Session.class);
                            s.setId(qDS.getId());
                            //sessions.add(s);

                            sessionAdapter.add(0, s);

                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                recyclerView.setAdapter(sessionAdapter);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });

    }

    @Override
    public void onItemClick(int postion) {

        Session s = sessions.get(postion);
        //Toast.makeText(this, s.getModulename() + " : files count :" + s.getFilesContent().size(), Toast.LENGTH_SHORT).show();

        if (activitysender.compareTo("availableteachersactivity") == 0) {
            //Toast.makeText(this, "To sessio view as student", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, SessionDetailStudentActivity.class);
            i.putExtra("session", s);
            User student = (User) getIntent().getSerializableExtra("student");
            i.putExtra("student", student);
            int p = postion + 1;
            i.putExtra("sessionnumber", p);
            i.putExtra("teacher", teacher);
            startActivity(i);
        } else {

            Intent addSessionActivity = new Intent(this, AddSessionActivity.class);
            addSessionActivity.putExtra("session", s);

            startActivity(addSessionActivity);
            finish();
        }
    }
}
