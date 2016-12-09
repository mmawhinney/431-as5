package com.dfs;

/**
 * Created by matheson on 2016-12-08.
 */
public class DfsServerException extends Exception {

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

}
