package com.dfs.CommandHandlers;

import com.dfs.DfsServerException;

import java.io.BufferedReader;

import static com.dfs.Constants.ERROR_204;
import static com.dfs.Constants.MESSAGE_PARTS;

public class AbortHandler implements CommandHandler {

    private int transactionId;

    private String command;
    private BufferedReader reader;

    public AbortHandler(String command, BufferedReader reader) {
        this.command = command;
        this.reader = reader;
    }

    public String getResponse() {
        return  "ABORT message received.\ntransaction ID = " + transactionId + "\n";
    }

    public void parseCommand() throws DfsServerException {
        String[] parts;
        parts = command.split(" ");
        if(parts.length < MESSAGE_PARTS) {
            throw new DfsServerException(ERROR_204);
        }
        transactionId = Integer.parseInt(parts[1]);
    }

}
