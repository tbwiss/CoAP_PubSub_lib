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
public class CreateTest {

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
    public void testCreateTopic() throws Exception {

        // send request
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

        Request request1 = new Request(CoAP.Code.POST);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request1.setPayload("temptz");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/temp/temptz");
    }

    @Test
    public void testCreateTopicApplicationLinkFormat() throws Exception {

        // send request
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("tempus");
        request.getOptions().setContentFormat(40);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response.getOptions().getLocationPathString(), "/ps/tempus");

        Thread.sleep(200);
        Request request1 = new Request(CoAP.Code.POST);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempus/");
        request1.setPayload("degree");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempus/degree");
    }

    @Test
    public void testCreateTopicUnsupportedCT() throws Exception {
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("tempus");
        request.getOptions().setContentFormat(120);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.NOT_ACCEPTABLE);

        //
        //  ************ IMPORTANT INFORMATION ************
        //
        //  -1 is allowed! implementation specification of Cf!!
        //  Apparently all numbers below -1 (f.e. -3 0r -20) are set to -1 so they are actually allowed!
        //  Correct that in the client side application
        Request request1 = new Request(CoAP.Code.POST);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.setPayload("tempusR");
        request1.getOptions().setContentFormat(300);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.NOT_ACCEPTABLE);

    }
    
    @Test
    public void testCreateLongTree() throws Exception {
        
        Request request1 = new Request(CoAP.Code.POST);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.setPayload("tempusCV");
        request1.getOptions().setContentFormat(40);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempusCV");

        
        Request request3 = new Request(CoAP.Code.GET);
        request3.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusCV/");
        request3.getOptions().setContentFormat(40);
        request3.send();
        System.out.println("client sent request");

        // receive response and check
        Response response3 = request3.waitForResponse(1000);
        assertNotNull("Client received no response", response3);
        System.out.println("client received response");
        assertEquals(response3.getCode(), CoAP.ResponseCode.NO_CONTENT);
        
        
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusCV");
        request.setPayload("tempusAS");
        request.getOptions().setContentFormat(40);
        request.send();
        System.out.println("client sent request");

        // SHOULD be forbidden!!
        Response response = request.waitForResponse(1500);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response.getOptions().getLocationPathString(), "/ps/tempusCV/tempusAS");

        
        Request request6 = new Request(CoAP.Code.GET);
        request6.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusCV/tempusAS/");
        request6.getOptions().setContentFormat(40);
        request6.send();
        System.out.println("client sent request");

        // receive response and check
        Response response6 = request6.waitForResponse(1000);
        assertNotNull("Client received no response", response6);
        System.out.println("client received response");
        assertEquals(response6.getCode(), CoAP.ResponseCode.NO_CONTENT);
        
        
        // another one
        Request request2 = new Request(CoAP.Code.POST);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusCV/tempusAS/");
        request2.setPayload("tempusKL");
        request2.getOptions().setContentFormat(40);
        request2.send();
        System.out.println("client sent request");

        // receive response and check
        Response response2 = request2.waitForResponse(2500);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response2.getOptions().getLocationPathString(), "/ps/tempusCV/tempusAS/tempusKL");

        
        Request request7 = new Request(CoAP.Code.GET);
        request7.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusCV/tempusAS/tempusKL/");
        request7.getOptions().setContentFormat(40);
        request7.send();
        System.out.println("client sent request");

        // receive response and check
        Response response7 = request7.waitForResponse(1000);
        assertNotNull("Client received no response", response7);
        System.out.println("client received response");
        assertEquals(response7.getCode(), CoAP.ResponseCode.NO_CONTENT);
    
    }

    @Test
    public void testCreateTopicExisting() throws Exception {

        Request request1 = new Request(CoAP.Code.POST);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.setPayload("tempusRT");
        request1.getOptions().setContentFormat(40);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);

        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("tempusRT");
        request.getOptions().setContentFormat(40);
        request.send();
        System.out.println("client sent request");

        // SHOULD be forbidden!!
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.FORBIDDEN);

        // another one
        Request request2 = new Request(CoAP.Code.POST);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusRT/");
        request2.setPayload("tempusZT");
        request2.getOptions().setContentFormat(40);
        request2.send();
        System.out.println("client sent request");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.CREATED);

        //SHOULD BE forbidden
        Request request3 = new Request(CoAP.Code.POST);
        request3.setConfirmable(true);
        request3.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusRT/");
        request3.setPayload("tempusZT");
        request3.getOptions().setContentFormat(40);
        request3.send();
        System.out.println("client sent request");

        // receive response and check
        Response response3 = request3.waitForResponse(1000);
        assertNotNull("Client received no response", response3);
        System.out.println("client received response");
        assertEquals(response3.getCode(), CoAP.ResponseCode.FORBIDDEN);

    }

    @Test
    public void testCreateTopicInvalidPath() throws Exception {
        Request request1 = new Request(CoAP.Code.POST);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.setPayload("tempusRT/äöü/?ut/");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request");

 
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.BAD_REQUEST);

        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("tempus/moe%/skd/");
        request.getOptions().setContentFormat(40);
        request.send();
        System.out.println("client sent request");

 
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.BAD_REQUEST);

        
    }

    private void createSimpleServer() {
        CoapEndpoint endpoint = new CoapEndpoint(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
        CoapBroker broker = new CoapBroker();
        broker.addEndpoint(endpoint);
        broker.start();
        serverPort = endpoint.getAddress().getPort();
    }
}
