package com.dfs;

import com.dfs.CommandHandlers.CommitHandler;
import com.dfs.CommandHandlers.NewTxnHandler;
import com.dfs.CommandHandlers.WriteHandler;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import static com.dfs.Constants.*;


public class ServerWorker implements Runnable {

    private String[] messageParts;
    private Socket clientSocket;
    private String directory;

    private Map<Integer, Transaction> transactions = new HashMap<>();

    public ServerWorker(Socket clientSocket, Map<Integer, Transaction> transactions, String directory) {
        this.clientSocket = clientSocket;
        this.transactions = transactions;
        this.directory = directory;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

            byte[] data = parseInput(in);
            String reply = handleCommand(data);
            out.writeBytes(reply);

            in.close();
            out.close();

            clientSocket.close();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }


    private byte[] parseInput(DataInputStream in) {
        byte[] b = new byte[4096];
        byte[] bytes = new byte[4096];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            int bytesRead = 0;
            clientSocket.setSoTimeout(2000);
            while((bytesRead = in.read(b)) > 0) {
                stream.write(b, 0, bytesRead);
            }
            bytes = stream.toByteArray();
        } catch (SocketException e) {
            System.out.println("socket exception caught");
            bytes = stream.toByteArray();
        } catch (IOException e) {
            bytes = stream.toByteArray();
            System.out.println(e.getLocalizedMessage());
        }

        try(ByteArrayOutputStream data = new ByteArrayOutputStream()) {
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
            return data.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return new byte[]{};
    }

    private String handleCommand(byte[] data) throws IOException {
        if(messageParts[0] == null) {
            return "";
        }
        Integer id = Integer.parseInt(messageParts[1]);
        Transaction transaction;
        if(transactions.containsKey(id)) {
            transaction = transactions.get(id);
        } else {
            transaction = new Transaction(messageParts);
            transactions.put(id, transaction);
        }

        String commandType = messageParts[0].toUpperCase();
        System.out.println(commandType);
        try {
            if (commandType.contains(READ)) {
                return "Read received";
            } else if (commandType.contains(NEW_TXN)) {
                NewTxnHandler newTxn = new NewTxnHandler(messageParts, data, transaction);
                newTxn.parseCommand();
                newTxn.parseFileName();
                return newTxn.getResponse();
            } else if (commandType.contains(WRITE)) {
                WriteHandler write = new WriteHandler(messageParts, data, transaction);
                write.parseCommand();
                write.handleData();
                return write.getResponse();
            } else if (commandType.contains(COMMIT)) {
                CommitHandler commit = new CommitHandler(messageParts, transaction, directory);
                commit.parseCommand();
                commit.writeToDisk();
                return commit.getResponse();
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
}
