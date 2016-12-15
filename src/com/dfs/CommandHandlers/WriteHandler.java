package com.dfs.CommandHandlers;

import com.dfs.DfsServerException;
import com.dfs.Transaction;

import java.io.*;
import java.util.Arrays;


public class WriteHandler implements CommandHandler {

    private int transactionId;
    private int seqNumber;
    private int contentLength;

    private String[] command;
    private byte[] data;

    private Transaction transaction;

    public WriteHandler(String[] command, byte[] data, Transaction transaction) {
        this.command = command;
        this.data = data;
        this.transaction = transaction;
    }

    public String handleCommand() {
        try {
            parseCommand();
            handleData();
            return "\n";
        } catch (DfsServerException e) {
            return "ERROR " + transactionId + " " + seqNumber + " " + e.getErrorCode() + " " + e.getMessage() + " " + e.getMessage().length() + "\n";
        }
    }

    private void parseCommand() throws DfsServerException {
        transactionId = Integer.parseInt(command[1]);
        seqNumber = Integer.parseInt(command[2]);
        contentLength = Integer.parseInt(command[3]);
    }

    private void handleData() {
        // after we parsed the command, we get left with "\n\r\n" in the data array
        // This will get rid of those extra characters
        // it also trims the data array so it cuts off at contentLength
        byte[] fileData = Arrays.copyOfRange(data, 3, contentLength + 3);
        writeData(fileData);
        transaction.incrementSeqNumber();
    }

    private void writeData(byte[] fileData) {
        try {
            transaction.getByteStream().write(fileData);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }
}
