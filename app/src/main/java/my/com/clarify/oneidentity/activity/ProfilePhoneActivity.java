package my.com.clarify.oneidentity.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.View;
import android.widget.EditText;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;

public class ProfilePhoneActivity extends AppCompatActivity {
    public AppDelegate delegate;
    public AppCompatImageView imageBack;
    public EditText inputMobile;
    public EditText inputTelephone;
    public AppCompatImageView imageSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_phone);
        delegate = (AppDelegate)getApplication();

        imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        inputMobile = findViewById(R.id.input_mobile);
        inputTelephone = findViewById(R.id.input_telephone);

        final SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        final String mobile = preferences.getString(getResources().getString(R.string.param_mobile), "");
        final String telephone = preferences.getString(getResources().getString(R.string.param_telephone), "");
        inputMobile.setText(mobile);
        inputTelephone.setText(telephone);

        imageSave = findViewById(R.id.image_save);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMobile.setError(null);
                if(inputMobile.getText().toString().equals(""))
                    inputMobile.setError(getString(R.string.error_required_field));
                if(!inputMobile.getText().toString().equals(""))
                {
                    SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag,
                            Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getResources().getString(R.string.param_mobile), inputMobile.getText().toString());
                    editor.putString(getResources().getString(R.string.param_telephone), inputTelephone.getText().toString());
                    editor.apply();
                    finish();
                }
            }
        });
        if(preferences.getBoolean(getString(R.string.param_identity_credential_status), false)) {
            inputMobile.setFocusable(false);
            inputTelephone.setFocusable(false);
            imageSave.setVisibility(View.GONE);
        }
    }
}