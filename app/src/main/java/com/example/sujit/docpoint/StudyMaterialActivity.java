package com.example.sujit.docpoint;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.itsrts.pptviewer.PPTViewer;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import java.security.Timestamp;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.File;

public class StudyMaterialActivity extends AppCompatActivity {


    //SharedPreferences downloadSharedPreferences;
    //ListView listView;
    //ArrayList arrayList;
    //ArrayAdapter arrayAdapter;
    //Handler handler;
    RecyclerView recyclerView;

    private FirebaseRecyclerAdapter<Files, ViewHolder> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<Files> options;
    private Query query;


    ProgressDialog progressDialog;
    String term,batch,subject,subject_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_material);

        //downloadSharedPreferences = getDeafultSharedPreferences( Context.MODE_PRIVATE);

     //   downloadSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        term = getIntent().getStringExtra("term");
        batch = getIntent().getStringExtra("batch");
        subject = getIntent().getStringExtra("subject");
        subject_name = getIntent().getStringExtra("subject_name");



        Log.i("term",term);
        Log.i("batch",batch);
        Log.i("subject",subject);
        Log.i("subject_name",subject_name);



       //     listView = findViewById(R.id.list_view);
         //   arrayList=new ArrayList();
           // arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);

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

        public void initialize(){


            //listView.setAdapter(arrayAdapter);
           // handler = new Handler();

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Downloading...");
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            recyclerView = findViewById(R.id.recycler_view);


            recyclerView.hasFixedSize();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            query = FirebaseDatabase.getInstance()
                    .getReference().child("files").child(subject_name);

            query.keepSynced(true);

            options = new FirebaseRecyclerOptions.Builder<Files>()
                    .setQuery(query, Files.class).setLifecycleOwner(this)
                    .build();


            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists())

                    {



                        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Files, StudyMaterialActivity.ViewHolder>(options) {



                            @Override
                            protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final  Files model) {

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

                                                        Uri uri = FileProvider.getUriForFile(StudyMaterialActivity.this,
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

                                                        progressDialog.show();
                                                        int progresspercentage = (int) ((int)(100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                                                        progressDialog.incrementProgressBy(progresspercentage);

                                                    }
                                                });

                                            }
                                            else
                                            {
                                                Uri uri = FileProvider.getUriForFile(StudyMaterialActivity.this,
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
                                            Toast.makeText(StudyMaterialActivity.this, "File could not be downloaded !!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });



                            }

                            @NonNull
                            @Override
                            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_item_layout, parent, false);
                               StudyMaterialActivity.ViewHolder holder = new StudyMaterialActivity.ViewHolder(view);
                                return holder;

                            }
                        };


                        recyclerView.setAdapter(firebaseRecyclerAdapter);






                    }
                    else
                    {
                        Toast.makeText(StudyMaterialActivity.this, "No files for this unit.", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });







            /*Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    String location = batch+"/"+"term"+term+"/"+subject_name+"/";
                    Log.i("location",location);

                    OkHttpClient client = new OkHttpClient();
                    Request request = null;
                    try {
                        request = new Request.Builder().url("http://192.168.43.108/download.php?location="+ URLEncoder.encode(location, "UTF-8")).build();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    //reads all the files from 'uploads/' folder....and stores it in the arrayList!
                    Response response = null;
                    try {
                        response = client.newCall(request).execute();
                        JSONArray array = new JSONArray(response.body().string());

                        for (int i = 0; i <array.length(); i++){

                            String file_name = array.getString(i);

                            if(arrayList.indexOf(file_name) == -1)
                                arrayList.add(file_name);
                        }


                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                arrayAdapter.notifyDataSetChanged();
                            }
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            t.start();


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    final String selected_file = ((TextView)view).getText().toString();
                    Log.i("selected_file",selected_file);

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String location = batch+"/"+"term"+term+"/"+subject_name+"/";
                            Log.i("location",location);


                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder().url("http://192.168.43.108/downloads/"+location+ selected_file).build();

                            String baseDir = String.valueOf(Environment.getExternalStorageDirectory());
                            Log.i("baseDir",baseDir);

                            File f = new File(baseDir +"/Download/"+ selected_file);
                            Uri uri = FileProvider.getUriForFile(StudyMaterialActivity.this,
                                    getString(R.string.file_provider_authority),
                                    f);
                            Log.i("Uri",String.valueOf(uri));



                            if(!f.exists()) {
                                Log.i("inside if","true");

                                Response response = null;
                                try {
                                    response = client.newCall(request).execute();
                                    float file_size = response.body().contentLength();

                                    BufferedInputStream inputStream = new BufferedInputStream(response.body().byteStream());
                                    OutputStream stream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Download/" + selected_file);

                                    downloadSharedPreferences.edit().putString(String.valueOf(System.currentTimeMillis()),String.valueOf(uri));



                                    byte[] data = new byte[8192];
                                    float total = 0;
                                    int read_bytes = 0;

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            progressDialog.show();
                                        }
                                    });


                                    while ((read_bytes = inputStream.read(data)) != -1) {

                                        total = total + read_bytes;
                                        stream.write(data, 0, read_bytes);
                                        progressDialog.setProgress((int) ((total / file_size) * 100));

                                    }

                                    progressDialog.dismiss();
                                    stream.flush();
                                    stream.close();
                                    response.body().close();

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                    intent.setDataAndType(uri, "application/*");
                                    startActivity(intent);


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                            else{

                                Log.i("inside else","true");
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                intent.setDataAndType(uri, "application/*");
                                startActivity(intent);
                            }

                        }
                    });
                    t.start();

                }

            });
            */


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
