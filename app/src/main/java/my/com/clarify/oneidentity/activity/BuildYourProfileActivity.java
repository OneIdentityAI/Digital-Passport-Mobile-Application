package my.com.clarify.oneidentity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.client.android.CaptureActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedro.library.AutoPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;

public class BuildYourProfileActivity extends AppCompatActivity {
    public AppCompatImageView imageMenu;
    public TextView textPercentage;
    public RelativeLayout layoutProfile;
    public AppCompatImageView imagePie1;
    public AppCompatImageView imagePie2;
    public AppCompatImageView imagePie3;
    public AppCompatImageView imagePie4;
    public AppCompatImageView imagePie5;
    public AppCompatImageView imagePie6;
    public AppCompatImageView imagePie7;
    public AppCompatImageView imagePie8;
    public ProgressDialog progressDialog;
    public AppDelegate delegate;
    public AppCompatImageView imageCreateIdentity;

    public ArrayList<String> credDefIdList = new ArrayList<String>();
    public ArrayList<String> schemaIdList = new ArrayList<String>();
    public ArrayList<String> referrantList = new ArrayList<String>();
    public ArrayList<String> contentList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delegate = (AppDelegate)getApplication();
        setContentView(R.layout.activity_build_your_profile);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        imageMenu = findViewById(R.id.image_menu);
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(BuildYourProfileActivity.this, imageMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                        if(!preferences.getBoolean(getString(R.string.param_identity_credential_status), false))
                        {
                            alert("Identity Credential Required", getString(R.string.get_credential_message));
                            return true;
                        }

                        if(item.getTitle().toString().toLowerCase().equals("payment wallet"))
                        {
                            Intent intent = new Intent(BuildYourProfileActivity.this, PaymentWalletListActivity.class);
                            startActivity(intent);
                        }
                        else if(item.getTitle().toString().toLowerCase().equals("did"))
                        {
                            Intent intent = new Intent(BuildYourProfileActivity.this, DIDListActivity.class);
                            startActivity(intent);
                        }
                        else if(item.getTitle().toString().toLowerCase().equals("connection"))
                        {
                            Intent intent = new Intent(BuildYourProfileActivity.this, ConnectionListActivity.class);
                            startActivity(intent);
                        }
                        else if(item.getTitle().toString().toLowerCase().equals("message"))
                        {
                            Intent intent = new Intent(BuildYourProfileActivity.this, MessageListActivity.class);
                            startActivity(intent);
                        }
                        else if(item.getTitle().toString().toLowerCase().equals("credential"))
                        {
                            Intent intent = new Intent(BuildYourProfileActivity.this, CredentialListActivity.class);
                            startActivity(intent);
                        }
                        else if(item.getTitle().toString().toLowerCase().equals("sign in to digital services"))
                        {
                            Intent intent = new Intent(BuildYourProfileActivity.this, CaptureActivity.class);
                            intent.setAction("com.google.zxing.client.android.SCAN");
                            intent.putExtra("SAVE_HISTORY", false);
                            startActivityForResult(intent, AppDelegate.BARCODE_REQUEST_CODE);
                            shouldRefresh = false;
                        }
                        else if(item.getTitle().toString().toLowerCase().equals("sign out"))
                        {
                            preferences.edit().clear().commit();
                            Intent i = new Intent(BuildYourProfileActivity.this, WelcomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });
        layoutProfile = findViewById(R.id.layout_profile);
        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BuildYourProfileActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });
        textPercentage = findViewById(R.id.text_percentage);
        imagePie1 = findViewById(R.id.image_pie_1);
        imagePie2 = findViewById(R.id.image_pie_2);
        imagePie3 = findViewById(R.id.image_pie_3);
        imagePie4 = findViewById(R.id.image_pie_4);
        imagePie5 = findViewById(R.id.image_pie_5);
        imagePie6 = findViewById(R.id.image_pie_6);
        imagePie7 = findViewById(R.id.image_pie_7);
        imagePie8 = findViewById(R.id.image_pie_8);
        imageCreateIdentity = findViewById(R.id.image_create_identity);
        imageCreateIdentity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                if(preferences.getBoolean(getString(R.string.param_identity_credential_status), false))
                {
                    Intent intent = new Intent(BuildYourProfileActivity.this, CredentialListActivity.class);
                    startActivity(intent);
                }
                else if(sum >= 8 && !preferences.getBoolean(getString(R.string.param_identity_credential_status), false))
                    createOneIdentity();
            }
        });
        imageCreateIdentity.setVisibility(View.GONE);
        AutoPermissions.Companion.loadAllPermissions(this, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == AppDelegate.BARCODE_REQUEST_CODE)
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

    public boolean shouldRefresh = true;
    public void onResume()
    {
        super.onResume();
        if(shouldRefresh)
            updateProgress();

        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        Log.e("Token:", preferences.getString(getString(R.string.param_token), ""));
    }

    public int sum = 0;
    public void updateProgress()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        int progress1 = 0;
        if(!preferences.getString(getResources().getString(R.string.param_wallet_name), "").equals(""))
            progress1 = 1;
        if(progress1 > 0)
            imagePie1.setImageResource(R.drawable.rounded_progress_wallet_white);

        int progress2 = 0;
        if(!preferences.getString(getResources().getString(R.string.param_first_name), "").equals("")
                && !preferences.getString(getResources().getString(R.string.param_last_name), "").equals("")
                && !preferences.getString(getResources().getString(R.string.param_nationality), "").equals("")
                && !preferences.getString(getResources().getString(R.string.param_job_title), "").equals("")
                && !preferences.getString(getResources().getString(R.string.param_email), "").equals("")
                && !preferences.getString(getResources().getString(R.string.param_security_question), "").equals("")
                && !preferences.getString(getResources().getString(R.string.param_security_answer), "").equals(""))
            progress2 = 1;
        if(progress2 > 0)
            imagePie2.setImageResource(R.drawable.rounded_progress_user_white);

        int progress3 = 0;
        if(!preferences.getString(getResources().getString(R.string.param_address1), "").equals(""))
            progress3 = 1;
        if(progress3 > 0)
            imagePie3.setImageResource(R.drawable.rounded_progress_home_white);

        int progress4 = 0;
        if(!preferences.getString(getResources().getString(R.string.param_mobile), "").equals(""))
            progress4 = 1;
        if(progress4 > 0)
            imagePie4.setImageResource(R.drawable.rounded_progress_phone_white);

        int progress5 = 0;
        if(!preferences.getString(getResources().getString(R.string.param_dob), "").equals(""))
            progress5 = 1;
        if(progress5 > 0)
            imagePie5.setImageResource(R.drawable.rounded_progress_birthday_white);

        int progress6 = 0;
        if(!preferences.getString(getResources().getString(R.string.param_passport), "").equals(""))
            progress6 = 1;
        if(progress6 > 0)
            imagePie6.setImageResource(R.drawable.rounded_progress_passport_white);

        int progress7 = 0;
        if(!preferences.getString(getResources().getString(R.string.param_selfie), "").equals(""))
            progress7 = 1;
        if(progress7 > 0)
            imagePie7.setImageResource(R.drawable.rounded_progress_selfie_white);

        int progress8 = 0;
        if(!preferences.getString(getResources().getString(R.string.param_address_image), "").equals(""))
            progress8 = 1;
        if(progress8 > 0)
            imagePie8.setImageResource(R.drawable.rounded_progress_proof_white);

        sum = progress1 + progress2 + progress3 + progress4 + progress5 + progress6 + progress7 + progress8;
        double division = (sum/8.0)*100.0;
        Log.e("Sum:" + sum, "Division:" + division);
        textPercentage.setText(Math.round(division) + "%");
        retrieveData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void createOneIdentity()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
            jsonObject.put("job_title", preferences.getString(getString(R.string.param_job_title), ""));
            jsonObject.put("first_name", preferences.getString(getString(R.string.param_first_name), ""));
            jsonObject.put("last_name", preferences.getString(getString(R.string.param_last_name), ""));
            jsonObject.put("email", preferences.getString(getString(R.string.param_email), ""));
            jsonObject.put("gender", preferences.getString(getString(R.string.param_gender), ""));
            jsonObject.put("dob", preferences.getString(getString(R.string.param_dob), ""));
            jsonObject.put("nationality", preferences.getString(getString(R.string.param_nationality_code), ""));
            jsonObject.put("id_no", preferences.getString(getString(R.string.param_passport_no), ""));
            jsonObject.put("id_image", preferences.getString(getString(R.string.param_passport), ""));
            jsonObject.put("proof_address_image", preferences.getString(getString(R.string.param_address_image), ""));
            jsonObject.put("selfie_image", preferences.getString(getString(R.string.param_selfie), ""));
            jsonObject.put("company_name", preferences.getString(getString(R.string.param_company_name), ""));
            jsonObject.put("doc_type", preferences.getString(getString(R.string.param_id_type), ""));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.e("Json", jsonObject.toString());
        AsynRestClient.genericPost(BuildYourProfileActivity.this, AsynRestClient.identityVerificationUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    if(jsonObject.has("error"))
                    {
                        JSONObject errorObject = jsonObject.getJSONObject("error");
                        alert(errorObject.getString("type"), errorObject.getString("message"));
                        return;
                    }

                    String status = jsonObject.getString("status");
                    if(!status.equals("200"))
                    {
                        String type = jsonObject.getString("type");
                        String message = jsonObject.getString("message");
                        alert(type, message);
                        return;
                    }
                    imageCreateIdentity.setVisibility(View.GONE);
                    isCalled = false;
                    retrieveData();
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

    public boolean isCalled = false;
    private void retrieveData()
    {
        if(isCalled)
            return;

        isCalled = true;
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        if(preferences.getBoolean(getResources().getString(R.string.param_identity_credential_status), false))
        {
            imageCreateIdentity.setVisibility(View.VISIBLE);
            Glide.with(BuildYourProfileActivity.this).load(R.drawable.view_credential).into(imageCreateIdentity);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(BuildYourProfileActivity.this, AsynRestClient.listCredentialUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    credDefIdList.clear();
                    schemaIdList.clear();
                    referrantList.clear();
                    contentList.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    int indexMatching = -1;
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject innerObject = (JSONObject) jsonArray.get(i);
                        credDefIdList.add(innerObject.getString("cred_def_id"));
                        schemaIdList.add(innerObject.getString("schema_id"));
                        referrantList.add(innerObject.getString("referent"));
                        contentList.add(innerObject.getJSONObject("attrs").toString());
                        if(innerObject.getString("cred_def_id").equals(AppDelegate.credDefIdOneIdentity))
                            indexMatching = i;
                    }

                    SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                    if(indexMatching > -1) {
                        final SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(getResources().getString(R.string.param_identity_credential_status), true);
                        editor.putString(getResources().getString(R.string.param_identity_credential), contentList.get(credDefIdList.indexOf(AppDelegate.credDefIdOneIdentity)));
                        editor.apply();
                        alert(getString(R.string.success), getString(R.string.success_identity_credential));
                        imageCreateIdentity.setVisibility(View.VISIBLE);
                        Glide.with(BuildYourProfileActivity.this).load(R.drawable.view_credential).into(imageCreateIdentity);
                    }
                    else if(sum >= 8 && !preferences.getBoolean(getString(R.string.param_identity_credential_status), false))
                        imageCreateIdentity.setVisibility(View.VISIBLE);
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
                //Log.e("Helo", "Hello");
                isCalled = false;
            }
        });
    }

    private void webSignIn(String id)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(BuildYourProfileActivity.this, AsynRestClient.webSignInUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
}