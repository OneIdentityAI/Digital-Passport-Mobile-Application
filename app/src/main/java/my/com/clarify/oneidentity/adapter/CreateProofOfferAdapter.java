package my.com.clarify.oneidentity.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.activity.CreateProofOfferActivity;
import my.com.clarify.oneidentity.activity.DIDListActivity;
import my.com.clarify.oneidentity.data.AppDelegate;
import my.com.clarify.oneidentity.data.AsynRestClient;

public class CreateProofOfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public int VIEW_TYPE_DATA = 0;
    public CreateProofOfferActivity activity;
    int scrollCount = 0;
    public CreateProofOfferAdapter(CreateProofOfferActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.list_item_create_proof_offer, viewGroup, false);
        ItemViewHolder listHolder = new ItemViewHolder(mainGroup);
        return listHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final ItemViewHolder viewHolder = (ItemViewHolder)holder;
        viewHolder.textName.setText(activity.attributeNameList.get(position));
        if(!activity.attributeSchemaNameList.get(position).equals("")) {
            viewHolder.textSchema.setText("from " + activity.attributeSchemaNameList.get(position) + " (" + activity.attributeSchemaVerList.get(position) + ")");
            viewHolder.inputValue.setEnabled(false);
        }
        else
        {
            viewHolder.inputValue.setEnabled(true);
        }
        viewHolder.inputValue.setText(activity.attributeValueList.get(position));
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return activity.attributeNameList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DATA;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout layout;
        public TextView textName;
        public TextView textSchema;
        public EditText inputValue;

        public ItemViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_region);
            textName = view.findViewById(R.id.text_name);
            textSchema = view.findViewById(R.id.text_schema);
            inputValue = view.findViewById(R.id.input_value);
        }
    }
}