package com.dfs.CommandHandlers;

import com.dfs.DfsServerException;
import com.dfs.Transaction;

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

    public WriteHandler(String[] command, byte[] data, Transaction transaction) {
        this.command = command;
        this.data = data;
        this.transaction = transaction;
    }

    public String handleCommand() {
        try {
            parseCommand();
            handleData();
            Integer write = transaction.checkForMissingWrites();
            if(write != -1) {
                return "ACK_RESEND " + transactionId + " " + seqNumber + " 0 0" + "\r\n\r\n\r\n";
            } else {
                return "ACK " + transactionId + " " + seqNumber + "\r\n\r\n\r\n";
            }
        } catch (DfsServerException e) {
            return "ERROR " + transactionId + " " + seqNumber + " " + e.getErrorCode() + " " + e.getMessage().length() + "\r\n\r\n" + e.getMessage() + "\n";
        }
    }

    private void parseCommand() throws DfsServerException {
        if(command.length < MESSAGE_PARTS) {
            throw new DfsServerException(204, ERROR_204);
        }
        transactionId = Integer.parseInt(command[1]);
        seqNumber = Integer.parseInt(command[2]);
        contentLength = Integer.parseInt(command[3]);
    }

    private void handleData() throws DfsServerException {
        // after we parsed the command, we get left with "\n\r\n" in the data array
        // This will get rid of those extra characters
        // it also trims the data array so it cuts off at contentLength
        byte[] fileData = Arrays.copyOfRange(data, 3, contentLength + 3);
        addData(fileData);
        transaction.incrementSeqNumber();
        transaction.incrementWriteCount();
    }

    private void addData(byte[] fileData) throws DfsServerException {
        transaction.addToWriteMessages(seqNumber, fileData);
    }
}
