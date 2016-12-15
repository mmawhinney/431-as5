package com.dfs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static com.dfs.Constants.ERROR_205;

public class Transaction {

    private int id;
    private int currentSeqNum;
    private String fileName;
    private ByteArrayOutputStream byteStream;
    private int writeCount;

    private Map<Integer, byte[]> writeMessages;


    public Transaction(String[] command) {
        id = Integer.parseInt(command[1]);
        currentSeqNum = Integer.parseInt(command[2]);
        byteStream = new ByteArrayOutputStream();
        writeMessages = new TreeMap<>();
        writeCount = 1;
    }

    public Transaction(String[] command, int id) {
        this.id = id;
        currentSeqNum = Integer.parseInt(command[2]);
        byteStream = new ByteArrayOutputStream();
        writeMessages = new TreeMap<>();
        writeCount = 1;
    }

    public void addToWriteMessages(Integer seqNum, byte[] data) {
        writeMessages.put(seqNum, data);
    }

    private boolean isCommitAllowed() {
        Set keys = writeMessages.keySet();
        for(int i = 1; i < writeCount; i++) {
            if(!keys.contains(i)) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<Integer> missingWrites() {
        Set keys = writeMessages.keySet();
        ArrayList<Integer> missingWrites = new ArrayList<>();
        for(int i = 1; i < writeCount; i++) {
            System.out.println("key = " + i);
            if(!keys.contains(i)) {
                missingWrites.add(i);
            }
        }
        return missingWrites;
    }

    public ByteArrayOutputStream getByteStream() {
        return byteStream;
    }

    public synchronized void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public int getId() {
        return id;
    }

    public int getCurrentSeqNum() {
        return currentSeqNum;
    }

    public synchronized void incrementSeqNumber() {
        currentSeqNum++;
    }

    public synchronized void incrementWriteCount() {
        writeCount++;
    }

    public void writeDatatoStream() throws DfsServerException {
        for(Map.Entry<Integer, byte[]> entry : writeMessages.entrySet()) {
            byte[] bytes = entry.getValue();
            try {
                byteStream.write(bytes);
            } catch (IOException e) {
                throw new DfsServerException(205, ERROR_205);
            }
        }
    }

    public Integer checkForMissingWrites() {
        if(!isCommitAllowed()) {
            ArrayList<Integer> missingWrites = missingWrites();
            if(missingWrites != null) {
                System.out.println("missing write found = " + missingWrites.get(0));
                return missingWrites.get(0);
            }
        }
        return -1;
    }
}
