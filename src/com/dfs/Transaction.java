package com.dfs;

import java.io.ByteArrayOutputStream;

public class Transaction {

    private int id;
    private int currentSeqNum;
    private String fileName;
    private ByteArrayOutputStream byteStream;


    public Transaction(String[] command) {
        id = Integer.parseInt(command[1]);
        currentSeqNum = Integer.parseInt(command[2]);
        byteStream = new ByteArrayOutputStream();
    }

    public ByteArrayOutputStream getByteStream() {
        return byteStream;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public int getId() {
        return id;
    }

    public int getCurrentSeqNum() {
        return currentSeqNum;
    }

    public void incrementSeqNumber() {
        currentSeqNum++;
    }
}
