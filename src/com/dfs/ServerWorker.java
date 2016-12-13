package com.dfs;

import com.dfs.CommandHandlers.NewTxnHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static com.dfs.Constants.*;


public class ServerWorker implements Runnable {

    private String[] messageParts;
    private Socket clientSocket;

    public ServerWorker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

            System.out.println("parsing input...");
            byte[] data = parseInput(in);
            System.out.println("input parsed...");
            handleCommand(data);
            out.writeBytes("Testing\n");

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
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            int bytesRead = 0;
            while((bytesRead = in.read(b)) > 0) {
                System.out.println("bytes read = " + bytesRead);
                stream.write(b, 0, bytesRead);
            }
            bytes = stream.toByteArray();
        } catch (IOException e) {
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


//        for(String str : parts) {
//            System.out.println(str);
//        }
        // return remaining data
    }

    private String handleCommand(byte[] data) throws IOException {
        if(messageParts[0] == null) {
            return "";
        }
        String commandType = messageParts[0].toUpperCase();
        try {
            if (commandType.contains(READ)) {
                return "Read received";
            } else if (commandType.contains(NEW_TXN)) {
                System.out.println("creating a transaction");
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
}
