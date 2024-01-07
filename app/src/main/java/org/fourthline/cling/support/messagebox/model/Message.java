package org.fourthline.cling.support.messagebox.model;

import org.fourthline.cling.support.messagebox.parser.MessageDOM;
import org.fourthline.cling.support.messagebox.parser.MessageDOMParser;
import org.fourthline.cling.support.messagebox.parser.MessageElement;
import org.seamless.xml.DOM;
import org.seamless.xml.ParserException;

import java.util.Random;


public abstract class Message implements ElementAppender {
    private final Category category;
    private DisplayType displayType;
    private final int id;
    protected final Random randomGenerator;


    public enum Category {
        SMS("SMS"),
        INCOMING_CALL("Incoming Call"),
        SCHEDULE_REMINDER("Schedule Reminder");
        
        public String text;

        Category(String str) {
            this.text = str;
        }
    }


    public enum DisplayType {
        MINIMUM("Minimum"),
        MAXIMUM("Maximum");
        
        public String text;

        DisplayType(String str) {
            this.text = str;
        }
    }

    public Message(Category category, DisplayType displayType) {
        this(0, category, displayType);
    }

    public Message(int i, Category category, DisplayType displayType) {
        Random random = new Random();
        this.randomGenerator = random;
        this.id = i == 0 ? random.nextInt(Integer.MAX_VALUE) : i;
        this.category = category;
        this.displayType = displayType;
    }

    public int getId() {
        return this.id;
    }

    public Category getCategory() {
        return this.category;
    }

    public DisplayType getDisplayType() {
        return this.displayType;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.id == ((Message) obj).id;
    }

    public int hashCode() {
        return this.id;
    }

    public String toString() {
        try {
            MessageDOMParser messageDOMParser = new MessageDOMParser();
            MessageDOM messageDOM = (MessageDOM) messageDOMParser.createDocument();
            MessageElement createRoot = messageDOM.createRoot(messageDOMParser.createXPath(), "Message");
            createRoot.createChild("Category").setContent(getCategory().text);
            createRoot.createChild("DisplayType").setContent(getDisplayType().text);
            appendMessageElements(createRoot);
            return messageDOMParser.print((DOM) messageDOM, 0, false).replaceAll("<Message xmlns=\"urn:samsung-com:messagebox-1-0\">", "").replaceAll("</Message>", "");
        } catch (ParserException e) {
            throw new RuntimeException(e);
        }
    }
}
