package com.hkoo.slackbot.slack;

import com.hkoo.slackbot.PLMatchApi;
import lombok.extern.slf4j.Slf4j;
import me.ramswaroop.jbot.core.common.Controller;
import me.ramswaroop.jbot.core.common.EventType;
import me.ramswaroop.jbot.core.common.JBot;
import me.ramswaroop.jbot.core.slack.Bot;
import me.ramswaroop.jbot.core.slack.models.Event;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.WebSocketSession;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;

@Slf4j
@JBot
@Profile("slack")
public class SlackBot extends Bot {

    private PLMatchApi PLMatchApi = new PLMatchApi();

    private static final Logger logger = LoggerFactory.getLogger(SlackBot.class);
    private ZonedDateTime seoulDateTime;
    @Value("${slackBotToken}")
    private String slackToken;

    @Override
    public String getSlackToken() {
        return slackToken;
    }

    @Override
    public Bot getSlackBot() {
        return this;
    }

    @Controller(events = EventType.MESSAGE, pattern = "^([a-z ]{2})(\\d+)([a-z ]{2})$")
    public void onReceiveMessage(WebSocketSession session, Event event, Matcher matcher) {
        reply(session, event, "First group: " + matcher.group(0) + "\n" +
                "Second group: " + matcher.group(1) + "\n" +
                "Third group: " + matcher.group(2) + "\n" +
                "Fourth group: " + matcher.group(3));
    }


    @Controller(events = EventType.PIN_ADDED)
    public void onPinadded(WebSocketSession session, Event event){
        reply(session, event, "Thanks for the pin! You can find all pinned items under channel details.");
    }

    @Controller(events = EventType.FILE_SHARED)
    public void onFileShared(WebSocketSession session, Event event) {
        logger.info("File shared: {}", event);
    }

    @Controller(pattern = "(프리미어리그)", next = "matchMenu")
    public void plMatchs(WebSocketSession session, Event event){
        startConversation(event,"matchMenu");
        reply(session, event, "프리미어경기에 대한 무슨 정보가 필요하신가요? \n 1. 최근 경기 \n 2. 이번주 경기 \n 3. 팀 경기 일정");
    }
    @Controller(next = "matchTeams")
    public void matchMenu(WebSocketSession session, Event event){
        if (event.getText().contains("최근 경기")) {
            for (String match : PLMatchApi.getRecencyMatchs()) {
                reply(session, event, match);
            }
            stopConversation(event);
        }else if (event.getText().contains("이번주")){
            for (String match : PLMatchApi.getThisWeekMatchs()){
                reply(session, event, match);
            }
            stopConversation(event);
        }else if (event.getText().contains("팀 경기")){
            startConversation(event,"matchTeams");
            reply(session,event,"검색하고자 하는 팀을 입력해주세요. (ex. 토트넘)");
        }
        else{
            reply(session,event,"명령어가 잘못되었습니다. 혹시 명령어를 알고 싶다면 'help'를  입력해주세요~");
            stopConversation(event);
        }
    }
    @Controller
    public void matchTeams(WebSocketSession session, Event event){
        String team = event.getText();
        for (String match : PLMatchApi.getRecencyTeamMatchs(team)){
            reply(session, event, match);
        }
        stopConversation(event);
    }

    @Controller(pattern = "(오늘 날짜)")
    public void dayNow(WebSocketSession session, Event event){
        seoulDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        reply(session, event, seoulDateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
    }

    @Controller(pattern = "(시간)")
    public void timeNow(WebSocketSession session, Event event){
        seoulDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        reply(session, event, seoulDateTime.format(DateTimeFormatter.ofPattern("HH시 mm분 ss초")));
    }
    @Controller(pattern = "(help)(test)")
    public void commandHelp(WebSocketSession session, Event event){
        String helpComment = " '시간' : 현재 시간을 알려줍니다. \n" +
                "'오늘 날짜' : 오늘의 날짜를 알려줍니다. \n" +
                "'프리미어리그' : 프리미어리그 경기에 대한 명령어를 알려줍니다.\n" +
                "'최근 경기' : 최근 프리미어리그 8경기를 알려줍니다.\n" +
                "'이번주 경기' : 요번주 프리미어리그 경기 정보를 알려줍니다.\n" +
                "'팀 경기 일정' : 최근 팀의 경기 정보를 알려줍니다.";
        reply(session, event, helpComment);
    }


//    @Controller(pattern = "(setup meeting)", next = "confirmTiming")
//    public void setupMeeting(WebSocketSession session, Event event) {
//        startConversation(event, "confirmTiming");   // start conversation
//        reply(session, event, "Cool! At what time (ex. 15:30) do you want me to set up the meeting?");
//    }
//
//    @Controller(next = "askTimeForMeeting")
//    public void confirmTiming(WebSocketSession session, Event event) {
//        reply(session, event, "Your meeting is set at " + event.getText() +
//                ". Would you like to repeat it tomorrow?");
//        nextConversation(event);    // jump to next question in conversation
//    }
//    @Controller(next = "askWhetherToRepeat")
//    public void askTimeForMeeting(WebSocketSession session, Event event) {
//        if (event.getText().contains("yes")) {
//            reply(session, event, "Okay. Would you like me to set a reminder for you?");
//            nextConversation(event);    // jump to next question in conversation
//        } else {
//            reply(session, event, "No problem. You can always schedule one with 'setup meeting' command.");
//            stopConversation(event);    // stop conversation only if user says no
//        }
//    }
//
//    @Controller
//    public void askWhetherToRepeat(WebSocketSession session, Event event) {
//        if (event.getText().contains("yes")) {
//            reply(session, event, "Great! I will remind you tomorrow before the meeting.");
//        } else {
//            reply(session, event, "Okay, don't forget to attend the meeting tomorrow :)");
//        }
//        stopConversation(event);    // stop conversation
//    }
}
