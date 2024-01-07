package org.fourthline.cling.binding.xml;

import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;
import org.seamless.util.Exceptions;
import org.seamless.xml.ParserException;
import org.seamless.xml.XmlPullParserUtils;
import org.xml.sax.SAXParseException;

import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RecoveringUDA10DeviceDescriptorBinderImpl extends UDA10DeviceDescriptorBinderImpl {
    private static Logger log = Logger.getLogger(RecoveringUDA10DeviceDescriptorBinderImpl.class.getName());

    public <D extends Device> D describe(D d, String str) throws DescriptorBindingException, ValidationException {
        String fixGarbageTrailingChars;
        int i = 0;
        String str2;
        DescriptorBindingException e;
        String fixXMLEntities;
        if (str != null) {
            str = str.trim();
        }
        return (D) super.describe( d, str);
    }

    private String fixGarbageLeadingChars(String str) {
        int indexOf = str.indexOf("<?xml");
        return indexOf == -1 ? str : str.substring(indexOf);
    }

    protected String fixGarbageTrailingChars(String str, DescriptorBindingException descriptorBindingException) {
        int indexOf = str.indexOf("</root>");
        if (indexOf == -1) {
            log.warning("No closing </root> element in descriptor");
            return null;
        } else if (str.length() != indexOf + 7) {
            log.warning("Detected garbage characters after <root> node, removing");
            return str.substring(0, indexOf) + "</root>";
        } else {
            return null;
        }
    }

    protected String fixMissingNamespaces(String str, DescriptorBindingException descriptorBindingException) {
        String message;
        Throwable cause = descriptorBindingException.getCause();
        if (((cause instanceof SAXParseException) || (cause instanceof ParserException)) && (message = cause.getMessage()) != null) {
            Matcher matcher = Pattern.compile("The prefix \"(.*)\" for element").matcher(message);
            if (!matcher.find() || matcher.groupCount() != 1) {
                matcher = Pattern.compile("undefined prefix: ([^ ]*)").matcher(message);
                if (matcher.find()) {
                }
                return null;
            }
            String group = matcher.group(1);
            Logger logger = log;
            logger.warning("Fixing missing namespace declaration for: " + group);
            Matcher matcher2 = Pattern.compile("<root([^>]*)").matcher(str);
            if (!matcher2.find() || matcher2.groupCount() != 1) {
                log.fine("Could not find <root> element attributes");
                return null;
            }
            String group2 = matcher2.group(1);
            Logger logger2 = log;
            logger2.fine("Preserving existing <root> element attributes/namespace declarations: " + matcher2.group(0));
            Matcher matcher3 = Pattern.compile("<root[^>]*>(.*)</root>", 32).matcher(str);
            if (!matcher3.find() || matcher3.groupCount() != 1) {
                log.fine("Could not extract body of <root> element");
                return null;
            }
            String group3 = matcher3.group(1);
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root " + String.format(Locale.ROOT, "xmlns:%s=\"urn:schemas-dlna-org:device-1-0\"", group) + group2 + ">" + group3 + "</root>";
        }
        return null;
    }

    protected void handleInvalidDescriptor(String str, DescriptorBindingException descriptorBindingException) throws DescriptorBindingException {
        throw descriptorBindingException;
    }

    protected <D extends Device> D handleInvalidDevice(String str, D d, ValidationException validationException) throws ValidationException {
        throw validationException;
    }
}
