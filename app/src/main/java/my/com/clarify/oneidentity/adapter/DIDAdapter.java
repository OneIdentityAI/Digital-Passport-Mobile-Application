package my.com.clarify.oneidentity.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.activity.DIDActivity;

public class DIDAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public int VIEW_TYPE_DATA = 0;
    public DIDActivity activity;
    int scrollCount = 0;
    public DIDAdapter(DIDActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.list_item_did, viewGroup, false);
        ItemViewHolder listHolder = new ItemViewHolder(mainGroup);
        return listHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final ItemViewHolder viewHolder = (ItemViewHolder)holder;
        viewHolder.textName.setText(activity.didNameList.get(position));
        if(activity.isPairwiseList.get(position) == 0)
        {
            viewHolder.textAction.setText("Tap to use for new connection / View Detail");
            viewHolder.textUse.setText("Available");
            viewHolder.textUse.setTextColor(ContextCompat.getColor(activity, R.color.colorLightIndigo));
        }
        else
        {
            viewHolder.textAction.setText("Tap to View Detail");
            viewHolder.textUse.setText("Used");
            viewHolder.textUse.setTextColor(ContextCompat.getColor(activity, R.color.colorRed));
        }
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.alertItemMenu(position);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return activity.didNameList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DATA;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout layout;
        public TextView textName;
        public TextView textUse;
        public TextView textAction;

        public ItemViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_region);
            textName = view.findViewById(R.id.text_name);
            textUse = view.findViewById(R.id.text_use);
            textAction = view.findViewById(R.id.text_action);
        }
    }
}