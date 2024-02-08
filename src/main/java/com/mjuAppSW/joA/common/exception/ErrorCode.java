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
    ACCESS_FORBIDDEN(403, "M002", "접근 권한이 없는 계정입니다."), // 미사용
    INVALID_S3(500, "M003", "S3 저장소 접근에 실패했습니다."),
    ACCESS_STOPPED(403, "M004", "정지된 계정입니다."),
    MEMBER_ALREADY_EXISTED(409, "M005", "이미 존재하는 사용자입니다."),
    JOINING_MAIL(409, "M006", "회원가입 중인 이메일입니다."),
    SESSION_NOT_FOUND(404, "M007", "세션 id가 유효하지 않습니다."),
    MAIL_NOT_VERIFY(400, "M008", "이메일 인증이 완료되지 않았습니다."),
    INVALID_CERTIFY_NUMBER(400, "M009", "인증번호가 올바르지 않습니다."),
    INVALID_LOGIN_ID(400, "M010", "올바른 아이디 형식이 아닙니다."),
    LOGIN_ID_ALREADY_EXISTED(409, "M011", "이미 사용 중인 아이디입니다."),
    INVALID_PASSWORD(400, "M012", "올바른 비밀번호 형식이 아닙니다."),
    LOGIN_ID_NOT_AUTHORIZED(404, "M013", "아이디 중복 확인이 완료되지 않았습니다."),
    PERMANENT_BAN(403, "M014", "영구 정지된 계정입니다."),
    PASSWORD_NOT_FOUND(404, "M015", "비밀번호가 올바르지 않습니다."),

    // PCollege
    COLLEGE_NOT_FOUND(404,"P001" , "학교 정보를 찾을 수 없습니다."),
    OUT_OF_COLLEGE(409, "P002", "사용자가 학교 밖에 위치합니다."),

    // Report Category
    REPORT_CATEGORY_NOT_FOUND(404, "RC001", "신고 카테고리가 존재하지 않습니다."),

    // Vote
    VOTE_NOT_FOUND(404, "V001", "투표가 존재하지 않습니다."),
    VOTE_CATEGORY_NOT_FOUND(404, "V002", "투표 카테고리가 존재하지 않습니다."),
    VOTE_ALREADY_EXISTED(409, "V003", "이미 투표가 존재합니다."),
    INVALID_VOTE_EXISTED(403, "V004", "투표 신고로 인해 접근이 제한된 계정입니다."),

    // Vote Report
    VOTE_REPORT_ALREADY_EXISTED(409, "VR001", "이미 투표 신고가 존재합니다."),

    // Room
    ROOM_EXISTED(409, "R001", "이미 채팅방이 존재합니다."),
    OVER_ONE_DAY(400, "R002", "방이 생성된지 24시간이 지났습니다."),
    ROOM_NOT_FOUND(404, "R003", "방을 찾을 수 없습니다."),
    ROOM_ALREADY_EXTEND(409, "R004", "이미 연장된 방입니다."),

    // RoomInMember
    RIM_NOT_FOUND(404, "RIM001", "채팅방을 찾을 수 없습니다."),
    RIM_ALREADY_EXISTED(409, "RIM002", "이미 두 사용자의 채팅방이 존재합니다."),
    RIM_ALREADY_VOTE_RESULT(409, "RIM003", "이미 채팅방 연장에 대한 투표가 존재합니다."),

    // MessageReport
    MESSAGE_REPORT_ALREADY_EXISTED(409, "MR001", "이미 신고된 메시지가 존재합니다."),
    MESSAGE_REPORT_NOT_FOUND(404, "MR002", "신고된 메시지를 찾을 수 없습니다."),
    MESSAGE_REPORT_ALREADY_REPORT(409, "MR003", "상대방을 신고한 메시지가 존재합니다."),
    MESSAGE_REPORT_ALREADY_REPORTED(409, "MR004", "상대방에게 신고된 메시지가 존재합니다."),

    // Message
    MESSAGE_NOT_FOUND(404, "MG001", "메시지를 찾을 수 없습니다."),
    FAIL_ENCRYPT(500, "MG002", "메시지 암호화를 실패했습니다."),
    FAIL_DECRYPT(500, "MG003", "메시지 복호화를 실패했습니다."),

    // WebSocket
    ROOM_SESSION_LIST_IS_NULL(400, "W001", "방 섹션 리스트가 Null 입니다."),
    MEMBER_SESSION_LIST_IS_NULL(400, "W002", "사용자 섹션 리스트가 Null 입니다.");

    private final int status;
    private final String code;
    private final String message;
}
