package com.example.demo.util;

import com.example.demo.controller.ImagePostRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageTaggingUtility {
    public static final String IMAGGA_ENDPOINT = "https://api.imagga.com/v2/tags";
    public static final String HTTP_REQUEST_POST = "POST";
    public static final String HTTP_REQUEST_GET = "GET";
    public static final String TAG_LIMIT = "10";
    public static final String TAG_CONFIDENCE_THRESHOLD = "40.0";
    private static String basicAuth;


    public static List<String> getTagsFromImageUrl(ImagePostRequest postRequest) {
        // Create URL
        String url = String.format("%s?image_url=%s&limit=%s&threshold=%s",IMAGGA_ENDPOINT,postRequest.getUrl(),TAG_LIMIT,TAG_CONFIDENCE_THRESHOLD);
        // Init IO variables
        HttpURLConnection connection = null;
        BufferedReader connectionInput = null;
        InputStreamReader inputStreamReader = null;
        String response = null;
        URL urlObject = null;
        try {
            // Call Imagga GET endpoint to send image URL and get detected tags.
            urlObject = new URL(url);
            connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setRequestMethod(HTTP_REQUEST_GET);
            inputStreamReader = new InputStreamReader(connection.getInputStream());
            connectionInput = new BufferedReader(inputStreamReader);
            response = connectionInput.readLine();
        } catch(Throwable t) {
            System.out.println(t.getMessage());
            return null;
        } finally {
            try{
                if(connection != null) {
                    connection.disconnect();
                }
                if(connectionInput != null) {
                    connectionInput.close();
                }
                if(inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch(Throwable t) {
                // do nothing
            }
        }
        return getTagListFromJson(response,false);
    }

    /**
     * Call Imagga POST endpoint to upload the file and get detected tags.
     *
     * @param postRequest custom request class that contains url, filePath, label, and the objectDetection boolean.
     * @return List of tags detected.
     */
    @SuppressWarnings("ReassignedVariable")
    public static List<String> getTagsFromFile(ImagePostRequest postRequest) {
        String filePath = postRequest.getFilePath();
        File fileToUpload = new File(filePath);
        // Init IO variables
        String response = null;
        URL urlObject = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        DataOutputStream request = null;
        BufferedReader responseStreamReader = null;
        try {
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary =  "Image Upload";
            urlObject = new URL(IMAGGA_ENDPOINT);
            connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setRequestMethod(HTTP_REQUEST_POST);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            request = new DataOutputStream(connection.getOutputStream());
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + fileToUpload.getName() + "\"" + crlf);
            request.writeBytes(crlf);
            inputStream = new FileInputStream(fileToUpload);
            int bytesRead;
            byte[] dataBuffer = new byte[1024];
            while ((bytesRead = inputStream.read(dataBuffer)) != -1) {
                request.write(dataBuffer, 0, bytesRead);
            }
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
            request.flush();
            request.close();
            InputStream responseStream = new BufferedInputStream(connection.getInputStream());
            responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            response = stringBuilder.toString();
            responseStreamReader.close();
            responseStream.close();
            connection.disconnect();
        } catch(Throwable t) {
            System.out.println(t.getMessage());
            return null;
        }
        return getTagListFromJson(response, true);
    }

    private static List<String> getTagListFromJson(String jsonString, boolean manualLimitAndThresholdOverride) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch(Throwable t) {
            System.out.println(t.getMessage());
            return null;
        }
        JSONObject statusObject = jsonObject.getJSONObject("status");
        JSONObject resultObject = jsonObject.getJSONObject("result");
        // Check web service status to make sure
        if(!"success".equals(statusObject.get("type"))) {
            System.out.println("Could not get tags: " + statusObject.getString("text"));
            return null;
        }
        JSONArray jsonArray = resultObject.getJSONArray("tags");
        List<String> tagList = new ArrayList<>();
        // Limit the number of tags that are returned.
        int maxLength = jsonArray.length();
        if (manualLimitAndThresholdOverride && jsonArray.length() > Integer.parseInt(TAG_LIMIT)) {
            maxLength = Integer.parseInt(TAG_LIMIT);
        }
        // Iterate through the JSON to extract the tags.
        for(int i = 0; i < maxLength; i++) {
            double confidence = jsonArray.getJSONObject(i).getDouble("confidence");
            double confidenceThreshold = Double.parseDouble(TAG_CONFIDENCE_THRESHOLD);
            if(confidence > confidenceThreshold) {
                tagList.add(jsonArray.getJSONObject(i).getJSONObject("tag").getString("en"));
            }
        }
        return tagList;
    }
    @Value("${apiAuth}")
    public void setApiAuth(String apiAuth) {
        // Method to set the auth value
        basicAuth = apiAuth;
    }
}