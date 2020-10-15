package com.sim.elearning;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class AbsenceActivity extends AppCompatActivity {

    private DocumentReference sessionRef;
    private FirebaseFirestore db;

    List<String> students;
    ListView listViewAbsents;
    TextView textViewSessionTitle,getTextViewSessionModule;
    EditText editTextFullname;
    ListAdapter listAdapter;
    Button addButton,removeButton;
    int selectIndex;
    Session session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence);
        setTitle("Manage Student Absences");

        db = FirebaseFirestore.getInstance();



        students=new ArrayList<String>();

        listViewAbsents = findViewById(R.id.list_view_absence);
        textViewSessionTitle=findViewById(R.id.text_view_session_title);
        getTextViewSessionModule=findViewById(R.id.text_view_sesssion_module);
        editTextFullname=findViewById(R.id.edit_text_fullname);
        addButton=findViewById(R.id.button_add);
        removeButton=findViewById(R.id.button_remove);

        session = (Session) getIntent().getSerializableExtra("session");
        textViewSessionTitle.setText("Session: "+session.getTitle());
        getTextViewSessionModule.setText("Module :"+session.getModulename());

        listAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,students);
        listViewAbsents.setAdapter(listAdapter);

        sessionRef = db.collection("sessions").document(session.getId());

        sessionRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot ds) {

                        //teacher = ds.toObject(User.class);

                        session.setAbsentStudents((ArrayList<String>) ds.get("absentStudents"));
                        if(session.getAbsentStudents() != null){
                            for(int i=0;i<session.getAbsentStudents().size();i++){
                                students.add(session.getAbsentStudents().get(i));
                            }
                            listViewAbsents.setAdapter(listAdapter);
                        }



                    }
                });




        //students.add(new User("Mounir karimi"));
        //students.add(new User("Jamal jamali"));
        //students.add(new User("Kawtar Redouani"));




        listViewAbsents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = ((TextView) view);
                int l =students.size();

                for (int i = 0; i < l; i++) {
                    parent.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }

                v.setBackgroundColor(Color.argb(85, 200, 200, 200));

                if(position>=0){
                    selectIndex=position;
                    removeButton.setEnabled(true);
                }
            }
        });
    }

    public void addAbsentStudent(View view) {
        if(editTextFullname.getText().toString().isEmpty()){
            editTextFullname.setError("Enter a full name");
        }
        else {
            String st=new String(editTextFullname.getText().toString());
            session.getAbsentStudents().add(st);
            students.add(st);
            listViewAbsents.setAdapter(listAdapter);

            editTextFullname.getText().clear();
        }

    }

    public void removeAbsentStudent(View view) {

        session.getAbsentStudents().remove(selectIndex);
        students.remove(selectIndex);
        listViewAbsents.setAdapter(listAdapter);

        removeButton.setEnabled(false);
    }

    public void saveaAbsentStudent(View view) {

        //session.setAbsentStudents((ArrayList<User>) students);
        sessionRef.set(session)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //session saved
                        Toast.makeText(AbsenceActivity.this, "Absent students saved", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        finish();
                    }
                });
    }
}