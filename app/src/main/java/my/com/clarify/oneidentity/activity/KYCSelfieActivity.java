package my.com.clarify.oneidentity.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedro.library.AutoPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import ai.oneid.liveness.android.activity.CameraActivity;
import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;
import cz.msebera.android.httpclient.Header;

public class KYCSelfieActivity extends AppCompatActivity {
    public AppDelegate delegate;
    public AppCompatImageView imageBack;
    public AppCompatImageView imageClear;
    public AppCompatImageView imageProfile;
    public int LIVENESS_REQUEST_CODE = 1357;
    public Button buttonNext;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
        setContentView(R.layout.activity_kyc_selfie);
        delegate = (AppDelegate)getApplication();
        delegate.createDirectoryIfNotExist(Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath);

        AutoPermissions.Companion.loadAllPermissions(this, 1);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageClear = findViewById(R.id.image_clear);
        imageClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(KYCSelfieActivity.this);
                builder.setTitle(getString(R.string.delete_existing_data));
                builder.setMessage(getString(R.string.delete_existing_data_description));
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.param_kyc_selfie), "");
                        editor.putString(getString(R.string.param_kyc_id_document), "");
                        editor.putString(getString(R.string.param_kyc_verification_result), "");
                        editor.apply();
                        triggerButtonNext();
                        dialog.dismiss();
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

        imageProfile = findViewById(R.id.image_profile);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                Intent intent = new Intent(KYCSelfieActivity.this, CameraActivity.class);
                intent.putExtra("token", AppDelegate.livenessToken);
                intent.putExtra("apikey", AppDelegate.apikey);
                startActivityForResult(intent, LIVENESS_REQUEST_CODE);
            }
        });
        buttonNext = findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KYCSelfieActivity.this, KYCIdentityDocumentActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
        buttonNext.setVisibility(View.GONE);
        triggerButtonNext();
        delegate.isGooglePlayServicesAvailable(this);
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(KYCSelfieActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void triggerButtonNext()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        if(preferences.getString(getString(R.string.param_kyc_selfie), "").equals("")) {
            Glide.with(KYCSelfieActivity.this).load(R.drawable.default_user).apply(RequestOptions.circleCropTransform()).into(imageProfile);
            buttonNext.setVisibility(View.GONE);
            imageClear.setVisibility(View.INVISIBLE);
        }
        else
        {
            File f = new File(Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath + "/" + preferences.getString(getString(R.string.param_kyc_selfie), ""));
            Glide.with(KYCSelfieActivity.this).load(f).apply(RequestOptions.circleCropTransform()).into(imageProfile);
            buttonNext.setVisibility(View.VISIBLE);
            imageClear.setVisibility(View.VISIBLE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LIVENESS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String returnedResult = data.getData().toString();
            Log.e("Result", returnedResult);
            try {
                JSONObject json = new JSONObject(returnedResult);
                String fileName = json.getString("selfie_image");
                viewUpload(fileName, "selfie");
            }
            catch (Exception e) {
            }
        }
    }

    public void viewUpload(String documentName, final String type)
    {
        final SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", AppDelegate.livenessToken);
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
                if(!type.equals("id") && !type.equals("address"))
                    progressDialog.show();
            }

            @Override
            public void onFinish() {
                if(!type.equals("id") && !type.equals("address"))
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
                    String imageBase64 = jsonObject.getString("data");
                    byte[] imageByteArray = Base64.decode(imageBase64, Base64.DEFAULT);

                    try {
                        String newName = delegate.fileNameGenerator(KYCSelfieActivity.this);
                        File f = new File(Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath + "/" + newName + ".jpg");
                        f.createNewFile();
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(imageByteArray);
                        fo.flush();
                        fo.close();

                        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.param_kyc_selfie), newName + ".jpg");
                        editor.apply();
                        triggerButtonNext();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
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
                //alert(getString(R.string.error), getString(R.string.error_please_try_again));
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
