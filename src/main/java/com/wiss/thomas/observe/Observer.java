/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiss.thomas.observe;

/**
 *
 * @author thomas
 */
public abstract class Observer {

    protected TopicSubject subject;

    public abstract void update();
    public abstract void unsubscribe();

}
