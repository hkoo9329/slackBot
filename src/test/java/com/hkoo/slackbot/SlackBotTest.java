package com.hkoo.slackbot;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Slf4j
public class SlackBotTest {

    private PLMatchApi PLMatchApi;

    @Before
    public void init() {

    }
    @Test
    public void 날짜_포맷_테스트() throws java.text.ParseException {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = "2020-01-12 02:30:00";
        String strDate = transFormat.format(transFormat.parse(date));
        log.info(strDate);

    }

    @Test
    public void 팀경기_불러오기_테스트(){
        PLMatchApi = new PLMatchApi();
        String team = "토트넘";
        ArrayList<String> list = PLMatchApi.getRecencyTeamMatchs(team);
        for (String match : list){
            log.info(match);
        }
    }

    @Test
    public void 최근경기_불러오기_테스트() {
        PLMatchApi = new PLMatchApi();
        ArrayList<String> list = PLMatchApi.getRecencyMatchs();
        for (String match : list) {
            log.info(match);
        }
    }

//    @Test
//    public void json_파싱_테스트() throws JSONException {
//        JSONArray jsonArray = PLMatchApi.getRestApi("/matchs/recency");
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject = jsonArray.getJSONObject(i);
//            log.info(jsonObject.getString("match_day"));
//            log.info(jsonObject.getString("left_team"));
//            log.info(jsonObject.getString("right_team"));
//            log.info(jsonObject.getString("score"));
//        }
//    }


    @Test
    public void dateTime테스트() {
        ZonedDateTime seoulDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        log.info(seoulDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    @Test
    public void REST통신_테스트() throws JSONException, ParseException {
        String jsonData = "";
        BufferedReader br = null;
        StringBuffer outResult = null;
        String returnText = "";
        try {
            URL url = new URL("http://15.165.113.72/matchs/all");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.connect();

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));

            outResult = new StringBuffer();

            while ((jsonData = br.readLine()) != null) {
                outResult.append(jsonData);
            }
            returnText = outResult.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JSONArray jsonArray = new JSONArray(returnText);
        log.info(jsonArray.get(0).toString());

    }
}
