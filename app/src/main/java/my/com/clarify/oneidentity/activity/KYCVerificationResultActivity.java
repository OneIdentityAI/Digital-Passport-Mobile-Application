package my.com.clarify.oneidentity.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

public class KYCVerificationResultActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
        final SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_kyc_verification_result);


        imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
            Glide.with(KYCVerificationResultActivity.this).load(selfieFile).into(imageSelfie);

            File idFile = new File(Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath + "/" + preferences.getString(getString(R.string.param_kyc_id_document), ""));
            Glide.with(KYCVerificationResultActivity.this).load(idFile).into(imageId);
            textOverallResult.setText(jsonObject.getString("overall_result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(KYCVerificationResultActivity.this, KYCIdentityDocumentActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
