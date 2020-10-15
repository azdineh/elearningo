package com.sim.elearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class StudentHomeActivity extends AppCompatActivity {


    private User student;
    private TextView textViewName;
    private TextView textViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        setTitle("Student Home");

        student=(User) getIntent().getSerializableExtra("student");
        
        textViewName=findViewById(R.id.textViewFullName2);
        textViewEmail=findViewById(R.id.textViewEmail2);

        textViewName.setText(student.getFullname());
        textViewEmail.setText(student.getEmail());

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    public void toAvailableTeachers(View view) {
        Intent availabeTeachersActivity=new Intent(this,AvailableTeachersActivity.class);
        availabeTeachersActivity.putExtra("student",student);
        startActivity(availabeTeachersActivity);
    }
}
