package com.dfs;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by matheson on 2016-12-07.
 */
public class TCPServer {

    private String sentence;
    private String capitals;
    ServerSocket serverSocket;

    public TCPServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void run() throws IOException {
        while(true) {
            Socket socket = serverSocket.accept();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            sentence = reader.readLine();
            capitals = sentence.toLowerCase() + '\n';
            output.writeBytes(capitals);
        }
    }
}
