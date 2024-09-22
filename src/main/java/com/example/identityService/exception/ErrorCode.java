package com.example.identityService.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    USER_EXISTED(1001, "User existed", HttpStatus.BAD_REQUEST),
    NOTFOUND_USER(1002, "User doesn't exist or wrong Id", HttpStatus.NOT_FOUND),
    UserName_Invalid(1003, "user name must at least 8 character", HttpStatus.BAD_REQUEST),
    Password_Invalid(1004, "password must at least 8 character", HttpStatus.BAD_REQUEST),
    NotFound_Key(1005, "Invalid key", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED_BY_USERNAME(1006, "User doesn't exist or wrong username", HttpStatus.NOT_FOUND),
    Authenticated_fail(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    Authorize_fail(1008, "You do not have permission", HttpStatus.FORBIDDEN),
    UNCATEGORY_EXCEPTION(9999, "Uncategorized", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;
}
