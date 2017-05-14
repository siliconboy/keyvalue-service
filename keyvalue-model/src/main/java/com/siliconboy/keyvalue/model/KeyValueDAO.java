package com.siliconboy.keyvalue.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyValueDAO {

    Map<String, KeyValue> kvMap = null;

    public KeyValueDAO() {

        kvMap = new HashMap<String, KeyValue>();
    }

    public Map<String, KeyValue> getAllKeyValues() {
        File file = new File("KeyValues.dat");
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                kvMap = (Map<String, KeyValue>) ois.readObject();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(KeyValueDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return kvMap;
    }

    public KeyValue getKeyValue(String key) {
        Map<String, KeyValue> kvMap = getAllKeyValues();
        if (kvMap.containsKey(key)) {
            return kvMap.get(key);
        }
        return null;
    }

    public void addKeyValue(KeyValue keyValue) {
        Map<String, KeyValue> kvMap = getAllKeyValues();
        if (kvMap.containsKey(keyValue.getKey())) {
            KeyValue kv = kvMap.get(keyValue.getKey());
            kv.setValue(keyValue.getValue());
            saveKeyValueMap(kvMap);

        } else {
            kvMap.put(keyValue.getKey(), keyValue);
            saveKeyValueMap(kvMap);

        }
    }

    public void updateKeyValue(KeyValue keyValue) {
        Map<String, KeyValue> kvMap = getAllKeyValues();
        kvMap.put(keyValue.getKey(), keyValue);

        saveKeyValueMap(kvMap);
    }

    public int deleteKeyValue(String key, String cID) {
        Map<String, KeyValue> kvMap = getAllKeyValues();
        if (kvMap.containsKey(key)) {
            KeyValue kv = kvMap.get(key);
            kv.removeCallback(cID);
            return 1;
        }
        return 0;
    }

    private void saveKeyValueMap(Map<String, KeyValue> kvMap) {
        try {
            File file = new File("KeyValues.dat");
            FileOutputStream fos;

            fos = new FileOutputStream(file);

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(kvMap);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
