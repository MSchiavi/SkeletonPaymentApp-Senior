package com.imobile3.groovypayments.ui.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import com.imobile3.groovypayments.R;
import com.imobile3.groovypayments.ui.BaseActivity;

public class UserProfileActivity extends BaseActivity {

    private UserProfileViewModel userProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);

        setUpMainNavBar();
        setUpViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void setUpMainNavBar() {
        super.setUpMainNavBar();
        mMainNavBar.showBackButton();
        mMainNavBar.showLogo();
    }

    @Override
    protected void initViewModel() {
        userProfileViewModel = ViewModelProviders.of(this,new UserProfileViewModelFactory())
                .get(UserProfileViewModel.class);
    }

    private void setUpViews() {
        SharedPreferences loggedInId = this.getSharedPreferences(getString(R.string.LoggedInUserId_SP_Name), Context.MODE_PRIVATE);
        long id = loggedInId.getLong(getString(R.string.LoggedInUserId_Key),-1);
        if(id != -1){
            userProfileViewModel.setUser(id);
        }
        TextView lblUsername = findViewById(R.id.label_username);
        TextView lblEmail = findViewById(R.id.label_email);
        TextView lblHoursWeek = findViewById(R.id.label_hours_week);

        TextView username = findViewById(R.id.username);
        TextView email = findViewById(R.id.email);
        TextView hoursWeek = findViewById(R.id.hours_week);
        userProfileViewModel.getLoggedInUser().observe(this,loggedInUser -> {
            username.setText(loggedInUser.getDisplayName());
            email.setText(loggedInUser.getEmail());
            hoursWeek.setText(Double.toString(loggedInUser.getHours()));

            lblUsername.setVisibility(View.VISIBLE);
            lblEmail.setVisibility(View.VISIBLE);
            lblHoursWeek.setVisibility(View.VISIBLE);
        });
    }
}
