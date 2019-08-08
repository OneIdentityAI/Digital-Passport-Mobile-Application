package my.com.clarify.oneidentity.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag,
                Context.MODE_PRIVATE);
        final boolean status = preferences.getBoolean(getResources().getString(R.string.param_account_status), false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(!status)
                    intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                else
                    intent = new Intent(SplashActivity.this, BuildYourProfileActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
