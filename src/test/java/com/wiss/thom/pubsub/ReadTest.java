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
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 *
 * @author thomas
 */
@Category(Medium.class)
public class ReadTest {

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
    public void testReadTopic() throws Exception {

        // create topic
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
        
        // Publish some payload
        Request request2 = new Request(CoAP.Code.PUT);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request2.setPayload("9897");
        request2.getOptions().setContentFormat(50);
        request2.send();
        System.out.println("client sent request publish content");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);
        
        
        // read the topic content
        Request request1 = new Request(CoAP.Code.GET);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CONTENT);
        assertEquals(response1.getPayloadString(), "9897");
        
    }
    
    
    @Test
    public void testReadTopicAfterChange() throws Exception {

        // create topic
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
        
        // Publish some payload
        Request request2 = new Request(CoAP.Code.PUT);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request2.setPayload("9897");
        request2.getOptions().setContentFormat(50);
        request2.send();
        System.out.println("client sent request publish content");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);
        
        
        // read the topic content
        Request request1 = new Request(CoAP.Code.GET);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CONTENT);
        assertEquals(response1.getPayloadString(), "9897");
        
        
        // Publish some payload
        Request request4 = new Request(CoAP.Code.PUT);
        request4.setConfirmable(true);
        request4.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request4.setPayload("9897ertztrztrz");
        request4.getOptions().setContentFormat(50);
        request4.send();
        System.out.println("client sent request publish content");

        // receive response and check
        Response response4 = request4.waitForResponse(1000);
        assertNotNull("Client received no response", response4);
        System.out.println("client received response");
        assertEquals(response4.getCode(), CoAP.ResponseCode.CHANGED);
        
        
        // read the topic content
        Request request5 = new Request(CoAP.Code.GET);
        request5.setConfirmable(true);
        request5.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request5.getOptions().setContentFormat(50);
        request5.send();
        System.out.println("client sent request");

        // receive response and check
        Response response5 = request5.waitForResponse(1000);
        assertNotNull("Client received no response", response5);
        System.out.println("client received response");
        assertEquals(response5.getCode(), CoAP.ResponseCode.CONTENT);
        assertEquals(response5.getPayloadString(), "9897ertztrztrz");
        
    }
    
    
    @Test
    public void testReadTopicOutdatedMaxAge() throws Exception {
    
        // create topic
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("pert");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response.getOptions().getLocationPathString(), "/ps/pert");
        
        
        // Publish some payload with small maxAge 
        Request request2 = new Request(CoAP.Code.PUT);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/pert");
        request2.setPayload("9897");
        request2.getOptions().setMaxAge(2);
        request2.getOptions().setContentFormat(50);
        request2.send();
        System.out.println("client sent request publish content");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);
        
        Thread.sleep(2500);
        
        // read the topic content
        Request request1 = new Request(CoAP.Code.GET);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/pert");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.NO_CONTENT);
        
        
        // NOW set the max age infite...
        //
        //
        
        // Publish some payload with small maxAge 
        Request request3 = new Request(CoAP.Code.PUT);
        request3.setConfirmable(true);
        request3.setURI("coap://127.0.0.1:" + serverPort + "/ps/pert");
        request3.setPayload("989743ert3");
        request3.getOptions().setMaxAge(0);
        request3.getOptions().setContentFormat(50);
        request3.send();
        System.out.println("client sent request publish content");

        // receive response and check
        Response response3 = request3.waitForResponse(1000);
        assertNotNull("Client received no response", response3);
        System.out.println("client received response");
        assertEquals(response3.getCode(), CoAP.ResponseCode.CHANGED);
        
        
        // read the topic content
        Request request4 = new Request(CoAP.Code.GET);
        request4.setConfirmable(true);
        request4.setURI("coap://127.0.0.1:" + serverPort + "/ps/pert");
        request4.getOptions().setContentFormat(50);
        request4.send();
        System.out.println("client sent request");

        // receive response and check
        Response response4 = request4.waitForResponse(1000);
        assertNotNull("Client received no response", response4);
        System.out.println("client received response");
        assertEquals(response4.getCode(), CoAP.ResponseCode.CONTENT);
        assertEquals(response4.getPayloadString(), "989743ert3");
        
    }
    
    @Test
    public void testReadTopicUnsupportedCT() throws Exception {
        
        // create topic
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("lkzt");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response.getOptions().getLocationPathString(), "/ps/lkzt");
        
        
        // Publish some payload with small maxAge 
        Request request2 = new Request(CoAP.Code.PUT);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/lkzt");
        request2.setPayload("9897987");
        request2.getOptions().setContentFormat(50);
        request2.send();
        System.out.println("client sent request publish content");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);
        
        
        
        Request request3 = new Request(CoAP.Code.GET);
        request3.setConfirmable(true);
        request3.setURI("coap://127.0.0.1:" + serverPort + "/ps/lkzt");
        request3.getOptions().setContentFormat(80);
        request3.send();
        System.out.println("client sent request");

        // receive response and check
        Response response3 = request3.waitForResponse(1000);
        assertNotNull("Client received no response", response3);
        System.out.println("client received response");
        assertEquals(response3.getCode(), CoAP.ResponseCode.UNSUPPORTED_CONTENT_FORMAT);
        
        //
        //  ************ IMPORTANT INFORMATION ************
        //
        //  -1 is allowed! implementation specification of Cf!!
        //  Apparently all numbers below -1 (f.e. -3 0r -20) are set to -1 so they are actually allowed!
        //  Correct that in the client side application
        
        
        Request request1 = new Request(CoAP.Code.GET);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/lkzt");
        request1.getOptions().setContentFormat(300);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.UNSUPPORTED_CONTENT_FORMAT);
        
    }
    
    
    


    private void createSimpleServer() {
        CoapEndpoint endpoint = new CoapEndpoint(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
        CoapBroker broker = new CoapBroker();
        broker.addEndpoint(endpoint);
        broker.start();
        serverPort = endpoint.getAddress().getPort();
    }
}
