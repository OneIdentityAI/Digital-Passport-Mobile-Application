package my.com.clarify.oneidentity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.adapter.MessageAdapter;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;
import cz.msebera.android.httpclient.Header;

public class MessageActivity extends AppCompatActivity {
    public AppDelegate delegate;
    public AppCompatImageView imageBack;

    public MessageAdapter adapter;
    public RecyclerView recyclerView;
    public ArrayList<String> messageidList = new ArrayList<String>();
    public ArrayList<String> messageList = new ArrayList<String>();
    public ArrayList<String> typeList = new ArrayList<String>();
    public ArrayList<String> createdList = new ArrayList<String>();

    public ArrayList<String> nameList = new ArrayList<String>();
    public ArrayList<String> myDIDList = new ArrayList<String>();
    public ArrayList<String> theirDIDList = new ArrayList<String>();
    public ArrayList<String> theirEndpointDIDList = new ArrayList<String>();
    public ArrayList<String> theirEndpointVerkeyList = new ArrayList<String>();
    public ArrayList<String> usernameList = new ArrayList<String>();
    public ArrayList<String> walletNameList = new ArrayList<String>();
    public ArrayList<JSONObject> proofList = new ArrayList<JSONObject>();
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
        setContentView(R.layout.activity_message);
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
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new MessageAdapter(this);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_divider));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //recyclerView.addItemDecoration(itemDecorator);
        adapter.notifyDataSetChanged();
        retrieveConnectionData();
    }

    private void retrieveData()
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        String responseString = preferences.getString(getString(R.string.param_message_json), "");
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
            adapter.notifyDataSetChanged();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
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

    public void alertItemMenu(final int position)
    {
        final ArrayList<String> actionList = new ArrayList<String>();
        if(typeList.get(position).equals("MSG_TYPE_CONN_REQUEST"))
        {
            actionList.add("Accept Connection Request");
            actionList.add("Detail");
            actionList.add("Delete");
        }
        else if(typeList.get(position).equals("MSG_TYPE_CONN_ACCEPT"))
        {
            actionList.add("Acknowledge");
            actionList.add("Detail");
            actionList.add("Delete");
        }
        else if(typeList.get(position).equals("MSG_TYPE_SECURE_SEND"))
        {
            actionList.add("Reply");
            actionList.add("Detail");
            actionList.add("Delete");
        }
        else if(typeList.get(position).equals("MSG_TYPE_CREDENTIAL_OFFER"))
        {
            actionList.add("Detail");
            actionList.add("Delete");
        }
        else if(typeList.get(position).equals("MSG_TYPE_CREDENTIAL_ACCEPT_REQUEST"))
        {
            actionList.add("Receive Credential");
            actionList.add("Detail");
            actionList.add("Delete");
        }
        else if(typeList.get(position).equals("MSG_TYPE_PROOF_REQUEST"))
        {
            actionList.add("Detail");
            actionList.add("Delete");
        }
        else if(typeList.get(position).equals("MSG_TYPE_PROOF_OFFER"))
        {
            actionList.add("Detail");
            actionList.add("Delete");
        }
        else if(typeList.get(position).equals("MSG_TYPE_CREDENTIAL_REQUEST"))
        {
            actionList.add("Delete");
        }
        CharSequence data[] = actionList.toArray(new String[actionList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Action");
        builder.setItems(data, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(actionList.get(which).equals("Delete"))
                {
                    deleteItem(position);
                }
                if(actionList.get(which).equals("Accept Connection Request"))
                {
                    final EditText edittext = new EditText(MessageActivity.this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
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
                    final AlertDialog dialog2 = builder.create();
                    dialog2.show();
                    dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            edittext.setError(null);
                            if(!edittext.getText().toString().equals("")) {
                                addDid(edittext.getText().toString(), messageidList.get(position));
                                dialog2.dismiss();
                            }
                            else
                                edittext.setError(getString(R.string.error_value_cannot_be_empty));
                        }
                    });
                }
                else if(actionList.get(which).equals("Acknowledge"))
                {
                    acknowledgeConnectionAcceptance(position);
                }
                else if(actionList.get(which).equals("Detail"))
                {
                    if(typeList.get(position).equals("MSG_TYPE_CONN_REQUEST"))
                    {
                        Intent intent = new Intent(MessageActivity.this, GenericDetailActivity.class);
                        ArrayList<String> nameList = new ArrayList<String>();
                        ArrayList<String> valueList = new ArrayList<String>();
                        try {
                            Log.e("Message", messageList.get(position));
                            JSONObject jsonObject = new JSONObject(messageList.get(position));
                            nameList.add("Message id");
                            valueList.add(messageidList.get(position));
                            nameList.add("Message Type");
                            valueList.add("New Connection Request");
                            nameList.add("DID");
                            valueList.add(jsonObject.getString("did"));
                            nameList.add("Endpoint DID");
                            valueList.add(jsonObject.getString("endpoint_did"));
                            nameList.add("Endpoint Verkey");
                            valueList.add(jsonObject.getString("endpoint_verkey"));
                            nameList.add("Message Nonce");
                            valueList.add(jsonObject.getString("nonce"));
                            nameList.add("Datetime");
                            valueList.add(createdList.get(position));

                            intent.putExtra("Title", "New Connection Detail");
                            intent.putExtra("NameList", nameList);
                            intent.putExtra("ValueList", valueList);
                            startActivity(intent);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if(typeList.get(position).equals("MSG_TYPE_CONN_ACCEPT"))
                    {
                        Intent intent = new Intent(MessageActivity.this, GenericDetailActivity.class);
                        ArrayList<String> nameList = new ArrayList<String>();
                        ArrayList<String> valueList = new ArrayList<String>();
                        try {
                            JSONObject jsonObject = new JSONObject(messageList.get(position));
                            nameList.add("Message id");
                            valueList.add(messageidList.get(position));
                            nameList.add("Message Type");
                            valueList.add("Connection Request Accepted");
                            nameList.add("DID");
                            valueList.add(jsonObject.getString("did"));
                            nameList.add("Endpoint DID");
                            valueList.add(jsonObject.getString("endpoint_did"));
                            nameList.add("Message Nonce");
                            valueList.add(jsonObject.getString("nonce"));
                            nameList.add("Datetime");
                            valueList.add(createdList.get(position));

                            intent.putExtra("Title", "Connection Request Acceptance Detail");
                            intent.putExtra("NameList", nameList);
                            intent.putExtra("ValueList", valueList);
                            startActivity(intent);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if(typeList.get(position).equals("MSG_TYPE_SECURE_SEND"))
                    {
                        Intent intent = new Intent(MessageActivity.this, GenericDetailActivity.class);
                        ArrayList<String> nameList = new ArrayList<String>();
                        ArrayList<String> valueList = new ArrayList<String>();
                        try {
                            JSONObject jsonObject = new JSONObject(messageList.get(position));
                            nameList.add("Message id");
                            valueList.add(messageidList.get(position));
                            nameList.add("Message Type");
                            valueList.add("Secure Message");

                            String senderDid = jsonObject.getString("sender_did");
                            int index = -1;
                            for(int i = 0; i < nameList.size(); i++)
                            {
                                if(senderDid.equals(theirDIDList.get(i)))
                                {
                                    index = i;
                                    break;
                                }
                            }
                            if(index > -1)
                            {
                                //proofNameList.add("UserName");
                                //valueList.add(usernameList.get(index));
                                //proofNameList.add("Wallet Name");
                                //valueList.add(walletNameList.get(index));
                                nameList.add("Connection");
                                valueList.add(MessageActivity.this.nameList.get(index));
                                nameList.add("Sender DID");
                                valueList.add(senderDid);
                            }
                            nameList.add("Message");
                            valueList.add(jsonObject.getString("message"));
                            nameList.add("Datetime");
                            valueList.add(createdList.get(position));

                            intent.putExtra("Title", "Secure Message Detail");
                            intent.putExtra("NameList", nameList);
                            intent.putExtra("ValueList", valueList);
                            startActivity(intent);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if(typeList.get(position).equals("MSG_TYPE_CREDENTIAL_OFFER"))
                    {
                        Intent intent = new Intent(MessageActivity.this, GenericDetailActivity.class);
                        ArrayList<String> nameList = new ArrayList<String>();
                        ArrayList<String> valueList = new ArrayList<String>();
                        try {
                            JSONObject jsonObject = new JSONObject(messageList.get(position));
                            nameList.add("Message id");
                            valueList.add(messageidList.get(position));
                            nameList.add("Message Type");
                            valueList.add("New Credential Offer");

                            String senderDid = jsonObject.getString("sender_did");
                            int index = -1;
                            for(int i = 0; i < nameList.size(); i++)
                            {
                                if(senderDid.equals(theirDIDList.get(i)))
                                {
                                    index = i;
                                    break;
                                }
                            }
                            if(index > -1)
                            {
                                //proofNameList.add("UserName");
                                //valueList.add(usernameList.get(index));
                                //proofNameList.add("Wallet Name");
                                //valueList.add(walletNameList.get(index));
                                nameList.add("Connection");
                                valueList.add(MessageActivity.this.nameList.get(index));
                                nameList.add("Sender DID");
                                valueList.add(senderDid);
                            }
                            nameList.add("Schema Id");
                            valueList.add(jsonObject.getString("schema_id"));
                            nameList.add("Schema Name");
                            valueList.add(jsonObject.getString("schema_id").split(":")[2]);
                            nameList.add("Schema Version");
                            valueList.add(jsonObject.getString("schema_id").split(":")[3]);
                            nameList.add("Cred Def Id");
                            valueList.add(jsonObject.getString("cred_def_id"));


                            for(int i = 0; i< jsonObject.getJSONArray("attributes").length(); i++)
                            {
                                nameList.add("Attribute");
                                valueList.add(jsonObject.getJSONArray("attributes").getString(i));
                            }

                            nameList.add("Datetime");
                            valueList.add(createdList.get(position));

                            intent.putExtra("Title", "New Credential Offer Detail");
                            intent.putExtra("NameList", nameList);
                            intent.putExtra("ValueList", valueList);
                            startActivity(intent);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if(typeList.get(position).equals("MSG_TYPE_PROOF_REQUEST"))
                    {
                        Intent intent = new Intent(MessageActivity.this, GenericDetailActivity.class);
                        ArrayList<String> nameList = new ArrayList<String>();
                        ArrayList<String> valueList = new ArrayList<String>();
                        try {
                            JSONObject jsonObject = new JSONObject(messageList.get(position));
                            nameList.add("Message id");
                            valueList.add(messageidList.get(position));
                            nameList.add("Message Type");
                            valueList.add("New Proof Request");

                            String senderDid = jsonObject.getString("sender_did");
                            int index = -1;
                            for(int i = 0; i < nameList.size(); i++)
                            {
                                if(senderDid.equals(theirDIDList.get(i)))
                                {
                                    index = i;
                                    break;
                                }
                            }
                            if(index > -1)
                            {
                                //proofNameList.add("User Name");
                                //valueList.add(firstNameList.get(index) + " " + lastNameList.get(index));
                                //proofNameList.add("Company Name");
                                //valueList.add(companyNameList.get(index));
                                //proofNameList.add("Wallet Name");
                                //valueList.add(walletNameList.get(index));
                                nameList.add("Connection");
                                valueList.add(MessageActivity.this.nameList.get(index));
                                nameList.add("Sender DID");
                                valueList.add(senderDid);
                            }
                            JSONObject proofReq = jsonObject.getJSONObject("proof_request");
                            JSONArray mapping = jsonObject.getJSONArray("mapping");
                            nameList.add("Proof Request Nonce");
                            valueList.add(proofReq.getString("nonce"));
                            nameList.add("Proof Request Name");
                            valueList.add(proofReq.getString("name"));
                            nameList.add("Proof Request Version");
                            valueList.add(proofReq.getString("version"));

                            JSONObject jsonAttributesObject = proofReq.getJSONObject("requested_attributes");
                            for(int i = 0; i < 1000; i++)
                            {
                                if(jsonAttributesObject.has("attr" + i + "_referent"))
                                {
                                    String name = jsonAttributesObject.getJSONObject("attr" + i + "_referent").getString("field");

                                    nameList.add("Attribute " + i + " : " + name);
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
                                        valueList.add("Request access to Schema: " + schemaId.split(":")[2] + "  Version: " + schemaId.split(":")[3]);
                                    }
                                    else
                                    {
                                        valueList.add("User Input");
                                    }
                                }
                            }

                            nameList.add("Datetime");
                            valueList.add(createdList.get(position));

                            intent.putExtra("Title", "New Credential Offer Detail");
                            intent.putExtra("NameList", nameList);
                            intent.putExtra("ValueList", valueList);
                            startActivity(intent);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if(typeList.get(position).equals("MSG_TYPE_PROOF_OFFER"))
                    {
                        Intent intent = new Intent(MessageActivity.this, GenericDetailActivity.class);
                        ArrayList<String> nameList = new ArrayList<String>();
                        ArrayList<String> valueList = new ArrayList<String>();
                        try {
                            JSONObject jsonObject = new JSONObject(messageList.get(position));
                            nameList.add("Message id");
                            valueList.add(messageidList.get(position));
                            nameList.add("Message Type");
                            valueList.add("New Proof Offer");

                            String senderDid = jsonObject.getString("sender_did");
                            int index = -1;
                            for(int i = 0; i < nameList.size(); i++)
                            {
                                if(senderDid.equals(theirDIDList.get(i)))
                                {
                                    index = i;
                                    break;
                                }
                            }
                            if(index > -1)
                            {
                                //proofNameList.add("UserName");
                                //valueList.add(usernameList.get(index));
                                //proofNameList.add("Wallet Name");
                                //valueList.add(walletNameList.get(index));
                                nameList.add("Connection");
                                valueList.add(MessageActivity.this.nameList.get(index));
                                nameList.add("Sender DID");
                                valueList.add(senderDid);
                            }
                            JSONObject proofReq = jsonObject.getJSONObject("message").getJSONObject("proof_request");
                            nameList.add("Proof Request Nonce");
                            valueList.add(proofReq.getString("nonce"));
                            nameList.add("Proof Request Name");
                            valueList.add(proofReq.getString("name"));
                            nameList.add("Proof Request Version");
                            valueList.add(proofReq.getString("version"));

                            JSONArray proofOffer = jsonObject.getJSONObject("message").getJSONArray("proof_offer");
                            for(int i = 0; i < proofOffer.length(); i++)
                            {
                                JSONObject innerObject = proofOffer.getJSONObject(i);
                                String finalName = innerObject.getString("attribute_name");
                                if(!innerObject.getString("attribute_schema_id").equals(""))
                                    finalName += "    from " + innerObject.getString("attribute_schema_id").split(":")[2] + " (" + innerObject.getString("attribute_schema_id").split(":")[3] + ")";
                                nameList.add("Attribute: " + finalName);
                                valueList.add(innerObject.getString("attribute_value"));
                            }

                            nameList.add("Datetime");
                            valueList.add(createdList.get(position));

                            intent.putExtra("Title", "New Credential Offer Detail");
                            intent.putExtra("NameList", nameList);
                            intent.putExtra("ValueList", valueList);
                            startActivity(intent);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    else if(typeList.get(position).equals("MSG_TYPE_CREDENTIAL_ACCEPT_REQUEST"))
                    {
                        Intent intent = new Intent(MessageActivity.this, GenericDetailActivity.class);
                        ArrayList<String> name1List = new ArrayList<String>();
                        ArrayList<String> valueList = new ArrayList<String>();
                        try {
                            JSONObject jsonObject = new JSONObject(messageList.get(position));
                            name1List.add("Message id");
                            valueList.add(messageidList.get(position));
                            name1List.add("Message Type");
                            valueList.add("New Credential Issuance");

                            String senderDid = jsonObject.getString("sender_did");
                            int index = -1;
                            for(int i = 0; i < nameList.size(); i++)
                            {
                                if(senderDid.equals(theirDIDList.get(i)))
                                {
                                    index = i;
                                    break;
                                }
                            }
                            if(index > -1)
                            {
                                //name1List.add("UserName");
                                //valueList.add(usernameList.get(index));
                                //name1List.add("Wallet Name");
                                //valueList.add(walletNameList.get(index));
                                name1List.add("Connection");
                                valueList.add(MessageActivity.this.nameList.get(index));
                                name1List.add("Sender DID");
                                valueList.add(senderDid);
                            }
                            String schemaId = jsonObject.getString("schema_id");
                            String credDefId = jsonObject.getString("cred_def_id");
                            JSONObject values = jsonObject.getJSONObject("values");
                            name1List.add("Schema Name");
                            valueList.add(schemaId.split(":")[2]);
                            name1List.add("Schema Version");
                            valueList.add(schemaId.split(":")[3]);
                            name1List.add("Schema Id");
                            valueList.add(schemaId);
                            name1List.add("Credential Definition Id");
                            valueList.add(credDefId);
                            Iterator<String> iter = values.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    String value = values.getJSONObject(key).getString("raw");
                                    name1List.add("Attribute: " + key);
                                    valueList.add(value);
                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }

                            name1List.add("Datetime");
                            valueList.add(createdList.get(position));

                            intent.putExtra("Title", "New Credential Issuance Detail");
                            intent.putExtra("NameList", name1List);
                            intent.putExtra("ValueList", valueList);
                            startActivity(intent);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else if(actionList.get(which).equals("Receive Credential"))
                {
                    alert("Message Detail", AppDelegate.formatString(messageList.get(position)));
                }
                else if(actionList.get(which).equals("Reply"))
                {
                    try {
                        JSONObject jsonObject = new JSONObject(messageList.get(position));
                        String senderDid = jsonObject.getString("sender_did");
                        int index = -1;
                        for(int i = 0; i < nameList.size(); i++)
                        {
                            if(senderDid.equals(theirDIDList.get(i)))
                            {
                                index = i;
                                break;
                            }
                        }

                        if(index > -1) {
                            final int actualIndex = index;
                            AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                            builder.setTitle("Send Message");
                            builder.setMessage("Send an encrypted plain text message to this connection");
                            final EditText input = new EditText(MessageActivity.this);
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(input);
                            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendMessage(theirDIDList.get(actualIndex), input.getText().toString());
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
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.show();
    }

    private void addDid(String didName, final String messageId)
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

        progressDialog.setMessage(getString(R.string.creating_did));
        progressDialog.show();
        AsynRestClient.genericPost(this, AsynRestClient.addDIDUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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

                    JSONObject jsonInnerObject = jsonObject.getJSONObject("data");
                    String DID = jsonInnerObject.getString("did");
                    acceptConnectionRequest(messageId, DID);
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
                progressDialog.dismiss();
            }
        });
    }

    private void acceptConnectionRequest(String messageId, String DID)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("sender_did", DID);
            jsonObject.put("message_id", messageId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.e("Param", jsonObject.toString());

        progressDialog.setMessage(getString(R.string.accepting_connection_request));
        AsynRestClient.genericPost(this, AsynRestClient.acceptConnectionRequestUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                        progressDialog.dismiss();
                        return;
                    }
                    JSONObject jsonObjectInner = jsonObject.getJSONObject("data");
                    alert(getString(R.string.success), "Connection request accepted");
                    delegate.shouldRefreshConnectionList = true;
                    progressDialog.dismiss();
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
                progressDialog.dismiss();
            }
        });
    }

    private void sendMessage(String theirDID, String message)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("recipient_did", theirDID);
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

    private void deleteItem(int postion)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("message_id", messageidList.get(postion));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.deleteMessageUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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

    private void acknowledgeConnectionAcceptance(int postion)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("message_id", messageidList.get(postion));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        progressDialog.setMessage(getString(R.string.acknowledging_connection_acceptance));
        progressDialog.show();
        AsynRestClient.genericPost(this, AsynRestClient.acknowledgeConnectionAcceptanceUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                        progressDialog.dismiss();
                        return;
                    }

                    alert(getString(R.string.success), jsonObject.getString("message"));

                    delegate.shouldRefreshConnectionList = true;
                    String myDid = jsonObject.getJSONObject("data").getString("sender_did");
                    String recipientDid = jsonObject.getJSONObject("data").getString("recipient_did");
                    createProofOffer(myDid);
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
                progressDialog.dismiss();
            }
        });
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
                retrieveData();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void createProofOffer(String senderDid)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_oneid_token), ""));
            jsonObject.put("sender_did", senderDid);

            JSONObject proofReq = new JSONObject();
            proofReq.put("name", "ConnectionProfileExchange");
            proofReq.put("version", "1.0");

            JSONArray dataArray = new JSONArray();
            JSONObject dataObject1 = new JSONObject();
            dataObject1.put("name", "name");
            dataObject1.put("cred_def_id", AppDelegate.credDefIdOneIdentity);
            JSONObject dataObject2 = new JSONObject();
            dataObject2.put("name", "wallet_name");
            dataObject2.put("cred_def_id", "");
            dataArray.put(dataObject1);
            dataArray.put(dataObject2);
            proofReq.put("requested-attributes", dataArray);
            jsonObject.put("proof_request", proofReq);

            progressDialog.setMessage(getString(R.string.creating_profile_exchange_proof_request));
            AsynRestClient.genericPost(this, AsynRestClient.createProofRequestUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);

                        if(jsonObject.has("error"))
                        {
                            JSONObject errorObject = jsonObject.getJSONObject("error");
                            alert(errorObject.getString("type"), errorObject.getString("message"));
                            return;
                        }
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
        catch (Exception e)
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