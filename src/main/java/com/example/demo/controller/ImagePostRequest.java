package com.example.demo.controller;

public class ImagePostRequest {
    private String url;
    private String filePath;
    private String label;
    private boolean objectDetection;
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isObjectDetection() {
        return objectDetection;
    }

    public void setObjectDetection(boolean objectDetection) {
        this.objectDetection = objectDetection;
    }


    // Debugging
    @Override
    public String toString() {
        return "ImagePostRequest{" +
                "url='" + url + '\'' +
                ", filePath='" + filePath + '\'' +
                ", label='" + label + '\'' +
                ", objectDetection=" + objectDetection +
                '}';
    }
}
