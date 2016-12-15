package com.dfs;

import java.io.File;
import java.util.Map;

import static com.dfs.Constants.*;

public class AbortHandler implements CommandHandler {

    private int transactionId;

    private String[] command;
    private Transaction transaction;
    private int seqNumber;
    private String directory;
    private Map<Integer, Transaction> transactions;

    public AbortHandler(String[] command, Transaction transaction, Map<Integer, Transaction> transactions, String directory) {
        this.command = command;
        this.transaction = transaction;
        this.transactions = transactions;
        this.directory = directory;
    }

    public String handleCommand() {
        try {
            parseCommand();
            abortTransaction();

            return ACK + " " + transactionId + "\r\n\r\n\r\n";
        } catch (DfsServerException e) {
            return ERROR + " " + transactionId + " " + seqNumber + " " + e.getErrorCode()  + " " + e.getMessage().length() + "\r\n\r\n" + e.getMessage() + "\n";
        }
    }

    private void parseCommand() throws DfsServerException {
        if(command.length < MESSAGE_PARTS) {
            throw new DfsServerException(204, ERROR_204);
        }
        seqNumber = Integer.parseInt(command[2]);
        transactionId = Integer.parseInt(command[1]);
    }

    private void abortTransaction() {
        transactions.get(transactionId).setStatus(Constants.TXN_STATE.COMPLETE);
        String filePath = directory + transaction.getFileName();
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }
    }

}
