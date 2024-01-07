package org.fourthline.cling.binding.xml;


public class DescriptorBindingException extends Exception {
    public DescriptorBindingException(String str) {
        super(str);
    }

    public DescriptorBindingException(String str, Throwable th) {
        super(str, th);
    }
}
