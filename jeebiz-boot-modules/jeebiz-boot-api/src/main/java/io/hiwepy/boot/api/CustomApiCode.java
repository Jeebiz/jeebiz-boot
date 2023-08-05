package io.hiwepy.boot.api;

public interface CustomApiCode {

    int getCode();

    String getReason();

    default String getStatus() {
        return Constants.RT_SUCCESS;
    }

    ;

}
