package com.hkoo.slackbot;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class RestApi {
    private BufferedReader br = null;
    private StringBuffer outResult = null;
    private String jsonData = "";
    private String returnText = "";
    private HttpURLConnection conn;
    private JSONArray jsonArray;
    private final String SERVER_IP = "http://15.165.113.72";

    public JSONArray getRestApi(String apiUrl){
        try{
            URL url = new URL(SERVER_IP+apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Charset","UTF-8");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.connect();

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));

            outResult = new StringBuffer();

            while ((jsonData = br.readLine()) != null){
                outResult.append(jsonData);
            }
            returnText = outResult.toString();
            jsonArray = new JSONArray(returnText);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (br != null) br.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return jsonArray;
    }
}

