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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class AboutCdacDita extends AppCompatActivity {

    TextView teachingSchemaTextView,fundamentalRuleTextView;

    CardView teachingSchemaCardView,fundamentalRuleCardView;

    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    int progresspercentage;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_cdac_dita);


        teachingSchemaTextView=findViewById(R.id.teachingSchemaTextView);
        fundamentalRuleTextView=findViewById(R.id.fundamentalRuleTextView);

        teachingSchemaCardView=findViewById(R.id.teaching_schema_card_view);
        fundamentalRuleCardView=findViewById(R.id.fundamental_rule_card_view);

        permission_check();

        fundamentalRuleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url="";
                progresspercentage=0;
                downloadFile("fundamental_rules");

            }
        });

        teachingSchemaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url="";
                progresspercentage=0;
                downloadFile("teaching_schema");

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

    }


    private void downloadFile( final String type) {

        databaseReference= FirebaseDatabase.getInstance().getReference().child("about_dita").child(type);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                url=String.valueOf(dataSnapshot.child("url").getValue());

                storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);




                try {
                    //final File localFile = File.createTempFile(model.getPushId(),"."+model.getType(),getCacheDir());

                    final File localFile = new File(getCacheDir(),type+".pdf");
                          /* final File localFile;
                            localFile = new File(model.getName());
                            localFile.setWritable(true);*/
                    if(!localFile.exists()) {
                        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created

                                //String uri= localFile.getAbsolutePath();
                                // Uri uri =Uri.fromFile(localFile);

                                Uri uri = FileProvider.getUriForFile(AboutCdacDita.this,
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
                               progresspercentage = (int) ((int)(100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                                progressDialog.incrementProgressBy(progresspercentage);

                            }
                        });

                    }
                    else
                    {
                        Uri uri = FileProvider.getUriForFile(AboutCdacDita.this,
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
                    Toast.makeText(AboutCdacDita.this, "File could not be downloaded !!", Toast.LENGTH_SHORT).show();
                }









            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
