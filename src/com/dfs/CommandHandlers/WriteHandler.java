package com.dfs.CommandHandlers;

import com.dfs.DfsServerException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;

import static com.dfs.Constants.ERROR_204;
import static com.dfs.Constants.MESSAGE_PARTS;

public class WriteHandler implements CommandHandler {

    private int transactionId;
    private int seqNumber;
    private int contentLength;

    private String command;
    private BufferedReader reader;
    private BufferedInputStream in;

    private byte[] fileData;

    public WriteHandler(String command, BufferedReader reader, BufferedInputStream in) {
        this.command = command;
        this.reader = reader;
        this.in = in;
        fileData = new byte[1024];
    }



    public String getResponse() {
        return "WRITE message received.\ntransaction ID = " + transactionId + "\nsequence number = " + seqNumber +
                "\ncontent length = " + contentLength + "\n";
    }

    public void parseCommand() throws DfsServerException {
        String [] parts;
        parts = command.split(" ");
        if(parts.length < MESSAGE_PARTS) {
            throw new DfsServerException(ERROR_204);
        }
        transactionId = Integer.parseInt(parts[1]);
        seqNumber = Integer.parseInt(parts[2]);
        contentLength = Integer.parseInt(parts[3]);
    }

    public void parseData() throws IOException {
//        reader.readLine(); // consume new line in header
        byte[] b = new byte[1024];
        int bytesRead = in.read(b, 0, 1024);
        System.out.println("bytes read = " + bytesRead);
        while(bytesRead > 0) {
            bytesRead = in.read(fileData, 0, 1024);
            System.out.println("bytes read = " + bytesRead);
        }
        System.out.println(new String(fileData));
//        String data = reader.readLine();
//        while(data != null) {
//            fileData.add(data);
//            System.out.println(data);
//            data = reader.readLine();
//
//        }
    }
}
