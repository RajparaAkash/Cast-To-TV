package org.fourthline.cling.model;


public interface Command<T> {
    void execute(ServiceManager<T> serviceManager) throws Exception;
}
