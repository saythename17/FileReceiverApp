package com.alphacircle.filereceiverapp;

import com.alphacircle.filereceiverapp.FileInfo;

import java.util.List;

public interface FileFetchListener {
    void onFilesFetched(List<FileInfo> files);
}

