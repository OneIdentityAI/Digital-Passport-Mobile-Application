package my.com.clarify.oneidentity.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;
import cz.msebera.android.httpclient.Header;
import io.card.payment.CardIOActivity;

import static io.card.payment.CardIOActivity.RESULT_SCAN_SUPPRESSED;

public class DIIdentityDocumentActivity extends AppCompatActivity {
    public AppDelegate delegate;
    public RadioGroup rgIDDocumentType;
    public String documentType = "Passport";
    public LinearLayout layoutNationality;
    public EditText inputNationality;

    public AppCompatImageView imageUploadId;
    public AppCompatImageView imageBack;
    public Button buttonSubmit;
    public Button buttonVerificationResult;

    public Uri uri;
    public String imageName = "";
    public String imagePath = "";
    public String imageType = "";
    public ProgressDialog progressDialog;
    public static int PIX_REQUEST_CODE = 101;
    public static int MY_SCAN_REQUEST_CODE = 102;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
        setContentView(R.layout.activity_di_identity_document);

        delegate = (AppDelegate)getApplication();
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

        layoutNationality = findViewById(R.id.layout_nationality);
        layoutNationality.setVisibility(View.GONE);
        rgIDDocumentType = findViewById(R.id.radiogroup_id_document_type);
        rgIDDocumentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                layoutNationality.setVisibility(View.GONE);
                switch(checkedId){
                    case R.id.radio_document_type_passport:
                        documentType = "Passport";
                        break;
                    case R.id.radio_document_type_ic:
                        documentType = "IC";
                        layoutNationality.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        rgIDDocumentType.check(R.id.radio_document_type_passport);
        inputNationality = findViewById(R.id.input_nationality);
        inputNationality.setText("Malaysia");
        inputNationality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DIIdentityDocumentActivity.this);
                builder.setTitle(getString(R.string.choose_nationality));
                final String[] nameArray = {"Malaysia", "Indonesia"};
                builder.setItems(nameArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputNationality.setText(nameArray[which]);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        imageUploadId = findViewById(R.id.image_upload_id);
        imageUploadId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DIIdentityDocumentActivity.this);
                builder.setTitle(getString(R.string.choose_source));
                final ArrayList<String> nameList = new ArrayList<String>();
                if(!documentType.equals("IC"))
                {
                    nameList.add(getString(R.string.gallery));
                    nameList.add(getString(R.string.camera));
                    nameList.add(getString(R.string.document_scanner));
                }
                else
                {
                    nameList.add(getString(R.string.document_scanner));
                }
                builder.setItems(nameList.toArray(new String[nameList.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(nameList.get(which).equals(getString(R.string.gallery)))
                        {
                            FishBun.with(DIIdentityDocumentActivity.this).setImageAdapter(new GlideAdapter()).setMaxCount(1).setCamera(true).startAlbum();
                        }
                        else if(nameList.get(which).equals(getString(R.string.camera)))
                        {
                            Options options = Options.init()
                                    .setRequestCode(PIX_REQUEST_CODE)                                                 //Request code for activity results
                                    .setCount(1)                                                         //Number of images to restict selection count
                                    .setFrontfacing(false)                                                //Front Facing camera on start
                                    .setImageQuality(ImageQuality.HIGH)                                   //Pre selected Image Urls
                                    .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);           //Orientaion
                            Pix.start(DIIdentityDocumentActivity.this, options);
                        }
                        else
                        {
                            Intent scanIntent = new Intent(DIIdentityDocumentActivity.this, CardIOActivity.class);
                            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false); // default: false
                            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
                            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
                            scanIntent.putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO,true); // cambiar logo de paypal por el de card.io
                            scanIntent.putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE,true); // capture img
                            scanIntent.putExtra(CardIOActivity.EXTRA_CAPTURED_CARD_IMAGE,true); // capturar img
                            scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_SCAN,true); // supmit cuando termine de reconocer el documento
                            scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY,true); // esconder teclado
                            scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false);
                            startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            //}
        });
        buttonSubmit = findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectPassport();
            }
        });
        buttonSubmit.setVisibility(View.GONE);
        buttonVerificationResult = findViewById(R.id.button_verification_result);
        buttonVerificationResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DIIdentityDocumentActivity.this, DIReviewVerificationResultActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
        triggerButtonSubmit();
        triggerButtonVerificationResult();
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(DIIdentityDocumentActivity.this, DISelfieActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void triggerButtonVerificationResult()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        if(preferences.getString(getString(R.string.param_di_verification_result), "").equals("")) {
            buttonVerificationResult.setVisibility(View.GONE);
        }
        else
        {
            buttonVerificationResult.setVisibility(View.VISIBLE);
        }
    }

    public void triggerButtonSubmit()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        if(preferences.getString(getString(R.string.param_di_id_document), "").equals("")) {
            buttonSubmit.setVisibility(View.GONE);
        }
        else
        {
            File f = new File(Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath + "/" + preferences.getString(getString(R.string.param_di_id_document), ""));
            Glide.with(DIIdentityDocumentActivity.this).load(f).into(imageUploadId);
            buttonSubmit.setVisibility(View.VISIBLE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Define.ALBUM_REQUEST_CODE && resultCode == RESULT_OK)
        {
            ArrayList<Uri> imageUriList = data.getParcelableArrayListExtra(Define.INTENT_PATH);
            Log.e("Images", imageUriList.toString());
            if(imageUriList.size() > 0)
            {
                try {
                    Uri imageUri = imageUriList.get(0);
                    Cursor cursor = this.getContentResolver().query(imageUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                    cursor.moveToFirst();
                    imagePath = cursor.getString(0);
                    imagePath = AppDelegate.getResizedImagePathFromOriginalPath(DIIdentityDocumentActivity.this, imagePath, 1000, 1000);
                    cursor.close();
                    imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());

                    SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getString(R.string.param_di_id_document), imageName);
                    editor.apply();
                    triggerButtonSubmit();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == PIX_REQUEST_CODE) {
            ArrayList<String> imagePathList = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Log.e("returnValue", imagePathList.toString());
            if(imagePathList.size() > 0)
            {
                imagePath = imagePathList.get(0);
                imagePath = AppDelegate.getResizedImagePathFromOriginalPath(DIIdentityDocumentActivity.this, imagePath, 1000, 1000);
                imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());

                SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.param_di_id_document), imageName);
                editor.apply();
                triggerButtonSubmit();
            }
        }

        if (requestCode == MY_SCAN_REQUEST_CODE || requestCode== RESULT_SCAN_SUPPRESSED)
        {
            Bitmap card = CardIOActivity.getCapturedCardImage(data);
            String resizedFilePath = Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath + "/" + delegate.fileNameGenerator(DIIdentityDocumentActivity.this) + ".jpg";
            File file = new File(resizedFilePath);
            FileOutputStream fOut;
            try
            {
                fOut = new FileOutputStream(file);
                card.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
            }
            catch (Exception e)
            { // TODO
                e.printStackTrace();
                //scaledBitmap.recycle();
            }
            if(card != null)
                card.recycle();

            Log.e("path", imagePath);
            imagePath = resizedFilePath;
            imagePath = AppDelegate.getResizedImagePathFromOriginalPath(DIIdentityDocumentActivity.this, imagePath, 970, 970);
            imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());

            SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.param_di_id_document), imageName);
            editor.apply();
            triggerButtonSubmit();
        }
    }

    public void detectPassport()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        String selfiePath = Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath + "/" + preferences.getString(getString(R.string.param_di_selfie), "");
        String idDocumentPath = Environment.getExternalStorageDirectory().toString() + AppDelegate.appPath + "/" + preferences.getString(getString(R.string.param_di_id_document), "");
        Log.e("Selfie", selfiePath);
        Log.e("Document", idDocumentPath);
        AsynRestClient.detectPassport(selfiePath, idDocumentPath, new AsyncHttpResponseHandler()
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
                Log.e("Response", responseString);

                try {
                    JSONObject jsonObject = new JSONObject(responseString);

                    JSONObject facedataObject = jsonObject.getJSONObject("FaceData");
                    if(!facedataObject.has("confidence")) {
                        alert(getString(R.string.error), getString(R.string.error_no_face_detected));
                        return;
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.param_di_verification_result), responseString);
                editor.apply();

                Intent intent = new Intent(DIIdentityDocumentActivity.this, DIReviewVerificationResultActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                triggerButtonVerificationResult();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                //String responseString = new String(errorResponse);
                //Log.e("Upload response", responseString);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                int progress = Math.round((bytesWritten/totalSize)*100);
                progressDialog.setProgress(progress);
                progressDialog.setMax(100);
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
