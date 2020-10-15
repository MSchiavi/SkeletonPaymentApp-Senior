package com.imobile3.groovypayments.utils;

public class PasswordDoesNotMatchException extends Exception {
    public PasswordDoesNotMatchException(String errorMessage){
        super(errorMessage);
    }
}