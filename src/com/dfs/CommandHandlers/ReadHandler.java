package com.dfs.CommandHandlers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadHandler implements CommandHandler {
    private byte[] data;
    private String filename;
    private String directory;

    public ReadHandler(byte[] data, String directory) {
        this.data = data;
        this.directory = directory;
    }

    @Override
    public String handleCommand() {
        try {
            parseFileName();
            BufferedReader reader = new BufferedReader((new FileReader(directory + filename)));
            StringBuilder builder = new StringBuilder();
            String output;

            while ((output = reader.readLine()) != null) {
                builder.append(output);
            }
            reader.close();
            return builder.toString();
        } catch (FileNotFoundException e) {
            return "ERROR -1 0 "+ "206 " + e.getMessage().length() + "\r\n\r\n" + e.getMessage() + "\n";
        } catch (IOException e) {
            return "ERROR -1 0 "+ "205 " + e.getMessage().length() + "\r\n\r\n" + e.getMessage() + "\n";
        }
    }

    private void parseFileName() {
        byte[] filenameData = new byte[data.length];
        int j = 0;
        for (byte b : data) {
            if (b > 32 && b < 127) {
                filenameData[j] = b;
                j++;
            }
        }
        filename = new String(filenameData, 0, j);
    }
}
