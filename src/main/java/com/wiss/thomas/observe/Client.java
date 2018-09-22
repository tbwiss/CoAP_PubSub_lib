/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiss.thomas.observe;

import java.util.Set;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.config.NetworkConfig;

/**
 *
 * @author thomas
 */
public class Client {

    private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

    public Client() {
    }

    public void execute() {

        CoapClient client = new CoapClient();
        String baseURI = "coap://127.0.0.1:" + COAP_PORT + "/ps/";
        client.setURI(baseURI);
        
        
        
        /*
        Request r = new Request(Code.GET);
        Response t = new Response(ResponseCode.BAD_GATEWAY);
        r.getOptions().setObserve(1);
        */
        
        // build a checker if /ps/ is integrated in uri for all requests..

        /*
        Set<WebLink> links = client.discover();
        for (WebLink link : links) {
            System.out.println(link.getURI());
            client.setURI(baseURI + link.getURI());
        }
        */
        
        Set<WebLink> links = client.discoverTest("rt=core.ps");
        Set<WebLink> linksd = client.discover();
        
        client.get(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                
                String content = response.getResponseText();
                System.out.println("Client RESPONSE: " + content + " code: " + response.getCode());
            }

            @Override
            public void onError() {
                System.err.println("FAILED");
            }
        });
        
        
        client.delete(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                
                String content = response.getResponseText();
                System.out.println("Client RESPONSE: " + content + " code: " + response.getCode());
            }

            @Override
            public void onError() {
                System.err.println("FAILED");
            }
        });

        Set<WebLink> linksr = client.discover();
        /*
        CoapObserveRelation relation2 = client.observe(
                new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();
                System.out.println("-CO02----------");
                System.out.println(content);
            }

            @Override
            public void onError() {
                System.err.println("-Failed--------");
            }
        });

        client.get(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();
                System.out.println("Client RESPONSE: " + content + " code: " + response.getCode() );
            }

            @Override
            public void onError() {
                System.err.println("FAILED");
            }
        });

        client.put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();
                System.out.println("Client RESPONSE: " + content + " code: " + response.getCode());
            }

            @Override
            public void onError() {
                System.err.println("FAILED");
            }
        }, "dd", MediaTypeRegistry.TEXT_PLAIN);

        client.post(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                String content = response.getResponseText();
                System.out.println("Client RESPONSE: " + content + " code: " + response.getCode());
            }

            @Override
            public void onError() {
                System.err.println("FAILED");
            }
        }, "dd", MediaTypeRegistry.TEXT_PLAIN);

        client.delete(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                
                String content = response.getResponseText();
                System.out.println("Client RESPONSE: " + content + " code: " + response.getCode());
            }

            @Override
            public void onError() {
                System.err.println("FAILED");
            }
        });

         */
    }

}
