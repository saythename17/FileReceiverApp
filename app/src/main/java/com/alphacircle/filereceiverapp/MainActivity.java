package com.alphacircle.filereceiverapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends Activity implements FileFetchListener {
    private RecyclerView galleryRecyclerView;
    private GalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        galleryRecyclerView = findViewById(R.id.galleryRecyclerView);
        galleryAdapter = new GalleryAdapter(this);
        galleryRecyclerView.setAdapter(galleryAdapter);

        FileFetcher fileFetcher = new FileFetcher(this, MainActivity.this);
        fileFetcher.fetchFiles();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onFilesFetched(List<FileInfo> files) {
        if(files.isEmpty()) {
            Toast.makeText(this, "파일이 없습니다. 서버에 파일을 추가해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        galleryAdapter.setFiles(files);
        galleryAdapter.notifyDataSetChanged();
    }
}


