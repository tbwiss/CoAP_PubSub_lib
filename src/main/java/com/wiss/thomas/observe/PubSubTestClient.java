/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiss.thomas.observe;

import java.util.Set;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapPubSubClient;
import org.eclipse.californium.core.CoapSubscribeHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.network.config.NetworkConfig;

/**
 *
 * @author thomas
 */
public class PubSubTestClient {

    private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);
    private String baseURI;

    public void execute() {

        CoapPubSubClient client = new CoapPubSubClient();
        baseURI = "coap://127.0.0.1:" + COAP_PORT + "/ps/";
        client.setURI(baseURI);

        client.create("temps", 50);
        client.setURI(baseURI + "temps");

        client.create("tempsS", 50);
        client.setURI(baseURI + "temps/tempsS");

        client.create("tempsSS", 50);
        client.setURI(baseURI + "temps/tempsS/tempsSS");

        client.setURI(baseURI + "tprt/rt");
        client.publish("3254", 50);
        System.out.println(client.read(50).getResponseText());
        Set<WebLink> links = client.discover();

        /*
        client.subscribe(new CoapHandler(){
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("Sub1: " + response.getResponseText());
            }

            @Override
            public void onError() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        
        }, 50);*/
        client.subscribe(new CoapSubscribeHandler() {

            @Override
            public void onBadRequest(CoapResponse response) {
                System.out.println("Sub2 Bad request: " + response.getResponseText());
            }

            @Override
            public void onContent(CoapResponse response) {
                System.out.println("Sub2 Content: " + response.getResponseText());
            }

        }, 50);

        System.out.println(client.read(50).getResponseText());

        client.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("Sub2: " + response.getResponseText());
            }

            @Override
            public void onError() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        }, 50);

        client.publish("3254564", 50);

        System.out.println(client.read(50).getResponseText());

        client.publish("3254564fff", 50);

        System.out.println(client.read(50).getResponseText());

        System.out.println("Unsubscribe");
        client.unsubscribe();

        System.out.println(client.read(50).getResponseText());
        //System.out.println(client.remove().getCode());
        System.out.println(client.publish("325456DFER4", 50).getCode().toString());

        client.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                if (response.getOptions().hasObserve()) {
                    System.out.println("Sub2_new observe number: " + response.getOptions().getObserve());
                }
                System.out.println("Sub2_new: " + response.getResponseText() + ", code: " + response.getCode().toString());
            }

            @Override
            public void onError() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        }, 50);

        System.out.println(client.publish("dsjr", 50).getResponseText());

        System.out.println(client.read(50).getResponseText());

        System.out.println(client.remove().getCode());

        System.out.println(client.read(50).getCode());
        //client.unsubscribe();
        //System.out.println(client.publish("325456DFER4fdtret", 50).getCode());

        /*
        CoapClient client = new CoapClient();
        String baseURI = "coap://127.0.0.1:" + COAP_PORT + "/ps/";
        client.setURI(baseURI);
        
        Set<WebLink> links = client.discover();
        
       
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
         */
    }

    public void executeTwo() {

        CoapPubSubClient client = new CoapPubSubClient();
        baseURI = "coap://127.0.0.1:" + COAP_PORT + "/ps/";
        client.setURI(baseURI);

        client.create("karto", 50);

        baseURI = baseURI + "karto";

        for (int i = 0; i < 100; i++) {
            new ClientSubThread(i).start();
        }

        for (int i = 0; i < 100; i++) {
            new ClientThread().start();
        }

    }

    private class ClientThread extends Thread {

        ClientThread() {

        }

        @Override
        public void run() {
            CoapPubSubClient client = new CoapPubSubClient();
            client.setURI(baseURI);
            try {
                int i = 0;
                while (i < 10) {
                    long before = System.nanoTime();
                    CoapResponse response = client.publish(Thread.currentThread().getName() + "  nbr" + i, 50);
                    System.out.println(response.getCode());
                    long after = System.nanoTime();
                    //System.out.println((after - before));
                    if (response.getCode().toString().equals("4.29")) {
                        if (response.getOptions().hasMaxAge()) {
                            Thread.sleep((response.getOptions().getMaxAge() * 1000));
                        } else {
                            Thread.sleep(10000);
                        }
                    } else {
                        i++;
                        Thread.sleep(100);
                    }

                }
            } catch (InterruptedException e) {
                System.err.println(e);
            }

        }
    }

    private class ClientSubThread extends Thread {

        int nbr;

        ClientSubThread(int nbr) {
            this.nbr = nbr;
        }

        @Override
        public void run() {
            CoapPubSubClient client = new CoapPubSubClient();
            client.setURI(baseURI);
            try {
                client.subscribe(new CoapHandler() {
                    @Override
                    public void onLoad(CoapResponse response) {
                        System.out.println("Sub " + nbr + ": " + response.getResponseText() + ", code: " + response.getCode().toString());
                    }

                    @Override
                    public void onError() {
                        System.err.println("Sub " + nbr + ": ERROR");
                    }

                }, 50);

            } catch (Exception e) {
                System.err.println(e);
            }

        }
    }

}
