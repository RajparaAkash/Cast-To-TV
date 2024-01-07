package org.fourthline.cling.binding;


public interface AllowedValueRangeProvider {
    long getMaximum();

    long getMinimum();

    long getStep();
}
