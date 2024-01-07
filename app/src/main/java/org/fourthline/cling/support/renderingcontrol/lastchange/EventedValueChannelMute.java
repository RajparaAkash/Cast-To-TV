package org.fourthline.cling.support.renderingcontrol.lastchange;

import org.fourthline.cling.model.types.BooleanDatatype;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.support.lastchange.EventedValue;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.shared.AbstractMap;

import java.util.Map;


public class EventedValueChannelMute extends EventedValue<ChannelMute> {
    @Override
    protected Datatype getDatatype() {
        return null;
    }

    @Override
    protected ChannelMute valueOf(Map.Entry[] entryArr) throws InvalidValueException {
        return valueOf2((Map.Entry<String, String>[]) entryArr);
    }

    public EventedValueChannelMute(ChannelMute channelMute) {
        super(channelMute);
    }

    public EventedValueChannelMute(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    protected ChannelMute valueOf2(Map.Entry<String, String>[] entryArr) throws InvalidValueException {
        Channel channel = null;
        Boolean bool = null;
        for (Map.Entry<String, String> entry : entryArr) {
            if (entry.getKey().equals("channel")) {
                channel = Channel.valueOf(entry.getValue());
            }
            if (entry.getKey().equals("val")) {
                bool = new BooleanDatatype().valueOf(entry.getValue());
            }
        }
        if (channel == null || bool == null) {
            return null;
        }
        return new ChannelMute(channel, bool);
    }

    @Override
    public Map.Entry<String, String>[] getAttributes() {
        return new Map.Entry[]{new AbstractMap.SimpleEntry("val", new BooleanDatatype().getString(getValue().getMute())), new AbstractMap.SimpleEntry("channel", getValue().getChannel().name())};
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}
