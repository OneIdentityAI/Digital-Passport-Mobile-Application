package my.com.clarify.oneidentity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.client.android.CaptureActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public BottomNavigationView bottomNavigationView;
    public AppCompatImageView imageQRCode;
    public AppCompatImageView imageScan;
    public AppCompatImageView imageSignIn;
    public AppCompatImageView imageDelete;
    public CardView cardEndpointDID;

    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        imageQRCode = findViewById(R.id.image_qrcode);
        try {
            imageQRCode.setImageBitmap(AppDelegate.encodeStringAsQRBitmap(preferences.getString(getResources().getString(R.string.param_oneid_endpoint_did), "")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        cardEndpointDID = findViewById(R.id.card_endpoint_did);
        cardEndpointDID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyEndpointDIDActivity.class);
                startActivity(intent);
            }
        });
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.action_message) {
                    Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else if(item.getItemId() == R.id.action_credential) {
                    Intent intent = new Intent(MainActivity.this, CredentialActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else if(item.getItemId() == R.id.action_did) {
                    Intent intent = new Intent(MainActivity.this, DIDActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                return true;
            }
        });

        imageScan = findViewById(R.id.image_scan);
        imageScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                intent.setAction("com.google.zxing.client.android.SCAN");
                intent.putExtra("SAVE_HISTORY", false);
                startActivityForResult(intent, AppDelegate.BARCODE_REQUEST_CODE);
            }
        });
        imageSignIn = findViewById(R.id.image_sign_in);
        imageSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                intent.setAction("com.google.zxing.client.android.SCAN");
                intent.putExtra("SAVE_HISTORY", false);
                startActivityForResult(intent, AppDelegate.SIGN_IN_REQUEST_CODE);
            }
        });
        imageDelete = findViewById(R.id.image_delete);
        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.delete_existing_data));
                builder.setMessage(getString(R.string.delete_existing_data_description));
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.param_di_selfie), "");
                        editor.putString(getString(R.string.param_di_id_document), "");
                        editor.putString(getString(R.string.param_di_verification_result), "");

                        editor.putString(getString(R.string.param_oneid_wallet_name), "");
                        editor.putString(getString(R.string.param_oneid_wallet_address), "");
                        editor.putString(getString(R.string.param_oneid_created_at), "");
                        editor.putString(getString(R.string.param_oneid_endpoint_did), "");
                        editor.putString(getString(R.string.param_oneid_token), "");
                        editor.putString(getString(R.string.param_oneid_id_image), "");
                        editor.putString(getString(R.string.param_oneid_selfie_image), "");
                        editor.putString(getString(R.string.param_oneid_company_name), "");
                        editor.putString(getString(R.string.param_oneid_credential), "");

                        editor.apply();
                        dialog.dismiss();
                        onBackPressed();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == AppDelegate.BARCODE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                final String contents = intent.getStringExtra("SCAN_RESULT");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Create Connection");
                builder.setMessage("Confirm establishing connection with " + contents + "?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkDID(contents);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
            else if (resultCode == RESULT_CANCELED)
            {
            }
        }
        else if (requestCode == AppDelegate.SIGN_IN_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                String contents = intent.getStringExtra("SCAN_RESULT");
                webSignIn(contents);
            }
            else if (resultCode == RESULT_CANCELED)
            {

            }
        }
    }

    private void webSignIn(String id)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.e("id", id);
        Log.e("input", jsonObject.toString());
        Log.e("Url", AsynRestClient.webSignInUrl);
        AsynRestClient.genericPost(this, AsynRestClient.webSignInUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }


    public void checkDID(String endpointDID)
    {
        /*
        if(theirEndpointDIDList.size() == 0)
        {
            final EditText edittext = new EditText(MainActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setView(edittext);
            builder.setTitle(getString(R.string.create_new_did));
            builder.setMessage(getString(R.string.create_new_did_description));
            builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    edittext.setError(null);
                    if(!edittext.getText().toString().equals("")) {
                        addDid(edittext.getText().toString());
                        dialog.dismiss();
                    }
                    else
                        edittext.setError(getString(R.string.error_value_cannot_be_empty));
                }
            });
        }
        else {
            createConnectionRequest(endpointDID);
        }
        */
    }

    private void addDid(String didName)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("name", didName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.addDIDUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                alert(getString(R.string.error), getString(R.string.error_please_try_again));
                Log.e("Helo", "Hello");
            }
        });
    }

    private void createConnectionRequest(String endpointDID)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            //jsonObject.put("sender_did", didList.get(selectedPosition));
            jsonObject.put("recipient_endpoint_did", endpointDID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.e("Param", jsonObject.toString());
        AsynRestClient.genericPost(this, AsynRestClient.createConnectionRequestUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    JSONObject jsonObjectInner = jsonObject.getJSONObject("data");
                    alert(getString(R.string.success), "Connection request sent with the following nonce :" + jsonObjectInner.getString("nonce"));
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
