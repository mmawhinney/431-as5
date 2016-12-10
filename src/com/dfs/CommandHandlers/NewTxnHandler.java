package com.dfs.CommandHandlers;


import com.dfs.DfsServerException;

import java.io.BufferedReader;
import java.io.IOException;

import static com.dfs.Constants.ERROR_202;
import static com.dfs.Constants.ERROR_204;
import static com.dfs.Constants.MESSAGE_PARTS;

public class NewTxnHandler implements CommandHandler {

    private int transactionId;
    private int seqNumber;
    private int contentLength;
    private String filename;

    private byte[] data;
    private String[] command;

    public NewTxnHandler(String[] command, byte[] data) {
        this.command = command;
        this.data = data;
    }

//    private String command;
//    private BufferedReader reader;

//    public NewTxnHandler(String command, BufferedReader reader) {
//        this.command = command;
//        this.reader = reader;
//    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getResponse() {
        return  "NEW_TXN message received.\ntransaction ID = " + transactionId + "\nsequence number = " + seqNumber +
                "\ncontent length = " + contentLength + "\nfilename = " + filename + "\n";
    }

    public void parseCommand() throws DfsServerException {
        transactionId = Integer.parseInt(command[1]);
        seqNumber = Integer.parseInt(command[2]);
        contentLength = Integer.parseInt(command[3]);
        if(seqNumber != 0) {
            throw new DfsServerException(ERROR_202);
        }
    }

    public void parseFileName() {
        byte[] filenameData = new byte[data.length];
        int j = 0;
        for (byte b : data) {
            if (b > 32 && b < 127) {
                filenameData[j] = b;
                j++;
            }
        }
        filename = new String(filenameData, 0, j);
        System.out.println(filename);
    }

//    public void parseCommand() throws DfsServerException {
//        String[] parts;
//        parts = command.split(" ");
//        if(parts.length < MESSAGE_PARTS) {
//            throw new DfsServerException(ERROR_204);
//        }
//        transactionId = Integer.parseInt(parts[1]);
//        seqNumber = Integer.parseInt(parts[2]);
//        contentLength = Integer.parseInt(parts[3]);
//        if(seqNumber != 0) {
//            throw new DfsServerException(ERROR_202);
//        }
//    }

//    public void parseFileName() throws IOException {
//        reader.readLine(); // consume new line in header
//        filename = reader.readLine();
//    }
}
