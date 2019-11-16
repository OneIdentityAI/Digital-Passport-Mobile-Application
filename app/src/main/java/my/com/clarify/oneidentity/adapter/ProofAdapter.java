package my.com.clarify.oneidentity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.activity.ProofActivity;

public class ProofAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public int VIEW_TYPE_DATA = 0;
    public ProofActivity activity;
    int scrollCount = 0;
    public ProofAdapter(ProofActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.list_item_credential, viewGroup, false);
        ItemViewHolder listHolder = new ItemViewHolder(mainGroup);
        return listHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final ItemViewHolder viewHolder = (ItemViewHolder)holder;
        viewHolder.textName.setText(activity.proofNameList.get(position));
        viewHolder.textValue.setText("Version: " + activity.versionList.get(position));
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(activity, GenericDetailActivity.class);
//                intent.putExtra("Title", "");
//                ArrayList<String> proofNameList = new ArrayList<String>();
//                ArrayList<String> valueList = new ArrayList<String>();
//                try {
//                    JSONObject jsonObject = new JSONObject(activity.contentList.get(position));
//                    for(int i = 0; i< jsonObject.names().length(); i++){
//                        proofNameList.add(jsonObject.names().getString(i));
//                        valueList.add(jsonObject.get(jsonObject.names().getString(i)) + "");
//                    }
//                    intent.putExtra("Title", activity.schemaNameList.get(position));
//                    intent.putExtra("NameList", proofNameList);
//                    intent.putExtra("ValueList", valueList);
//                    activity.startActivity(intent);
//                }
//                catch (JSONException e)
//                {
//
//                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return activity.proofNameList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DATA;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout layout;
        public TextView textName;
        public TextView textValue;

        public ItemViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_region);
            textName = view.findViewById(R.id.text_name);
            textValue = view.findViewById(R.id.text_value);
        }
    }
}