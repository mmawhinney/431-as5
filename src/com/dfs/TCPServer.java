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

    private static final String READ = "READ";
    private static final String NEW_TXN = "NEW_TXN";
    private static final String WRITE = "WRITE";
    private static final String COMMIT = "COMMIT";
    private static final String ABORT = "ABORT";
    private static final String ACK = "ACK";
    private static final String ACK_RESEND = "ACK_RESEND";
    private static final String ERROR = "ERROR";

    // We'll use this map to keep track of each transaction, mapping the transaction ID to the transaction
    // Since WRITE & COMMIT messages contain the transaction id, this will let us query for the correct transaction
    // just based on that ID
    private Map<Integer, Transaction> transactions;

    private ServerSocket serverSocket;

    private String[] messageParts;

    public TCPServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        transactions = new HashMap<>();
    }

    public void run() throws IOException {
        while(true) {
            Socket socket = serverSocket.accept();

            DataInputStream in = new DataInputStream(socket.getInputStream());

//            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

//            String response = parseInput(reader, in);
            byte[] data = parseInput(in);
            handleCommand(data);
            output.writeBytes("testing" + '\n');
            socket.close();
        }
    }

    private byte[] parseInput(DataInputStream in) throws IOException {
        byte[] b = new byte[4096];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int bytesRead = 0;
        while((bytesRead = in.read(b)) > 0) {
            stream.write(b, 0, bytesRead);
        }
        byte[] bytes = stream.toByteArray();
        ByteArrayOutputStream data = new ByteArrayOutputStream();

        messageParts = new String[MESSAGE_PARTS];
        int start = 0;
        int partCount = 0;
        int i = 0;
        while(i < bytes.length) {
            if((bytes[i] == 32 || bytes[i] == 13) && partCount < MESSAGE_PARTS) {
                messageParts[partCount] = new String(bytes, start, i - start);
                partCount++;
                start = i + 1;
                i = start;
            } else if(partCount == MESSAGE_PARTS) {
                data.write(bytes[i]);
                i++;
            } else {
                i++;
            }
        }
//        for(String str : parts) {
//            System.out.println(str);
//        }
        // return remaining data
        return data.toByteArray();
    }

    private String handleCommand(byte[] data) throws IOException {
        String commandType = messageParts[0].toUpperCase();
        try {
            if (commandType.contains(READ)) {
                return "Read received";
            } else if (commandType.contains(NEW_TXN)) {
                NewTxnHandler newTxn = new NewTxnHandler(messageParts, data);
                newTxn.parseCommand();
                newTxn.parseFileName();
                return newTxn.getResponse();
            } else if (commandType.contains(WRITE)) {
//                WriteHandler write = new WriteHandler(data);
//                write.parseCommand();
//                write.parseData();
//                return "testing\n";//write.getResponse();
            } else if (commandType.contains(COMMIT)) {
//                CommitHandler commit = new CommitHandler(data);
//                commit.parseCommand();
//                return commit.getResponse();
            } else if (commandType.contains(ABORT)) {
//                AbortHandler abort = new AbortHandler(data);
//                abort.parseCommand();
//                return abort.getResponse();
            } else {
                return "unknown command received";
            }
        } catch (DfsServerException e) {
            return e.getLocalizedMessage();
        }
        return "test";
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
