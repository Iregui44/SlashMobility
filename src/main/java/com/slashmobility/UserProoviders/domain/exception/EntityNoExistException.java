package com.slashmobility.UserProoviders.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class EntityNoExistException extends RuntimeException {

    public EntityNoExistException(String message) {
        super(message);
    }

}
