package com.example.sujit.docpoint;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class AttendanceResultActivity extends AppCompatActivity {



    String mUser;
    String type;

    DatabaseReference databaseReference;

    RecyclerView recyclerView;

    private FirebaseRecyclerAdapter<Files, AttendanceResultActivity.ViewHolder> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<Files> options;
    private Query query;


    ProgressDialog progressDialog;
    String term,batch,subject,subject_name,prn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_result);

        type=getIntent().getStringExtra("type");

        getSupportActionBar().setTitle("View "+type);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);


        Log.i("type",type);
        mUser= FirebaseAuth.getInstance().getCurrentUser().getUid();

        initializeFirebase();








    }

    private void initializeFirebase() {






        databaseReference.child("uid").child(mUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot!=null)

                {
                    prn=String.valueOf(dataSnapshot.getValue());
                    if(prn!=null)

                    {

                        databaseReference.child("users").child(prn).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot!=null)

                                {
                                    term=String.valueOf(dataSnapshot.child("term").getValue());
                                    batch=String.valueOf(dataSnapshot.child("batch").getValue());
                                    Log.i("batch",batch);

                                    permission_check();
                                }



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private void permission_check() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

            }
        }

        initialize();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            initializeFirebase();
        }else {
            permission_check();
        }
    }

    public void initialize(){


        //listView.setAdapter(arrayAdapter);
        // handler = new Handler();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading...");
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        recyclerView = findViewById(R.id.recyclerview);


        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        query = FirebaseDatabase.getInstance()
                .getReference().child(type).child(batch);

        query.keepSynced(true);

        options = new FirebaseRecyclerOptions.Builder<Files>()
                .setQuery(query, Files.class).setLifecycleOwner(this)
                .build();


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())

                {



                    firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Files, AttendanceResultActivity.ViewHolder>(options) {



                        @Override
                        protected void onBindViewHolder(@NonNull final AttendanceResultActivity.ViewHolder holder, int position, @NonNull final  Files model) {

                            final String docFormat = model.getType();
                            Log.i("fileFormat",docFormat);

                            holder.textView.setText(model.getName());
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getUrl());



                                    try {
                                        //final File localFile = File.createTempFile(model.getPushId(),"."+model.getType(),getCacheDir());

                                        final File localFile = new File(getCacheDir(),model.getName());
                          /* final File localFile;
                            localFile = new File(model.getName());
                            localFile.setWritable(true);*/
                                        if(!localFile.exists()) {
                                            httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    // Local temp file has been created

                                                    //String uri= localFile.getAbsolutePath();
                                                    // Uri uri =Uri.fromFile(localFile);

                                                    Uri uri = FileProvider.getUriForFile(AttendanceResultActivity.this,
                                                            getString(R.string.file_provider_authority),
                                                            localFile);


                                                    Log.i("URI", uri.toString());
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                                    intent.setDataAndType(uri, "application/*");

                                                       /* Intent i;
                                                        //pdf viewer code added ..!!
                                                        if(docFormat.equals("pdf")) {
                                                            i = new Intent(StudyMaterialActivity.this, FileReaderActivity.class);
                                                            i.putExtra("uri", uri);
                                                            startActivity(i);

                                                        }
                                                            if(docFormat.equals("ppt")||docFormat.equals("pptx")) {
                                                                i = new Intent(StudyMaterialActivity.this, PptFileReaderActivity.class);

                                                                i.putExtra("uri",uri.toString());
                                                                startActivity(i);

                                                            }*/


                                                    progressDialog.dismiss();


                                                    startActivity(intent);


                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Handle any errors
                                                    progressDialog.dismiss();

                                                    Log.i("Exception",exception.getMessage());
                                                }
                                            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                                    progressDialog.setProgress(0);
                                                    progressDialog.show();
                                                    int progresspercentage = (int) ((int)(100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                                                    progressDialog.incrementProgressBy(progresspercentage);

                                                }
                                            });

                                        }
                                        else
                                        {
                                            Uri uri = FileProvider.getUriForFile(AttendanceResultActivity.this,
                                                    getString(R.string.file_provider_authority),
                                                    localFile);


                                            Log.i("URI", uri.toString());
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                            intent.setDataAndType(uri, "application/*");
                                            startActivity(intent);

                                            //code for pdfViewer starts -->>

                                              /*  if(docFormat.equals("pdf")) {
                                                    Intent i = new Intent(StudyMaterialActivity.this, FileReaderActivity.class);
                                                    i.putExtra("uri", uri.toString());
                                                    startActivity(i);
                                                }


                                                if(docFormat.equals("ppt")||docFormat.equals("pptx")) {
                                                  Intent  i = new Intent(StudyMaterialActivity.this, PptFileReaderActivity.class);

                                                    i.putExtra("uri",uri.toString());
                                                    startActivity(i);

                                                }*/


                                        }
                                    }
                                    catch(Exception e)
                                    {
                                        Log.i("Exception",e.getMessage());
                                        Toast.makeText(AttendanceResultActivity.this, "File could not be downloaded !!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });



                        }

                        @NonNull
                        @Override
                        public AttendanceResultActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_item_layout, parent, false);
                            AttendanceResultActivity.ViewHolder holder = new AttendanceResultActivity.ViewHolder(view);
                            return holder;

                        }
                    };

                    recyclerView.setAdapter(firebaseRecyclerAdapter);









                }
                else
                {
                    Toast.makeText(AttendanceResultActivity.this, "No files available.", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    public static  class ViewHolder  extends RecyclerView.ViewHolder{

        View itemView;
        TextView textView;
        public ViewHolder(View mview) {
            super(mview);
            itemView = mview;
            textView = itemView.findViewById(R.id.textView);
        }
    }





}
