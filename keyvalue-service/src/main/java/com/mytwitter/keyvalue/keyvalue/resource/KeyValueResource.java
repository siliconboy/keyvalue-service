package com.mytwitter.keyvalue.keyvalue.resource;

import com.codahale.metrics.annotation.Timed;
import com.mytwitter.keyvalue.keyvalue.model.KeyValue;
import com.mytwitter.keyvalue.keyvalue.model.KeyValueDAO;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.validator.UrlValidator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

@Path("/")
public class KeyValueResource {

    //     Response.Status.CREATED   201
    //     Response.Status.FORBIDDEN   403
    //     Response.Status.NO_CONTENT   204
    //     Response.Status.OK   200
    //     Response.Status.NOT_FOUND   404
    //     Response.Status.BAD_REQUEST   400
    KeyValueDAO keyValueDao = new KeyValueDAO();
    ClientConfig config = new ClientConfig();
    // timeout in 500 ms
    Client client = ClientBuilder.newClient().property(ClientProperties.CONNECT_TIMEOUT, 500).property(ClientProperties.READ_TIMEOUT, 500);

    public KeyValueResource() {
    }

    //get key's value    
    @GET
    @Path("/{key : .+}")
    @Timed
    public Response getKeyValue(@PathParam("key") String key) {
        KeyValue kv = keyValueDao.getKeyValue(key);
        if (kv != null) {
            return Response.ok(kv.getValue()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    //get key's callbacks    
    @GET
    @Path("/{key : .+}/callback")
    @Timed
    public Response getKeyCallback(@PathParam("key") String key) {

        KeyValue kv = keyValueDao.getKeyValue(key);
        if (kv == null || kv.getCallback().isEmpty()) {
            return Response.ok("").build();
        } else {
            StringBuilder sBuilder = new StringBuilder();
            Map<String, String> callMap = (HashMap<String, String>) kv.getCallback();
            for (Map.Entry<String, String> entry : callMap.entrySet()) {
                String id = entry.getKey();
                String url = entry.getValue();
                sBuilder.append("\"").append(id).append("\"").append(":").append("\"").append(url).append("\"").append(",");
            }
            sBuilder.deleteCharAt(sBuilder.length() - 1);
            return Response.ok(sBuilder.toString()).build();
        }

    }

    //set key's callbacks  
    @POST
    @Path("/{key : .+}/callback")
    @Timed
    public Response setKeyCallback(@PathParam("key") String key, String values) {
        System.out.println("input key: " + key + ":form: " + values);
        String[] inputs = values.split("=");
        String value = null;
        if (inputs[0].equals("url")) {
            value = inputs[1];
            UrlValidator urlValidator = new UrlValidator();
            if (!urlValidator.isValid(value)) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        KeyValue kv = keyValueDao.getKeyValue(key);
        if (kv == null) {
            kv = new KeyValue(key, null);
        }
        String id = kv.addCallback(value);

        keyValueDao.updateKeyValue(kv);
        return Response.ok(id).status(Response.Status.CREATED).build();
    }

    //set key's value
    @POST
    @Path("/{key : .+}")
    @Timed
    public Response setKeyValue(@PathParam("key") String key, String values) {

        String[] inputs = values.split("=");
        String value = null;
        if ("value".equalsIgnoreCase(inputs[0].trim())) {
            value = inputs[1];
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        KeyValue xKeyValue = keyValueDao.getKeyValue(key);
        if (xKeyValue != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("key=" + key + " ").append("current=" + xKeyValue.getValue() + " ").append("requested=" + value);
            // this loop should be able to use async call to improve performance
            for (Object url : xKeyValue.getCallback().values()) {
                //send request to callbacks
                WebTarget target = client.target((String) url);
                Response postResponse;
                try {
                    postResponse = target.request()
                            .post(Entity.entity(sb.toString(), MediaType.TEXT_PLAIN_TYPE));
                } catch (ProcessingException pe) {
                    pe.printStackTrace();
                    continue;  //ignore timeout
                }

                if (postResponse.getStatus() != 200) {
                    Response.status(Response.Status.BAD_REQUEST).build();
                } else {
                    if ("false".equalsIgnoreCase(postResponse.readEntity(String.class))) {
                        Response.status(Response.Status.BAD_REQUEST).build();
                    }
                }
            }
        }
        KeyValue kv = new KeyValue(key, value);
        keyValueDao.addKeyValue(kv);

        return Response.status(Response.Status.CREATED).build();
    }

    // delete callback
    @DELETE
    @Path("/{key : .+}/callback/{id}")
    @Timed
    public Response deleteCallback(@PathParam("key") String key, @PathParam("id") String id) {
        KeyValue kv = keyValueDao.getKeyValue(key);
        if (kv.removeCallback(id) != 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        keyValueDao.updateKeyValue(kv);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
