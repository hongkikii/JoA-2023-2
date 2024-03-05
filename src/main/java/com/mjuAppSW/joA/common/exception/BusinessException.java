package com.mjuAppSW.joA.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    // Heart
    public static final BusinessException HeartAlreadyExistedException = new BusinessException(
        ErrorCode.HEART_ALREADY_EXISTED
    );

    // Member
    public static final BusinessException AccessForbiddenException = new BusinessException(
        ErrorCode.ACCESS_FORBIDDEN
    );
    public static final BusinessException InvalidCertifyNumberException = new BusinessException(
        ErrorCode.INVALID_CERTIFY_NUMBER
    );
    public static final BusinessException InvalidLoginIdException = new BusinessException(
        ErrorCode.INVALID_LOGIN_ID
    );
    public static final BusinessException InvalidPasswordException = new BusinessException(
        ErrorCode.INVALID_PASSWORD
    );
    public static final BusinessException InvalidS3Exception = new BusinessException(
        ErrorCode.INVALID_S3
    );
    public static final BusinessException JoiningMailException = new BusinessException(
        ErrorCode.JOINING_MAIL
    );
    public static final BusinessException LoginIdAlreadyExistedException = new BusinessException(
        ErrorCode.LOGIN_ID_ALREADY_EXISTED
    );
    public static final BusinessException LoginIdNotAuthorizedException = new BusinessException(
        ErrorCode.LOGIN_ID_NOT_AUTHORIZED
    );
    public static final BusinessException MailNotVerifyException = new BusinessException(
        ErrorCode.MAIL_NOT_VERIFY
    );
    public static final BusinessException MemberAlreadyExistedException = new BusinessException(
        ErrorCode.MEMBER_ALREADY_EXISTED
    );
    public static final BusinessException MemberNotFoundException = new BusinessException(
        ErrorCode.MEMBER_NOT_FOUND
    );
    public static final BusinessException PasswordNotFoundException = new BusinessException(
        ErrorCode.PASSWORD_NOT_FOUND
    );
    public static final BusinessException PermanentBanException = new BusinessException(
        ErrorCode.PERMANENT_BAN
    );
    public static final BusinessException SessionNotFoundException = new BusinessException(
        ErrorCode.SESSION_NOT_FOUND
    );

    // Message
    public static final BusinessException FailDecryptException = new BusinessException(
        ErrorCode.FAIL_DECRYPT
    );
    public static final BusinessException FailEncryptException = new BusinessException(
        ErrorCode.FAIL_ENCRYPT
    );
    public static final BusinessException MessageNotFoundException = new BusinessException(
        ErrorCode.MESSAGE_NOT_FOUND
    );
    public static final BusinessException MessageReportAlreadyExistedException = new BusinessException(
        ErrorCode.MESSAGE_REPORT_ALREADY_EXISTED
    );
    public static final BusinessException MessageReportAlreadyReportedException = new BusinessException(
        ErrorCode.MESSAGE_REPORT_ALREADY_REPORTED
    );
    public static final BusinessException MessageReportAlreadyReportException = new BusinessException(
        ErrorCode.MESSAGE_REPORT_ALREADY_REPORT
    );
    public static final BusinessException MessageReportNotFoundException = new BusinessException(
        ErrorCode.MESSAGE_REPORT_NOT_FOUND
    );

    // Room
    public static final BusinessException RoomAlreadyExistedException = new BusinessException(
        ErrorCode.ROOM_EXISTED
    );
    public static final BusinessException OverOneDayException = new BusinessException(
        ErrorCode.OVER_ONE_DAY
    );
    public static final BusinessException RoomAlreadyExtendException = new BusinessException(
        ErrorCode.ROOM_ALREADY_EXTEND
    );
    public static final BusinessException RoomNotFoundException = new BusinessException(
        ErrorCode.ROOM_NOT_FOUND
    );

    // RoomInMember
    public static final BusinessException RoomInMemberAlreadyExistedException = new BusinessException(
        ErrorCode.RIM_ALREADY_EXISTED
    );
    public static final BusinessException RoomInMemberAlreadyVoteResultException = new BusinessException(
        ErrorCode.RIM_ALREADY_VOTE_RESULT
    );
    public static final BusinessException RoomInMemberNotFoundException = new BusinessException(
        ErrorCode.RIM_NOT_FOUND
    );

    // Vote
    public static final BusinessException InvalidVoteExistedException = new BusinessException(
        ErrorCode.INVALID_VOTE_EXISTED
    );
    public static final BusinessException ReportCategoryNotFoundException = new BusinessException(
        ErrorCode.REPORT_CATEGORY_NOT_FOUND
    );
    public static final BusinessException VoteAlreadyExistedException = new BusinessException(
        ErrorCode.VOTE_ALREADY_EXISTED
    );
    public static final BusinessException VoteCategoryNotFoundException = new BusinessException(
        ErrorCode.VOTE_CATEGORY_NOT_FOUND
    );
    public static final BusinessException VoteNotFoundException = new BusinessException(
        ErrorCode.VOTE_NOT_FOUND
    );
    public static final BusinessException VoteReportAlreadyExistedException = new BusinessException(
        ErrorCode.VOTE_REPORT_ALREADY_EXISTED
    );

    // Block
    public static final BusinessException BlockAccessForbiddenException = new BusinessException(
        ErrorCode.BLOCK_ACCESS_FORBIDDEN);

    public static final BusinessException BlockAlreadyExistedException = new BusinessException(
        ErrorCode.BLOCK_ALREADY_EXISTED);

    public static final BusinessException LocationNotFoundException = new BusinessException(
        ErrorCode.LOCATION_NOT_FOUND);

    // Location
    public static final BusinessException AccessStoppedException = new BusinessException(
        ErrorCode.ACCESS_STOPPED
    );
    public static final BusinessException CollegeNotFoundException = new BusinessException(
        ErrorCode.COLLEGE_NOT_FOUND
    );
    public static final BusinessException OutOfCollegeException = new BusinessException(
        ErrorCode.OUT_OF_COLLEGE
    );

    // WebSocket
    public static final BusinessException MemberSessionListNullException = new BusinessException(
        ErrorCode.MEMBER_SESSION_LIST_IS_NULL
    );
    public static final BusinessException RoomSessionListNullException = new BusinessException(
        ErrorCode.ROOM_SESSION_LIST_IS_NULL
    );
}
