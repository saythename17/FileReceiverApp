package com.alphacircle.filereceiverapp;

public class FileInfo {
    private String fileName;
    private long fileSize;
    private String fileType;

    public FileInfo(String fileName, long fileSize, String fileType) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileType() {
        return fileType;
    }
}

