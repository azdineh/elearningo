package com.sim.elearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DirectAction;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;

public class SessionDetailStudentActivity extends AppCompatActivity {

    private Session session;
    private TextView textViewDate;
    private TextView textViewSessionTitle;
    private TextView textViewModuleName;
    private TextView textViewTeacherName;
    private ListView litsview;
    private String sessiondate;
    private TextView textViewDescription;
    private int sessionnumber;
    private User teacher,student;
    private ArrayAdapter<FileContent> arrayAdapter;
    private StorageReference fbsRef;

    private final String TAG = "Elerningo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail_student);

        textViewDate = findViewById(R.id.textView15);
        textViewSessionTitle = findViewById(R.id.textview_session_name);
        textViewModuleName = findViewById(R.id.textview_module_name);
        textViewTeacherName = findViewById(R.id.textview_teachername);
        litsview = findViewById(R.id.listview3);
        textViewDescription = findViewById(R.id.textview_teacher_speech);

        fbsRef = FirebaseStorage.getInstance().getReference();

        setTitle("Session details");

        session = (Session) getIntent().getSerializableExtra("session");
        teacher = (User) getIntent().getSerializableExtra("teacher");
        student = (User) getIntent().getSerializableExtra("student");
        sessionnumber = (int) getIntent().getIntExtra("sessionnumber", 0);

        sessiondate = DateFormat.getDateInstance(DateFormat.FULL).format(Long.parseLong(session.getDate()));
        textViewDate.setText(sessiondate);
        textViewDescription.setText(session.getDescription());

        textViewSessionTitle.setText("Session " + sessionnumber + ": " + session.getTitle());
        textViewModuleName.setText(session.getModulename());
        textViewTeacherName.setText(teacher.getFullname());


        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, session.getFilesContent());
        litsview.setAdapter(arrayAdapter);

        Handler hander = new Handler();
        hander.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < litsview.getCount(); i++) {

                    TextView anItem = (TextView) litsview.getChildAt(i);
                    anItem.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_file_download_black_24dp, 0);
                }
            }
        }, 900);


        litsview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileContent fC = (FileContent) session.getFilesContent().get(position);
                download(fC.getRemoteName(), fC.getName());
            }
        });


    }

    private void download(String remotefilename, final String nameInLocal) {


        StorageReference fileRef = fbsRef.child("documents/" + remotefilename);
        fileRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(uri);

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, nameInLocal);

                        downloadManager.enqueue(request);
                        Toast.makeText(getApplicationContext(), "start downloading ", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void toDiscussion(View view) {
        Intent i = new Intent(this, DescussionActivity.class);
        i.putExtra("session", session);
        i.putExtra("user", student);
        startActivity(i);
        //finish();

    }
}

