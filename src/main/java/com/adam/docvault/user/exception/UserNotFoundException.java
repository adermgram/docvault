package com.adam.docvault.user.exception;

public class UserNotFoundException  extends RuntimeException{
    public UserNotFoundException(){
        super("User not found");
    }
}
