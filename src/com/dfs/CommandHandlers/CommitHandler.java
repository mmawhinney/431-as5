package com.dfs.CommandHandlers;

import com.dfs.DfsServerException;
import com.dfs.Transaction;

import java.io.*;

import static com.dfs.Constants.ERROR_204;
import static com.dfs.Constants.ERROR_205;
import static com.dfs.Constants.MESSAGE_PARTS;

public class CommitHandler implements CommandHandler {

    private int transactionId;
    private int seqNumber;
    private int contentLength;
    private String filename;
    private Transaction transaction;
    private String directory;

    private String[] command;

    public CommitHandler(String[] command, Transaction transaction, String directory) {
        this.command = command;
        this.transaction = transaction;
        this.directory = directory;
    }

    public String handleCommand() {
        try {
            parseCommand();
            writeToDisk();
            return "ACK " + transactionId + " " + seqNumber + "\n";
        } catch (DfsServerException e) {
            return "ERROR " + transactionId + " " + seqNumber + " " + e.getErrorCode() + " " + e.getMessage() + " " + e.getMessage().length() + "\n";
        }
    }

    private void parseCommand() throws DfsServerException {
        if(command.length < MESSAGE_PARTS) {
            throw new DfsServerException(204, ERROR_204);
        }
        transactionId = Integer.parseInt(command[1]);
        seqNumber = Integer.parseInt(command[2]);
        contentLength = Integer.parseInt(command[3]);
        if(transaction.getCurrentSeqNum() != seqNumber) {
            throw new DfsServerException(204, ERROR_204);
        }
    }

    private void writeToDisk() throws DfsServerException {
        String filepath = directory + transaction.getFileName();
        File file = new File(filepath);
        boolean fileExists = file.exists();
        try {
            if(!fileExists) {
                boolean fileCreated = file.createNewFile();
                if(!fileCreated) {
                    throw new DfsServerException(205, ERROR_205);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }


        ByteArrayOutputStream byteStream = transaction.getByteStream();
        try(FileOutputStream fileStream = new FileOutputStream(filepath, fileExists)) {
            byteStream.writeTo(fileStream);
            fileStream.flush();
            fileStream.getFD().sync();
        } catch (FileNotFoundException e) {
            System.out.println(e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
