package com.example.chromecastone.Dlna.model;

import java.util.Observable;


public class CObservable extends Observable {
    public void notifyAllObservers() {
        setChanged();
        notifyObservers();
    }
}
