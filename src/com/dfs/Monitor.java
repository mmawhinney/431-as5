package com.dfs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Monitor {

    public void logTransactions(Map<Integer, Transaction> transactions) {
        // Write to hidden view
        ArrayList<JSONObject> collect = transactions
                .values()
                .stream()
                .map(this::convertTxnToJson)
                .collect(Collectors.toCollection(ArrayList::new));
        JSONArray arr = new JSONArray(collect);
    }

    public Map<Integer, Transaction> recover(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String temp;

            while ((temp = reader.readLine()) != null) {
                builder.append(temp);
            }
            reader.close();
            JSONArray arr = new JSONArray(builder.toString());
            ArrayList<Transaction> collect = new ArrayList<>();
            for (int i = 0; i < arr.toList().size(); i++) {
                JSONObject tmp = (JSONObject) arr.get(i);

                collect.add(makeTxnFromJson(tmp));
            }
            HashMap<Integer, Transaction> out = new HashMap<>();
            collect.forEach((transaction) -> out.put(transaction.getId(), transaction));
            return out;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void cleanExit() {
    }

    public void dirtyExit() {
    }

    private JSONObject convertTxnToJson(Transaction txn) {
        JSONObject temp = new JSONObject();
        temp.put("id", txn.getId());
        temp.put("seq", txn.getCurrentSeqNum());
        temp.put("len", txn.getByteStream().size());
        temp.put("data", Arrays.toString(txn.getByteStream().toByteArray()));
        temp.put("status", txn.getStatus().toString());
        temp.put("file", txn.getFileName());
        temp.put("write", txn.getWriteCount());
        return temp;
    }

    public Transaction makeTxnFromJson(JSONObject obj) {
        return new Transaction(obj);
    }

}
