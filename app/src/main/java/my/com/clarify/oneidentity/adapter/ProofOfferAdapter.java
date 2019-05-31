package my.com.clarify.oneidentity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.activity.ConnectionListActivity;

public class ProofOfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public int VIEW_TYPE_DATA = 0;
    public ConnectionListActivity activity;
    public int scrollCount = 0;
    public int index = 0;
    public ProofOfferAdapter(ConnectionListActivity activity, int index)
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
            JSONObject data = new JSONObject(activity.proofOfferList.get(index).get(position));
            JSONObject proofReq = data.getJSONObject("message").getJSONObject("proof_request");
            JSONArray proofOffer = data.getJSONObject("message").getJSONArray("proof_offer");
            viewHolder.textName.setText(proofReq.getString("name") + "    ver: " + proofReq.getString("version"));
            String finalValue = "";
            for(int i = 0; i< proofOffer.length(); i++)
            {
                JSONObject innerObject = proofOffer.getJSONObject(i);
                finalValue += "Attribute: " + innerObject.getString("attribute_name");
                if(!innerObject.getString("attribute_schema_id").equals(""))
                {
                    finalValue += "     from " + innerObject.getString("attribute_schema_id").split(":")[2] + " (" + innerObject.getString("attribute_schema_id").split(":")[3] + ")\n";
                }
                else
                    finalValue += "\n";
                finalValue += innerObject.getString("attribute_value") + "\n\n";
            }
            viewHolder.textTotal.setText(finalValue);
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.alertSubItemMenu(4, index, position);
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
        return activity.proofOfferList.get(index).size();
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