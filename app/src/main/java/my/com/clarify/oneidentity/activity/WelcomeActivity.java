package my.com.clarify.oneidentity.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.javiersantos.appupdater.AppUpdater;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;


public class WelcomeActivity extends AppCompatActivity {
    public AppDelegate delegate;
    public LinearLayout layout1;
    public LinearLayout layout2;
    //public TextView textRestoreYourIdentity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        delegate = (AppDelegate)getApplication();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        }

        layout1 = findViewById(R.id.layout_1);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, KYCSelfieActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
        layout2 = findViewById(R.id.layout_2);
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, MODE_PRIVATE);
                if(preferences.getString(getString(R.string.param_oneid_credential), "").equals("")) {
                    Intent intent = new Intent(WelcomeActivity.this, DISelfieActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else {
                    Intent intent = new Intent(WelcomeActivity.this, ConnectionActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.start();
    }
}
