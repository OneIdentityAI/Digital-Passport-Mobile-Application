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
import android.widget.CheckBox;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;

public class IdentityCreationActivity extends AppCompatActivity{
    public AppCompatImageView imageBack;
    public EditText inputPassword;
    public EditText inputPasswordConfirm;
    public EditText inputGender;
    public EditText inputEmail;
    public EditText inputSecurityQuestion;
    public EditText inputSecurityAnswer;
    public CheckBox checkboxGotIt;
    public AppCompatImageView imageSaveAndContinue;

    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_creation);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IdentityCreationActivity.this, WelcomeActivity.class);
                startActivity(i);
                finish();
            }
        });
        inputPassword = findViewById(R.id.input_password);
        inputPasswordConfirm = findViewById(R.id.input_password_confirm);
        inputGender = findViewById(R.id.input_gender);
        inputGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(IdentityCreationActivity.this);
                builder.setTitle("Choose gender");
                final String[] data = getResources().getStringArray(R.array.gender);
                builder.setItems(data, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputGender.setText(data[which]);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        inputEmail = findViewById(R.id.input_email);
        inputSecurityQuestion = findViewById(R.id.input_security_question);
        inputSecurityQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(IdentityCreationActivity.this);
                builder.setTitle("Choose security question");
                final String[] securityQuestionArray = getResources().getStringArray(R.array.security_question);
                builder.setItems(securityQuestionArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputSecurityQuestion.setText(securityQuestionArray[which]);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        inputSecurityAnswer = findViewById(R.id.input_security_answer);
        checkboxGotIt = findViewById(R.id.checkbox_got_it);

        imageSaveAndContinue = findViewById(R.id.image_save_and_continue);
        imageSaveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPassword.setError(null);
                inputPasswordConfirm.setError(null);
                inputGender.setError(null);
                inputEmail.setError(null);
                inputSecurityQuestion.setError(null);
                inputSecurityAnswer.setError(null);
                if(inputPassword.getText().toString().equals(""))
                    inputPassword.setError(getString(R.string.error_required_field));
                if(inputPasswordConfirm.getText().toString().equals(""))
                    inputPasswordConfirm.setError(getString(R.string.error_required_field));
                if(inputGender.getText().toString().equals(""))
                    inputGender.setError(getString(R.string.error_required_field));
                if(inputEmail.getText().toString().equals(""))
                    inputEmail.setError(getString(R.string.error_required_field));
                if(inputSecurityQuestion.getText().toString().equals(""))
                    inputSecurityQuestion.setError(getString(R.string.error_required_field));
                if(inputSecurityAnswer.getText().toString().equals(""))
                    inputSecurityAnswer.setError(getString(R.string.error_required_field));

                if(!inputPassword.getText().toString().equals("") && !inputPasswordConfirm.getText().toString().equals("") &&
                        !inputGender.getText().toString().equals("") && !inputEmail.getText().toString().equals("") &&
                        !inputSecurityQuestion.getText().toString().equals("") && !inputSecurityAnswer.getText().toString().equals(""))
                {
                    if (inputPassword.getText().toString().length() < 8 || inputPassword.getText().toString().length() > 20)
                    {
                        inputPassword.setError(getString(R.string.error_password_length));
                    }
                    else if (!inputPassword.getText().toString().equals(inputPasswordConfirm.getText().toString()))
                    {
                        inputPassword.setError(getString(R.string.error_password_not_match));
                        inputPasswordConfirm.setError(getString(R.string.error_password_not_match));
                    }
                    else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches())
                    {
                        inputEmail.setError(getString(R.string.error_invalid_email));
                    }
                    else if (!checkboxGotIt.isChecked())
                    {
                        alert(getString(R.string.error), getString(R.string.error_check_box_got_it));
                    }
                    else
                    {
                        createIdentityWallet();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(IdentityCreationActivity.this, WelcomeActivity.class);
        startActivity(i);
        finish();
    }

    private void createIdentityWallet()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("wallet_name", "Normal Wallet");
            jsonObject.put("passphrase", inputPassword.getText().toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.createIdentityWalletUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                progressDialog.show();;
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

                    if(jsonObject.has("error"))
                    {
                        JSONObject errorObject = jsonObject.getJSONObject("error");
                        alert(errorObject.getString("type"), errorObject.getString("message"));
                        return;
                    }
                    JSONObject innerObject = jsonObject.getJSONObject("data");
                    SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(getResources().getString(R.string.param_account_status), true);
                    editor.putString(getResources().getString(R.string.param_wallet_name), innerObject.getString("wallet_name"));
                    editor.putString(getResources().getString(R.string.param_wallet_id), innerObject.getString("wallet_id"));
                    editor.putString(getResources().getString(R.string.param_passphrase), inputPassword.getText().toString());
                    editor.putString(getResources().getString(R.string.param_created_at), innerObject.getString("created_at"));
                    editor.putString(getResources().getString(R.string.param_endpoint_did), innerObject.getString("endpoint_did"));
                    editor.putString(getResources().getString(R.string.param_token), innerObject.getString("token"));

                    editor.putString(getResources().getString(R.string.param_passphrase), inputPassword.getText().toString());
                    editor.putString(getResources().getString(R.string.param_gender), inputGender.getText().toString());
                    editor.putString(getResources().getString(R.string.param_email), inputEmail.getText().toString());
                    editor.putString(getResources().getString(R.string.param_security_question), inputSecurityQuestion.getText().toString());
                    editor.putString(getResources().getString(R.string.param_security_answer), inputSecurityAnswer.getText().toString());

                    editor.apply();
                    Intent i = new Intent(IdentityCreationActivity.this, BuildYourProfileActivity.class);
                    startActivity(i);
                    finish();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                String responseString = new String(errorResponse);
                Log.e("Response", responseString);
                alert(getString(R.string.error), getString(R.string.error_please_try_again));
                Log.e("Helo", "Hello");
            }
        });
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