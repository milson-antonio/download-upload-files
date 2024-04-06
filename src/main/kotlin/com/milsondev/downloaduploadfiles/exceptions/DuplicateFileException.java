package com.milsondev.downloaduploadfiles.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicateFileException extends RuntimeException{
    public DuplicateFileException(String message) {
        super(message);
    }
}