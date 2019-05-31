package my.com.clarify.oneidentity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.adapter.CreateProofOfferAdapter;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;

public class CreateProofOfferActivity extends AppCompatActivity {
    public AppCompatImageView imageBack;

    public CreateProofOfferAdapter adapter;
    public RecyclerView recyclerView;
    public TextView textNoData;
    public ArrayList<String> attributeNameList = new ArrayList<String>();
    public ArrayList<String> attributeSchemaNameList = new ArrayList<String>();
    public ArrayList<String> attributeSchemaVerList = new ArrayList<String>();
    public ArrayList<String> attributeValueList = new ArrayList<String>();
    public ArrayList<String> attributeReferenceList = new ArrayList<String>();

    public ArrayList<String> credDefIdList = new ArrayList<String>();
    public ArrayList<String> schemaIdList = new ArrayList<String>();
    public ArrayList<String> referrantList = new ArrayList<String>();
    public ArrayList<String> contentList = new ArrayList<String>();
    public ProgressDialog progressDialog;

    public String senderDid = "";
    public String messageId = "";
    public String data = "";
    public ImageView imageCreate;
    public boolean isCreate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_proof_offer);

        senderDid = getIntent().getExtras().getString("sender_did");
        messageId = getIntent().getExtras().getString("message_id");
        data = getIntent().getExtras().getString("data");
        Log.e("Data", data);

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

        imageCreate = findViewById(R.id.image_create);
        imageCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAnyEmpty = false;

                for(int i=0;i< attributeNameList.size(); i++){
                    View view= recyclerView.getChildAt(i);
                    EditText editText = view.findViewById(R.id.input_value);
                    String value = editText.getText().toString();
                    if(value.equals(""))
                    {
                        isAnyEmpty = true;
                    }
                }

                if(isAnyEmpty)
                {
                    alert("Error", "Self-attested attribute cannot be blank");
                }
                else
                {
                    isCreate = true;
                    createProofOffer();
                }
            }
        });

        textNoData = findViewById(R.id.text_no_data);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CreateProofOfferAdapter(this);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_divider));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //recyclerView.addItemDecoration(itemDecorator);
        adapter.notifyDataSetChanged();
        retrieveData();
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
                        if(isCreate)
                        {
                             finish();
                        }
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

    private void retrieveData()
    {
        final SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.listCredentialUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    JSONArray json1Array = jsonObject.getJSONArray("data");
                    for(int i = 0; i < json1Array.length(); i++)
                    {
                        JSONObject innerObject = (JSONObject) json1Array.get(i);
                        credDefIdList.add(innerObject.getString("cred_def_id"));
                        schemaIdList.add(innerObject.getString("schema_id"));
                        referrantList.add(innerObject.getString("referent"));
                        contentList.add(innerObject.getJSONObject("attrs").toString());
                    }

                    try
                    {
                        JSONObject data2 = new JSONObject(data);
                        JSONObject proofReq = data2.getJSONObject("message").getJSONObject("proof_req");
                        JSONArray mapping = data2.getJSONObject("message").getJSONArray("mapping");
                        JSONObject jsonAttributesObject = proofReq.getJSONObject("requested_attributes");
                        for(int i = 0; i < 1000; i++)
                        {
                            if(jsonAttributesObject.has("attr" + i + "_referent"))
                            {
                                String name = jsonAttributesObject.getJSONObject("attr" + i + "_referent").getString("field");
                                attributeNameList.add(name);
                                attributeReferenceList.add("attr" + i + "_referent");
                                if(jsonAttributesObject.getJSONObject("attr" + i + "_referent").has("restrictions"))
                                {
                                    JSONArray jsonArray = jsonAttributesObject.getJSONObject("attr" + i + "_referent").getJSONArray("restrictions");
                                    String credDefId = jsonArray.getJSONObject(0).getString("cred_def_id");
                                    String schemaId = "";
                                    for(int j = 0; j < mapping.length(); j++)
                                    {
                                        if(mapping.getJSONObject(j).getString("cred_def_id").equals(credDefId))
                                            schemaId = mapping.getJSONObject(j).getString("schema_id");
                                    }
                                    attributeSchemaNameList.add(schemaId.split(":")[2]);
                                    attributeSchemaVerList.add(schemaId.split(":")[3]);

                                    int index = credDefIdList.indexOf(credDefId);
                                    if(index != -1) {
                                        JSONObject contentObject = new JSONObject(contentList.get(index));
                                        attributeValueList.add(contentObject.getString(name));
                                    }
                                }
                                else
                                {
                                    attributeSchemaNameList.add("");
                                    attributeSchemaVerList.add("");
                                    if(name.equals("wallet_name"))
                                        attributeValueList.add(preferences.getString(getString(R.string.param_wallet_name), ""));
                                    else
                                        attributeValueList.add("");
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    adapter.notifyDataSetChanged();
                    if(attributeNameList.size() > 0)
                        textNoData.setVisibility(View.GONE);
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

    private void createProofOffer()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
            jsonObject.put("sender_did", senderDid);
            jsonObject.put("message_id", messageId);

            for(int i=0;i< attributeNameList.size(); i++){
                View view= recyclerView.getChildAt(i);
                EditText editText = view.findViewById(R.id.input_value);
                String value = editText.getText().toString();
                attributeValueList.set(i, value);
            }

            JSONObject jsonProofObject = new JSONObject(data);
            JSONObject messageObject = jsonProofObject.getJSONObject("message").getJSONObject("proof_req");
            JSONObject requestAttributesObject = messageObject.getJSONObject("requested_attributes");
            JSONObject newAttributesObject = new JSONObject();
            for(int i = 0; i < 1000; i++)
            {
                if(requestAttributesObject.has("attr" + i + "_referent"))
                {
                    JSONObject attributeObject = requestAttributesObject.getJSONObject("attr" + i + "_referent");
                    if(attributeObject.has("restrictions")) {
                        newAttributesObject.put("attr" + i + "_referent", attributeObject);
                    }
                    else
                    {
                        int index = attributeReferenceList.indexOf("attr" + i + "_referent");
                        attributeObject.put("value", attributeValueList.get(index));
                    }
                }
            }
            messageObject.put("requested_attested", newAttributesObject);
            Log.e("Final format",  jsonObject.toString());
            jsonObject.put("proof", messageObject);

            Log.e("Json", jsonObject.toString());
            AsynRestClient.genericPost(this, AsynRestClient.createProofOfferUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                        else
                            finish();
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
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}