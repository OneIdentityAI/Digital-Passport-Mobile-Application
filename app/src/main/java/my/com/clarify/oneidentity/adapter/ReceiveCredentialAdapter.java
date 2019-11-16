package my.com.clarify.oneidentity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.activity.ConnectionActivity;

public class ReceiveCredentialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public int VIEW_TYPE_DATA = 0;
    public ConnectionActivity activity;
    public int scrollCount = 0;
    public int index = 0;
    public ReceiveCredentialAdapter(ConnectionActivity activity, int index)
    {
        this.activity = activity;
        this.index = index;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.list_item_receive_credential, viewGroup, false);
        ItemViewHolder listHolder = new ItemViewHolder(mainGroup);
        return listHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final ItemViewHolder viewHolder = (ItemViewHolder)holder;

        try {
            JSONObject data = new JSONObject(activity.receiveCredentialList.get(index).get(position));
            String senderDid = data.getString("sender_did");
            String schemaId = data.getString("schema_id");
            String credDefId = data.getString("cred_def_id");



            viewHolder.textTitle.setText(schemaId.split(":")[2] + "    ver: " + schemaId.split(":")[3]);
            String finalValue = "";
            Iterator<String> iter = data.getJSONObject("values").keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    String value = data.getJSONObject("values").getJSONObject(key).getString("raw");
                    finalValue += "Attribute: " + key;
                    finalValue += "\n";
                    finalValue += "Value: " + value + "\n\n";
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
            viewHolder.textSubtitle.setText(finalValue);
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.alertSubItemMenu(2, index, position);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount()
    {
        return activity.receiveCredentialList.get(index).size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DATA;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout layout;
        public TextView textTitle;
        public TextView textSubtitle;

        public ItemViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_region);
            textTitle = view.findViewById(R.id.text_title);
            textSubtitle = view.findViewById(R.id.text_subtitle);
        }
    }
}