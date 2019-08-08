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

public class ProfileAddressActivity extends AppCompatActivity {
    public AppDelegate delegate;
    public AppCompatImageView imageBack;
    public EditText inputAddress1;
    public EditText inputAddress2;
    public EditText inputCity;
    public EditText inputState;
    public EditText inputPostcode;
    public EditText inputCountry;
    public AppCompatImageView imageSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_address);
        delegate = (AppDelegate)getApplication();

        imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inputAddress1 = findViewById(R.id.input_address_line_1);
        inputAddress2 = findViewById(R.id.input_address_line_2);
        inputCity = findViewById(R.id.input_city);
        inputState = findViewById(R.id.input_state);
        inputPostcode = findViewById(R.id.input_postcode);
        inputCountry = findViewById(R.id.input_country);

        final SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag,
                Context.MODE_PRIVATE);
        final String address1 = preferences.getString(getResources().getString(R.string.param_address1), "");
        final String address2 = preferences.getString(getResources().getString(R.string.param_address2), "");
        final String city = preferences.getString(getResources().getString(R.string.param_city), "");
        final String state = preferences.getString(getResources().getString(R.string.param_state), "");
        final String postcode = preferences.getString(getResources().getString(R.string.param_postcode), "");
        final String country = preferences.getString(getResources().getString(R.string.param_country), "");
        inputAddress1.setText(address1);
        inputAddress2.setText(address2);
        inputCity.setText(city);
        inputState.setText(state);
        inputPostcode.setText(postcode);
        inputCountry.setText(country);

        imageSave = findViewById(R.id.image_save);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputAddress1.setError(null);
                inputCity.setError(null);
                inputState.setError(null);
                inputPostcode.setError(null);
                inputCountry.setError(null);
                if(inputAddress1.getText().toString().equals(""))
                    inputAddress1.setError(getString(R.string.error_required_field));
                if(inputCity.getText().toString().equals(""))
                    inputCity.setError(getString(R.string.error_required_field));
                if(inputState.getText().toString().equals(""))
                    inputState.setError(getString(R.string.error_required_field));
                if(inputPostcode.getText().toString().equals(""))
                    inputPostcode.setError(getString(R.string.error_required_field));
                if(inputCountry.getText().toString().equals(""))
                    inputCountry.setError(getString(R.string.error_required_field));

                if(!inputAddress1.getText().toString().equals("") && !inputCity.getText().toString().equals("") && !inputState.getText().toString().equals("") && !inputPostcode.getText().toString().equals("") && !inputCountry.getText().toString().equals(""))
                {
                    SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getResources().getString(R.string.param_address1), inputAddress1.getText().toString());
                    editor.putString(getResources().getString(R.string.param_address2), inputAddress2.getText().toString());
                    editor.putString(getResources().getString(R.string.param_city), inputCity.getText().toString());
                    editor.putString(getResources().getString(R.string.param_state), inputState.getText().toString());
                    editor.putString(getResources().getString(R.string.param_postcode), inputPostcode.getText().toString());
                    editor.putString(getResources().getString(R.string.param_country), inputCountry.getText().toString());
                    editor.apply();
                    finish();
                }
            }
        });

        if(preferences.getBoolean(getString(R.string.param_identity_credential_status), false)) {
            inputAddress1.setFocusable(false);
            inputAddress2.setFocusable(false);
            inputCity.setFocusable(false);
            inputState.setFocusable(false);
            inputPostcode.setFocusable(false);
            inputCountry.setFocusable(false);
            imageSave.setVisibility(View.GONE);
        }
    }
}