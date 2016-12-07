package com.dfs;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        int port = Integer.parseInt(args[1]);

        try {
            TCPServer server = new TCPServer(port);
            server.run();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}


