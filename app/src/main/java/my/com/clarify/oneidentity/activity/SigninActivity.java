package my.com.clarify.oneidentity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;

public class SigninActivity extends AppCompatActivity {
    public AppCompatImageView imageBack;
    public EditText inputWalletId;
    public EditText inputPassword;
    public AppCompatImageView imageValidate;
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        inputWalletId = findViewById(R.id.input_wallet_id);
        inputPassword = findViewById(R.id.input_password);
        imageValidate = findViewById(R.id.image_validate);
        imageValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputWalletId.setError(null);
                inputPassword.setError(null);
                if(inputWalletId.getText().toString().equals(""))
                    inputWalletId.setError(getString(R.string.error_required_field));
                if(inputPassword.getText().toString().equals(""))
                    inputPassword.setError(getString(R.string.error_required_field));

                if(!inputWalletId.getText().toString().equals("") && !inputPassword.getText().toString().equals(""))
                {
                    viewIdentityWallet();
                }
            }
        });
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        String walletId = preferences.getString(getResources().getString(R.string.param_wallet_id), inputWalletId.getText().toString());
        String passphrase = preferences.getString(getResources().getString(R.string.param_passphrase), inputPassword.getText().toString());
        inputWalletId.setText(walletId);
        inputPassword.setText(passphrase);
    }

    private void viewIdentityWallet()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        AsynRestClient.genericPost(this, AsynRestClient.viewIdentityWalletUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String responseString = new String(responseBody);
                Log.e("Response", responseString);
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    if(!jsonObject.getString("status").equals("200"))
                    {
                        alert(getString(R.string.error), jsonObject.getString("message") + System.lineSeparator() + jsonObject.getString("message"));
                    }
                    else
                    {
                        JSONObject innerObject = jsonObject.getJSONObject("data");
                        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(getResources().getString(R.string.param_account_status), true);
                        editor.putString(getResources().getString(R.string.param_wallet_name), innerObject.getString("wallet_name"));
                        editor.putString(getResources().getString(R.string.param_wallet_id), innerObject.getString("wallet_id"));
                        editor.putString(getResources().getString(R.string.param_passphrase), inputPassword.getText().toString());
                        editor.putString(getResources().getString(R.string.param_created_at), innerObject.getString("created_at"));
                        editor.putString(getResources().getString(R.string.param_endpoint_did), innerObject.getString("endpoint_did"));
                        editor.apply();
                        Intent i = new Intent(SigninActivity.this, BuildYourProfileActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                alert(getString(R.string.error), getString(R.string.error_please_try_again));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SigninActivity.this, WelcomeActivity.class);
        startActivity(i);
        finish();
    }

    public void alert(String title, String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}