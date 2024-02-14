package com.mjuAppSW.joA.common.constant;

public class Constants {

    public static class Cache {
        public static String CERTIFY_NUMBER = "CertifyNum";
        public static String BEFORE_EMAIL = "BeforeEmail";
        public static String AFTER_EMAIL = "AfterEmail";
        public static String ID = "Id";

        public static Integer BEFORE_CERTIFY_TIME = 7;
        public static Integer AFTER_CERTIFY_TIME = 60;
        public static Integer AFTER_SAVE_LOGIN_ID_TIME = 30;
    }

    public static class Mail {
        public static String USER_ID_IS = "id";
        public static String TEMPORARY_PASSWORD_IS = "임시 비밀번호";
        public static String CERTIFY_NUMBER_IS = "인증번호";
    }

    public static class S3Uploader {
        public static String ERROR = "error";
    }

    public static class Room {
        public static String EXTEND = "0";
        public static String NOT_EXTEND = "1";
        public static Integer ONE_DAY_HOURS = 24;
        public static Integer SEVEN_DAY_HOURS = 168;
        public static Integer OVER_ONE_DAY = 1;
        public static Integer OVER_SEVEN_DAY = 7;
    }

    public static class RoomInMember{
        public static String EXIT = "0";
        public static String NOT_EXIT = "1";
        public static String APPROVE_VOTE = "0";
        public static String DISAPPROVE_VOTE = "1";
        public static String BEFORE_VOTE = "2";
    }

    public static class MessageReport{
        public static Integer NINETY_DAY_HOURS = 2160;
    }

    public static class Message{
        public static String CHECKED = "0";
    }

    public static class WebSocketHandler{
        public static String R_SEPARATOR = "R";
        public static String M_SEPARATOR = "M";
        public static Integer LIMIT_SEPARATOR = 4;
        public static String SEPARATOR = " ";
        public static Integer OVER_ONE_DAY = 1;
        public static Integer OVER_SEVEN_DAY = 7;
        public static String OPPONENT_CHECK_MESSAGE = "0";
        public static Integer NORMAL_OPERATION = 0;
        public static String AND_OPERATION = "&";
        public static String EQUAL_OPERATION = "=";
        public static String ALARM_REPORTED_ROOM = "신고된 방입니다.";
        public static String ALARM_OPPONENT_EXITED = "상대방이 나갔습니다.";
        public static String ALARM_OPPONENT_IS_WITH_DRAWAL = "상대방이 탈퇴하였습니다.";
        public static String ALARM_OVER_ONE_DAY = "방 유효시간이 24시간을 초과하였습니다.";
        public static String ALARM_OVER_SEVEN_DAY = "방 유효시간이 7일을 초과하였습니다.";
        public static Integer MAX_CAPACITY_IN_ROOM = 2;
    }

    public static class MemberStatus {
        public static int STEP_1_REPORT_COUNT = 5;
        public static int STEP_2_REPORT_COUNT = 10;
        public static int STEP_3_REPORT_COUNT = 15;
        public static int STEP_1_DATE = 2;
        public static int STEP_2_DATE = 8;
    }

    public static class SlackService{
        public static final String ERROR_MESSAGE_TITLE = "🤯 *에러 발생*";
        public static final String ERROR_COLOR = "#eb4034";
    }
    public static class SlackServiceUtil{
        public static final String ERROR_MESSAGE = "*Error Message:*\n";
        public static final String ERROR_STACK = "*Error Stack:*\n";
        public static final String ERROR_URI = "*Error URI:*\n";
        public static final String ERROR_METHOD = "*Error Method:*\n";
        public static final String ERROR_DATE = "*Error Date:*\n";
        public static final String FILTER_STRING = "joA";
        public static final String POINTER = "```";
        public static final String NEW_LINE = "\n";
    }

    public static Integer ZERO = 0;
    public static String EMPTY_STRING = "";
    public static String EMAIL_SPLIT = "@";
}
