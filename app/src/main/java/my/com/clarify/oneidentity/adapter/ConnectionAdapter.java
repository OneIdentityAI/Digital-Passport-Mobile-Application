package my.com.clarify.oneidentity.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.activity.ConnectionActivity;

public class ConnectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public int VIEW_TYPE_DATA = 0;
    public ConnectionActivity activity;
    int scrollCount = 0;
    public ConnectionAdapter(ConnectionActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.list_item_connection, viewGroup, false);
        ItemViewHolder listHolder = new ItemViewHolder(mainGroup);
        return listHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final ItemViewHolder viewHolder = (ItemViewHolder)holder;
        viewHolder.textName.setText(activity.nameList.get(position));
        String userName = activity.firstNameList.get(position) + " " + activity.lastNameList.get(position);
        String companyName = activity.companyNameList.get(position);
        String walletName = activity.walletNameList.get(position);
        userName = userName.trim().equals("")?"Unknown":userName;
        companyName = companyName.equals("")?"Unknown":companyName;
        walletName = walletName.equals("")?"Unknown":walletName;
        //viewHolder.textDetail.setText("Connect To: \n" + userName + " from " + companyName + "\nWallet Name: " + walletName);
        viewHolder.textDetail.setText("Endpoint DID: " + activity.theirEndpointDIDList.get(position));
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.alertItemMenu(position);
            }
        });

        String action = "";
        if(activity.totalCredentialOfferList.get(position) > 0) {
            if(!action.equals(""))
                action += " / ";
            action += " &#9864;&#x20;" + activity.totalCredentialOfferList.get(position) + " Cred Offer&#x20;";
        }
        if(activity.totalReceiveCredentialList.get(position) > 0) {
            if(!action.equals(""))
                action += " / ";
            action += " &#9864;&#x20;" + activity.totalReceiveCredentialList.get(position) + " Cred Issuance&#x20;";
        }
        if(activity.totalProofRequestList.get(position) > 0) {
            if(!action.equals(""))
                action += " / ";
            action += " &#9864;&#x20;" + activity.totalProofRequestList.get(position) + " Proof Request&#x20;";
        }
        if(activity.totalProofOfferList.get(position) > 0) {
            if(!action.equals(""))
                action += " / ";
            action += " &#9864;&#x20;" + activity.totalProofOfferList.get(position) + " Proof Offer&#x20;";
        }
        if(!action.equals(""))
            action = action;
        viewHolder.textAction.setText(Html.fromHtml(action));
    }

    @Override
    public int getItemCount()
    {
        return activity.nameList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DATA;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout layout;
        public TextView textName;
        public TextView textDetail;
        public TextView textAction;

        public ItemViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_region);
            textName = view.findViewById(R.id.text_name);
            textDetail = view.findViewById(R.id.text_detail);
            textAction = view.findViewById(R.id.text_action);
        }
    }
}