package my.com.clarify.oneidentity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import my.com.clarify.oneidentity.adapter.ProofAdapter;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;
import cz.msebera.android.httpclient.Header;

public class ProofActivity extends AppCompatActivity {
    public AppCompatImageView imageBack;
    public AppCompatImageView imageAdd;

    public ProofAdapter adapter;
    public RecyclerView recyclerView;
    public ProgressDialog progressDialog;
    public ArrayList<String> proofNameList = new ArrayList<String>();
    public ArrayList<String> versionList = new ArrayList<String>();
    public ArrayList<String> createdAtList = new ArrayList<String>();
    public ArrayList<ArrayList<String>> credDefIdList = new  ArrayList<ArrayList<String>>();
    public ArrayList<ArrayList<String>> fieldList = new  ArrayList<ArrayList<String>>();
    public ArrayList<ArrayList<String>> valueList = new  ArrayList<ArrayList<String>>();


    public ArrayList<String> nameList = new ArrayList<String>();
    public ArrayList<String> myDIDList = new ArrayList<String>();
    public ArrayList<String> theirDIDList = new ArrayList<String>();
    public ArrayList<String> theirEndpointDIDList = new ArrayList<String>();
    public ArrayList<String> theirEndpointVerkeyList = new ArrayList<String>();
    public ArrayList<String> usernameList = new ArrayList<String>();
    public ArrayList<String> walletNameList = new ArrayList<String>();
    public ArrayList<JSONObject> proofList = new ArrayList<JSONObject>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
        setContentView(R.layout.activity_proof);

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

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ProofAdapter(this);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_divider));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //recyclerView.addItemDecoration(itemDecorator);
        adapter.notifyDataSetChanged();
        listProof();
    }

    private void retrieveConnectionData()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        String responseString = preferences.getString(getString(R.string.param_connection_json), "");
        Log.e("Connection response", responseString);
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            if(!jsonObject.getString("status").equals("200"))
            {
                alert(getString(R.string.error), jsonObject.getString("message"));
            }
            else
            {
                nameList.clear();
                myDIDList.clear();
                theirDIDList.clear();
                theirEndpointDIDList.clear();
                theirEndpointVerkeyList.clear();
                usernameList.clear();
                walletNameList.clear();

                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for(int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject innerObject = (JSONObject) jsonArray.get(i);
                    nameList.add(innerObject.getString("name"));
                    myDIDList.add(innerObject.getString("my_did"));
                    theirDIDList.add(innerObject.getString("their_did"));
                    theirEndpointDIDList.add(innerObject.getString("their_endpoint_did"));
                    theirEndpointVerkeyList.add(innerObject.getString("their_endpoint_verkey"));
                    String userName = "";
                    String walletName = "";
                    for(int j = 0; j < proofList.size(); j++)
                    {
                        JSONObject proofObject = proofList.get(j);
                        if(proofObject.getString("sender_did").equals(innerObject.getString("their_did")))
                        {
                            userName = proofObject.getJSONArray("proof_offer").getJSONObject(0).getString("attribute_value");
                            walletName = proofObject.getJSONArray("proof_offer").getJSONObject(1).getString("attribute_value");
                        }
                    }
                    usernameList.add(userName);
                    walletNameList.add(walletName);
                }
                listProof();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
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

                    proofNameList.clear();
                    versionList.clear();
                    createdAtList.clear();
                    credDefIdList.clear();
                    fieldList.clear();
                    valueList.clear();

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject innerObject = (JSONObject) jsonArray.get(i);
                        JSONObject proofRequest = innerObject.getJSONObject("proof_request");
                        proofNameList.add(proofRequest.getString("name"));
                        versionList.add(proofRequest.getString("version"));

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        format.setTimeZone(TimeZone.getTimeZone("UTC"));

                        try
                        {
                            Date formattedDatetime = format.parse(innerObject.getString("created_at"));
                            createdAtList.add(newFormat.format(formattedDatetime));
                        }
                        catch (Exception e)
                        {
                            createdAtList.add(innerObject.getString("created_at"));
                        }
                        credDefIdList.add(new ArrayList<String>());
                        fieldList.add(new ArrayList<String>());
                        valueList.add(new ArrayList<String>());
                    }
                    adapter.notifyDataSetChanged();
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

    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}