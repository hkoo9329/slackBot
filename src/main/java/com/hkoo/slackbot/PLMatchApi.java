package com.hkoo.slackbot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Component
public class PLMatchApi {
    private BufferedReader br;
    private StringBuffer outResult;
    private String jsonData;
    private String returnText;
    private HttpURLConnection conn;
    private JSONArray jsonArray;
    private final String SERVER_IP = "http://15.165.113.72";
    private final String RECENCY_MATCH = "/matchs/recency";
    private final String WEEK_MATCH = "/matchs/recency/week";


    public ArrayList<String> getRecencyMatchs(){
       return makeJsonArrayToArrayList(getRestApi(RECENCY_MATCH));
    }
    public ArrayList<String> getThisWeekMatchs(){
        return makeJsonArrayToArrayList(getRestApi(WEEK_MATCH));
    }
    public ArrayList<String> getRecencyTeamMatchs(String team){
        try {
            return makeJsonArrayToArrayList(getRestApi(RECENCY_MATCH+"/"+ URLEncoder.encode(team,"UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> makeJsonArrayToArrayList(JSONArray jsonArray){
        ArrayList<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String score = jsonObject.getString("score");
                if (score.equals("null")){
                    score="";
                }
                list.add(matchDayDateFormat(jsonObject.getString("match_day"))+" "
                        +jsonObject.getString("left_team")+" "
                        +score+" "
                        +jsonObject.getString("right_team"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    private JSONArray getRestApi(String apiUrl){
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

    private String matchDayDateFormat(String date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strDate = "";
        try {
            strDate = dateFormat.format(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }
}

