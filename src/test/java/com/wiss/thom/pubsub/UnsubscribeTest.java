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
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.rule.CoapNetworkRule;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 *
 * @author thomas
 */
@Category(Medium.class)
public class UnsubscribeTest {

    @ClassRule
    public static CoapNetworkRule network = new CoapNetworkRule(CoapNetworkRule.Mode.DIRECT, CoapNetworkRule.Mode.NATIVE);

    private int serverPort;

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

    @Test
    public void testUnsubscribeToEmptyTopic() throws Exception {

        // Create a topic
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("temp");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response.getOptions().getLocationPathString(), "/ps/temp");
        
        
        // Subscribe to it
        Request request1 = new Request(CoAP.Code.GET);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request1.setObserve();
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request subscribe to empty topic");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CONTENT);
        
        
        Thread.sleep(200);
        
        // Unsubscribe to it
        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request2.setObserveCancel();
        request2.getOptions().setContentFormat(50);
        request2.send();
        System.out.println("client sent request Unsubscribe to empty topic");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.CONTENT);
    }
    
    @Test
    public void testUnsubscribeToLoadedTopic() throws Exception {

        // Create a topic
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("temp1");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response.getOptions().getLocationPathString(), "/ps/temp1");
        
        
        // Publish some payload
        Request request2 = new Request(CoAP.Code.PUT);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp1");
        request2.setPayload("76556");
        request2.getOptions().setContentFormat(50);
        request2.send();
        System.out.println("client sent request publish content");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);
        
        
       
        // Subscribe to it
        Request request1 = new Request(CoAP.Code.GET);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp1");
        request1.setObserve();
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CONTENT);
        System.out.println(response2.getOptions().getObserve() + "  " + response1.getPayloadString());
        //assertTrue(response2.getOptions().getObserve() > 1);
        assertEquals(response1.getPayloadString(), "76556");
        
        
        Request request3 = new Request(CoAP.Code.GET);
        request3.setConfirmable(true);
        request3.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp1");
        request3.setObserveCancel();
        request3.getOptions().setContentFormat(50);
        request3.send();
        System.out.println("client sent request");

        // receive response and check
        Response response3 = request3.waitForResponse(1000);
        assertNotNull("Client received no response", response3);
        System.out.println("client received response");
        assertEquals(response3.getCode(), CoAP.ResponseCode.CONTENT);
        System.out.println(response2.getOptions().getObserve() + "  " + response3.getPayloadString());
        assertEquals(response1.getPayloadString(), "76556");
    }
    

    

    private void createSimpleServer() {
        CoapEndpoint endpoint = new CoapEndpoint(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
        CoapBroker broker = new CoapBroker();
        broker.addEndpoint(endpoint);
        broker.start();
        serverPort = endpoint.getAddress().getPort();
    }
}
