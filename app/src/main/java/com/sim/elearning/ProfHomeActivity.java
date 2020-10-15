package com.sim.elearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfHomeActivity extends AppCompatActivity {

    private TextView textViewFullName;
    private  TextView textViewEmail;

    private User teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_home);

        setTitle("Teacher home");

        textViewFullName=findViewById(R.id.textViewFullName);
        textViewEmail=findViewById(R.id.textViewEmail);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent=getIntent();

        teacher = (User) intent.getSerializableExtra("teacher");
        textViewFullName.setText(teacher.getFullname());
        textViewEmail.setText(teacher.getEmail());


    }

    public void toAddModuleActivity(View view) {
        Intent addModuleActivity = new Intent(this, AddModuleActivity.class);
        addModuleActivity.putExtra("teacher", teacher);
        startActivity(addModuleActivity);
        //finish();
    }

    public void toAddSessionActivity(View view) {
        Intent addSessionActivity = new Intent(this, AddSessionActivity.class);
        addSessionActivity.putExtra("teacher",teacher);
        addSessionActivity.putExtra("activitysender","profhome");
        addSessionActivity.putExtra("session",new Session());
        startActivity(addSessionActivity);
        //finish();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    public void toSessionsActivity(View v){
        Intent sessionsActivity = new Intent(this, SessionsActivity.class);
        sessionsActivity.putExtra("teacher",teacher);
        startActivity(sessionsActivity);
    }


}
