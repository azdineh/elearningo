package com.sim.elearning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AddSessionActivity extends AppCompatActivity {

    ListView listView1;
    User teacher;

    private DocumentReference userRef;
    private FirebaseFirestore db;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private EditText editTextTitle;
    private EditText editTextDescription;

    TextView tVDate;
    ArrayList<FileContent> items;
    ArrayAdapter<FileContent> itemsAdapter;
    Uri fileuri;
    ProgressBar progressBar;
    Session session;
    int selectedItemIndex = 0;
    int currentSelectItemSpinner = -1;

    Button addButton, removeButton, uploadButton, removeSessionButton, savaButton;
    TextView textViewOpenDisscussion;

    private final String TAG = "Elerningo";


    Spinner spinner1;

    private Date now;
    private String activitysender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        setTitle("Add new session");

        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();


        listView1 = findViewById(R.id.listView1);
        spinner1 = findViewById(R.id.spinner1);

        addButton = findViewById(R.id.buttonAdd);
        removeButton = findViewById(R.id.intervention_buttonRemove);
        uploadButton = findViewById(R.id.buttonUpload);
        removeSessionButton = findViewById(R.id.buttonRemoveSession);
        textViewOpenDisscussion = findViewById(R.id.textView_openDiscussion);
        savaButton = findViewById(R.id.buttonSave);
        editTextTitle = findViewById(R.id.edittext_title);
        editTextDescription = findViewById(R.id.textview_teacher_description);


        tVDate = findViewById(R.id.textViewDate);

        progressBar = findViewById(R.id.progressBar2);


        items = new ArrayList<>();
/*        items.add("lesson1");
        items.add("TP1");
        items.add("TD2");*/

        session = (Session) getIntent().getSerializableExtra("session");
        activitysender = (String) getIntent().getSerializableExtra("activitysender");


        String currentDate = null;
        if (activitysender != null) {

            session = new Session();

            Calendar calendar = Calendar.getInstance();
            now = calendar.getTime();
            currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(now);


        } else {
            setTitle("Session details");
            //savaButton.setText("Update this session");
            savaButton.setVisibility(View.GONE);
            removeSessionButton.setVisibility(View.VISIBLE);
            textViewOpenDisscussion.setVisibility(View.VISIBLE);
            addButton.setEnabled(false);
            removeButton.setEnabled(false);
            editTextTitle.setText(session.getTitle());
            editTextTitle.setEnabled(false);
            items.addAll(session.getFilesContent());
            spinner1.setEnabled(false);
            currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(Long.parseLong(session.getDate()));
            editTextDescription.setText(session.getDescription());
            editTextDescription.setEnabled(false);
        }

        tVDate.setText(currentDate);
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);


        listView1.setAdapter(itemsAdapter);

        //public final TextView v;
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = ((TextView) view);
                int l = listView1.getCount();
                String msg = " count " + l + " current position " + position;
                Toast.makeText(AddSessionActivity.this, msg, Toast.LENGTH_SHORT).show();

                for (int i = 0; i < l; i++) {
                    parent.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }

                v.setBackgroundColor(Color.argb(70, 200, 200, 200));
                //
                removeButton.setVisibility(View.VISIBLE);
                //uploadButton.setVisibility(View.VISIBLE);
                selectedItemIndex = position;
                removeButton.setEnabled(true);
//                if (items.get(selectedItemIndex).getUri().isEmpty()) {
//                    uploadButton.setEnabled(true);
//                } else {
//                    uploadButton.setEnabled(false);
//                }


            }
        });


    }

    public void fillSpinner(User teacher) {
        ArrayList<String> modulelist = teacher.getModules();
        modulelist.add(0, "Select a module");
        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, modulelist);

        spinner1.setAdapter(spinnerAdapter);
        spinner1.setSelection(0);

    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    @Override
    protected void onResume() {
        super.onResume();


        session = (Session) getIntent().getSerializableExtra("session");
        activitysender = (String) getIntent().getSerializableExtra("activitysender");

        String teacherid = null;
        if (activitysender != null) {

            teacher = (User) getIntent().getSerializableExtra("teacher");
            teacherid = teacher.getId();
            session.setTeacherid(teacher.getId());
            Log.d(TAG, "onResume: teacher2 id" + teacherid);
        } else {
            teacherid = session.getTeacherid();
            Log.d(TAG, "onResume: teacher1 id" + teacherid);

        }
        userRef = db.collection("users").document(teacherid);


        if (currentSelectItemSpinner < 0) {


            userRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot ds) {

                            teacher = ds.toObject(User.class);
                            teacher.setModules((ArrayList<String>) ds.get("modules"));
                            fillSpinner(teacher);
                            if (session.getTeacherid() != "") {
                                int pos = teacher.getModules().indexOf(session.getModulename());
                                spinner1.setSelection(pos);
                            }
                        }
                    });
        }

        //Toast.makeText(this, "On resume event", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        currentSelectItemSpinner = spinner1.getSelectedItemPosition();
    }


    public void shooseFile(View view) {

        Intent chooserIntent = new Intent();
        chooserIntent.setType("*/*");
        chooserIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(chooserIntent, 120);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 120 && resultCode == RESULT_OK && data != null && data.getData() != null) {


            fileuri = data.getData();
            //Toast.makeText(this, "file uri" + data.getData().getPath(), Toast.LENGTH_LONG).show();
            FileContent fCBeforeUploading = new FileContent();
            fCBeforeUploading.setName(getFullName(data.getData()));
            //items.add(fCBeforeUploading);
            itemsAdapter.add(fCBeforeUploading);
            //listView1.setAdapter(itemsAdapter);
            //listView1.setSelection(items.size() - 1);
            selectedItemIndex = items.size() - 1;
            uploadFile();

            Toast.makeText(this, "Itmes size :" + items.size(), Toast.LENGTH_SHORT).show();


            addButton.setEnabled(false);


        }
    }


    public String getFullName(Uri uri) {
        Cursor returnCursor =
                getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        //int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        return returnCursor.getString(nameIndex);
    }

    private void uploadFile() {
        if (spinner1.getSelectedItemPosition() < 0) {
            Toast.makeText(this, "No module name selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fileuri != null) {

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(7);
                    uploadButton.setEnabled(false);
                }
            }, 1500);

            StorageReference fileRef = mStorageRef.child("documents/" + System.currentTimeMillis() + "." + getFileExtension(getFullName(fileuri)));

            fileRef.putFile(fileuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }, 500);

                            Toast.makeText(AddSessionActivity.this, "File uploaded", Toast.LENGTH_SHORT).show();

                            //FileContent fC = new FileContent();
                            //fC.setName(getFullName(fileuri));

                            //Toast.makeText(AddSessionActivity.this, "Current index :" + selectedItemIndex, Toast.LENGTH_SHORT).show();
                            items.get(selectedItemIndex).setUri(taskSnapshot.getUploadSessionUri().toString());
                            items.get(selectedItemIndex).setRemoteName(taskSnapshot.getMetadata().getName());

                            //Toast.makeText(AddSessionActivity.this, "Remote name :" + taskSnapshot.getMetadata().getName(), Toast.LENGTH_SHORT).show();

                            session.getFilesContent().add(items.get(selectedItemIndex));

                            ((TextView) listView1.getChildAt(selectedItemIndex)).setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                            addButton.setEnabled(true);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddSessionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                            progressBar.setProgress((int) (progress));
                            //Toast.makeText(AddSessionActivity.this, ""+progress, Toast.LENGTH_SHORT).show();

                        }
                    });


        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }


    }

    public String getFileExtension(String fullname) {

        int pointIndex = fullname.lastIndexOf(".");
        return fullname.substring(pointIndex + 1, fullname.length());
    }


    public void saveSession(View view) {


        if (spinner1.getSelectedItemPosition() < 1) {
            Toast.makeText(this, "No module name selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTextTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, "No session title is entred", Toast.LENGTH_SHORT).show();
            return;
        }

        session.setDate("" + System.currentTimeMillis());
        session.setTitle(editTextTitle.getText().toString());
        session.setModulename(spinner1.getSelectedItem().toString());
        session.setDescription(editTextDescription.getText().toString());
        Log.d(TAG, "saveSession: teacher id" + teacher.getId());
        //session.setTeacherid(teacher.getId());

        //save sessions Collection
        final CollectionReference sessionsRef = db.collection("sessions");
        final DocumentReference sessionRef = sessionsRef.document(); //new document in firebase
        //session.setId(sessionRef.getId());
        final DocumentReference discussionRef = db.collection("discussions").document();
        session.setDiscussionid(discussionRef.getId());
        sessionRef.set(session)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //session saved
                        Toast.makeText(AddSessionActivity.this, "Session saved", Toast.LENGTH_SHORT).show();

                        Discussion discussion = new Discussion();
                        discussionRef.set(discussion)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Toast.makeText(AddSessionActivity.this, "", Toast.LENGTH_SHORT).show();
                                        session.setId(sessionRef.getId());
                                        session.setDiscussionid(sessionsRef.getId());
                                    }
                                });
                        onBackPressed();
                        finish();
                    }
                });


    }

    public void uploadFile(View view) {
        uploadFile();


    }

    public void removeFile(View view) {
        final FileContent currentFC = items.get(selectedItemIndex);


        if (!currentFC.getUri().isEmpty()) {
            StorageReference fileRef = mStorageRef.child("documents/" + currentFC.getRemoteName());


            fileRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            itemsAdapter.remove(currentFC);
                            Toast.makeText(AddSessionActivity.this, "File removed successfully", Toast.LENGTH_SHORT).show();
                            addButton.setVisibility(View.VISIBLE);
                            removeButton.setEnabled(false);
                            uploadButton.setEnabled(false);
                        }
                    });
        } else {
            itemsAdapter.remove(currentFC);
            addButton.setEnabled(true);
            removeButton.setEnabled(false);
            uploadButton.setEnabled(false);
        }


    }

    public void removeFileFun(final FileContent fc) {


        if (!fc.getUri().isEmpty()) {
            StorageReference fileRef = mStorageRef.child("documents/" + fc.getRemoteName());
            fileRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddSessionActivity.this, "File removed successfully", Toast.LENGTH_SHORT).show();
                            rcount = rcount + 1;
                        }
                    });
        } else {
        }
    }

    int rcount = 0;

    public void removeFilesContent(final Session s) {

        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (rcount < s.getFilesContent().size()) {
                    removeFileFun(s.getFilesContent().get(rcount));
                } else {
                    //Toast.makeText(AddSessionActivity.this, "Files removed", Toast.LENGTH_SHORT).show();
                    if (session.getId() != null) {
                        DocumentReference sessionRef = db.collection("sessions").document(session.getId());

                        sessionRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AddSessionActivity.this, "Session deleted", Toast.LENGTH_SHORT).show();

                                        Intent sessionsActivity = new Intent(AddSessionActivity.this, SessionsActivity.class);
                                        User teacher = new User();
                                        teacher.setId(session.getTeacherid());
                                        sessionsActivity.putExtra("teacher", teacher);
                                        startActivity(sessionsActivity);
                                        finish();
                                    }
                                });

                    }
                    rcount = 0;
                    timer.cancel();
                }
            }
        }, 0, 700);
    }


    public void removeSession(View v) {

        removeFilesContent(session);

    }

    public void toDiscussion(View view) {
        Intent i = new Intent(this, DescussionActivity.class);
        i.putExtra("session", session);
        i.putExtra("user", teacher);
        startActivity(i);
        //finish();

    }
}
