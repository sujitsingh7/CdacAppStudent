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
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class ScheduleActivity extends AppCompatActivity {


    CardView courseCardView,examCardView;
    String mCurrentUser;

    TextView courseTextView,examTextView;

    DatabaseReference databaseReference;

    String url;
    String name;
    ProgressDialog progressDialog;

    String term;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        getSupportActionBar().setTitle("Schedule");

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);




        courseCardView =findViewById(R.id.course_card_view);
        courseTextView=findViewById(R.id.courseTextView);

        examCardView=findViewById(R.id.exam_card_view);
        examTextView=findViewById(R.id.examTextView);





        permission_check();

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
            initialize();
        }else {
            permission_check();
        }
    }

    public void initialize() {


        //listView.setAdapter(arrayAdapter);
        // handler = new Handler();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading...");
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);




                databaseReference.child("schedule").child("Course").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {
                           final String name = String.valueOf(dataSnapshot.child("name").getValue());
                            url = String.valueOf(dataSnapshot.child("url").getValue());

                            courseTextView.setText(name);

                            courseCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    download(url, name);
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




                databaseReference.child("uid").child(mCurrentUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {



                            String prn = String.valueOf(dataSnapshot.getValue());

                            databaseReference.child("users").child(prn).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                    term =String.valueOf(dataSnapshot.child("term").getValue());

                                    term="term"+String.valueOf(term);


                                    databaseReference.child("schedule").child(term).child("Exam").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.exists()) {


                                                url = String.valueOf(dataSnapshot.child("url").getValue());


                                                   final String name = String.valueOf(dataSnapshot.child("name").getValue());
                                                   Log.i("name",name);
                                                    examTextView.setText(name);

                                                  examCardView.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {



                                                          download(url, name);

                                                      }
                                                  });



                                            }
                                            else
                                            {
                                                examTextView.setText("Exam Schedule");
                                                examCardView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                            Toast.makeText(ScheduleActivity.this, "Sorry.Schedule not available!", Toast.LENGTH_SHORT).show();



                                                    }
                                                });
                                              // Toast.makeText(ScheduleActivity.this, "Sorry.Schedule not available!", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });




                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





            }


    public void download(String url,String name)
    {


        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);



        try {
            //final File localFile = File.createTempFile(model.getPushId(),"."+model.getType(),getCacheDir());

            final File localFile = new File(getCacheDir(),name);
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

                        Uri uri = FileProvider.getUriForFile(ScheduleActivity.this,
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
                Uri uri = FileProvider.getUriForFile(ScheduleActivity.this,
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
            Toast.makeText(ScheduleActivity.this, "File could not be downloaded !!", Toast.LENGTH_SHORT).show();
        }

    }


}



