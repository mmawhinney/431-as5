package com.dfs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Monitor {

    public static void doExit(Map<Integer, Transaction> transactions, String directory) {
        logTransactions(transactions, directory);
    }

    private static void logTransactions(Map<Integer, Transaction> transactions, String directory) {
            ArrayList<JSONObject> collect = transactions
                    .values()
                    .stream()
                    .map(Monitor::convertTxnToJson)
                    .collect(Collectors.toCollection(ArrayList::new));

            JSONArray arr = new JSONArray(collect);
            try {
                PrintWriter log = new PrintWriter(directory + Constants.TXN_FILE, "UTF-8");
                log.println(arr.toString());
                log.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
    }

    private static Map<Integer, Transaction> recover(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String temp;

            while ((temp = reader.readLine()) != null) {
                builder.append(temp);
            }
            reader.close();
            String jsonTxt = builder.toString();
            if (jsonTxt.isEmpty()) {
                return new HashMap<>();
            }

            JSONArray arr = new JSONArray(jsonTxt);
            ArrayList<Transaction> collect = new ArrayList<>();
            for (int i = 0; i < arr.toList().size(); i++) {
                JSONObject tmp = (JSONObject) arr.get(i);

                collect.add(makeTxnFromJson(tmp));
            }
            HashMap<Integer, Transaction> out = new HashMap<>();
            collect.forEach((transaction) -> {
                // Drops txns older than 5 min
                long life = System.currentTimeMillis() - transaction.getTime();
                if (life <= Constants.TIMEOUT) {
                    out.put(transaction.getId(), transaction);
                }
            });

            if (collect.size() > 1) {
                return out;
            } else {
                return new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<Integer, Transaction> bootCheck(String directory) {
        try {
            File txns = new File(directory + Constants.TXN_FILE);
            if (!txns.exists()) {
                if (!txns.createNewFile()) {
                    throw new FileNotFoundException("Could not make .txns file");
                }
            }
            return recover(txns.getCanonicalPath());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new HashMap<>();
    }

    public static void cleanExit(String directory) {
        File txns = new File(directory + Constants.TXN_FILE);
        if (txns.exists()) {
            txns.deleteOnExit();
        }
    }

    private static JSONObject convertTxnToJson(Transaction txn) {
        JSONObject temp = new JSONObject();
        temp.put("id", txn.getId());
        temp.put("seq", txn.getCurrentSeqNum());
        temp.put("len", txn.getByteStream().size());
        temp.put("data", Arrays.toString(txn.getByteStream().toByteArray()));
        temp.put("status", txn.getStatus().toString());
        temp.put("file", txn.getFileName());
        temp.put("write", txn.getWriteCount());
        temp.put("time", txn.getTime());
        return temp;
    }

    private static Transaction makeTxnFromJson(JSONObject obj) {
        return new Transaction(obj);
    }

}
