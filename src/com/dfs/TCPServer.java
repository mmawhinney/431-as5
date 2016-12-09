package com.dfs;

import com.dfs.CommandHandlers.AbortHandler;
import com.dfs.CommandHandlers.CommitHandler;
import com.dfs.CommandHandlers.NewTxnHandler;
import com.dfs.CommandHandlers.WriteHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private static final String READ = "READ";
    private static final String NEW_TXN = "NEW_TXN";
    private static final String WRITE = "WRITE";
    private static final String COMMIT = "COMMIT";
    private static final String ABORT = "ABORT";
    private static final String ACK = "ACK";
    private static final String ACK_RESEND = "ACK_RESEND";
    private static final String ERROR = "ERROR";

    private ServerSocket serverSocket;

    public TCPServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void run() throws IOException {
        while(true) {
            Socket socket = serverSocket.accept();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            String response = parseInput(reader);
            output.writeBytes(response + '\n');
            socket.close();
        }
    }

    private String parseInput(BufferedReader reader) throws IOException {
        String commandLine = reader.readLine();
        if(commandLine == null) {
            return "";
        }
        String upperInput = commandLine.toUpperCase();
        try {
            if (upperInput.contains(READ)) {
                return "Read received";
            } else if (upperInput.contains(NEW_TXN)) {
                NewTxnHandler newTxn = new NewTxnHandler(commandLine, reader);
                newTxn.parseCommand();
                newTxn.parseFileName();
                return newTxn.getResponse();
            } else if (upperInput.contains(WRITE)) {
                WriteHandler write = new WriteHandler(commandLine, reader);
                write.parseCommand();
//                write.parseData();
                return "testing\n";//write.getResponse();
            } else if (upperInput.contains(COMMIT)) {
                CommitHandler commit = new CommitHandler(commandLine, reader);
                commit.parseCommand();
                return commit.getResponse();
            } else if (upperInput.contains(ABORT)) {
                AbortHandler abort = new AbortHandler(commandLine, reader);
                abort.parseCommand();
                return abort.getResponse();
            } else {
                return "unknown command received";
            }
        } catch (DfsServerException e) {
            return e.getLocalizedMessage();
        }
    }
}
