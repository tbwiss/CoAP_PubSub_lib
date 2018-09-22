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
public class PublishTest {

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
    public void testPublishToTopic() throws Exception {

        // Create a new topic
        Request request = new Request(CoAP.Code.POST);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.setPayload("temp9");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response.getOptions().getLocationPathString(), "/ps/temp9");

        // Publish to it
        Request request2 = new Request(CoAP.Code.PUT);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/temp9");
        request2.setPayload("435");
        request2.getOptions().setContentFormat(50);
        request2.send();
        System.out.println("client sent request /ps/temp9");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.CHANGED);
    }

    @Test
    public void testCreateTopicOnPublish() throws Exception {

        Request request1 = new Request(CoAP.Code.PUT);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempus6");
        request1.setPayload("659878");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request /ps/tempus6");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempus6");
    }

    @Test
    public void testCreateSubTopicOnPublish() throws Exception {

        Request request1 = new Request(CoAP.Code.PUT);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempus3");
        request1.setPayload("6578");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request /ps/tempus3");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempus3");

        Thread.sleep(200);
        Request request4 = new Request(CoAP.Code.PUT);
        request4.setConfirmable(true);
        request4.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempus3/tempi");
        request4.setPayload("657768");
        request4.getOptions().setContentFormat(50);
        request4.send();
        System.out.println("client sent request /ps/tempus3/tempi");

        // receive response and check
        Response response4 = request4.waitForResponse(1000);
        assertNotNull("Client received no response", response4);
        System.out.println("client received response");
        assertEquals(response4.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response4.getOptions().getLocationPathString(), "/ps/tempus3/tempi");

        Thread.sleep(200);
        Request request5 = new Request(CoAP.Code.PUT);
        request5.setConfirmable(true);
        request5.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempus3/tempi/tump");
        request5.setPayload("65776855");
        request5.getOptions().setContentFormat(50);
        request5.send();
        System.out.println("client sent request /ps/tempus3/tempi/tump");

        // receive response and check
        Response response5 = request5.waitForResponse(1000);
        assertNotNull("Client received no response", response5);
        System.out.println("client received response");
        assertEquals(response5.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response5.getOptions().getLocationPathString(), "/ps/tempus3/tempi/tump");
    }

    @Test
    public void testCreateSubTopicOnPublishAndAddContent() throws Exception {

        Request request1 = new Request(CoAP.Code.PUT);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempus8");
        request1.setPayload("65788790");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request /ps/tempus8");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempus8");

        Request request4 = new Request(CoAP.Code.PUT);
        request4.setConfirmable(true);
        request4.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempus8/tempi");
        request4.setPayload("657768");
        request4.getOptions().setContentFormat(50);
        request4.send();
        System.out.println("client sent request /ps/tempus8/tempi");

        // receive response and check
        Response response4 = request4.waitForResponse(1000);
        assertNotNull("Client received no response", response4);
        System.out.println("client received response");
        assertEquals(response4.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response4.getOptions().getLocationPathString(), "/ps/tempus8/tempi");

        Request request6 = new Request(CoAP.Code.PUT);
        request6.setConfirmable(true);
        request6.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempus8/tempi");
        request6.setPayload("43544");
        request6.getOptions().setContentFormat(50);
        request6.send();
        System.out.println("client sent request /ps/tempus8/tempi for update");

        // receive response and check
        Response response6 = request6.waitForResponse(1000);
        assertNotNull("Client received no response", response6);
        System.out.println("client received response");
        assertEquals(response6.getCode(), CoAP.ResponseCode.CHANGED);
    }

    @Test
    public void testInvalidPathForCreateTopic() throws Exception {
        
        Request request1 = new Request(CoAP.Code.PUT);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempus/df+ur/33");
        request1.setPayload("65788790");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request /ps/tempus/df+ur/33");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.BAD_REQUEST);

        Request request4 = new Request(CoAP.Code.PUT);
        request4.setConfirmable(true);
        request4.setURI("coap://127.0.0.1:" + serverPort + "/ps/kldf/fe*re/");
        request4.setPayload("657768");
        request4.getOptions().setContentFormat(50);
        request4.send();
        System.out.println("client sent request /ps/kldf/fe*re/");

        // receive response and check
        Response response4 = request4.waitForResponse(1000);
        assertNotNull("Client received no response", response4);
        System.out.println("client received response");
        assertEquals(response4.getCode(), CoAP.ResponseCode.BAD_REQUEST);

    }

    private void createSimpleServer() {
        CoapEndpoint endpoint = new CoapEndpoint(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
        CoapBroker broker = new CoapBroker();
        broker.addEndpoint(endpoint);
        broker.start();
        serverPort = endpoint.getAddress().getPort();
    }
}
