package my.com.clarify.oneidentity.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;

public class ProfileActivity extends AppCompatActivity {
    public AppCompatImageView imageBack;
    public LinearLayout layoutWallet;
    public TextView textWallet;
    public LinearLayout layoutName;
    public TextView textName;
    public LinearLayout layoutAddress;
    public TextView textAddress;
    public LinearLayout layoutPhone;
    public TextView textPhone;
    public LinearLayout layoutDob;
    public TextView textDob;
    public LinearLayout layoutPassport;
    public TextView textPassport;
    public LinearLayout layoutSelfie;
    public TextView textSelfie;
    public LinearLayout layoutProof;
    public TextView textProof;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layoutWallet = findViewById(R.id.layout_wallet);
        layoutWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ProfileWalletActivity.class);
                startActivity(i);
            }
        });
        layoutName = findViewById(R.id.layout_name);
        layoutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ProfileMyIdentityActivity.class);
                startActivity(i);
            }
        });
        layoutAddress = findViewById(R.id.layout_address);
        layoutAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ProfileAddressActivity.class);
                startActivity(i);
            }
        });
        layoutPhone = findViewById(R.id.layout_phone);
        layoutPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ProfilePhoneActivity.class);
                startActivity(i);
            }
        });
        layoutDob = findViewById(R.id.layout_dob);
        layoutDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ProfileDobActivity.class);
                startActivity(i);
            }
        });
        layoutPassport = findViewById(R.id.layout_passport);
        layoutPassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ProfilePassportActivity.class);
                startActivity(i);
            }
        });
        layoutSelfie = findViewById(R.id.layout_selfie);
        layoutSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ProfileSelfieActivity.class);
                startActivity(i);
            }
        });
        layoutProof = findViewById(R.id.layout_proof);
        layoutProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ProfileProofActivity.class);
                startActivity(i);
            }
        });
        textWallet = findViewById(R.id.text_wallet);
        textName = findViewById(R.id.text_name);
        textAddress = findViewById(R.id.text_address);
        textPhone = findViewById(R.id.text_phone);
        textDob = findViewById(R.id.text_dob);
        textPassport = findViewById(R.id.text_passport);
        textSelfie = findViewById(R.id.text_selfie);
        textProof = findViewById(R.id.text_proof);
    }

    public void onResume()
    {
        super.onResume();;
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag,
                Context.MODE_PRIVATE);
        String walletName = preferences.getString(getResources().getString(R.string.param_wallet_name), "");
        String mobile = preferences.getString(getResources().getString(R.string.param_mobile), "");
        String dob = preferences.getString(getResources().getString(R.string.param_dob), "");
        String firstName = preferences.getString(getResources().getString(R.string.param_first_name), "");
        String lastName = preferences.getString(getResources().getString(R.string.param_last_name), "");
        String address1 = preferences.getString(getResources().getString(R.string.param_address1), "");
        String passport = preferences.getString(getResources().getString(R.string.param_passport), "");
        String selfie = preferences.getString(getResources().getString(R.string.param_selfie), "");
        String proof = preferences.getString(getResources().getString(R.string.param_address_image), "");
        textName.setText(lastName + " " + firstName);
        textWallet.setText(walletName);
        textAddress.setText(address1);
        textPhone.setText(mobile);
        textDob.setText(dob);
        textPassport.setText(passport.equals("")?"Not Updated":"Updated");
        textSelfie.setText(selfie.equals("")?"Not Updated":"Updated");
        textProof.setText(proof.equals("")?"Not Updated":"Updated");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}