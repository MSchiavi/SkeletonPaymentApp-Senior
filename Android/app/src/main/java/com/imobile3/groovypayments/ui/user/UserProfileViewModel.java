package com.imobile3.groovypayments.ui.user;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.imobile3.groovypayments.concurrent.GroovyExecutors;
import com.imobile3.groovypayments.data.DatabaseHelper;
import com.imobile3.groovypayments.data.LoginRepository;
import com.imobile3.groovypayments.data.Result;
import com.imobile3.groovypayments.data.entities.UserEntity;
import com.imobile3.groovypayments.data.model.LoggedInUser;

/**
 * The ViewModel serves as an async bridge between the View (Activity, Fragment)
 * and our backing data repository (Database).
 */
public class UserProfileViewModel extends ViewModel {

    private LoginRepository mRepository;
    UserProfileViewModel(LoginRepository repository){mRepository = repository;}

    private MutableLiveData<LoggedInUser> loggedInUser = new MutableLiveData<>();

    public LiveData<LoggedInUser> getLoggedInUser(){
        return loggedInUser;
    }

    public void setUser(long id){
        GroovyExecutors.getInstance().getDiskIo().execute(() -> {
            UserEntity user = DatabaseHelper.getInstance().getDatabase().getUserDao().getUserById(id);
            Result<LoggedInUser> result = mRepository.login(user.getEmail(),user.getPassword());
            if(result instanceof Result.Success){
                loggedInUser.postValue(((Result.Success<LoggedInUser>) result).getData());
            }
        });
    }

}
