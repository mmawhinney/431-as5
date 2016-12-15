package com.dfs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class TCPServer {

    // We'll use this map to keep track of each transaction, mapping the transaction ID to the transaction
    // Since WRITE & COMMIT messages contain the transaction id, this will let us query for the correct transaction
    // just based on that ID
    private Map<Integer, Transaction> transactions;

    private ServerSocket serverSocket;
    private String directory;

    private static int transactionCount = 0;

    public static synchronized void incrementTransactionCount() {
        transactionCount++;
    }

    public static int getTransactionCount() {
        return transactionCount;
    }

    public TCPServer(int port, String ip, String directory) throws IOException {
        InetAddress addr = InetAddress.getByName(ip);
        serverSocket = new ServerSocket(port, 0, addr);
        transactions = new HashMap<>();
        this.directory = directory;
    }

    public void run() throws IOException {
        while(true) {
            Socket clientSocket = serverSocket.accept();
            Thread serverWorker = new Thread(new ServerWorker(clientSocket, transactions, directory));
            serverWorker.start();
        }
    }
}
