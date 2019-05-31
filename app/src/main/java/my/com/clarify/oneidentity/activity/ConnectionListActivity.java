package my.com.clarify.oneidentity.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.adapter.ConnectionAdapter;
import my.com.clarify.oneidentity.adapter.CredentialOfferAdapter;
import my.com.clarify.oneidentity.adapter.ProofOfferAdapter;
import my.com.clarify.oneidentity.adapter.ProofRequestAdapter;
import my.com.clarify.oneidentity.adapter.ReceiveCredentialAdapter;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;

public class ConnectionListActivity extends AppCompatActivity {
    public AppCompatImageView imageBack;
    public AppCompatImageView imageAdd;

    public ConnectionAdapter adapter;
    public RecyclerView recyclerView;
    public TextView textNoData;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_list);

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
        imageAdd = findViewById(R.id.image_add);
        imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert(getString(R.string.information), getString(R.string.connection_add_message));
            }
        });

        textNoData = findViewById(R.id.text_no_data);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ConnectionAdapter(this);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_divider));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //recyclerView.addItemDecoration(itemDecorator);
        adapter.notifyDataSetChanged();
    }

    private void retrieveData()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.listConnectionUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    adapter.notifyDataSetChanged();
                    if(jsonArray.length() > 0)
                        textNoData.setVisibility(View.GONE);

                    retrieveMessage();
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
        listProof();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionListActivity.this);
                    builder.setTitle("Send Message");
                    builder.setMessage("Send an encrypted plain text message to this connection");
                    final EditText input = new EditText(ConnectionListActivity.this);
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
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
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
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
            jsonObject.put("type", "");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.listMessageUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    messageidList.clear();
                    messageList.clear();
                    typeList.clear();
                    createdList.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));

                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject innerObject = (JSONObject) jsonArray.get(i);
                        messageidList.add(innerObject.getString("message_id"));
                        messageList.add(innerObject.getString("message"));
                        typeList.add(innerObject.getString("type"));
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
                    if(jsonArray.length() > 0)
                        textNoData.setVisibility(View.GONE);

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

    public boolean isCalled = false;
    private void listProof()
    {
        if(isCalled)
            return;

        isCalled = true;
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.listProofUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    proofList.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject innerObject = (JSONObject) jsonArray.get(i);
                        proofList.add(innerObject);
                    }
                    retrieveData();
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
                alert(getString(R.string.error), getString(R.string.error_please_try_again));
                String responseString = new String(errorResponse);
                Log.e("Response", responseString);
                isCalled = false;
            }
        });
    }

    public Dialog dialogCredentialOffer;

    public CredentialOfferAdapter coAdapter;
    public RecyclerView coRecyclerView;
    public void alertCredentialOffer(int position)
    {
        dialogCredentialOffer = new Dialog(ConnectionListActivity.this);
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
        dialogReceiveCredential = new Dialog(ConnectionListActivity.this);
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
        dialogProofOffer = new Dialog(ConnectionListActivity.this);
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
        dialogProofRequest = new Dialog(ConnectionListActivity.this);
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
                    dialogProofRequest.dismiss();
                    Intent intent = new Intent(ConnectionListActivity.this, CreateProofOfferActivity.class);
                    intent.putExtra("message_id", proofRequestMessageIdList.get(position1).get(position2));
                    intent.putExtra("data", proofRequestList.get(position1).get(position2));
                    intent.putExtra("sender_did", myDIDList.get(position1));
                    startActivity(intent);
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
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
            jsonObject.put("sender_did", myDIDList.get(position1));
            jsonObject.put("message_id", credentialOfferMessageIdList.get(position1).get(position2));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.acceptCredentialOfferUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                Log.e("Helo", "Hello");
            }
        });
    }

    public void verifyAndAcceptProofOffer(final int position1, final int position2)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
            jsonObject.put("sender_did", myDIDList.get(position1));
            jsonObject.put("message_id", proofOfferMessageIdList.get(position1).get(position2));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.verifyAndAcceptProofOfferUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
            jsonObject.put("sender_did", myDIDList.get(position1));
            jsonObject.put("message_id", receiveCredentialMessageIdList.get(position1).get(position2));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.receiveCredentialUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                Log.e("Helo", "Hello");
            }
        });
    }
}