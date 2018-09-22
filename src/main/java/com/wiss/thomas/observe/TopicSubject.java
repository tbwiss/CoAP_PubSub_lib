/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wiss.thomas.observe;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thomas
 */
public class TopicSubject {

    private List<Observer> observers = new ArrayList<Observer>();
    private Object stateValue = null;  // byte[] or string
    private long maxAgeOfValue = 0;
    private long lastUpdatedTimestamp;
    private int contentFormat;

    public TopicSubject() {
        this.stateValue = null;
        updateSubjectTTL();
    }

    public TopicSubject(Object state, int contentFormat) {
        this.stateValue = state;
        this.contentFormat = contentFormat;
        updateSubjectTTL();
    }

    public Object getStateValue() {
        return stateValue;
    }

    public synchronized void setState(Object state) {
        this.stateValue = state;
        notifyAllObservers();
        updateSubjectTTL();
    }

    public void updateState(Object state) {
        setState(state);
    }

    public synchronized void clearState() {
        stateValue = null;
        updateSubjectTTL();
    }

    public int getStateContentFormat() {
        return contentFormat;
    }

    public void setStateContentFormat(int contentFormat) {
        this.contentFormat = contentFormat;
    }

    public long getMaxAgeOfValue() {
        return maxAgeOfValue;
    }
    
    public long getMaxAgeOfValueInSeconds() {
        return (maxAgeOfValue/1000);
    }

    /**
     * Set maxAge in seconds
     * @param maxAgeOfValue time in seconds
     */
    public void setMaxAgeOfValue(long maxAgeOfValue) {
        if (maxAgeOfValue >= 0) {
            // max_Age in message is in seconds, here handled in milliseconds
            this.maxAgeOfValue = (maxAgeOfValue*1000);
        }
    }
    
    public synchronized void updateSubjectTTL(){
        lastUpdatedTimestamp = System.currentTimeMillis();
    }

    /**
     * A value of 0 means indefinite TTL for the subject
     *
     * @return time to live in milliseconds, zero means indefinite lifetime.
     */
    public synchronized long getSubjectTTL() {
        if (maxAgeOfValue == 0) {
            return 0;
        }
        return (lastUpdatedTimestamp + maxAgeOfValue) - System.currentTimeMillis();
    }

    public synchronized boolean isSubjectMaxAgeExceeded() {
        if (maxAgeOfValue == 0) {
            return false;
        }
        return (getSubjectTTL() < 0);
    }
    
    
    
    

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public synchronized void removeObserver(Observer observer) {
        observer.unsubscribe();
        observers.remove(observer);
    }
    
    public synchronized boolean containsObserver(Observer observer){
        return observers.contains(observer);
    }

    public synchronized boolean isObserved() {
        return !observers.isEmpty();
    }

    public synchronized void notifyAllObservers() {
        for (Observer obs : observers) {
            obs.update();
        }
    }

    public synchronized void removeAllObservers() {
        for (Observer obs : observers) {
            removeObserver(obs);
        }
    }
}
