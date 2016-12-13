package com.dfs;

import com.dfs.CommandHandlers.AbortHandler;
import com.dfs.CommandHandlers.CommitHandler;
import com.dfs.CommandHandlers.NewTxnHandler;
import com.dfs.CommandHandlers.WriteHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static com.dfs.Constants.MESSAGE_PARTS;

public class TCPServer {



    // We'll use this map to keep track of each transaction, mapping the transaction ID to the transaction
    // Since WRITE & COMMIT messages contain the transaction id, this will let us query for the correct transaction
    // just based on that ID
    private Map<Integer, Transaction> transactions;

    private ServerSocket serverSocket;

//    private String[] messageParts;

    public TCPServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        transactions = new HashMap<>();
//        messageParts = new String[MESSAGE_PARTS];
    }

    public void run() throws IOException {
        while(true) {
            Socket clientSocket = serverSocket.accept();
            Thread serverWorker = new Thread(new ServerWorker(clientSocket));
            serverWorker.start();
//            Socket socket = serverSocket.accept();
//
//            DataInputStream in = new DataInputStream(socket.getInputStream());
//
////            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
//
////            String response = parseInput(reader, in);
//
//            output.writeBytes("testing" + '\n');
//            socket.close();
        }
    }



//    private String parseInput(BufferedReader reader, BufferedInputStream in) throws IOException {
//        String commandLine = reader.readLine();
//        if(commandLine == null) {
//            return "";
//        }
//        String upperInput = commandLine.toUpperCase();
//        try {
//            if (upperInput.contains(READ)) {
//                return "Read received";
//            } else if (upperInput.contains(NEW_TXN)) {
//                NewTxnHandler newTxn = new NewTxnHandler(commandLine, reader);
//                newTxn.parseCommand();
//                newTxn.parseFileName();
//                return newTxn.getResponse();
//            } else if (upperInput.contains(WRITE)) {
//                WriteHandler write = new WriteHandler(commandLine, reader, in);
//                write.parseCommand();
//                write.parseData();
//                return "testing\n";//write.getResponse();
//            } else if (upperInput.contains(COMMIT)) {
//                CommitHandler commit = new CommitHandler(commandLine, reader);
//                commit.parseCommand();
//                return commit.getResponse();
//            } else if (upperInput.contains(ABORT)) {
//                AbortHandler abort = new AbortHandler(commandLine, reader);
//                abort.parseCommand();
//                return abort.getResponse();
//            } else {
//                return "unknown command received";
//            }
//        } catch (DfsServerException e) {
//            return e.getLocalizedMessage();
//        }
//    }
}
