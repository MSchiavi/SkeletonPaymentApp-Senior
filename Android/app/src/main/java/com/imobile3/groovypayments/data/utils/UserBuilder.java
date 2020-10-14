package com.imobile3.groovypayments.data.utils;

import androidx.annotation.NonNull;

import com.imobile3.groovypayments.data.entities.UserEntity;

public class UserBuilder {

    private UserBuilder() {

    }

    @NonNull
    public static UserEntity build(
            long id,
            String firstName,
            String lastName,
            String userName,
            String email,
            String password
    ) {
        UserEntity result = new UserEntity();
        result.setId(id);
        result.setFirstName(firstName);
        result.setLastName(lastName);
        result.setUsername(userName);
        result.setEmail(email);
        result.setPassword(password);
        return result;
    }
}
