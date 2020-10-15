package com.sim.elearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class DescussionActivity extends AppCompatActivity implements InterventionAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private InterventionAdapter interventionAdapter;
    private EditText editTextMessage;
    private TextView textViewNoInterventions;
    private Discussion discussion;

    private FirebaseFirestore fstore;
    private DocumentReference discussionRef;
    private Session session;
    private User user;
    private final String TAG = "Elerningo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descussion);
        setTitle("Discussion");

        // from activity sender
        session = (Session) getIntent().getSerializableExtra("session");
        user = (User) getIntent().getSerializableExtra("user");

        fstore = FirebaseFirestore.getInstance();
        discussionRef = fstore.collection("discussions").document(session.getDiscussionid());

        discussion = new Discussion();

        interventionAdapter = new InterventionAdapter(DescussionActivity.this, discussion.getInterventions());
        interventionAdapter.setOnItemClickListener(this);

        discussionRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());
                    discussion = documentSnapshot.toObject(Discussion.class);
                    if(discussion.getInterventions().size()==0){
                        textViewNoInterventions.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onEvent: "+discussion.getInterventions().get(0).getIntervention());
                        interventionAdapter = new InterventionAdapter(DescussionActivity.this, discussion.getInterventions());
                        recyclerView.setAdapter(interventionAdapter);
                    }


                } else {
                    Log.d(TAG, "Current data: null");
                }

            }
        });

        editTextMessage = findViewById(R.id.editText_message);
        recyclerView = findViewById(R.id.recycle_view_desscussion);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textViewNoInterventions = findViewById(R.id.textview_intervention);


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onItemClick(int postion) {
        Toast.makeText(this, "Item clicked" + postion, Toast.LENGTH_SHORT).show();
    }

    public void sendMessage(View view) {
        if (editTextMessage.getText().toString().isEmpty())
            return;

        Intervention v = new Intervention();
        v.setFullname(user.getFullname());
        v.setIntervention(editTextMessage.getText().toString());
        v.setUnixtimestamp(""+System.currentTimeMillis());
        //interventionAdapter.add(v);
        //recyclerView.setAdapter(interventionAdapter);

        discussion.getInterventions().add(v);

        discussionRef.set(discussion)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DescussionActivity.this, "Intervention added", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
