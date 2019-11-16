package my.com.clarify.oneidentity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;
import cz.msebera.android.httpclient.Header;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

public class DIReviewVerificationResultActivity extends AppCompatActivity {
    public AppCompatImageView imageBack;
    public TextView textVerifiedOn;
    public TextView textPepIsPep;
    public TextView textPepSource;
    public TextView textPepRef;
    public TextView textSanctionIsSdn;
    public TextView textSanctionSdnSource;
    public TextView textSanctionSdnRef;
    public TextView textSanctionSdnSearchType;
    public TextView textDOBCheck;
    public TextView textNationalityCheck;
    public TextView textGenderCheck;
    public TextView textExpiryCheck;
    public TextView textIdExpiredCheck;
    public TextView textIdNoCheck;
    public TextView textIcNoCheck;
    public TextView textNameCheck;
    public TextView textAddress;
    public LinearLayout layoutExpiry;
    public LinearLayout layoutAddress;
    public TextView textFaceMatchImageType;
    public TextView textFaceMatchScore;
    public TextView textFaceMatchResult;
    public TextView textOverallResult;
    public ImageViewZoom imageId;
    public ImageViewZoom imageSelfie;


    public LinearLayout layout1;
    public LinearLayout layout2;
    public TextView text2IssuedPlace;
    public TextView text2IDNo;
    public TextView text2Name;
    public TextView text2PlaceDateBirth;
    public TextView text2Gender;
    public TextView text2Nationality;
    public TextView text2Address;
    public TextView text2RT;
    public TextView text2Desa;
    public TextView text2Kecamatan;
    public TextView text2Religion;
    public TextView text2MaritalStatus;
    public TextView text2Occupation;


    public String idImage;
    public byte[] byteImageId;
    public String selfieImage;
    public byte[] byteImageSelfie;
    public Button buttonCreateIdentity;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
        final SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_di_review_verification_result);

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

        buttonCreateIdentity = findViewById(R.id.button_create_identity);
        buttonCreateIdentity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edittext = new EditText(DIReviewVerificationResultActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(DIReviewVerificationResultActivity.this);
                builder.setView(edittext);
                builder.setTitle(getString(R.string.provide_company_name));
                builder.setMessage(getString(R.string.provide_company_name_description));
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
                            SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                            final SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(getString(R.string.param_oneid_company_name), edittext.getText().toString());
                            editor.apply();
                            createIdentityWallet(edittext.getText().toString());
                            dialog.dismiss();
                        }
                        else
                            edittext.setError(getString(R.string.error_value_cannot_be_empty));
                    }
                });
            }
        });
        buttonCreateIdentity.setVisibility(View.GONE);

        textVerifiedOn = findViewById(R.id.text_verified_on);
        textPepIsPep = findViewById(R.id.text_pep_is_pep);
        textPepSource = findViewById(R.id.text_pep_source);
        textPepRef = findViewById(R.id.text_pep_ref);
        textSanctionIsSdn = findViewById(R.id.text_sanction_is_sdn);
        textSanctionSdnSource = findViewById(R.id.text_sanction_sdn_source);
        textSanctionSdnRef = findViewById(R.id.text_sanction_sdn_ref);
        textSanctionSdnSearchType = findViewById(R.id.text_sanction_search_type);

        layoutExpiry = findViewById(R.id.layout_expiry);
        layoutAddress = findViewById(R.id.layout_address);
        textAddress = findViewById(R.id.text_address_check);
        textDOBCheck = findViewById(R.id.text_dob_check);
        textNationalityCheck = findViewById(R.id.text_nationality_check);
        textGenderCheck = findViewById(R.id.text_gender_check);
        textExpiryCheck = findViewById(R.id.text_expiry_date_check);
        textIdExpiredCheck = findViewById(R.id.text_id_expired_check);
        textIdNoCheck = findViewById(R.id.text_id_no_check);
        textIcNoCheck = findViewById(R.id.text_ic_no_check);
        textNameCheck = findViewById(R.id.text_name_check);
        textFaceMatchImageType = findViewById(R.id.text_face_match_image_type);
        textFaceMatchScore = findViewById(R.id.text_face_match_score);
        textFaceMatchResult = findViewById(R.id.text_face_match_result);
        textOverallResult = findViewById(R.id.text_overall_result);

        layout1 = findViewById(R.id.layout_1);
        layout2 = findViewById(R.id.layout_2);
        text2IssuedPlace = findViewById(R.id.text_2_issued_place);
        text2IDNo = findViewById(R.id.text_2_id_no);
        text2Name = findViewById(R.id.text_2_name);
        text2PlaceDateBirth = findViewById(R.id.text_2_place_date_birth);
        text2Gender = findViewById(R.id.text_2_gender);
        text2Nationality = findViewById(R.id.text_2_nationality);
        text2Address  = findViewById(R.id.text_2_address);
        text2RT = findViewById(R.id.text_2_rt);
        text2Desa = findViewById(R.id.text_2_desa);
        text2Kecamatan = findViewById(R.id.text_2_kecamatan);
        text2Religion = findViewById(R.id.text_2_religion);
        text2MaritalStatus = findViewById(R.id.text_2_marital_status);
        text2Occupation = findViewById(R.id.text_2_occupation);


        imageId = findViewById(R.id.image_id);
        imageSelfie = findViewById(R.id.image_selfie);
        try {
            String jsonResponse = preferences.getString(getString(R.string.param_kyc_verification_result), "");
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject pepObject = jsonObject.getJSONObject("pep_data");
            textPepIsPep.setText(pepObject.getString("isPEP"));
            textPepSource.setText(pepObject.getString("PEPSource"));
            textPepRef.setText(pepObject.getString("PEPRef"));
            JSONObject sanctionObject = jsonObject.getJSONObject("sanction_data");
            textSanctionIsSdn.setText(sanctionObject.getString("isSDN"));
            textSanctionSdnSource.setText(sanctionObject.getString("SDNSource"));
            textSanctionSdnRef.setText(sanctionObject.getString("SDNRef"));
            textSanctionSdnSearchType.setText(sanctionObject.getString("searchType"));

            textFaceMatchResult.setText(jsonObject.getString("face_match"));
            //if(jsonObject.getString("face_match").toLowerCase().equals("yes")) {
            JSONObject facedataObject = jsonObject.getJSONObject("FaceData");
            String confidence = facedataObject.getString("confidence");
            textFaceMatchScore.setText(confidence);
            //}

            if(jsonObject.getString("Type").equals("ID") && jsonObject.getString("Country").equals("Malaysia"))
            {
                JSONObject icInfo = jsonObject.getJSONObject("IC-Info");
                textDOBCheck.setText(icInfo.getString("Detected-DOB"));
                textNationalityCheck.setText(icInfo.getString("Detected-Nationality"));
                textGenderCheck.setText(icInfo.getString("Detected-Gender"));
                textIcNoCheck.setText(icInfo.getString("Detected-ID-No"));
                textNameCheck.setText(icInfo.getString("Detected-Name"));
                textAddress.setText(icInfo.getString("Detected-Address"));
                textFaceMatchImageType.setText(jsonObject.getString("Type"));
                layoutExpiry.setVisibility(View.GONE);
                layoutAddress.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
            }
            else if(jsonObject.getString("Type").equals("ID") && jsonObject.getString("Country").equals("Indonesia"))
            {
                JSONObject icInfo = jsonObject.getJSONObject("IC-Info");
                text2IssuedPlace.setText(icInfo.getString("Detected-Issued-Place"));
                text2IDNo.setText(icInfo.getString("Detected-ID-No"));
                text2Name.setText(icInfo.getString("Detected-Name"));
                text2PlaceDateBirth.setText(icInfo.getString("Detected-Place-Date-Birth"));
                text2Gender.setText(icInfo.getString("Detected-Gender"));
                text2Nationality.setText(icInfo.getString("Detected-Nationality"));
                text2RT.setText(icInfo.getString("Detected-RT"));
                text2Desa.setText(icInfo.getString("Detected-Desa"));
                text2Kecamatan.setText(icInfo.getString("Detected-Kecamatan"));
                text2Address.setText(icInfo.getString("Detected-Address"));
                text2Religion.setText(icInfo.getString("Detected-Religion"));
                text2MaritalStatus.setText(icInfo.getString("Detected-Marital-Status"));
                text2Occupation.setText(icInfo.getString("Detected-Occupation"));
                textFaceMatchImageType.setText(jsonObject.getString("Type"));
                layoutExpiry.setVisibility(View.GONE);
                layoutAddress.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
            }
            else {
                textDOBCheck.setText(jsonObject.getString("Dob"));
                textNationalityCheck.setText(jsonObject.getString("Country"));
                textGenderCheck.setText(jsonObject.getString("Gender"));
                textIdExpiredCheck.setText(jsonObject.getString("expiry_check"));
                textExpiryCheck.setText(jsonObject.getString("Expiry"));
                textIdNoCheck.setText(jsonObject.getString("PassportNo"));
                textIcNoCheck.setText(jsonObject.getString("IcNo"));
                textNameCheck.setText(jsonObject.getString("Name"));
                textFaceMatchImageType.setText(jsonObject.getString("Type"));
                layoutExpiry.setVisibility(View.VISIBLE);
                layoutAddress.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
            }
            String verifiedOn = jsonObject.getString("verified_on");

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date formattedDatetime = format.parse(verifiedOn);
                verifiedOn = newFormat.format(formattedDatetime);
                textVerifiedOn.setText(verifiedOn);
            } catch (Exception e) {
            }


            File selfieFile = new File(Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath + "/" + preferences.getString(getString(R.string.param_kyc_selfie), ""));
            Glide.with(DIReviewVerificationResultActivity.this).load(selfieFile).into(imageSelfie);

            File idFile = new File(Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath + "/" + preferences.getString(getString(R.string.param_kyc_id_document), ""));
            Glide.with(DIReviewVerificationResultActivity.this).load(idFile).into(imageId);
            textOverallResult.setText(jsonObject.getString("overall_result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(DIReviewVerificationResultActivity.this, DIIdentityDocumentActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void upload(final String documentType)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        String token = preferences.getString(getResources().getString(R.string.param_oneid_token), "");

        String imagePath = "";
        if(documentType.equals("selfie")) {
            imagePath = Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath + "/" + preferences.getString(getString(R.string.param_di_selfie), "");
            progressDialog.setMessage(getString(R.string.uploading_selfie_image));
        }
        else if(documentType.equals("id")) {
            imagePath = Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath + "/" + preferences.getString(getString(R.string.param_di_id_document), "");
            progressDialog.setMessage(getString(R.string.uploading_id_document_image));
        }

        AsynRestClient.upload(token, documentType, imagePath, new AsyncHttpResponseHandler()
        {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
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
                        if(documentType.equals("id")) {
                            editor.putString(getResources().getString(R.string.param_oneid_id_image), fileName);
                            progressDialog.setMessage(getString(R.string.issuing_identity_credential));
                            createOneIdentity();
                        }
                        else if(documentType.equals("selfie"))
                        {
                            editor.putString(getResources().getString(R.string.param_oneid_selfie_image), fileName);
                            upload("id");
                        }
                        editor.apply();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                alert(getString(R.string.error), getString(R.string.error_please_try_again));
                progressDialog.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
            }
        });
    }

    private void createIdentityWallet(String name)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        String icNo = "";
        try {
            String jsonResponse = preferences.getString(getString(R.string.param_di_verification_result), "");
            JSONObject jsonObject = new JSONObject(jsonResponse);
            icNo = jsonObject.getString("IcNo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("wallet_name", name);
            jsonObject.put("passphrase", icNo);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        progressDialog.setMessage(getString(R.string.creating_identity_wallet));
        progressDialog.show();

        Log.e("Url", AsynRestClient.createIdentityWalletUrl);
        Log.e("Param", jsonObject.toString());
        AsynRestClient.genericPost(this, AsynRestClient.createIdentityWalletUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
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
                    editor.putString(getResources().getString(R.string.param_oneid_wallet_name), innerObject.getString("wallet_name"));
                    editor.putString(getResources().getString(R.string.param_oneid_wallet_address), innerObject.getString("wallet_id"));
                    editor.putString(getResources().getString(R.string.param_oneid_created_at), innerObject.getString("created_at"));
                    editor.putString(getResources().getString(R.string.param_oneid_endpoint_did), innerObject.getString("endpoint_did"));
                    editor.putString(getResources().getString(R.string.param_oneid_token), innerObject.getString("token"));
                    editor.apply();

                    upload("selfie");
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                alert(getString(R.string.error), getString(R.string.error_please_try_again));
                progressDialog.dismiss();
            }
        });
    }

    private void createOneIdentity()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        String dob = "";
        String country = "";
        String gender = "";
        String expiry = "";
        String passportNo = "";
        String icNo = "";
        String name = "";
        String issuedOn = "";
        try {
            String jsonResponse = preferences.getString(getString(R.string.param_di_verification_result), "");
            JSONObject jsonObject = new JSONObject(jsonResponse);
            dob = jsonObject.getString("Dob");
            country = jsonObject.getString("Country");
            gender = jsonObject.getString("Gender");
            expiry = jsonObject.getString("Expiry");
            passportNo = jsonObject.getString("PassportNo");
            icNo = jsonObject.getString("IcNo");
            String verifiedOn = jsonObject.getString("verified_on");

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date formattedDatetime = format.parse(verifiedOn);
                verifiedOn = newFormat.format(formattedDatetime);
                issuedOn = verifiedOn;
            } catch (Exception e) {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("name", name);
            jsonObject.put("id_no", passportNo);
            jsonObject.put("ic_no", icNo);
            jsonObject.put("expiry_date", expiry);
            jsonObject.put("gender", gender);
            jsonObject.put("nationality",country);
            jsonObject.put("dob", dob);
            jsonObject.put("issue_date", issuedOn);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.e("Json", jsonObject.toString());
        AsynRestClient.genericPost(DIReviewVerificationResultActivity.this, AsynRestClient.identityVerificationDemoUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                progressDialog.dismiss();
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

                    SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getResources().getString(R.string.param_oneid_credential), jsonObject.toString());
                    editor.apply();

                    Intent intent = new Intent(DIReviewVerificationResultActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                alert(getString(R.string.error), getString(R.string.error_please_try_again));
                progressDialog.dismiss();
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
