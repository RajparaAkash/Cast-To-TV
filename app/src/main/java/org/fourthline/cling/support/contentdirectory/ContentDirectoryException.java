package org.fourthline.cling.support.contentdirectory;

import com.example.chromecastone.CastServer.CastServerService;

import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;


public class ContentDirectoryException extends ActionException {

    public ContentDirectoryException(int i, String str) {
        super(i, str);
    }

    public ContentDirectoryException(int i, String str, Throwable th) {
        super(i, str, th);
    }

    public ContentDirectoryException(ErrorCode errorCode, String str) {
        super(errorCode, str);
    }

    public ContentDirectoryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ContentDirectoryException(ContentDirectoryErrorCode contentDirectoryErrorCode, String str) {
        super(ErrorCode.valueOf(contentDirectoryErrorCode.getDescription() + ". " + str + CastServerService.ROOT_DIR));
        int code = contentDirectoryErrorCode.getCode();
    }

    public ContentDirectoryException(ContentDirectoryErrorCode contentDirectoryErrorCode) {
        super(contentDirectoryErrorCode.getCode(), contentDirectoryErrorCode.getDescription());
    }
}
