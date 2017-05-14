/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mytwitter.keyvalue.keyvalue.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


public class KeyValue implements Serializable {

    private static final long serialVersionUID = 1L;
    private String key;
    private String value;
    private Map<String, String> callbackMap; //id, url map
    private final AtomicLong counter;

    public KeyValue() {
        this.counter = new AtomicLong();
    }

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
        this.counter = new AtomicLong();
        this.callbackMap = new HashMap<String, String>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
   
    public String addCallback(String url) {
        String id = String.valueOf(counter.incrementAndGet());
        callbackMap.put(id, url);
        return id;
    }
    
    
    public int removeCallback(String id) {
        if (callbackMap.containsKey(id)) {
            callbackMap.remove(id);
            return 0;
        }
        else {
            return 1;
        }
    }
    
    public Map getCallback() {
        return callbackMap;
    }

    
    public String getValue() {
        return value;
    }

   
    public void setValue(String value) {
        this.value = value;
    }

//    @Override
//    public boolean equals(Object object) {
//        if (object == null) {
//            return false;
//        } else if (!(object instanceof KeyValue)) {
//            return false;
//        } else {
//            KeyValue kv = (KeyValue) object;
//            if (key.equals(kv.getKey())
//                    && value.equals(kv.getValue())) {
//                return true;
//            }
//        }
//        return false;
//    }

}
