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

    public static class MAIL {
        public static String USER_ID_IS = "id";
        public static String TEMPORARY_PASSWORD_IS = "임시 비밀번호";
        public static String CERTIFY_NUMBER_IS = "인증번호";
    }

    public static class S3Uploader {
        public static String ERROR = "error";
    }

    public static Integer ZERO = 0;
    public static String EMPTY_STRING = "";
    public static String EMAIL_SPLIT = "@";
}
