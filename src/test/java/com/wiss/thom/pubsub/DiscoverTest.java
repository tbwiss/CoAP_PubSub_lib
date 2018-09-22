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
public class DiscoverTest {

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
    public void testDiscoverEmptyQueryPS() throws Exception {

        // send request
        Request request = new Request(CoAP.Code.GET);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.getOptions().setUriQuery("");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
        System.out.println("&&&&&&&  " + response.getPayloadString());
        assertEquals(response.getPayloadString(), "</ps>;ct=\"40 40\";obs;rt=\"ps core.ps core.ps.discover\",</.well-known/core>");
    }

    @Test
    public void testDiscoverQueryRT() throws Exception {

        // send request
        Request request = new Request(CoAP.Code.GET);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.getOptions().setUriQuery("rt=core.ps");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
        System.out.println("&&&&&&&  " + response.getPayloadString());
        assertEquals(response.getPayloadString(), "</ps>;ct=\"40 40\";obs;rt=\"ps core.ps core.ps.discover\"");
        
        
         // send request
        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request2.getOptions().setUriQuery("rt=core.ps.discover");
        request2.getOptions().setContentFormat(50);
        request2.send();
        System.out.println("client sent request");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.CONTENT);
        System.out.println("&&&&&&&  " + response2.getPayloadString());
        assertEquals(response2.getPayloadString(), "</ps>;ct=\"40 40\";obs;rt=\"ps core.ps core.ps.discover\"");
    }
    
    
    @Test
    public void testDiscoverQueryNewTopicRT() throws Exception {
        
        
        // create topic
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("pdft");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response.getOptions().getLocationPathString(), "/ps/pdft");
        
        
        // send request
        Request request1 = new Request(CoAP.Code.GET);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.getOptions().setUriQuery("rt=pdft");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CONTENT);
        System.out.println("&&&&&&&  " + response1.getPayloadString());
        assertEquals(response1.getPayloadString(), "</ps/pdft>;ct=50;obs;rt=\"pdft\"");
    }
    
    @Test
    public void testDiscoverQueryNewTopicCT() throws Exception {
        
        
        // create topic
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("pdfti");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response.getOptions().getLocationPathString(), "/ps/pdfti");
        
        
        // send request
        Request request1 = new Request(CoAP.Code.GET);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.getOptions().setUriQuery("ct=50");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CONTENT);
        System.out.println("&&&&&&&  " + response1.getPayloadString());
        assertEquals(response1.getPayloadString(), "</ps/pdfti>;ct=50;obs;rt=\"pdfti\"");
        
        
        // send request
        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request2.getOptions().setUriQuery("ct=40");
        request2.getOptions().setContentFormat(50);
        request2.send();
        System.out.println("client sent request");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.CONTENT);
        System.out.println("&&&&&&&  " + response2.getPayloadString());
        assertEquals(response2.getPayloadString(), "</ps>;ct=\"40 40\";obs;rt=\"ps core.ps core.ps.discover\"");
        
    
    }
    
    
    @Test
    public void testDiscoverEmptyQueryWKC() throws Exception {

        // send request
        Request request = new Request(CoAP.Code.GET);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/.well-known/core");
        request.getOptions().setUriQuery("");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
        System.out.println("&&&&&&&  " + response.getPayloadString());
        assertEquals(response.getPayloadString(), "</ps>;ct=\"40 40\";obs;rt=\"ps core.ps core.ps.discover\",</.well-known/core>");
    }
    
    
    @Test
    public void testDiscoverNotExisting() throws Exception {

        // send request
        Request request = new Request(CoAP.Code.GET);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/prs");
        request.getOptions().setUriQuery("");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.NOT_FOUND);
    }
    
    
    @Test
    public void testDiscoverMalformedQuery() throws Exception {

        // send request
        Request request = new Request(CoAP.Code.GET);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.getOptions().setUriQuery("cd=50");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
        System.out.println("&&&&&&&  " + response.getPayloadString());
        assertEquals(response.getPayloadString(), "");
        
         // send request
        Request request1 = new Request(CoAP.Code.GET);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.getOptions().setUriQuery("rz=core.ps");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CONTENT);
        System.out.println("&&&&&&&  " + response1.getPayloadString());
        assertEquals(response1.getPayloadString(), "");
    }

    private void createSimpleServer() {
        CoapEndpoint endpoint = new CoapEndpoint(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
        CoapBroker broker = new CoapBroker();
        broker.addEndpoint(endpoint);
        broker.start();
        serverPort = endpoint.getAddress().getPort();
    }
}
