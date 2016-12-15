package com.dfs.CommandHandlers;

import com.dfs.Constants;
import com.dfs.DfsServerException;
import com.dfs.Transaction;

import java.io.File;
import java.util.Map;

import static com.dfs.Constants.ERROR_204;
import static com.dfs.Constants.MESSAGE_PARTS;

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

    public String getResponse() {
        return  "ACK " + transactionId + "\n";
    }

    public String handleCommand() {
        try {
            parseCommand();
            abortTransaction();
            return "ACK " + transactionId + "\n";
        } catch (DfsServerException e) {
            return "ERROR " + transactionId + " " + seqNumber + " " + e.getErrorCode() + " " + e.getMessage() + " " + e.getMessage().length() + "\n";
        }
    }

    public void parseCommand() throws DfsServerException {
        if(command.length < MESSAGE_PARTS) {
            throw new DfsServerException(204, ERROR_204);
        }
        seqNumber = Integer.parseInt(command[2]);
        transactionId = Integer.parseInt(command[1]);
    }

    public void abortTransaction() {
//        transactions.remove(transactionId, transaction);
        transactions.get(transactionId).setStatus(Constants.TXN_STATE.COMPLETE);
        String filePath = directory + transaction.getFileName();
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }
    }

}
