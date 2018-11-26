package com.example.sujit.docpoint;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VerifyActivity extends AppCompatActivity {

    String batch,prn,term,subject,type;
    ListView listView;
    ArrayList arrayList;
    ArrayAdapter arrayAdapter;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        batch = getIntent().getStringExtra("batch");
        prn = getIntent().getStringExtra("prn");
        term = getIntent().getStringExtra("term");
        subject = getIntent().getStringExtra("subject");
        type = getIntent().getStringExtra("type");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        listView = findViewById(R.id.list_view);
        arrayList=new ArrayList();
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);

        listView.setAdapter(arrayAdapter);
        handler = new Handler();


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String location = batch+"/"+"term"+term+"/"+subject+"/"+type+"/";
                Log.i("location",location);

                OkHttpClient client = new OkHttpClient();
                Request request = null;
                try {
                    request = new Request.Builder().url("http://192.168.73.108/verify.php?location="+ URLEncoder.encode(location, "UTF-8")).build();
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
    }
}
