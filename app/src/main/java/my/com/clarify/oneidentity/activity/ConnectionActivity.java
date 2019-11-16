package my.com.clarify.oneidentity.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.client.android.CaptureActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.adapter.ConnectionAdapter;
import my.com.clarify.oneidentity.adapter.CredentialOfferAdapter;
import my.com.clarify.oneidentity.adapter.ProofAdapter;
import my.com.clarify.oneidentity.adapter.ProofOfferAdapter;
import my.com.clarify.oneidentity.adapter.ProofRequestAdapter;
import my.com.clarify.oneidentity.adapter.ReceiveCredentialAdapter;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;
import cz.msebera.android.httpclient.Header;

public class ConnectionActivity extends AppCompatActivity {
    public AppDelegate delegate;
    public BottomNavigationView bottomNavigationView;
    public AppCompatImageView imageQRCode;
    public AppCompatImageView imageScan;
    public AppCompatImageView imageSignIn;
    public AppCompatImageView imageDelete;
    public AppCompatImageView imageRefresh;
    public CardView cardEndpointDID;
    public CardView cardConnectionRequest;
    public TextView textConnectionRequestCount;
    public CardView cardConnectionAcknowledgement;
    public TextView textConnectionAcknowledgementCount;

    public ConnectionAdapter adapter;
    public RecyclerView recyclerView;
    public ArrayList<String> nameList = new ArrayList<String>();
    public ArrayList<String> myDIDList = new ArrayList<String>();
    public ArrayList<String> theirDIDList = new ArrayList<String>();
    public ArrayList<String> theirEndpointDIDList = new ArrayList<String>();
    public ArrayList<String> theirEndpointVerkeyList = new ArrayList<String>();
    public ArrayList<String> firstNameList = new ArrayList<String>();
    public ArrayList<String> lastNameList = new ArrayList<String>();
    public ArrayList<String> companyNameList = new ArrayList<String>();
    public ArrayList<String> walletNameList = new ArrayList<String>();
    public ArrayList<Integer> totalCredentialOfferList = new ArrayList<Integer>();
    public ArrayList<ArrayList<String>> credentialOfferList = new ArrayList<ArrayList<String>>();
    public ArrayList<ArrayList<String>> credentialOfferMessageIdList = new ArrayList<ArrayList<String>>();
    public ArrayList<Integer> totalReceiveCredentialList = new ArrayList<Integer>();
    public ArrayList<ArrayList<String>> receiveCredentialList = new ArrayList<ArrayList<String>>();
    public ArrayList<ArrayList<String>> receiveCredentialMessageIdList = new ArrayList<ArrayList<String>>();
    public ArrayList<Integer> totalProofRequestList = new ArrayList<Integer>();
    public ArrayList<ArrayList<String>> proofRequestList = new ArrayList<ArrayList<String>>();
    public ArrayList<ArrayList<String>> proofRequestMessageIdList = new ArrayList<ArrayList<String>>();

    public ArrayList<Integer> totalProofOfferList = new ArrayList<Integer>();
    public ArrayList<ArrayList<String>> proofOfferList = new ArrayList<ArrayList<String>>();
    public ArrayList<ArrayList<String>> proofOfferMessageIdList = new ArrayList<ArrayList<String>>();

    public ArrayList<String> messageidList = new ArrayList<String>();
    public ArrayList<String> messageList = new ArrayList<String>();
    public ArrayList<String> typeList = new ArrayList<String>();
    public ArrayList<String> createdList = new ArrayList<String>();

    public ArrayList<JSONObject> proofList = new ArrayList<JSONObject>();
    public ProgressDialog progressDialog;


    public Handler handler = new Handler();
    public int delay = 10000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
        setContentView(R.layout.activity_connection);
        delegate = (AppDelegate)getApplication();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ConnectionAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
                Intent intent = new Intent(ConnectionActivity.this, MyEndpointDIDActivity.class);
                startActivity(intent);
            }
        });

        cardConnectionRequest = findViewById(R.id.card_connection_request);
        cardConnectionRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectionActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });
        cardConnectionRequest.setVisibility(View.GONE);
        textConnectionRequestCount = findViewById(R.id.text_connection_request_count);

        cardConnectionAcknowledgement = findViewById(R.id.card_connection_acknowledgement);
        cardConnectionAcknowledgement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectionActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });
        cardConnectionAcknowledgement.setVisibility(View.GONE);
        textConnectionAcknowledgementCount = findViewById(R.id.text_connection_acknowledgement_count);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.action_message) {
                    Intent intent = new Intent(ConnectionActivity.this, MessageActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else if(item.getItemId() == R.id.action_credential) {
                    Intent intent = new Intent(ConnectionActivity.this, CredentialActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else if(item.getItemId() == R.id.action_did) {
                    Intent intent = new Intent(ConnectionActivity.this, DIDActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else if(item.getItemId() == R.id.action_proof) {
                    Intent intent = new Intent(ConnectionActivity.this, ProofActivity.class);
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
                Intent intent = new Intent(ConnectionActivity.this, CaptureActivity.class);
                intent.setAction("com.google.zxing.client.android.SCAN");
                intent.putExtra("SAVE_HISTORY", false);
                startActivityForResult(intent, AppDelegate.BARCODE_REQUEST_CODE);
            }
        });
        imageSignIn = findViewById(R.id.image_sign_in);
        imageSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectionActivity.this, CaptureActivity.class);
                intent.setAction("com.google.zxing.client.android.SCAN");
                intent.putExtra("SAVE_HISTORY", false);
                startActivityForResult(intent, AppDelegate.SIGN_IN_REQUEST_CODE);
            }
        });
        imageDelete = findViewById(R.id.image_delete);
        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionActivity.this);
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
        imageRefresh = findViewById(R.id.image_refresh);
        imageRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.shouldRefreshConnectionList = true;
                retrieveConnection();
            }
        });
        //listProof();
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(ConnectionActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public static void showBadge(Context context, BottomNavigationView
            bottomNavigationView, @IdRes int itemId, String value) {
        removeBadge(bottomNavigationView, itemId);
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        View badge = LayoutInflater.from(context).inflate(R.layout.layout_news_badge, bottomNavigationView, false);

        TextView text = badge.findViewById(R.id.badge_text_view);
        text.setText(value);
        itemView.addView(badge);
    }

    public static void removeBadge(BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        if (itemView.getChildCount() == 3) {
            itemView.removeViewAt(2);
        }
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

    public void checkDID(final String endpointDID)
    {
        if(theirEndpointDIDList.size() == 0 || !theirEndpointDIDList.contains(endpointDID))
        {
            final EditText edittext = new EditText(ConnectionActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionActivity.this);
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
                        addDid(edittext.getText().toString(), endpointDID);
                        dialog.dismiss();
                    }
                    else
                        edittext.setError(getString(R.string.error_value_cannot_be_empty));
                }
            });
        }
        else {
            alert(getString(R.string.connection_existed), getString(R.string.connection_existed_description));
        }
    }

    private void addDid(String didName, final String endpointDID)
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

                    JSONObject jsonInnerObject = jsonObject.getJSONObject("data");
                    String DID = jsonInnerObject.getString("did");
                    createConnectionRequest(endpointDID, DID);
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

    private void createConnectionRequest(String endpointDID, String DID)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("sender_did", DID);
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

    private void retrieveConnection()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        if(preferences.getString(getString(R.string.param_connection_json), "").equals(""))
            delegate.shouldRefreshConnectionList = true;

        if(delegate.shouldRefreshConnectionList)
        {
            delegate.shouldRefreshConnectionList = false;

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            AsynRestClient.genericPost(this, AsynRestClient.listConnectionUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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

                    SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getString(R.string.param_connection_json), responseString);
                    editor.apply();

                    processConnectionResponse();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    alert(getString(R.string.error), getString(R.string.error_please_try_again));
                    Log.e("Helo", "Hello");
                }
            });
        }
        else
            processConnectionResponse();
    }

    public void processConnectionResponse()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        try {
            JSONObject jsonObject = new JSONObject(preferences.getString(getString(R.string.param_connection_json), ""));
            if(jsonObject.has("error"))
            {
                JSONObject errorObject = jsonObject.getJSONObject("error");
                alert(errorObject.getString("type"), errorObject.getString("message"));
                return;
            }
            nameList.clear();
            myDIDList.clear();
            theirDIDList.clear();
            theirEndpointDIDList.clear();
            theirEndpointVerkeyList.clear();
            firstNameList.clear();
            lastNameList.clear();
            companyNameList.clear();
            walletNameList.clear();
            totalCredentialOfferList.clear();
            credentialOfferList.clear();
            credentialOfferMessageIdList.clear();
            totalReceiveCredentialList.clear();
            receiveCredentialList.clear();
            receiveCredentialMessageIdList.clear();
            totalProofRequestList.clear();
            proofRequestList.clear();
            proofRequestMessageIdList.clear();
            totalProofOfferList.clear();
            proofOfferList.clear();
            proofOfferMessageIdList.clear();

            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject innerObject = (JSONObject) jsonArray.get(i);
                nameList.add(innerObject.getString("name"));
                myDIDList.add(innerObject.getString("my_did"));
                theirDIDList.add(innerObject.getString("their_did"));
                theirEndpointDIDList.add(innerObject.getString("their_endpoint_did"));
                theirEndpointVerkeyList.add(innerObject.getString("their_endpoint_verkey"));

                String firstName = "";
                String lastName = "";
                String companyName = "";
                String walletName = "";
                for(int j = 0; j < proofList.size(); j++)
                {
                    JSONObject proofObject = proofList.get(j);
                    if(proofObject.getString("sender_did").equals(innerObject.getString("their_did")))
                    {
                        firstName = proofObject.getJSONArray("proof_offer").getJSONObject(0).getString("attribute_value");
                        lastName = proofObject.getJSONArray("proof_offer").getJSONObject(1).getString("attribute_value");
                        companyName = proofObject.getJSONArray("proof_offer").getJSONObject(2).getString("attribute_value");
                        walletName = proofObject.getJSONArray("proof_offer").getJSONObject(3).getString("attribute_value");
                    }
                }
                firstNameList.add(firstName);
                lastNameList.add(lastName);
                companyNameList.add(companyName);
                walletNameList.add(walletName);
                totalCredentialOfferList.add(0);
                credentialOfferList.add(new ArrayList<String>());
                credentialOfferMessageIdList.add(new ArrayList<String>());
                totalReceiveCredentialList.add(0);
                receiveCredentialList.add(new ArrayList<String>());
                receiveCredentialMessageIdList.add(new ArrayList<String>());
                totalProofRequestList.add(0);
                proofRequestList.add(new ArrayList<String>());
                proofRequestMessageIdList.add(new ArrayList<String>());
                totalProofOfferList.add(0);
                proofOfferList.add(new ArrayList<String>());
                proofOfferMessageIdList.add(new ArrayList<String>());
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        retrieveMessage();
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

    public void onResume()
    {
        super.onResume();

        retrieveConnection();
    }

    public void onPause()
    {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    public void alertItemMenu(final int position)
    {
        final ArrayList<String> actionList = new ArrayList<String>();
        if(totalCredentialOfferList.get(position) > 0)
            actionList.add("View Credential Offer");
        if(totalReceiveCredentialList.get(position) > 0)
            actionList.add("View Credential Issuance");
        if(totalProofRequestList.get(position) > 0)
            actionList.add("View Proof Request");
        if(totalProofOfferList.get(position) > 0)
            actionList.add("View Proof Offer");
        actionList.add("Send Message");

        CharSequence action[] = actionList.toArray(new String[actionList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Action");
        builder.setItems(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(actionList.get(which).equals("Send Message"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionActivity.this);
                    builder.setTitle("Send Message");
                    builder.setMessage("Send an encrypted plain text message to this connection");
                    final EditText input = new EditText(ConnectionActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendMessage(position, input.getText().toString());
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
                else if(actionList.get(which).equals("View Credential Offer"))
                {
                    alertCredentialOffer(position);
                }
                else if(actionList.get(which).equals("View Credential Issuance"))
                {
                    alertReceiveCredential(position);
                }
                else if(actionList.get(which).equals("View Proof Request"))
                {
                    alertProofRequest(position);
                }
                else if(actionList.get(which).equals("View Proof Offer"))
                {
                    alertProofOffer(position);
                }
            }
        });
        builder.show();
    }

    private void sendMessage(int postion, String message)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("recipient_did", theirDIDList.get(postion));
            jsonObject.put("message_content", message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.sendMessageUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    alert(getString(R.string.success), jsonObject.getString("message"));
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

    private void retrieveMessage()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("type", "");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.listMessageUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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

                SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.param_message_json), responseString);
                editor.apply();

                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    if(jsonObject.has("error"))
                    {
                        JSONObject errorObject = jsonObject.getJSONObject("error");
                        //alert(errorObject.getString("type"), errorObject.getString("message"));
                        return;
                    }
                    messageidList.clear();
                    messageList.clear();
                    typeList.clear();
                    createdList.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));

                    int totalConnectionRequest = 0;
                    int totalConnectionAcknowledgement = 0;
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject innerObject = (JSONObject) jsonArray.get(i);
                        messageidList.add(innerObject.getString("message_id"));
                        messageList.add(innerObject.getString("message"));
                        typeList.add(innerObject.getString("type"));
                        if(innerObject.getString("type").equals("MSG_TYPE_CONN_REQUEST"))
                            totalConnectionRequest++;
                        if(innerObject.getString("type").equals("MSG_TYPE_CONN_ACCEPT"))
                            totalConnectionAcknowledgement++;
                        try
                        {
                            Date formattedDatetime = format.parse(innerObject.getString("created_at"));
                            createdList.add(newFormat.format(formattedDatetime));
                        }
                        catch (Exception e)
                        {
                            createdList.add(innerObject.getString("created_at"));
                        }
                    }
                    if(totalConnectionRequest > 0)
                    {
                        cardConnectionRequest.setVisibility(View.VISIBLE);
                        textConnectionRequestCount.setText(totalConnectionRequest + "");
                    }
                    else
                    {
                        cardConnectionRequest.setVisibility(View.GONE);
                    }
                    if(totalConnectionAcknowledgement > 0)
                    {
                        cardConnectionAcknowledgement.setVisibility(View.VISIBLE);
                        textConnectionAcknowledgementCount.setText(totalConnectionAcknowledgement + "");
                    }
                    else
                    {
                        cardConnectionAcknowledgement.setVisibility(View.GONE);
                    }
                    if(messageidList.size() > 0)
                    {
                        showBadge(ConnectionActivity.this, bottomNavigationView, R.id.action_message, messageidList.size() + "");
                    }
                    else
                    {
                        removeBadge(bottomNavigationView, R.id.action_message);
                    }

                    for(int i = 0; i < theirDIDList.size(); i++)
                    {
                        int value1 = 0;
                        int value2 = 0;
                        int value3 = 0;
                        int value4 = 0;
                        for(int j = 0; j < messageidList.size(); j++)
                        {
                            //Process credential offer request
                            if(typeList.get(j).equals("MSG_TYPE_CREDENTIAL_OFFER"))
                            {
                                JSONObject message = new JSONObject(messageList.get(j));
                                String senderDid = message.getString("sender_did");
                                if(senderDid.equals(theirDIDList.get(i)))
                                {
                                    value1++;
                                    credentialOfferList.get(i).add(messageList.get(j));
                                    credentialOfferMessageIdList.get(i).add(messageidList.get(j));
                                }
                            }
                            //Process credential offer request
                            if(typeList.get(j).equals("MSG_TYPE_CREDENTIAL_ACCEPT_REQUEST"))
                            {
                                JSONObject message = new JSONObject(messageList.get(j));
                                String senderDid = message.getString("sender_did");
                                if(senderDid.equals(theirDIDList.get(i)))
                                {
                                    value2++;
                                    receiveCredentialList.get(i).add(messageList.get(j));
                                    receiveCredentialMessageIdList.get(i).add(messageidList.get(j));
                                }
                            }
                            //Process proof request
                            if(typeList.get(j).equals("MSG_TYPE_PROOF_REQUEST"))
                            {
                                JSONObject message = new JSONObject(messageList.get(j));
                                String senderDid = message.getString("sender_did");
                                if(senderDid.equals(theirDIDList.get(i)))
                                {
                                    value3++;
                                    proofRequestList.get(i).add(messageList.get(j));
                                    proofRequestMessageIdList.get(i).add(messageidList.get(j));
                                }
                            }
                            //Process proof offer
                            if(typeList.get(j).equals("MSG_TYPE_PROOF_OFFER"))
                            {
                                JSONObject message = new JSONObject(messageList.get(j));
                                String senderDid = message.getString("sender_did");
                                if(senderDid.equals(theirDIDList.get(i)))
                                {
                                    value4++;
                                    proofOfferList.get(i).add(messageList.get(j));
                                    proofOfferMessageIdList.get(i).add(messageidList.get(j));
                                }
                            }
                        }
                        totalCredentialOfferList.set(i, value1);
                        totalReceiveCredentialList.set(i, value2);
                        totalProofRequestList.set(i, value3);
                        totalProofOfferList.set(i, value4);
                    }
                    adapter.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                handler.postDelayed(new Runnable(){
                    public void run(){
                        retrieveConnection();
                    }
                }, delay);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //alert(getString(R.string.error), getString(R.string.error_please_try_again));
                Log.e("Helo", "Hello");
            }
        });
    }

    private void listProof()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        progressDialog.setMessage(getString(R.string.please_wait));
        AsynRestClient.genericPost(this, AsynRestClient.listProofUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    proofList.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject innerObject = (JSONObject) jsonArray.get(i);
                        proofList.add(innerObject);
                    }
                    retrieveConnection();
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

    public Dialog dialogCredentialOffer;

    public CredentialOfferAdapter coAdapter;
    public RecyclerView coRecyclerView;
    public void alertCredentialOffer(int position)
    {
        dialogCredentialOffer = new Dialog(ConnectionActivity.this);
        dialogCredentialOffer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCredentialOffer.setContentView(R.layout.popup_credential_offer_list);
        dialogCredentialOffer.setCanceledOnTouchOutside(true);

        Window window = dialogCredentialOffer.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        coRecyclerView = dialogCredentialOffer.findViewById(R.id.recycler_view);
        coRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        coAdapter = new CredentialOfferAdapter(this, position);
        coRecyclerView.setAdapter(coAdapter);
        coAdapter.notifyDataSetChanged();

        ImageView buttonBack = dialogCredentialOffer.findViewById(R.id.image_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCredentialOffer.dismiss();
            }
        });

        dialogCredentialOffer.show();
    }

    public Dialog dialogReceiveCredential;
    public ReceiveCredentialAdapter rcAdapter;
    public RecyclerView rcRecyclerView;
    public void alertReceiveCredential(int position)
    {
        dialogReceiveCredential = new Dialog(ConnectionActivity.this);
        dialogReceiveCredential.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogReceiveCredential.setContentView(R.layout.popup_receive_credential);
        dialogReceiveCredential.setCanceledOnTouchOutside(true);

        Window window = dialogReceiveCredential.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        rcRecyclerView = dialogReceiveCredential.findViewById(R.id.recycler_view);
        rcRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcAdapter = new ReceiveCredentialAdapter(this, position);
        rcRecyclerView.setAdapter(rcAdapter);
        rcAdapter.notifyDataSetChanged();

        ImageView buttonBack = dialogReceiveCredential.findViewById(R.id.image_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogReceiveCredential.dismiss();
            }
        });

        dialogReceiveCredential.show();
    }

    public Dialog dialogProofOffer;
    public ProofOfferAdapter poAdapter;
    public RecyclerView poRecyclerView;
    public void alertProofOffer(int position)
    {
        dialogProofOffer = new Dialog(ConnectionActivity.this);
        dialogProofOffer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogProofOffer.setContentView(R.layout.popup_proof_offer);
        dialogProofOffer.setCanceledOnTouchOutside(true);

        Window window = dialogProofOffer.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        poRecyclerView = dialogProofOffer.findViewById(R.id.recycler_view);
        poRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        poAdapter = new ProofOfferAdapter(this, position);
        poRecyclerView.setAdapter(poAdapter);
        poAdapter.notifyDataSetChanged();

        ImageView buttonBack = dialogProofOffer.findViewById(R.id.image_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogProofOffer.dismiss();
            }
        });

        dialogProofOffer.show();
    }

    public Dialog dialogProofRequest;
    public ProofRequestAdapter prAdapter;
    public RecyclerView prRecyclerView;
    public void alertProofRequest(int position)
    {
        dialogProofRequest = new Dialog(ConnectionActivity.this);
        dialogProofRequest.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogProofRequest.setContentView(R.layout.popup_proof_request);
        dialogProofRequest.setCanceledOnTouchOutside(true);

        Window window = dialogProofRequest.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        prRecyclerView = dialogProofRequest.findViewById(R.id.recycler_view);
        prRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        prAdapter = new ProofRequestAdapter(this, position);
        prRecyclerView.setAdapter(prAdapter);
        prAdapter.notifyDataSetChanged();

        ImageView buttonBack = dialogProofRequest.findViewById(R.id.image_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogProofRequest.dismiss();
            }
        });

        dialogProofRequest.show();
    }

    public void alertSubItemMenu(final int type, final int position1, final int position2)
    {
        final ArrayList<String> actionList = new ArrayList<String>();
        if(type == 1)
            actionList.add("Accept Offer");
        if(type == 2)
            actionList.add("Receive Credential");
        if(type == 3)
            actionList.add("Create Proof Offer");
        if(type == 4)
            actionList.add("Verify and Accept Proof Offer");

        CharSequence action[] = actionList.toArray(new String[actionList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Action");
        builder.setItems(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(actionList.get(which).equals("Accept Offer"))
                {
                    dialogCredentialOffer.dismiss();
                    acceptOffer(position1, position2);
                }
                if(actionList.get(which).equals("Receive Credential"))
                {
                    dialogReceiveCredential.dismiss();
                    receiveCredential(position1, position2);
                }
                if(actionList.get(which).equals("Create Proof Offer"))
                {
                    if(position1 <= proofRequestMessageIdList.size() && position2 <= proofRequestMessageIdList.get(position1).size()) {
                        dialogProofRequest.dismiss();
                        Intent intent = new Intent(ConnectionActivity.this, CreateProofOfferActivity.class);
                        intent.putExtra("message_id", proofRequestMessageIdList.get(position1).get(position2));
                        intent.putExtra("data", proofRequestList.get(position1).get(position2));
                        intent.putExtra("sender_did", myDIDList.get(position1));
                        startActivity(intent);
                    }
                }
                if(actionList.get(which).equals("Verify and Accept Proof Offer"))
                {
                    dialogProofOffer.dismiss();
                    verifyAndAcceptProofOffer(position1, position2);
                }
            }
        });
        builder.show();
    }

    public void acceptOffer(final int position1, final int position2)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("sender_did", myDIDList.get(position1));
            jsonObject.put("message_id", credentialOfferMessageIdList.get(position1).get(position2));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(ConnectionActivity.this, AsynRestClient.acceptCredentialOfferUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                        //alert(errorObject.getString("type"), errorObject.getString("message"));
                        return;
                    }
                    alert(getString(R.string.success), jsonObject.getString("message"));
                    retrieveConnection();
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

    public void verifyAndAcceptProofOffer(final int position1, final int position2)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("sender_did", myDIDList.get(position1));
            jsonObject.put("message_id", proofOfferMessageIdList.get(position1).get(position2));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(ConnectionActivity.this, AsynRestClient.verifyAndAcceptProofOfferUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    alert(getString(R.string.success), jsonObject.getString("message"));
                    listProof();
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

    public void receiveCredential(final int position1, final int position2)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("sender_did", myDIDList.get(position1));
            jsonObject.put("message_id", receiveCredentialMessageIdList.get(position1).get(position2));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(ConnectionActivity.this, AsynRestClient.receiveCredentialUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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

                    alert(getString(R.string.success), jsonObject.getString("message"));
                    retrieveConnection();
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
}