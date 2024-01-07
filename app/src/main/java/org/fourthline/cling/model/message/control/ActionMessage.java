package org.fourthline.cling.model.message.control;


public interface ActionMessage {
    String getActionNamespace();

    String getBodyString();

    boolean isBodyNonEmptyString();

    void setBody(String str);
}
