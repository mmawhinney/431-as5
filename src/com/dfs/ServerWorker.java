package com.dfs;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

import static com.dfs.Constants.*;


public class ServerWorker implements Runnable {

    private String[] messageParts;
    private Socket clientSocket;
    private String directory;

    private Map<Integer, Transaction> transactions;

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
            while ((bytesRead = in.read(b)) > 0) {
                stream.write(b, 0, bytesRead);
            }
            bytes = stream.toByteArray();
        } catch (SocketException e) {
            System.out.println("socket exception caught");
            bytes = stream.toByteArray();
        } catch (IOException e) {
            bytes = stream.toByteArray();
        }

        try (ByteArrayOutputStream data = new ByteArrayOutputStream()) {
            messageParts = new String[MESSAGE_PARTS];
            int start = 0;
            int partCount = 0;
            int i = 0;
            while (i < bytes.length) {
                if ((bytes[i] == 32 || bytes[i] == 13) && partCount < MESSAGE_PARTS) {
                    messageParts[partCount] = new String(bytes, start, i - start);
                    partCount++;
                    start = i + 1;
                } else if (partCount == MESSAGE_PARTS) {
                    data.write(bytes[i]);
                }
                i++;
            }
            return data.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return new byte[]{};
    }

    private synchronized void addToTransactionsMap(Integer id, Transaction transaction) {
        transactions.put(id, transaction);
    }

    private String handleCommand(byte[] data) throws IOException {
        if (messageParts[0] == null) {
            return "";
        }
        Integer id = Integer.parseInt(messageParts[1]);
        Transaction transaction = transactions.get(id);
        String commandType = messageParts[0].toUpperCase();

        if (commandType.contains(READ)) {
            ReadHandler reader = new ReadHandler(data, directory);
            return reader.handleCommand();
        } else if (commandType.contains(NEW_TXN)) {
            if (transactions.containsKey(TCPServer.getTransactionCount() + 1)) {
                return ERROR + " " + id + " " + messageParts[2] + " " + "201" + " " + ERROR_201.length() + "\r\n\r\n" + ERROR_201 + "\n";
            }
            TCPServer.incrementTransactionCount();
            transaction = new Transaction(messageParts, TCPServer.getTransactionCount());
            addToTransactionsMap(TCPServer.getTransactionCount(), transaction);
            NewTxnHandler newTxn = new NewTxnHandler(messageParts, data, transaction);
            return newTxn.handleCommand();
        } else if (commandType.contains(WRITE)) {
            WriteHandler write = new WriteHandler(messageParts, data, transaction);
            return write.handleCommand();
        } else if (commandType.contains(COMMIT)) {
            CommitHandler commit = new CommitHandler(messageParts, transaction, directory, transactions);
            return commit.handleCommand();
        } else if (commandType.contains(ABORT)) {
            AbortHandler abort = new AbortHandler(messageParts, transaction, transactions, directory);
            return abort.handleCommand();
        } else if (commandType.contains(EXIT)) {
            Monitor.cleanExit(directory);
            System.exit(0);
        } else {
            return ERROR + " " + id + " 0 " + "202 " + ERROR_202.length() + "\r\n\r\n" + ERROR_202 + "\n";
        }
        return ERROR + " " + id + " 0 " + "202 " + ERROR_202.length() + "\r\n\r\n" + ERROR_202 + "\n";
    }
}
