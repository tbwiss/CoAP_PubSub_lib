/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiss.thom.pubsub;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.eclipse.californium.category.Medium;
import org.eclipse.californium.core.CoapBroker;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapPubSubClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.rule.CoapNetworkRule;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 *
 * @author thomas
 */
@Category(Medium.class)
public class UseCaseTest {

    @ClassRule
    public static CoapNetworkRule network = new CoapNetworkRule(CoapNetworkRule.Mode.DIRECT, CoapNetworkRule.Mode.NATIVE);

    private int serverPort;
    private CoapPubSubClient clientOne;
    private CoapPubSubClient clientTwo;
    private CoapPubSubClient clientThree;
    private String baseURI;
    private String baseURIplain;

    @Before
    public void initLogger() {
        System.out.println(System.lineSeparator() + "Start " + getClass().getSimpleName());
        EndpointManager.clear();
        createSimpleServer();
    }

    @After
    public void after() {
        System.out.println("End " + getClass().getSimpleName());
    }

    // THESE are asynchronous tests, there are some exemples in the Cf tests!
    //
    // pub, sub, remove (should give a message that now unsub), pub -> no sub should be there
    // create, pub, sub, pub, get new values
    // pub , sub, pub, unsub, pub, no values
    // pub, sub, pub , get new value
    // pub, sub1, sub2, pub, get new values -> make more clients, only one sub per topic per client
    // pub, sub1, sub2, pub, get new values, unsub1, pub, get new values
    // pub, sub1, sub2, sub3, pub, get new values, unsub1, pub, unsub2, pub
    //
    @Test
    public void testUseCaseSimplePubSubPub() throws Exception {
        clientOne.setURI(baseURI + "kirt/r");

        CoapResponse response1 = clientOne.publish("text23", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/kirt/r");

        clientOne.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("sub received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);

        CoapResponse response2 = clientOne.publish("text33", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);
    }

    @Test
    public void testUseCasePubSubPubUnsubPub() throws Exception {

        clientOne.setURI(baseURI + "ki/ztu");

        CoapResponse response1 = clientOne.publish("text8983", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/ki/ztu");

        clientOne.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("sub received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);

        CoapResponse response2 = clientOne.publish("text35553", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);

        clientOne.unsubscribe();

        CoapResponse response3 = clientOne.publish("text3555663", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response3.getCode(), CoAP.ResponseCode.CHANGED);
    }

    @Test
    public void testUseCasePubSubRemove() throws Exception {
        System.out.println("REMOVE topic while subs present");

        clientOne.setURI(baseURI + "ki/ptiz");

        CoapResponse response1 = clientOne.publish("text999580", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/ki/ptiz");

        clientOne.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                //
                // INFO: if CONTENT response text is empty it should be a unsubscribe message...
                //
                System.out.println("sub received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);

            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);

        CoapResponse response2 = clientOne.remove();
        assertEquals(response2.getCode(), CoAP.ResponseCode.DELETED);

        CoapResponse response3 = clientOne.publish("text49859680", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response3.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response3.getOptions().getLocationPathString(), "/ps/ki/ptiz");

    }

    @Test
    public void testUseCaseCreatePubSubPub() throws Exception {

        CoapResponse response1 = clientOne.create("ki/päöüu", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/ki/päöüu");

        clientOne.setURI(baseURIplain + response1.getOptions().getLocationPathString());

        CoapResponse response3 = clientOne.publish("text5843", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response3.getCode(), CoAP.ResponseCode.CHANGED);

        clientOne.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("sub received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);

        CoapResponse response2 = clientOne.publish("text5843555", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);
    }
    
    @Test
    public void testUseSubUnsubSub() throws Exception {
        clientOne.setURI(baseURI + "podfi/fsdk");

        CoapResponse response1 = clientOne.publish("tez8793", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/podfi/fsdk");
        
        clientOne.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("subC1 received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);

        CoapResponse response2 = clientOne.publish("text8zu3", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);
        
        clientOne.unsubscribe();
        
        CoapResponse response3 = clientOne.publish("This should been printed", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response3.getCode(), CoAP.ResponseCode.CHANGED);
        
        clientOne.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("subC1_new received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);
        
        CoapResponse response4 = clientOne.publish("prt7435", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response4.getCode(), CoAP.ResponseCode.CHANGED);
    }

    @Test
    public void testUseCaseTwoSub() throws Exception {
        clientOne.setURI(baseURI + "kirt/two");

        CoapResponse response1 = clientOne.publish("text73", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/kirt/two");

        clientTwo.setURI(baseURI + "kirt/two");

        clientOne.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("subC1 received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);

        clientTwo.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("subC2 received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);

        CoapResponse response2 = clientTwo.publish("text83", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);
    }

    @Test
    public void testUseCaseTwoSubThenOneUnsub() throws Exception {
        clientTwo.setURI(baseURI + "kirt/twoFrt4");

        CoapResponse response1 = clientTwo.publish("text233", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/kirt/twoFrt4");

        clientOne.setURI(baseURI + "kirt/twoFrt4");

        clientOne.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("subC1 received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);

        clientTwo.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("subC2 received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);

        CoapResponse response2 = clientTwo.publish("text343", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);

        clientOne.unsubscribe();

        CoapResponse response3 = clientTwo.publish("text563", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response3.getCode(), CoAP.ResponseCode.CHANGED);
    }

    @Test
    public void testUseCaseThreeSubThenTwoUnsub() throws Exception {
        clientTwo.setURI(baseURI + "gret/fmnsd94/dsf45");

        CoapResponse response1 = clientTwo.publish("kler3498", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/gret/fmnsd94/dsf45");

        clientThree.setURI(baseURI + "gret/fmnsd94/dsf45");
        clientOne.setURI(baseURI + "gret/fmnsd94/dsf45");

        clientOne.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("subC1 received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);

        clientTwo.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("subC2 received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);
        
         clientThree.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("subC3 received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);

        CoapResponse response2 = clientThree.publish("text9093745", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);

        clientTwo.unsubscribe();

        CoapResponse response3 = clientOne.publish("text56333", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response3.getCode(), CoAP.ResponseCode.CHANGED);
        
        clientOne.unsubscribe();

        CoapResponse response5 = clientThree.publish("fert_454", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response5.getCode(), CoAP.ResponseCode.CHANGED);
        
        clientTwo.subscribe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                System.out.println("subC2_new received response: " + response.getResponseText());
                assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
            }

            @Override
            public void onError() {
                System.err.println("Error");
            }
        }, MediaTypeRegistry.TEXT_PLAIN);
        
        CoapResponse response6 = clientThree.publish("END_484", MediaTypeRegistry.TEXT_PLAIN);
        assertEquals(response6.getCode(), CoAP.ResponseCode.CHANGED);

    }

    private void createSimpleServer() {
        CoapEndpoint endpoint = new CoapEndpoint(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
        CoapBroker broker = new CoapBroker();
        broker.addEndpoint(endpoint);
        broker.start();
        serverPort = endpoint.getAddress().getPort();

        baseURIplain = "coap://localhost:" + serverPort;
        baseURI = "coap://localhost:" + serverPort + "/ps/";

        clientOne = new CoapPubSubClient();
        clientOne.setURI(baseURI);

        clientTwo = new CoapPubSubClient();
        clientTwo.setURI(baseURI);

        clientThree = new CoapPubSubClient();
        clientThree.setURI(baseURI);
    }

}
