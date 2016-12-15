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

    public static void doExit(Map<Integer, Transaction> transactions, ArrayList<String> cmds, String directory) {
        logTransactions(transactions, directory);
        logCommands(cmds, directory);
    }

    private static void logTransactions(Map<Integer, Transaction> transactions, String directory) {
        // Write to hidden view
        if (filterCompleted(transactions).size() > 0) {
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

    }

    public static void logCommands(ArrayList<String> commands, String directory) {
        try {
            PrintWriter log = new PrintWriter(directory + Constants.CMD_FILE, "UTF-8");
            log.println(commands.toString());
            log.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static Map<Integer, Transaction> recover(String file) {
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

            // checks for incomplete txn
            ArrayList<Transaction> collect1 = filterCompleted(out);

            if (collect1.size() > 1) {
                return out;
            } else {
                return new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArrayList<Transaction> filterCompleted(Map<Integer, Transaction> out) {
        return out.values()
                .stream()
                .filter(transaction -> transaction.getStatus() != Constants.TXN_STATE.COMPLETE)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static Map<Integer, Transaction> bootCheck(String directory) {
        try {
            File cmds = new File(directory + Constants.CMD_FILE);
            File txns = new File(directory + Constants.TXN_FILE);
            if (!cmds.exists()) {
                if (!cmds.createNewFile()) {
                    throw new FileNotFoundException("Could not make .cmds file");
                }
            }
            if (!txns.exists()) {
                if (!txns.createNewFile()) {
                    throw new FileNotFoundException("Could not make .txns file");
                }
            }
            return recover(txns.getCanonicalPath());
//            for(Transaction t : tmp.values()){
//                System.out.println(t.getId() + " " + t.getStatus());
//            }
//            BufferedReader reader = new BufferedReader(new FileReader(cmds));
//            String temp;
//            while ((temp = reader.readLine()) != null) {
//                System.out.println(temp);
//            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new HashMap<>();
    }

    public static void cleanExit(String directory) {
        File cmds = new File(directory + Constants.CMD_FILE);
        File txns = new File(directory + Constants.TXN_FILE);
        if (cmds.exists()) {
            cmds.deleteOnExit();
        }
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
