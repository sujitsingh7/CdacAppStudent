package com.example.sujit.docpoint;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.itsrts.pptviewer.PPTViewer;

public class PptFileReaderActivity extends AppCompatActivity {

    PPTViewer pptViewer;
    String fileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppt_file_reader);


        fileUri=getIntent().getStringExtra("uri");
        Log.i("File Uri",fileUri);


        pptViewer = (PPTViewer) findViewById(R.id.pptViewer);
        //String path = Environment.getExternalStorageDirectory().getPath()
          //      + "/Download/ssadagopan.ppt";
        pptViewer.setNext_img(R.drawable.next).setPrev_img(R.drawable.prev)
                .setSettings_img(R.drawable.settings)
                .setZoomin_img(R.drawable.zoomin)
                .setZoomout_img(R.drawable.zoomout);
        pptViewer.loadPPT(this, fileUri);


    }
}
