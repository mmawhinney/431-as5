package com.dfs.CommandHandlers;

import com.dfs.DfsServerException;
import com.dfs.Transaction;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;

import static com.dfs.Constants.ERROR_204;
import static com.dfs.Constants.MESSAGE_PARTS;

public class WriteHandler implements CommandHandler {

    private int transactionId;
    private int seqNumber;
    private int contentLength;

    private String[] command;
    private byte[] data;

    private Transaction transaction;


//    public WriteHandler(String command, BufferedReader reader, BufferedInputStream in) {
//        this.command = command;
//        this.reader = reader;
//        this.in = in;
//        fileData = new byte[1024];
//    }

    public WriteHandler(String[] command, byte[] data, Transaction transaction) {
        this.command = command;
        this.data = data;
        this.transaction = transaction;
    }


    public String getResponse() {
        return "\n";
    }

    public void parseCommand() throws DfsServerException {
        transactionId = Integer.parseInt(command[1]);
        seqNumber = Integer.parseInt(command[2]);
        contentLength = Integer.parseInt(command[3]);
    }

    public void handleData() {
        byte[] fileData = Arrays.copyOfRange(data, 3, contentLength + 3);
        writeData(fileData);
        for(byte b : fileData) {
            System.out.println((char)b);
        }
    }

    private void writeData(byte[] fileData) {
        try {
            transaction.getByteStream().write(fileData);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }


//    public void parseData() throws IOException {
//        reader.readLine(); // consume new line in header
//        byte[] b = new byte[1024];
//        int bytesRead = in.read(b, 0, 1024);
//        System.out.println("bytes read = " + bytesRead);
//        while(bytesRead > 0) {
//            bytesRead = in.read(fileData, 0, 1024);
//            System.out.println("bytes read = " + bytesRead);
//        }
//        System.out.println(new String(fileData));
//        String data = reader.readLine();
//        while(data != null) {
//            fileData.add(data);
//            System.out.println(data);
//            data = reader.readLine();
//
//        }
//    }
}
