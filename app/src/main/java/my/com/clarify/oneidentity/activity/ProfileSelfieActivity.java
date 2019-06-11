package my.com.clarify.oneidentity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedro.library.AutoPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ai.oneid.liveness.android.activity.CameraActivity;
import cz.msebera.android.httpclient.Header;
import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;

public class ProfileSelfieActivity extends AppCompatActivity {
    public AppDelegate delegate;
    public AppCompatImageView imageBack;
    public AppCompatImageView imageUpload;
    public Button buttonAutomaticVerification;
    public ProgressDialog progressDialog;
    public ProgressDialog progressLoadingDialog;
    public int LIVENESS_REQUEST_CODE = 1;

    public String filename = "";
    public String filepath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_profile_selfie);
        delegate = (AppDelegate)getApplication();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing File");
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressLoadingDialog = new ProgressDialog(this);
        progressLoadingDialog.setMessage("Loading file...");
        progressLoadingDialog.setCancelable(true);

        imageBack = (AppCompatImageView)findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageUpload = (AppCompatImageView) findViewById(R.id.image_upload);
        delegate.createDirectoryIfNotExist(Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath);

        buttonAutomaticVerification = findViewById(R.id.button_automatic_verification);
        buttonAutomaticVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                Intent intent = new Intent(ProfileSelfieActivity.this, CameraActivity.class);
                intent.putExtra("token", preferences.getString(getString(R.string.param_token), ""));
                intent.putExtra("apikey", AppDelegate.apikey);
                startActivityForResult(intent, LIVENESS_REQUEST_CODE);
            }
        });
        AutoPermissions.Companion.loadAllPermissions(this, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, listener);
    }

    public boolean isCalled = false;
    @Override
    public void onResume()
    {
        super.onResume();

        final SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        final String selfie = preferences.getString(getResources().getString(R.string.param_selfie), "");
        if(!selfie.equals("")) {

            if(isCalled)
                return;

            isCalled = true;
            viewUpload(selfie);
        }

        if(preferences.getBoolean(getString(R.string.param_identity_credential_status), false)) {
            //buttonAutomaticVerification.setVisibility(View.GONE);
        }
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

    private void viewUpload(String documentName)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
            jsonObject.put("document_name", documentName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.e("Input", jsonObject.toString());
        AsynRestClient.genericPost(this, AsynRestClient.viewUploadUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                progressLoadingDialog.show();;
            }

            @Override
            public void onFinish() {
                progressLoadingDialog.dismiss();
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
                    String imageBase64 = jsonObject.getString("data");
                    byte[] imageByteArray = Base64.decode(imageBase64, Base64.DEFAULT);
                    Glide.with(ProfileSelfieActivity.this)
                            .load(imageByteArray)
                            .into(imageUpload);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                isCalled = false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //alert(getString(R.string.error), getString(R.string.error_please_try_again));
                isCalled = false;
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LIVENESS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String returnedResult = data.getData().toString();
                Log.e("Result", returnedResult);
                try {
                    JSONObject json = new JSONObject(returnedResult);
                    String fileName = json.getString("selfie_image");

                    SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getResources().getString(R.string.param_selfie), fileName);
                    editor.apply();
                } catch (Exception e) {

                }
            }
        }
    }
}