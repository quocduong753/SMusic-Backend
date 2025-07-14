package com.example.musicplayer.exception;

public enum   ErrorCode {
    INVALID_INPUT(1001, "Invalid input data"),
    RESOURCE_NOT_FOUND(1002, "Resource not found"),
    ACCESS_DENIED(1003, "Access denied"),
    DATABASE_ERROR(1004, "Database operation failed"),
    UNCATEGORIZED_EXCEPTION(1005, "Uncategorized error"),
    INVALID_KEY(1006,"Invalid message key"),
    UNAUTHORIZED(1007, "Unauthorized"),
    TOKEN_EXPIRED(1008, "Token expired"),
    INVALID_TOKEN(1009, "Invalid token"),
    INTERNAL_SERVER_ERROR(1010, "Internal server error"),
    DUPLICATE_RESOURCE(1011,"Resource already exists"),

    NOTIFICATION_NOT_FOUND(2001, "Notification not found"),
    NOTIFICATION_ACCESS_DENIED(2002, "You are not allowed to access this notification"),

    SONG_NOT_FOUND(3001, "Song not found"),

    NAME_INVALID(4001, "Name must be at least 2 characters"),
    PASSWORD_INVALID(4002,"Password must be at least 6 characters"),
    EMAIL_INVALID(4003,"Email invalid"),
    PASSWORD_INCORRECT(4004, "Password is incorrect"),
    PASSWORD_NOT_SAME(4005,"passwords can't be the same"),
    EMAIL_ALREADY_EXISTS(4006, "Email already exists"),
    OTP_INVALID(4007,"Invalid OTP"),
    OTP_EXPIRED(4008,"OTP has expired"),
    GENRE_REQUIRED(4009, "Genre is required"),
    URL_REQUIRED(4010, "URL is required"),
    TITLE_REQUIRED(4011, "Title is required"),
    FORBIDDEN(4012, "Forbidden"),
    NEED_VIP_TO_SHARE(4013, "You must be a VIP to share this song"),
    EMAIL_NOT_FOUND(4014, "Email does not exist"),

    REPORT_NOT_FOUND(5001,"Báo cáo không tồn tại"),
    REPORT_ALREADY_HANDLED(5002,"Báo cáo đã được xử lý"),;


    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
