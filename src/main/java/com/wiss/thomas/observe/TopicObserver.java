/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiss.thomas.observe;

import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 *
 * @author thomas
 */
public class TopicObserver extends Observer {

    private CoapExchange exchange;
    

    public TopicObserver(TopicSubject subject, CoapExchange exchange) {
        this.subject = subject;
        this.subject.addObserver(this);
        this.exchange = exchange;
    }

    @Override
    public void update() {
        // exchange respond with content observe
        // include the maxAgeOfValue in Max-Age
        // include the observe option as well
        // sequence number which increases section 4.4 in observe rfc
    }

    @Override
    public void unsubscribe() {
        // exchange respond to unsubscirbe
        // include the observe option as well
    }

}
