package com.dfs.CommandHandlers;

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

    public AbortHandler(String[] command, Transaction transaction) {
        this.command = command;
        this.transaction = transaction;
    }

    public String getResponse() {
        return  "ACK " + transactionId + "\n";
    }

    public void parseCommand() throws DfsServerException {
        if(command.length < MESSAGE_PARTS) {
            throw new DfsServerException(ERROR_204);
        }

        transactionId = Integer.parseInt(command[1]);
    }

    public void abortTransaction(Map<Integer, Transaction> transactions, String directory) {
        transactions.remove(transactionId, transaction);
        String filePath = directory + transaction.getFileName();
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }
    }

}
