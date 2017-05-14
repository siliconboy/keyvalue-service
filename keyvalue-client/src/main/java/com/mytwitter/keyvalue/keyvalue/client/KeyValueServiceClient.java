package com.mytwitter.keyvalue.keyvalue.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.ClientBuilder;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class KeyValueServiceClient {

    private static String keyValueService
            = "http://localhost:8082/";
    private static String keyValueResource
            = "mykey";

    public static String test() {

        Client client = ClientBuilder.newClient();

        WebTarget target = client.target(keyValueService);
        Response postResponse = target.path(keyValueResource).request(MediaType.TEXT_PLAIN_TYPE)
                .get();
        
        return String.valueOf(postResponse.getStatus());
    }

    public static void main(String args[]) {
        String response = test();
        System.out.println(response);
    }
}
