package com.dfs.CommandHandlers;

import com.dfs.DfsServerException;

import java.io.BufferedReader;
import java.io.IOException;

import static com.dfs.Constants.ERROR_204;
import static com.dfs.Constants.MESSAGE_PARTS;

public class CommitHandler implements CommandHandler {

    private int transactionId;
    private int seqNumber;
    private int contentLength;
    private String filename;

    private String command;
    private BufferedReader reader;

    public CommitHandler(String command, BufferedReader reader) {
        this.command = command;
        this.reader = reader;
    }

    public String getResponse() {
        return  "COMMIT message received.\ntransaction ID = " + transactionId + "\nsequence number = " + seqNumber +
                "\ncontent length = " + contentLength + "\n";
    }

    public void parseCommand() throws DfsServerException {
        String[] parts;
        parts = command.split(" ");
        if(parts.length < MESSAGE_PARTS) {
            throw new DfsServerException(ERROR_204);
        }
        transactionId = Integer.parseInt(parts[1]);
        seqNumber = Integer.parseInt(parts[2]);
        contentLength = Integer.parseInt(parts[3]);
    }
}
