package com.dfs;

public class Constants {

    public static final int MESSAGE_PARTS = 4;

    public static final String ERROR_201 = "Invalid Transaction ID";
    public static final String ERROR_202 = "Invalid operation";
    public static final String ERROR_204 = "Invalid message format";
    public static final String ERROR_205 = "File I/O failure";
    public static final String ERROR_206 = "File not found";

    public static final String READ = "READ";
    public static final String NEW_TXN = "NEW_TXN";
    public static final String WRITE = "WRITE";
    public static final String COMMIT = "COMMIT";
    public static final String ABORT = "ABORT";
    public static final String ACK = "ACK";
    public static final String ACK_RESEND = "ACK_RESEND";
    public static final String ERROR = "ERROR";
    public static final String CMD_FILE = ".cmdlog";
    public static final String TXN_FILE = ".transactions";

    public static final long TIMEOUT = 300000;

    public enum TXN_STATE {COMPLETE, NEW, PARTIAL}
}
