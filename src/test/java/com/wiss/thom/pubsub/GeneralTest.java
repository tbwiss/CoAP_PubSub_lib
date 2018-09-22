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
import org.eclipse.californium.core.PubSubValidator;
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
public class GeneralTest {

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
    public void testNotFoundTopic() throws Exception {

        // send request
        Request request = new Request(CoAP.Code.GET);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/helotre");
        request.getOptions().setContentFormat(50);
        request.send();
        System.out.println("client sent request");

        // receive response and check
        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.NOT_FOUND);

        // send request
        Request request1 = new Request(CoAP.Code.DELETE);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/helotre3");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.NOT_FOUND);

        // send request
        Request request2 = new Request(CoAP.Code.POST);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/prs/helotre3");
        request2.getOptions().setContentFormat(50);
        request2.send();
        System.out.println("client sent request");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.NOT_FOUND);

        // send request
        Request request3 = new Request(CoAP.Code.PUT);
        request3.setConfirmable(true);
        request3.setURI("coap://127.0.0.1:" + serverPort + "/prs/");
        request3.setPayload("dd");
        request3.getOptions().setContentFormat(50);
        request3.send();
        System.out.println("client sent request");

        // receive response and check
        Response response3 = request3.waitForResponse(1000);
        assertNotNull("Client received no response", response3);
        System.out.println("client received response");
        assertEquals(response3.getCode(), CoAP.ResponseCode.NOT_FOUND);
    }

    @Test
    public void testValidatePath() throws Exception {
        System.out.println("validate paths");
        
        // allowed
        assertEquals(PubSubValidator.validatePath("/ps/temp/tem/pr"), true);
        assertEquals(PubSubValidator.validatePath("/ps/onz/tpreerrtrt/ksdjf/njdsf/"), true);
        assertEquals(PubSubValidator.validatePath("/ps/onz/öäü/ÖÄÜ/åÅ/"), true);
        assertEquals(PubSubValidator.validatePath("/ps/onz/öäü/sdf#eäöpr/"), true);
        assertEquals(PubSubValidator.validatePath("/ps/onz/öäü/sdfeäöpr/èéà"), true);
        assertEquals(PubSubValidator.validatePath("/ps/onz/0123456789/sdfeäöpr/"), true);

        // not allowed
        assertEquals(PubSubValidator.validatePath("\tr\rrt\rtrrt"), false);
        assertEquals(PubSubValidator.validatePath("temp/t(em/pr"), false);
        assertEquals(PubSubValidator.validatePath("\\temp\\tem\\pr"), false);
        assertEquals(PubSubValidator.validatePath("temp/tem/p)r"), false);
        assertEquals(PubSubValidator.validatePath("temp/tem/pr/gt:dd/dd"), false);
        assertEquals(PubSubValidator.validatePath("/ps/onz/ö@äü/ÖÄÜ/O<HARE/"), false);
        assertEquals(PubSubValidator.validatePath("/ps/o+nz/öäü/ÖÄÜ/OHARE/"), false);
        assertEquals(PubSubValidator.validatePath("/ps/onz/öäü/ÖÄÜ/O<HARE/"), false);
        assertEquals(PubSubValidator.validatePath("/ps/onz/öäü/ÖÄÜ/O>HARE/"), false);
        assertEquals(PubSubValidator.validatePath("/ps/onz/f&r/"), false);
        assertEquals(PubSubValidator.validatePath("/ps/onz/öäü/ÖÄÜ/O>HARE/"), false);
        assertEquals(PubSubValidator.validatePath("/ps/onz/öäü/ÖÄÜ/O'HARE/"), false);
        assertEquals(PubSubValidator.validatePath("/ps/onz/öäü/ÖÄÜ/O-HARE/"), false);
        assertEquals(PubSubValidator.validatePath("temp/te?m/pr"), false);
        assertEquals(PubSubValidator.validatePath("!temp/tem/pr"), false);
        assertEquals(PubSubValidator.validatePath("\temp/tem/pr"), false);
        assertEquals(PubSubValidator.validatePath("/*temp/tem/pr"), false);
    }

    private void createSimpleServer() {
        CoapEndpoint endpoint = new CoapEndpoint(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
        CoapBroker broker = new CoapBroker();
        broker.addEndpoint(endpoint);
        broker.start();
        serverPort = endpoint.getAddress().getPort();
    }
}
