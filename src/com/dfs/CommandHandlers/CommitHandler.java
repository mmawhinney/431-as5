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
    private boolean commitSuccess;

    public CommitHandler(String[] command, Transaction transaction, String directory) {
        this.command = command;
        this.transaction = transaction;
        this.directory = directory;
    }

    public String getResponse() {
        if(commitSuccess) {
            return "ACK " + transactionId + " " + seqNumber + "\n";
        } else {
            return "ERROR " + transactionId + " " + seqNumber + " 205 " + ERROR_205 + " " + ERROR_205.length() + "\n";
        }
    }

    public void parseCommand() throws DfsServerException {
        if(command.length < MESSAGE_PARTS) {
            throw new DfsServerException(ERROR_204);
        }
        transactionId = Integer.parseInt(command[1]);
        seqNumber = Integer.parseInt(command[2]);
        contentLength = Integer.parseInt(command[3]);
    }

    public void writeToDisk() throws DfsServerException {
        String filepath = directory + transaction.getFileName();
        File file = new File(filepath);
        try {
            if(!file.exists()) {
                boolean fileCreated = file.createNewFile();
                if(!fileCreated) {
                    throw new DfsServerException(ERROR_205);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        ByteArrayOutputStream byteStream = transaction.getByteStream();
        try(FileOutputStream fileStream = new FileOutputStream(filepath)) {
            byteStream.writeTo(fileStream);
            fileStream.flush();
            fileStream.getFD().sync();
        } catch (FileNotFoundException e) {
            commitSuccess = false;
            System.out.println(e.getLocalizedMessage());
        } catch (IOException e) {
            commitSuccess = false;
            System.out.println(e.getLocalizedMessage());
        }
        commitSuccess = true;
    }
}
