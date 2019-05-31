package my.com.clarify.oneidentity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import my.com.clarify.oneidentity.adapter.PaymentWalletAdapter;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;

public class PaymentWalletListActivity extends AppCompatActivity {
    public AppCompatImageView imageBack;
    public AppCompatImageView imageAdd;

    public PaymentWalletAdapter adapter;
    public RecyclerView recyclerView;
    public TextView textNoData;
    public ArrayList<String> addressList = new ArrayList<String>();
    public ArrayList<String> currencyList = new ArrayList<String>();
    public ArrayList<String> createdList = new ArrayList<String>();
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_wallet_list);

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
                Intent intent = new Intent(PaymentWalletListActivity.this, PaymentWalletAddActivity.class);
                startActivity(intent);
            }
        });

        textNoData = findViewById(R.id.text_no_data);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new PaymentWalletAdapter(this);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_divider));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //recyclerView.addItemDecoration(itemDecorator);
        adapter.notifyDataSetChanged();
    }

    public boolean isCalled = false;
    private void retrieveData()
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

        AsynRestClient.genericPost(this, AsynRestClient.listPaymentWalletUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                    addressList.clear();
                    currencyList.clear();
                    createdList.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));

                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject innerObject = (JSONObject) jsonArray.get(i);
                        addressList.add(innerObject.getString("payment_wallet_address"));
                        currencyList.add(innerObject.getString("payment_wallet_currency"));
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

    public void alertItemMenu(final int position)
    {
        CharSequence colors[] = new CharSequence[] {"Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Action");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0)
                {
                    deleteItem(position);
                }
            }
        });
        builder.show();
    }

    private void deleteItem(int postion)
    {
        SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
            jsonObject.put("payment_wallet_address", addressList.get(postion));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AsynRestClient.genericPost(this, AsynRestClient.deletePaymentWalletUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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