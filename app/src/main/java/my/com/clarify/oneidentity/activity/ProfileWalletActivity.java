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

public class ProfileWalletActivity extends AppCompatActivity {
    public AppDelegate delegate;
    public AppCompatImageView imageBack;
    public EditText inputWalletName;
    public EditText inputWalletId;
    public AppCompatImageView imageSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delegate = (AppDelegate)getApplication();
        setContentView(R.layout.activity_profile_wallet);

        imageBack = (AppCompatImageView)findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        inputWalletName = (EditText) findViewById(R.id.input_wallet_name);
        inputWalletId = (EditText) findViewById(R.id.input_wallet_id);

        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag,
                Context.MODE_PRIVATE);
        final String walletName = preferences.getString(getResources().getString(R.string.param_wallet_name), "");
        final String walletId = preferences.getString(getResources().getString(R.string.param_wallet_id), "");
        inputWalletName.setText(walletName);
        inputWalletId.setText(walletId);

        inputWalletId.setFocusable(false);
        inputWalletName.setFocusable(false);
    }
}