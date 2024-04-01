package com.milsondev.downloaduploadfiles.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.PAYLOAD_TOO_LARGE)
public class FileSizeException extends RuntimeException{
    public FileSizeException(String message) {
        super(message);
    }
}