package my.com.clarify.oneidentity.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.pedro.library.AutoPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.adapter.DIDAdapter;
import my.com.clarify.oneidentity.adapter.PaymentWalletAdapter;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;

public class DIDListActivity extends AppCompatActivity {
    public AppCompatImageView imageBack;
    public AppCompatImageView imageAdd;

    public DIDAdapter adapter;
    public RecyclerView recyclerView;
    public TextView textNoData;
    public ArrayList<String> didNameList = new ArrayList<String>();
    public ArrayList<String> didList = new ArrayList<String>();
    public ArrayList<String> verkeyList = new ArrayList<String>();
    public ArrayList<Integer> isPairwiseList = new ArrayList<Integer>();
    public ArrayList<String> createdList = new ArrayList<String>();
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_did_list);

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
                Intent intent = new Intent(DIDListActivity.this, DIDAddActivity.class);
                startActivity(intent);
            }
        });

        textNoData = findViewById(R.id.text_no_data);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new DIDAdapter(this);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_divider));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //recyclerView.addItemDecoration(itemDecorator);
        adapter.notifyDataSetChanged();
        AutoPermissions.Companion.loadAllPermissions(this, 1);
    }

    public boolean isCalled = false;
    private void retrieveData()
    {
        Log.e("Is Called", isCalled + "");
        if(isCalled)
            return;

        isCalled = true;
        Log.e("Trace", "Trace");
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.listDIDUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    didNameList.clear();
                    didList.clear();
                    verkeyList.clear();
                    isPairwiseList.clear();
                    createdList.clear();

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject innerObject = (JSONObject) jsonArray.get(i);
                        didNameList.add(innerObject.getString("name"));
                        didList.add(innerObject.getString("did"));
                        verkeyList.add(innerObject.getString("verkey"));
                        isPairwiseList.add(innerObject.getInt("is_pairwise"));
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
                    if(jsonArray.length() > 0)
                        textNoData.setVisibility(View.GONE);
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
                Log.e("Helo", "Hello");
                isCalled = false;
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
        retrieveData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public int selectedPosition = 0;
    public void createConnection(final int position)
    {
        if(isPairwiseList.get(position) == 0)
        {
            selectedPosition = position;
            Intent intent = new Intent(DIDListActivity.this, CaptureActivity.class);
            intent.setAction("com.google.zxing.client.android.SCAN");
            intent.putExtra("SAVE_HISTORY", false);
            startActivityForResult(intent, AppDelegate.BARCODE_REQUEST_CODE);
        }
        else
        {
            alert("DID Pairwised", "This DID is no longer available for pairwise");
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
                        createConnectionRequest(contents);
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
    }

    private void createConnectionRequest(String endpointDID)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
            jsonObject.put("sender_did", didList.get(selectedPosition));
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


    public void alertItemMenu(final int position)
    {
        final ArrayList<String> actionList = new ArrayList<String>();
        if(isPairwiseList.get(position) == 0)
            actionList.add("Connect");
        actionList.add("Detail");
        CharSequence data[] = actionList.toArray(new String[actionList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Action");
        builder.setItems(data, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(actionList.get(which).equals("Connect"))
                {
                    createConnection(position);
                }
                else if(actionList.get(which).equals("Detail"))
                {
                    Intent intent = new Intent(DIDListActivity.this, GenericDetailActivity.class);
                    ArrayList<String> nameList = new ArrayList<String>();
                    ArrayList<String> valueList = new ArrayList<String>();
                    nameList.add("Name");
                    valueList.add(didNameList.get(position));
                    nameList.add("DID");
                    valueList.add(didList.get(position));
                    nameList.add("Verkey");
                    valueList.add(verkeyList.get(position));
                    nameList.add("Created Datetime");
                    valueList.add(createdList.get(position));
                    if(isPairwiseList.get(position) == 0)
                    {
                        nameList.add("Status");
                        valueList.add("Available");
                    }
                    else
                    {
                        nameList.add("Status");
                        valueList.add("Used");
                    }
                    intent.putExtra("Title", "DID Detail");
                    intent.putExtra("NameList", nameList);
                    intent.putExtra("ValueList", valueList);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }
}