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
import org.eclipse.californium.core.PubSubTopic;
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
public class GarbageCollectionTest {

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
    public void testGCofSingleTopic() throws Exception {

        Request request1 = new Request(CoAP.Code.POST);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.setPayload("tempusVH");
        request1.getOptions().setContentFormat(40);
        request1.getOptions().setMaxAge(1);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempusVH");

        Thread.sleep(2000);

        // Trigger the GC with discovery!
        Request request = new Request(CoAP.Code.GET);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.send();
        System.out.println("client sent request");

        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
        
        Thread.sleep(100); // make sure discvoery is done

        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusVH/");
        request2.getOptions().setContentFormat(40);
        request2.send();
        System.out.println("client sent request");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.NOT_FOUND);
    }
    
    @Test
    public void testGCofSingleTopicBeforeDelete() throws Exception {

        Request request1 = new Request(CoAP.Code.POST);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.setPayload("tempusWö");
        request1.getOptions().setContentFormat(40);
        request1.getOptions().setMaxAge(1);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempusWö");

        Thread.sleep(2000);

        // Trigger the GC with delete! 
        //
        //  Topic will be deleted with GC first and then the request is processed 
        //  which leads to a topic NOT_FOUND failure!
        //
        Request request = new Request(CoAP.Code.DELETE);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusWö");
        request.send();
        System.out.println("client sent request");

        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.NOT_FOUND);
    }
    
    @Test
    public void testGCofSingleTopicNotExceeded() throws Exception {

        Request request1 = new Request(CoAP.Code.POST);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.setPayload("tempusXCV");
        request1.getOptions().setContentFormat(40);
        request1.getOptions().setMaxAge(100);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempusXCV");

        Thread.sleep(2000);

        // Trigger the GC with discovery!
        Request request = new Request(CoAP.Code.GET);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.send();
        System.out.println("client sent request");

        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);

        Thread.sleep(100); // make sure discvoery is done
        
        
        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusXCV");
        request2.getOptions().setContentFormat(40);
        request2.send();
        System.out.println("client sent request");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.NO_CONTENT);
    }

    @Test
    public void testSingleTreeTopicRemove() throws Exception {
        
        Request request1 = new Request(CoAP.Code.POST);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.setPayload("tempusPO");
        request1.getOptions().setContentFormat(40);
        request1.getOptions().setMaxAge(0);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempusPO");

        
        Request request5 = new Request(CoAP.Code.POST);
        request5.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusPO");
        request5.setPayload("tempusIU");
        request5.getOptions().setContentFormat(40);
        request5.getOptions().setMaxAge(2);
        request5.send();
        System.out.println("client sent request");

        // receive response and check
        Response response5 = request5.waitForResponse(1000);
        assertNotNull("Client received no response", response5);
        System.out.println("client received response");
        assertEquals(response5.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response5.getOptions().getLocationPathString(), "/ps/tempusPO/tempusIU");
        
        Request request9 = new Request(CoAP.Code.POST);
        request9.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusPO/tempusIU/");
        request9.setPayload("tempusXY");
        request9.getOptions().setContentFormat(40);
        request9.getOptions().setMaxAge(0);
        request9.send();
        System.out.println("client sent request");

        // receive response and check
        Response response9 = request9.waitForResponse(1000);
        assertNotNull("Client received no response", response9);
        System.out.println("client received response");
        assertEquals(response9.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response9.getOptions().getLocationPathString(), "/ps/tempusPO/tempusIU/tempusXY");
        
        Thread.sleep(2000);

        // Trigger the GC with discovery!
        Request request = new Request(CoAP.Code.GET);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.send();
        System.out.println("client sent request");

        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
        
        Thread.sleep(100); // make sure discvoery is done
        
        // CHECK the GC
        
        Request request21 = new Request(CoAP.Code.GET);
        request21.setConfirmable(true);
        request21.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusPO/tempusIU/tempusXY");
        request21.getOptions().setContentFormat(40);
        request21.send();
        System.out.println("client sent request");
        
        // receive response and check
        Response response21 = request21.waitForResponse(1000);
        assertNotNull("Client received no response", response21);
        System.out.println("client received response");
        assertEquals(response21.getCode(), CoAP.ResponseCode.NOT_FOUND);
        
        
        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusPO/tempusIU");
        request2.getOptions().setContentFormat(40);
        request2.send();
        System.out.println("client sent request");
        
        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.NOT_FOUND);
        
        Request request7 = new Request(CoAP.Code.GET);
        request7.setConfirmable(true);
        request7.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusPO/");
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
    public void testTwoSeparateTopic() throws Exception {
        Request request1 = new Request(CoAP.Code.POST);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.setPayload("tempusWQ");
        request1.getOptions().setContentFormat(40);
        request1.getOptions().setMaxAge(0);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempusWQ");

        Request request5 = new Request(CoAP.Code.POST);
        request5.setConfirmable(true);
        request5.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request5.setPayload("tempusWP");
        request5.getOptions().setContentFormat(40);
        request5.getOptions().setMaxAge(2);
        request5.send();
        System.out.println("client sent request");

        // receive response and check
        Response response5 = request5.waitForResponse(1000);
        assertNotNull("Client received no response", response5);
        System.out.println("client received response");
        assertEquals(response5.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response5.getOptions().getLocationPathString(), "/ps/tempusWP");

        Thread.sleep(2000);

        // Trigger the GC with discovery!
        Request request = new Request(CoAP.Code.GET);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.send();
        System.out.println("client sent request");

        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
        
        Thread.sleep(100); // make sure discvoery is done
        

        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusWP/");
        request2.getOptions().setContentFormat(40);
        request2.send();
        System.out.println("client sent request");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.NOT_FOUND);
        
        Request request7 = new Request(CoAP.Code.GET);
        request7.setConfirmable(true);
        request7.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusWQ/");
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
    public void testTwoSeparateTopicOnCreate() throws Exception {
        Request request5 = new Request(CoAP.Code.POST);
        request5.setConfirmable(true);
        request5.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request5.setPayload("tempusNö");
        request5.getOptions().setContentFormat(40);
        request5.getOptions().setMaxAge(1);
        request5.send();
        System.out.println("client sent request");

        // receive response and check
        Response response5 = request5.waitForResponse(1000);
        assertNotNull("Client received no response", response5);
        System.out.println("client received response");
        assertEquals(response5.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response5.getOptions().getLocationPathString(), "/ps/tempusNö");

        Thread.sleep(2000);
        
        // Trigger the GC with CREATE!
        Request request1 = new Request(CoAP.Code.POST);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.setPayload("tempusNå");
        request1.getOptions().setContentFormat(40);
        request1.getOptions().setMaxAge(20);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempusNå");
        

        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusNö/");
        request2.getOptions().setContentFormat(40);
        request2.send();
        System.out.println("client sent request");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.NOT_FOUND);
        
        Request request7 = new Request(CoAP.Code.GET);
        request7.setConfirmable(true);
        request7.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusNå/");
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
    public void testThreeSeparateTopic() throws Exception {
        Request request1 = new Request(CoAP.Code.POST);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request1.setPayload("tempusKM");
        request1.getOptions().setContentFormat(40);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempusKM");

        Request request5 = new Request(CoAP.Code.POST);
        request5.setConfirmable(true);
        request5.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request5.setPayload("tempusKü");
        request5.getOptions().setContentFormat(40);
        request5.getOptions().setMaxAge(2);
        request5.send();
        System.out.println("client sent request");

        // receive response and check
        Response response5 = request5.waitForResponse(1000);
        assertNotNull("Client received no response", response5);
        System.out.println("client received response");
        assertEquals(response5.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response5.getOptions().getLocationPathString(), "/ps/tempusKü");
        
        Request request51 = new Request(CoAP.Code.POST);
        request51.setConfirmable(true);
        request51.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request51.setPayload("tempusKö");
        request51.getOptions().setContentFormat(40);
        request51.getOptions().setMaxAge(20);
        request51.send();
        System.out.println("client sent request");

        // receive response and check
        Response response51 = request51.waitForResponse(1000);
        assertNotNull("Client received no response", response51);
        System.out.println("client received response");
        assertEquals(response51.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response51.getOptions().getLocationPathString(), "/ps/tempusKö");

        Thread.sleep(2000);

        // Trigger the GC with discovery!
        Request request = new Request(CoAP.Code.GET);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/");
        request.send();
        System.out.println("client sent request");

        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.CONTENT);
        
        Thread.sleep(100); // make sure discvoery is done
        

        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusKM/");
        request2.getOptions().setContentFormat(40);
        request2.send();
        System.out.println("client sent request");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.NO_CONTENT);
        
        Request request7 = new Request(CoAP.Code.GET);
        request7.setConfirmable(true);
        request7.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusKü/");
        request7.getOptions().setContentFormat(40);
        request7.send();
        System.out.println("client sent request");

        // receive response and check
        Response response7 = request7.waitForResponse(1000);
        assertNotNull("Client received no response", response7);
        System.out.println("client received response");
        assertEquals(response7.getCode(), CoAP.ResponseCode.NOT_FOUND);
        
        Request request71 = new Request(CoAP.Code.GET);
        request71.setConfirmable(true);
        request71.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusKö/");
        request71.getOptions().setContentFormat(40);
        request71.send();
        System.out.println("client sent request");

        // receive response and check
        Response response71 = request71.waitForResponse(1000);
        assertNotNull("Client received no response", response71);
        System.out.println("client received response");
        assertEquals(response71.getCode(), CoAP.ResponseCode.NO_CONTENT);
    }
    
    @Test
    public void testThreeSeparateTopicPublishOnDelete() throws Exception {
        Request request1 = new Request(CoAP.Code.PUT);
        request1.setConfirmable(true);
        request1.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusLä");
        request1.setPayload("456546P");
        request1.getOptions().setContentFormat(50);
        request1.send();
        System.out.println("client sent request");

        // receive response and check
        Response response1 = request1.waitForResponse(1000);
        assertNotNull("Client received no response", response1);
        System.out.println("client received response");
        assertEquals(response1.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response1.getOptions().getLocationPathString(), "/ps/tempusLä");

        Request request5 = new Request(CoAP.Code.PUT);
        request5.setConfirmable(true);
        request5.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusLö");
        request5.setPayload("fgjrt");
        request5.getOptions().setContentFormat(50);
        request5.getOptions().setMaxAge(2);
        request5.send();
        System.out.println("client sent request");

        // receive response and check
        Response response5 = request5.waitForResponse(1000);
        assertNotNull("Client received no response", response5);
        System.out.println("client received response");
        assertEquals(response5.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response5.getOptions().getLocationPathString(), "/ps/tempusLö");
        
        Request request51 = new Request(CoAP.Code.PUT);
        request51.setConfirmable(true);
        request51.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusLå");
        request51.setPayload("gfd567");
        request51.getOptions().setContentFormat(50);
        request51.getOptions().setMaxAge(15);
        request51.send();
        System.out.println("client sent request");

        // receive response and check
        Response response51 = request51.waitForResponse(1000);
        assertNotNull("Client received no response", response51);
        System.out.println("client received response");
        assertEquals(response51.getCode(), CoAP.ResponseCode.CREATED);
        assertEquals(response51.getOptions().getLocationPathString(), "/ps/tempusLå");

        Thread.sleep(2000);

        // Trigger the GC with delete!
        Request request = new Request(CoAP.Code.DELETE);
        request.setConfirmable(true);
        request.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusLä");
        request.send();
        System.out.println("client sent request");

        Response response = request.waitForResponse(1000);
        assertNotNull("Client received no response", response);
        System.out.println("client received response");
        assertEquals(response.getCode(), CoAP.ResponseCode.DELETED);
        
        Thread.sleep(100); // make sure delete is done
        

        Request request2 = new Request(CoAP.Code.GET);
        request2.setConfirmable(true);
        request2.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusLä/");
        request2.getOptions().setContentFormat(50);
        request2.send();
        System.out.println("client sent request");

        // receive response and check
        Response response2 = request2.waitForResponse(1000);
        assertNotNull("Client received no response", response2);
        System.out.println("client received response");
        assertEquals(response2.getCode(), CoAP.ResponseCode.NOT_FOUND);
        
        Request request7 = new Request(CoAP.Code.GET);
        request7.setConfirmable(true);
        request7.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusLö/");
        request7.getOptions().setContentFormat(50);
        request7.send();
        System.out.println("client sent request");

        // receive response and check
        Response response7 = request7.waitForResponse(1000);
        assertNotNull("Client received no response", response7);
        System.out.println("client received response");
        assertEquals(response7.getCode(), CoAP.ResponseCode.NOT_FOUND);
        
        Request request71 = new Request(CoAP.Code.GET);
        request71.setConfirmable(true);
        request71.setURI("coap://127.0.0.1:" + serverPort + "/ps/tempusLå/");
        request71.getOptions().setContentFormat(50);
        request71.send();
        System.out.println("client sent request");

        // receive response and check
        Response response71 = request71.waitForResponse(1000);
        assertNotNull("Client received no response", response71);
        System.out.println("client received response");
        assertEquals(response71.getCode(), CoAP.ResponseCode.CONTENT);
        assertEquals(response71.getPayloadString(), "gfd567");
    }

    private void createSimpleServer() {
        CoapEndpoint endpoint = new CoapEndpoint(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
        CoapBroker broker = new CoapBroker();
        broker.addEndpoint(endpoint);
        broker.addTopic(new PubSubTopic("tempis", 40, 0));
        broker.addTopic(new PubSubTopic("tempsis", 40, 0));
        broker.start();

        serverPort = endpoint.getAddress().getPort();
    }
}
