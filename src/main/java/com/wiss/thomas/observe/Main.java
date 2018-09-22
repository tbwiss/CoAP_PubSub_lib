/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiss.thomas.observe;

import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.eclipse.californium.core.CaliforniumLogger;

/**
 *
 * @author thomas
 */
public class Main {

    public static void main(String[] args) {
        CaliforniumLogger.initialize();
        CaliforniumLogger.setLevel(Level.SEVERE);
        
        try {
            PubSubTestBroker broker = new PubSubTestBroker();
            broker.addEndpoints();
            broker.setExecutor(Executors.newScheduledThreadPool(8));
            broker.start();
            PubSubTestClient client = new PubSubTestClient();
            client.execute();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
