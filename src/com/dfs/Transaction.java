package com.dfs;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.dfs.Constants.ERROR_202;
import static com.dfs.Constants.ERROR_205;

public class Transaction {

    private int id;
    private int currentSeqNum;
    private String fileName;
    private ByteArrayOutputStream byteStream;
    private long time;

    private int writeCount;
    private byte[] data;

    private Constants.TXN_STATE status;

    private Map<Integer, byte[]> writeMessages;


    public Transaction(JSONObject in) {
        id = in.getInt("id");
        currentSeqNum = in.getInt("seq");
        fileName = in.getString("file");
        byteStream = new ByteArrayOutputStream();
        data = in.getString("data").getBytes();
        writeCount = in.getInt("write");
        writeMessages = new TreeMap<>();
        fileName = in.getString("file");
        String statusString = in.getString("status");
        if(statusString.equals(Constants.TXN_STATE.COMPLETE.name())){
            status = Constants.TXN_STATE.COMPLETE;
        } else if(statusString.equals(Constants.TXN_STATE.PARTIAL.name())){
            status = Constants.TXN_STATE.PARTIAL;
        } else {
            status = Constants.TXN_STATE.NEW;
        }
        time = in.getLong("time");
    }

    public Transaction(String[] command) {
        id = Integer.parseInt(command[1]);
        currentSeqNum = Integer.parseInt(command[2]);
        byteStream = new ByteArrayOutputStream();
        writeMessages = new TreeMap<>();
        writeCount = 1;
        status = Constants.TXN_STATE.NEW;
        time = System.currentTimeMillis();
    }

    public Transaction(String[] command, int id) {
        this.id = id;
        currentSeqNum = Integer.parseInt(command[2]);
        byteStream = new ByteArrayOutputStream();
        writeMessages = new TreeMap<>();
        writeCount = 1;
        status = Constants.TXN_STATE.NEW;
        time = System.currentTimeMillis();
    }

    public void addToWriteMessages(Integer seqNum, byte[] data) throws DfsServerException {
        if (writeMessages.containsKey(seqNum)) {
            throw new DfsServerException(202, ERROR_202);
        }
        writeMessages.put(seqNum, data);
    }

    private boolean isCommitAllowed() {
        Set keys = writeMessages.keySet();
        for (int i = 1; i < writeCount; i++) {
            if (!keys.contains(i)) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<Integer> missingWrites() {
        Set keys = writeMessages.keySet();
        ArrayList<Integer> missingWrites = new ArrayList<>();
        for (int i = 1; i < writeCount; i++) {
            if (!keys.contains(i)) {
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

    public Constants.TXN_STATE getStatus() {
        return status;
    }

    public synchronized void setStatus(Constants.TXN_STATE status) {
        this.status = status;
    }

    public int getWriteCount() {
        return writeCount;
    }

    public void setWriteCount(int writeCount) {
        this.writeCount = writeCount;
    }

    public void writeDatatoStream() throws DfsServerException {
        status = Constants.TXN_STATE.PARTIAL;
        for (Map.Entry<Integer, byte[]> entry : writeMessages.entrySet()) {
            byte[] bytes = entry.getValue();
            try {
                byteStream.write(bytes);
            } catch (IOException e) {
                throw new DfsServerException(205, ERROR_205);
            }
        }
    }

    public Integer checkForMissingWrites() {
        if (!isCommitAllowed()) {
            ArrayList<Integer> missingWrites = missingWrites();
            if (missingWrites != null) {
                return missingWrites.get(0);
            }
        }
        return -1;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
