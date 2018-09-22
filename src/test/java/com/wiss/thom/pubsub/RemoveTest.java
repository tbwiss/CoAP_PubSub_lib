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
public class RemoveTest {

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
    public void testRemoveTopic() throws Exception {

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

        Request request1 = new Request(CoAP.Code.DELETE);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request1.send();
        System.out.println("client sent request delete");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.DELETED);

        // Try to read
        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request2.getOptions().setContentFormat(50);
        request2.send();
        System.out.println("client sent request delete");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.NOT_FOUND);

    }

    @Test
    public void testRemoveSubTopic() throws Exception {
        // create topic
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("temp/zert");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response.getOptions().getLocationPathString(), "/ps/temp/zert");

        Request request1 = new Request(CoAP.Code.DELETE);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp/zert");
        request1.send();
        System.out.println("client sent request delete");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.DELETED);

        // Try to read
        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request2.getOptions().setContentFormat(40);
        request2.send();
        System.out.println("client sent request ");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.NO_CONTENT);

        // Try to read
        Request request4 = new Request(CoAP.Code.GET);
        request4.setConfirmable(true);
        request4.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp/zert");
        request4.getOptions().setContentFormat(50);
        request4.send();
        System.out.println("client sent request ");

        // receive response and check
        Response response4 = request4.waitForResponse(1000);
        assertNotNull("Client received no response", response4);
        System.out.println("client received response");
        assertEquals(response4.getCode(), CoAP.ResponseCode.NOT_FOUND);

    }

    @Test
    public void testRemoveSubTopicTree() throws Exception {

        // create topic
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("temp/zert");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response.getOptions().getLocationPathString(), "/ps/temp/zert");

        Request request1 = new Request(CoAP.Code.DELETE);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request1.send();
        System.out.println("client sent request delete");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.DELETED);

        // Try to read
        Request request4 = new Request(CoAP.Code.GET);
        request4.setConfirmable(true);
        request4.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp/zert");
        request4.getOptions().setContentFormat(50);
        request4.send();
        System.out.println("client sent request ");

        // receive response and check
        Response response4 = request4.waitForResponse(1000);
        assertNotNull("Client received no response", response4);
        System.out.println("client received response");
        assertEquals(response4.getCode(), CoAP.ResponseCode.NOT_FOUND);

        // Try to read
        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp");
        request2.getOptions().setContentFormat(40);
        request2.send();
        System.out.println("client sent request ");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.NOT_FOUND);

    }

    private void createSimpleServer() {
        CoapEndpoint endpoint = new CoapEndpoint(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
        CoapBroker broker = new CoapBroker();
        broker.addEndpoint(endpoint);
        broker.start();
        serverPort = endpoint.getAddress().getPort();
    }
}
