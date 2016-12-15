package com.dfs.CommandHandlers;


import com.dfs.DfsServerException;
import com.dfs.Transaction;

import static com.dfs.Constants.ERROR_202;

public class NewTxnHandler implements CommandHandler {

    private int transactionId;
    private int seqNumber;
    private int contentLength;
    private String filename;

    private byte[] data;
    private String[] command;
    private Transaction transaction;

    public NewTxnHandler(String[] command, byte[] data, Transaction transaction) {
        this.command = command;
        this.data = data;
        this.transaction = transaction;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getResponse() {
        return  "ACK " + transactionId + " " + seqNumber + "\n";
    }

    public void parseCommand() throws DfsServerException {
        transactionId = transaction.getId();
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
        transaction.setFileName(filename);
        System.out.println(filename);
    }

}
