package my.com.clarify.oneidentity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.adapter.GenericDetailAdapter;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;

public class GenericDetailActivity extends AppCompatActivity {
    public AppCompatImageView imageBack;

    public GenericDetailAdapter adapter;
    public RecyclerView recyclerView;
    public TextView textNoData;
    public ArrayList<String> nameList = new ArrayList<String>();
    public ArrayList<String> valueList = new ArrayList<String>();
    public ArrayList<String> base64DataList = new ArrayList<String>();
    public String pageTitle = "";
    public TextView textTitle;
    public ProgressDialog progressDialog;
    public int index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_detail);

        pageTitle = getIntent().getExtras().getString("Title");
        nameList = getIntent().getExtras().getStringArrayList("NameList");
        valueList = getIntent().getExtras().getStringArrayList("ValueList");
        for(int i = 0; i < nameList.size(); i++)
        {
            base64DataList.add("");
        }

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

        textTitle = findViewById(R.id.text_title);
        textTitle.setText(pageTitle);
        textNoData = findViewById(R.id.text_no_data);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new GenericDetailAdapter(this);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_divider));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //recyclerView.addItemDecoration(itemDecorator);
        adapter.notifyDataSetChanged();
        viewUpload();
    }

    private void viewUpload()
    {
        if(index < nameList.size())
        {
            if(valueList.get(index).endsWith(".jpg") || valueList.get(index).endsWith(".jpeg") || valueList.get(index).endsWith(".png"))
            {
                SharedPreferences preferences = getSharedPreferences(AppDelegate.SharedPreferencesTag, Context.MODE_PRIVATE);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("token", preferences.getString(getString(R.string.param_token), ""));
                    jsonObject.put("document_name", valueList.get(index));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                Log.e("Input", jsonObject.toString());
                AsynRestClient.genericPost(this, AsynRestClient.viewUploadUrl, jsonObject.toString(), new AsyncHttpResponseHandler() {
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
                            String imageBase64 = jsonObject.getString("data");
                            base64DataList.set(index, imageBase64);
                            adapter.notifyDataSetChanged();
                            index++;
                            viewUpload();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        //alert(getString(R.string.error), getString(R.string.error_please_try_again));
                        Log.e("Helo", "Hello");
                    }
                });
            }
            else
            {
                base64DataList.set(index, "");
                adapter.notifyDataSetChanged();
                index++;
                viewUpload();
            }
        }
        else
        {
            adapter.notifyDataSetChanged();
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
}