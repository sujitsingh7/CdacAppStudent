package com.example.sujit.docpoint;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class DownloadsActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter arrayAdapter;
    ArrayList arrayList;


    SharedPreferences downloadSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        //Intent intent =new Intent();
        //intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        //startActivity(intent);

       downloadSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        listView = findViewById(R.id.list_view);
        arrayList=new ArrayList();
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);


        Map<String,?> entries = downloadSharedPreferences.getAll();
        Set<String> keys = entries.keySet();
        for (String key : keys) {

            Log.i("key",key);
            arrayList.add(downloadSharedPreferences.getString(key,""));

        }
        arrayAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String url =((TextView)view).getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                intent.setDataAndType(Uri.parse(url), "application/*");
                startActivity(intent);


            }
        });











    }
}
