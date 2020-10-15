package com.sim.elearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AvailableTeachersActivity extends AppCompatActivity {

    private FirebaseFirestore fstore;
    private CollectionReference usersRef;
    private ArrayList<User> teachers;
    private ListView listView;
    private ArrayAdapter<User> teachersAdapter;

    private final String TAG = "Elerningo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_teachers);

        setTitle("Available Teachers");

        listView = findViewById(R.id.listview2);

        fstore = FirebaseFirestore.getInstance();
        usersRef = fstore.collection("users");

        teachers = new ArrayList<>();

        teachersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, teachers);
        listView.setAdapter(teachersAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User teacher = teachers.get(position);
                //Log.d(TAG, "onItemClick: "+teacher.getId());
                Intent sessionsActivity = new Intent(getApplicationContext(), SessionsActivity.class);
                sessionsActivity.putExtra("teacher", teacher);
                User student= (User) getIntent().getSerializableExtra("student");
                sessionsActivity.putExtra("student",student);
                sessionsActivity.putExtra("activitysender", "availableteachersactivity");
                startActivity(sessionsActivity);
            }
        });

        usersRef
                .whereEqualTo("usertype", "teacher")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot qDS : queryDocumentSnapshots) {
                            User teacher = qDS.toObject(User.class);
                            teacher.setId(qDS.getId());
                            teachersAdapter.add(teacher);


                        }
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (int i=0;i<listView.getCount();i++){
                                    TextView tV = (TextView) listView.getChildAt(i);
                                    tV.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_person_inverse_24dp, 0, 0, 0);
                                }
                            }
                        }, 50);


                    }
                });

    }
}
