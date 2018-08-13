package com.example.sujit.docpoint;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SubjectsActivity extends AppCompatActivity {



    String uid;
    String batch,term,prn;
    String activeSubject;
    String startDate;
    String endDate;
    DatabaseReference mRef;

    String currentDate;
    TextView activeAssignmentTextView,fileChosenTextView;
    Button chooseFileButton,uploadFileButton,viewFileButton;
    String activeAssignment;
    String type;



    String fileName;

    String url;

    String URL;

    ProgressDialog progressDialog;


    String urlForDB;

    Date start_date,end_date,current_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);



        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading..");
        progressDialog.setMessage("Please Wait..!");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        url= "uploads/";
        URL="http://172.29.5.9:8090/fileupload.php";


        urlForDB="http://172.29.5.9:8090/uploadintodb.php";


        fileChosenTextView=findViewById(R.id.file_chosen_textview);

        chooseFileButton=findViewById(R.id.choose_file_button);
        uploadFileButton=findViewById(R.id.upload_file_button);
        viewFileButton = findViewById(R.id.view_file_button);


        activeAssignmentTextView=findViewById(R.id.active_assignment_textview);
        activeAssignment = " Assigment due for ";

        uid = FirebaseAuth.getInstance().getUid();

        Calendar c = Calendar.getInstance();

        final SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = df2.format(c.getTime());
        try {
            current_date = df2.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.i("current-date-format",currentDate);

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.keepSynced(true);




        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(Build.VERSION.SDK_INT<23)
                {
                    chooseFile();

                }
                else
                {
                    //if sdk int>23 check for self permission

                    if(ContextCompat.checkSelfPermission(SubjectsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                    {
                        //if permission is not granted ask for permission

                        ActivityCompat.requestPermissions(SubjectsActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    }
                    else
                    {
                        chooseFile();
                    }


                }
            }
        });


        mRef.child("uid").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    prn = String.valueOf(dataSnapshot.getValue());

                    mRef.child("users").child(prn).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {

                                term = String.valueOf(dataSnapshot.child("term").getValue());

                                batch = String.valueOf(dataSnapshot.child("batch").getValue());

                                String term_string = "term"+term;

                                url = url+batch+"/"+term_string+"/";

                                Log.i("url",url);



                                mRef.child("active_assignments").child(term_string).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.exists())
                                        {
                                           startDate = String.valueOf(dataSnapshot.child("start_date").getValue());
                                           endDate = String.valueOf(dataSnapshot.child("end_date").getValue());
                                           activeSubject=String.valueOf(dataSnapshot.child("subject").getValue());
                                           type = String.valueOf(dataSnapshot.child("type").getValue());

                                           url = url +activeSubject+"/"+type+"/";

                                           if (type.equals("lab_assignment"))
                                               activeAssignment ="Lab "+activeAssignment;

                                               if(type.equals("theory _assignment"))

                                                   activeAssignment ="Theory "+activeAssignment;

                                           activeAssignment=activeAssignment+activeSubject+" from "+ startDate+" to " + endDate+"!";

                                            try {
                                                start_date = df2.parse(startDate);

                                                end_date = df2.parse(endDate);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            if((current_date.before(end_date)||current_date.equals(end_date))&&(current_date.equals(start_date)||current_date.after(start_date))){


                                                activeAssignmentTextView.setVisibility(View.VISIBLE);
                                                activeAssignmentTextView.setText(activeAssignment);
                                                chooseFileButton.setVisibility(View.VISIBLE);

                                            }
                                            else
                                            {
                                                activeAssignmentTextView.setVisibility(View.VISIBLE);
                                                activeAssignmentTextView.setText("No assignments to upload now");

                                            }

                                        }
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

                    }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }


        });



    }

    private void chooseFile() {

        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(10)
                .withFilter(Pattern.compile(".*\\.pdf$")) // Filtering files and directories by file name using regexp
                .start();




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==1&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            chooseFile();


        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if(requestCode==10 && data!=null) {

            String full_path =data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            fileName = full_path.substring(full_path.lastIndexOf("/")+1);

            String fileNameWithoutFormat = fileName.substring(0,fileName.lastIndexOf('.'));
            Log.i("fileWithoutFormat",fileNameWithoutFormat);

          if(fileNameWithoutFormat.equals(prn)) {
              fileChosenTextView.setText(fileName);
              fileChosenTextView.setVisibility(View.VISIBLE);
              uploadFileButton.setVisibility(View.VISIBLE);

              uploadFileButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                      progressDialog.show();
                      final Handler mHandler = new Handler();
                      Thread t = new Thread(new Runnable() {
                          @Override
                          public void run() {
                              String status = "";


                              File f = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                              String content_type = getMimeType(f.getPath());
                              String file_path = f.getAbsolutePath();


                              OkHttpClient client = new OkHttpClient.Builder()
                                      .connectTimeout(30, TimeUnit.SECONDS)
                                      .writeTimeout(30, TimeUnit.SECONDS)
                                      .readTimeout(30, TimeUnit.SECONDS)
                                      .build();
                              RequestBody file_body = RequestBody.create(MediaType.parse(content_type), f);

                              RequestBody request_body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("type", content_type)
                                      .addFormDataPart("URL", url)
                                      .addFormDataPart("uploaded_file", file_path.substring(file_path.lastIndexOf("/") + 1), file_body).build();


                              okhttp3.Request request;
                              request = new okhttp3.Request.Builder()
                                      .url(URL)
                                      .post(request_body)
                                      .build();

                              try {
                                  okhttp3.Response response;
                                  response = client.newCall(request).execute();


                                  if (!response.isSuccessful()) {
                                      status = "Upload Unsuccessful !";
                                      throw new IOException("Error : " + response);


                                  } else {
                                      status = "Upload Successful !";




                                      StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, urlForDB,
                                              new com.android.volley.Response.Listener<String>() {
                                                  @Override
                                                  public void onResponse(String response) {
                                                      Log.i("response",response);
                                                      try {
                                                          JSONObject jsonObject = new JSONObject(response);
                                                          String success = String.valueOf(jsonObject.get("success"));

                                                          Log.i("response", success);
                                                      } catch (JSONException e) {
                                                          e.printStackTrace();
                                                          Log.i("tagconvertstr", "["+response+"]");
                                                          Log.i("error",e.getMessage());
                                                          Toast.makeText(SubjectsActivity.this, "Error !", Toast.LENGTH_SHORT).show();
                                                      }


                                                  }
                                              }, new com.android.volley.Response.ErrorListener() {
                                          @Override
                                          public void onErrorResponse(VolleyError error) {

                                              Log.i("error",error.getMessage());
                                              Toast.makeText(SubjectsActivity.this, "Error !", Toast.LENGTH_SHORT).show();
                                          }
                                      })

                                      {
                                          @Override
                                          protected Map<String, String> getParams() throws AuthFailureError {
                                              Map<String,String> params = new HashMap<>();
                                              params.put("type",type);
                                              params.put("subject",activeSubject.toLowerCase());
                                              params.put("prn",prn);
                                              params.put("batch",batch);
                                              params.put("term",term);

                                              return params;
                                          }
                                      };

                                      MySingleton.getInstance(SubjectsActivity.this).addToRequestQueue(stringRequest);


                                  }







                                  progressDialog.dismiss();



                                  final String finalStatus = status;
                                  mHandler.post(new Runnable() {
                                      @Override
                                      public void run() {
                                          // make operation on UI - on example
                                          // on progress bar.


                                          Toast.makeText(SubjectsActivity.this, finalStatus, Toast.LENGTH_SHORT).show();
                                          if(finalStatus.equals("Upload Successful !"))
                                          {

                                          viewFileButton.setVisibility(View.VISIBLE);

                                          viewFileButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {

                                                  Intent intent = new Intent(SubjectsActivity.this, VerifyActivity.class);

                                                  intent.putExtra("batch", batch);
                                                  intent.putExtra("term", term);
                                                  intent.putExtra("subject", activeSubject);
                                                  intent.putExtra("prn", prn);
                                                  intent.putExtra("type",type);

                                                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                  startActivity(intent);

                                              }
                                          });
                                      }

                                      }
                                  });


                              } catch (IOException e) {
                                  e.printStackTrace();
                              }


                          }
                      });
                      t.start();

                  }
              });
          }
          else
          {
              Toast.makeText(SubjectsActivity.this,"Sorry..Your file's name should be your PRN No.",Toast.LENGTH_SHORT).show();

          }
        }
    }

    private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if(extension.isEmpty()){
            int i = path.lastIndexOf('.');
            if (i > 0) {
                return path.substring(i + 1);
            }else{
                return  MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
        }else{
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
    }




}
