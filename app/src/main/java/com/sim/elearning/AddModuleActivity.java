package com.sim.elearning;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AddModuleActivity extends AppCompatActivity {

    FirebaseFirestore fstore;
    DocumentReference userRef;
    User teacher;

    EditText editTextModuleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_module);

        setTitle("Add new module");
        fstore = FirebaseFirestore.getInstance();
        editTextModuleName=findViewById(R.id.eTModuleName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        teacher = (User) getIntent().getSerializableExtra("teacher");
        userRef = fstore.collection("users").document(teacher.getId());

    }

    public void addModule(View view) {
        if(TextUtils.isEmpty(editTextModuleName.getText())){
            editTextModuleName.setError("Name is required");
        }
        else {
            teacher.getModules().add(editTextModuleName.getText().toString());
            userRef.update("modules",teacher.getModules());
            Toast.makeText(this, "Module is added", Toast.LENGTH_SHORT).show();
        }
    }
}
