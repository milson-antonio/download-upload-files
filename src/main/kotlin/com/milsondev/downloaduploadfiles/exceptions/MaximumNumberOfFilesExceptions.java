package com.milsondev.downloaduploadfiles.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
public class MaximumNumberOfFilesExceptions extends RuntimeException{
    public MaximumNumberOfFilesExceptions(String message) {
        super(message);
    }
}