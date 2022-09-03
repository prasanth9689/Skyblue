package com.skyblue.skybluea.download;

public class DownloadModel {

    String id;
    String fileName;
    String fileSize;
    String fileDownloadDate;
    String filePath;

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getFileDownloadDate() {
        return fileDownloadDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileDownloadDate(String fileDownloadDate) {
        this.fileDownloadDate = fileDownloadDate;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
