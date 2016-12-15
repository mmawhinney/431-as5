package com.dfs;

/**
 * Created by matheson on 2016-12-08.
 */
public class DfsServerException extends Exception {

    private int errorCode;

    public DfsServerException() {
        super();
    }

    public DfsServerException(String message) {
        super(message);
    }

    public DfsServerException(Throwable throwable) {
        super(throwable);
    }

    public DfsServerException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DfsServerException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
