package my.com.clarify.oneidentity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;

public class ProfileProofActivity extends AppCompatActivity {
    public AppDelegate delegate;
    public AppCompatImageView imageBack;
    public AppCompatImageView imageUpload;
    public AppCompatImageView imageSave;

    public Uri uri;
    public String imageName = "";
    public String imagePath = "";
    public ProgressDialog progressDialog;
    public ProgressDialog progressLoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_proof);
        delegate = (AppDelegate)getApplication();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing File");
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressLoadingDialog = new ProgressDialog(this);
        progressLoadingDialog.setMessage("Loading file...");
        progressLoadingDialog.setCancelable(true);

        final SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag,
                Context.MODE_PRIVATE);
        final String addressImage = preferences.getString(getResources().getString(R.string.param_address_image), "");

        imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageUpload =  findViewById(R.id.image_upload);
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                if(!preferences.getBoolean(getString(R.string.param_identity_credential_status), false)) {
                    ImagePicker.with(ProfileProofActivity.this)
                            .setToolbarColor("#58b3ff")
                            .setStatusBarColor("#027FE5")       //  StatusBar color (works with SDK >= 21  )
                            .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                            .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
                            .setProgressBarColor("#027FE5")     //  ProgressBar color
                            .setBackgroundColor("#FFFFFF")      //  Background color
                            .setCameraOnly(false)               //  Camera mode
                            .setMultipleMode(false)              //  Select multiple images or single image
                            .setFolderMode(true)                //  Folder mode
                            .setShowCamera(true)                //  Show camera button
                            .setMaxSize(1)                     //  Max images can be selected
                            .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
                            .setKeepScreenOn(true)              //  Keep screen on when selecting images
                            .start();                           //  Start ImagePicker
                }
            }
        });
        if(!addressImage.equals(""))
            viewUpload(addressImage);

        imageSave = findViewById(R.id.image_save);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imagePath.equals(""))
                {
                    upload();
                }
            }
        });
        if(preferences.getBoolean(getString(R.string.param_identity_credential_status), false)) {
            imageSave.setVisibility(View.GONE);
        }
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
                    Glide.with(ProfileProofActivity.this)
                            .load(imageByteArray)
                            .into(imageUpload);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //alert(getString(R.string.error), getString(R.string.error_please_try_again));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            imagePath = images.get(0).getPath();

            imagePath = AppDelegate.getResizedImagePathFromOriginalPath(this, imagePath, 1000, 1000);
            String strPath = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
            imageName = strPath;
            Log.e("imagePath", imagePath);
            Log.e("imageName", imageName);
            setPhoto();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setPhoto()
    {
        if(!imagePath.equals(""))
        {
            Glide.with(this).load(new File(imagePath)).into(imageUpload);
        }
    }

    public void upload()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        String token = preferences.getString(getResources().getString(R.string.param_token), "");
        AsynRestClient.upload(token, "address", imagePath, new AsyncHttpResponseHandler()
        {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response)
            {
                String responseString = new String(response);
                Log.e("Upload response", responseString);
                try {
                    JSONObject jsonObject = new JSONObject(responseString.replace("null", "\"\""));
                    if(jsonObject.has("error"))
                    {
                        JSONObject errorObject = jsonObject.getJSONObject("error");
                        alert(errorObject.getString("type"), errorObject.getString("message"));
                        return;
                    }
                    String status = jsonObject.getString("status");
                    if (status.equals("200"))
                    {
                        String fileName = jsonObject.getString("file_name");

                        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getResources().getString(R.string.param_address_image), fileName);
                        editor.apply();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                String responseString = new String(errorResponse);
                Log.e("Upload response", responseString);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                int progress = Math.round((bytesWritten/totalSize)*100);
                progressDialog.setProgress(progress);
                progressDialog.setMax(100);
            }
        });
    }
}