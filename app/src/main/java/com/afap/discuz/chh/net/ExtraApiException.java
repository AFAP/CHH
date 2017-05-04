package com.afap.discuz.chh.net;

/**
 * 自定义异常
 */
public class ExtraApiException extends RuntimeException {

    public final static  int AUTH_TIMEOUT = 401;




    private int status;
    private String message;


    public ExtraApiException(int status, String message) {
        this.status = status;
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ExtraApiException{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
