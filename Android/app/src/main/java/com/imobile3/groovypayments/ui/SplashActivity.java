package com.imobile3.groovypayments.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.imobile3.groovypayments.R;
import com.imobile3.groovypayments.ui.login.LoginActivity;
import com.imobile3.groovypayments.ui.main.MainDashboardActivity;

import androidx.annotation.Nullable;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        ImageView logo = findViewById(R.id.brand_logo);

        // Animate the brand logo to slide up from bottom.
        Animation animationUtils = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        logo.setAnimation(animationUtils);
        animationUtils.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startLoginActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void startLoginActivity() {
        new Handler().postDelayed(this::startLoginActivityLogic, 500L);
    }

    public void startLoginActivityLogic() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.logged_in_user_id_sp_name),MODE_PRIVATE);
        long id = sharedPreferences.getLong(getString(R.string.logged_in_user_id_key),-1);
        Class nextActivityClass;

        if(id == -1){
            nextActivityClass = LoginActivity.class;
        }else{
            nextActivityClass = MainDashboardActivity.class;
        }
        SplashActivity.this.startActivity(
                new Intent(SplashActivity.this, nextActivityClass)
        );
        finish();
    }

    @Override
    protected void initViewModel() {
        // Not used
    }
}
