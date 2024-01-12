package com.mjuAppSW.joA.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(500, "C001", "서버에 오류가 발생하였습니다."),

    // Block
    BLOCK_ACCESS_FORBIDDEN(403, "B001", "차단 조치에 의해 접근 권한이 없습니다."),
    BLOCK_ALREADY_EXISTED(409, "B002", "이미 차단한 사용자입니다."),

    // Heart
    HEART_ALREADY_EXISTED(409, "H001", "이미 하트가 존재합니다."),

    // Location
    LOCATION_NOT_FOUND(404, "L001", "사용자의 위치 정보를 찾을 수 없습니다."),

    // Member
    MEMBER_NOT_FOUND(404,"M001","사용자를 찾을 수 없습니다."),
    ACCESS_FORBIDDEN(403, "M002", "접근 권한이 없는 계정입니다."),
    INVALID_S3(500, "M003", "S3 저장소 접근에 실패했습니다."),
    ACCESS_STOPPED(403, "M004", "정지된 계정입니다."),
    MEMBER_ALREADY_EXISTED(409, "M005", "이미 존재하는 사용자입니다."),
    JOINING_MAIL(409, "M006", "사용 중인 이메일입니다."),
    SESSION_NOT_FOUND(404, "M007", "세션 id가 유효하지 않습니다."),
    MAIL_NOT_VERIFY(400, "M008", "이메일 인증이 완료되지 않았습니다."),
    INVALID_CERTIFY_NUMBER(400, "M009", "인증번호가 올바르지 않습니다."),
    INVALID_LOGIN_ID(400, "M010", "올바른 아이디 형식이 아닙니다."),
    LOGIN_ID_ALREADY_EXISTED(409, "M011", "이미 사용 중인 아이디입니다."),
    INVALID_PASSWORD(400, "M012", "올바른 비밀번호 형식이 아닙니다."),
    LOGIN_ID_NOT_AUTHORIZED(404, "M013", "아이디 중복 확인이 완료되지 않았습니다."),
    MAIL_FORBIDDEN(403, "M014", "접근이 제한된 이메일입니다."),
    PASSWORD_NOT_FOUND(404, "M015", "비밀번호가 올바르지 않습니다."),

    // PCollege
    COLLEGE_NOT_FOUND(404,"P001" , "학교 정보를 찾을 수 없습니다."),
    OUT_OF_COLLEGE(409, "P002", "사용자가 학교 밖에 위치합니다."),

    // Room
    ROOM_EXISTED(409, "R001", "이미 채팅방이 존재합니다."),

    // Report Category
    REPORT_CATEGORY_NOT_FOUND(404, "RC001", "신고 카테고리가 존재하지 않습니다."),

    // Vote
    VOTE_NOT_FOUND(404, "V001", "투표가 존재하지 않습니다."),
    VOTE_CATEGORY_NOT_FOUND(404, "V002", "투표 카테고리가 존재하지 않습니다."),
    VOTE_ALREADY_EXISTED(409, "V003", "이미 투표가 존재합니다."),

    // Vote Report
    VOTE_REPORT_ALREADY_EXISTED(409, "VR001", "이미 투표 신고가 존재합니다.");

    private final int status;
    private final String code;
    private final String message;
}
