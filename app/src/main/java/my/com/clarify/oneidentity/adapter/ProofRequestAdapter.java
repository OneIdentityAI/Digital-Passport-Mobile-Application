package my.com.clarify.oneidentity.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.activity.ConnectionListActivity;

public class ProofRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public int VIEW_TYPE_DATA = 0;
    public ConnectionListActivity activity;
    public int scrollCount = 0;
    public int index = 0;
    public ProofRequestAdapter(ConnectionListActivity activity, int index)
    {
        this.activity = activity;
        this.index = index;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.list_item_proof_request, viewGroup, false);
        ItemViewHolder listHolder = new ItemViewHolder(mainGroup);
        return listHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final ItemViewHolder viewHolder = (ItemViewHolder)holder;

        try {

            JSONObject data = new JSONObject(activity.proofRequestList.get(index).get(position));
            JSONObject proofReq = data.getJSONObject("proof_request");
            viewHolder.textName.setText(proofReq.getString("name") + "    ver: " + proofReq.getString("version"));
            JSONArray mapping = data.getJSONArray("mapping");
            JSONObject jsonAttributesObject = proofReq.getJSONObject("requested_attributes");
            String attributesString = "";
            for(int i = 0; i < 1000; i++)
            {
                if(jsonAttributesObject.has("attr" + i + "_referent"))
                {
                    String name = jsonAttributesObject.getJSONObject("attr" + i + "_referent").getString("field");

                    attributesString += "Attribute " + i + " : " + name + "\n";
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
                        attributesString += "Request access to Schema: " + schemaId.split(":")[2] + "  Version: " + schemaId.split(":")[3] + "\n\n";
                    }
                    else
                    {
                        attributesString += "User Input" + "\n\n";
                    }
                }
            }
            viewHolder.textTotal.setText(attributesString);
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.alertSubItemMenu(3, index, position);
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
        return activity.proofRequestList.get(index).size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DATA;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout layout;
        public TextView textName;
        public TextView textTotal;

        public ItemViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_region);
            textName = view.findViewById(R.id.text_name);
            textTotal = view.findViewById(R.id.text_total);
        }
    }
}