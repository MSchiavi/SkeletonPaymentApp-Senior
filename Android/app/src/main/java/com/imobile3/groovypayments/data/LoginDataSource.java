package com.imobile3.groovypayments.data;

import com.imobile3.groovypayments.data.entities.UserEntity;
import com.imobile3.groovypayments.data.model.LoggedInUser;
import com.imobile3.groovypayments.utils.PasswordDoesNotMatchException;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
           UserEntity user = DatabaseHelper.getInstance().getDatabase().getUserDao().getUserByEmail(username);
            if(user.getPassword().equals(password)){
                LoggedInUser loggedInUser = new LoggedInUser(
                        Long.toString(user.getId()),
                        user.getUsername()
                );
                return new Result.Success<>(loggedInUser);
            }
            return new Result.Error<>(new PasswordDoesNotMatchException("Incorrect Password"));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
