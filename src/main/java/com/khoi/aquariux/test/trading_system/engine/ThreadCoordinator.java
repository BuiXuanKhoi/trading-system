package com.khoi.aquariux.test.trading_system.engine;

public class ThreadCoordinator {

    public void unlock(Object instance){
        synchronized (instance.getClass()){

        }
    }
}
