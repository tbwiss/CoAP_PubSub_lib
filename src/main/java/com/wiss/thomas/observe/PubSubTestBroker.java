/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiss.thomas.observe;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import org.eclipse.californium.core.CoapBroker;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;

/**
 *
 * @author thomas
 */
public class PubSubTestBroker extends CoapBroker {

    private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

    public PubSubTestBroker() throws SocketException {

        /*
        Resource root = getRoot();
        Resource wellKnown = root.getChild(".well-known");
        Resource aResource = wellKnown.getChild("core");   // this is the root
        //root.add(new ObservableTestResource("ps"));
        
        addTopic(new ObservableTestResource("ktre"));
        //add(new ObservableTestResource("observe"));
        // here you would need to add the "ps"-resource for the pubsub API
         */
 /*
        addTopic(new PubSubTopic("tmps", MediaTypeRegistry.APPLICATION_LINK_FORMAT, 234923094));

        PubSubTopic sd = new PubSubTopic("tmp", MediaTypeRegistry.APPLICATION_LINK_FORMAT, 234923094);
        PubSubTopic sdChild2 = new PubSubTopic("sdc", MediaTypeRegistry.APPLICATION_JSON, 234923094);
        PubSubTopic sdChild = new PubSubTopic("sd", MediaTypeRegistry.APPLICATION_LINK_FORMAT, 234923094);
        
        sdChild.addTopic(sdChild2);
        sd.addTopic(sdChild);
        addTopic(sd);
        
        PubSubTopic rd = new PubSubTopic("zert", MediaTypeRegistry.APPLICATION_LINK_FORMAT, 234923094);
        addTopic(rd);
        */
         
    }

    /**
     * Add individual endpoints listening on default CoAP port on all IPv4
     * addresses of all network interfaces.
     */
    public void addEndpoints() {
        for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
            // only binds to IPv4 addresses and localhost
            if (addr instanceof Inet4Address || addr instanceof Inet6Address || addr.isLoopbackAddress()) {
                InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
                addEndpoint(new CoapEndpoint(bindToAddress));
                System.out.println("Added endpoint at addr:: " + addr.getHostAddress());
            }
        }
    }
}
