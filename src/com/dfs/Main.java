package com.dfs;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        String directory = args[2];

        try {
            TCPServer server = new TCPServer(port, ip, directory);
            server.run();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}


