package my.com.clarify.oneidentity.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.activity.MessageListActivity;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public int VIEW_TYPE_DATA = 0;
    public MessageListActivity activity;
    int scrollCount = 0;
    public MessageAdapter(MessageListActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.list_item_message, viewGroup, false);
        ItemViewHolder listHolder = new ItemViewHolder(mainGroup);
        return listHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final ItemViewHolder viewHolder = (ItemViewHolder)holder;
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.alertItemMenu(position);
            }
        });

        if(activity.typeList.get(position).equals("MSG_TYPE_CONN_REQUEST"))
        {
            viewHolder.textAction.setText("Tap to View Detail / Delete\n(Accept connection must be done from web)");
            viewHolder.textType.setText("New Connection Request");

            try {
                JSONObject jsonObject = new JSONObject(activity.messageList.get(position));
                String userName = "";
                String companyName = "";
                String walletName = "";
                userName = userName.trim().equals("")?"Unknown":userName;
                companyName = companyName.equals("")?"Unknown":companyName;
                walletName = walletName.equals("")?"Unknown":walletName;
                viewHolder.textId.setText(userName + " from " + companyName + "\n" + "Wallet Name: " + walletName + "\n" + "Datetime: " + activity.createdList.get(position));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_CONN_ACCEPT"))
        {
            viewHolder.textAction.setText("Tap to Acknowledge / View Detail / Delete");
            viewHolder.textType.setText("Connection Request Accepted");
            try {
                JSONObject jsonObject = new JSONObject(activity.messageList.get(position));
                String userName = "";
                String companyName = "";
                String walletName = "";
                userName = userName.trim().equals("")?"Unknown":userName;
                companyName = companyName.equals("")?"Unknown":companyName;
                walletName = walletName.equals("")?"Unknown":walletName;
                viewHolder.textId.setText(userName + " from " + companyName + "\n" + "Wallet Name: " + walletName + "\n" + "Datetime: " + activity.createdList.get(position));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_SECURE_SEND"))
        {
            viewHolder.textAction.setText("Tap to Reply / View Detail / Delete");
            try {
                JSONObject jsonObject = new JSONObject(activity.messageList.get(position));
                String senderDid = jsonObject.getString("sender_did");
                int index = -1;
                for(int i = 0; i < activity.nameList.size(); i++)
                {
                    if(senderDid.equals(activity.theirDIDList.get(i)))
                    {
                        index = i;
                        break;
                    }
                }
                if(index > -1) {
                    String userName = activity.firstNameList.get(index) + " " + activity.lastNameList.get(index);
                    String companyName = activity.companyNameList.get(index);
                    String walletName = activity.walletNameList.get(index);
                    userName = userName.trim().equals("")?"Unknown":userName;
                    companyName = companyName.equals("")?"Unknown":companyName;
                    walletName = walletName.equals("")?"Unknown":walletName;
                    viewHolder.textId.setText("Message From:\n" + userName + " from " + companyName + "\n" + "Wallet Name: " + walletName + "\n" + "Message : " + jsonObject.getString("message") + "\n" + "Datetime: " + activity.createdList.get(position));
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            viewHolder.textType.setText("Secure Message");
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_CREDENTIAL_OFFER"))
        {
            viewHolder.textAction.setText("Tap to View Detail / Delete\n(Accept Cred Offer must be done from connection page)");
            try {
                JSONObject jsonObject = new JSONObject(activity.messageList.get(position));
                String senderDid = jsonObject.getString("sender_did");
                int index = -1;
                for(int i = 0; i < activity.nameList.size(); i++)
                {
                    if(senderDid.equals(activity.theirDIDList.get(i)))
                    {
                        index = i;
                        break;
                    }
                }
                if(index > -1) {
                    String userName = activity.firstNameList.get(index) + " " + activity.lastNameList.get(index);
                    String companyName = activity.companyNameList.get(index);
                    String walletName = activity.walletNameList.get(index);
                    userName = userName.trim().equals("")?"Unknown":userName;
                    companyName = companyName.equals("")?"Unknown":companyName;
                    walletName = walletName.equals("")?"Unknown":walletName;
                    viewHolder.textId.setText("Credential Offer From:\n" + userName + " from " + companyName + "\n" + "Wallet Name: " + walletName);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            viewHolder.textType.setText("New Credential Offer");
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_CREDENTIAL_REQUEST"))
        {
            viewHolder.textAction.setText("Tap to Delete\n(Issue Cred must be done from web portal)");
            try {
                JSONObject jsonObject = new JSONObject(activity.messageList.get(position));
                String senderDid = jsonObject.getString("sender_did");
                int index = -1;
                for(int i = 0; i < activity.nameList.size(); i++)
                {
                    if(senderDid.equals(activity.theirDIDList.get(i)))
                    {
                        index = i;
                        break;
                    }
                }
                if(index > -1) {
                    String userName = activity.firstNameList.get(index) + " " + activity.lastNameList.get(index);
                    String companyName = activity.companyNameList.get(index);
                    String walletName = activity.walletNameList.get(index);
                    userName = userName.trim().equals("")?"Unknown":userName;
                    companyName = companyName.equals("")?"Unknown":companyName;
                    walletName = walletName.equals("")?"Unknown":walletName;
                    viewHolder.textId.setText("Credential Request From:\n" + userName + " from " + companyName + "\n" + "Wallet Name: " + walletName);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            viewHolder.textType.setText("New Credential Offer");
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_PROOF_REQUEST"))
        {
            viewHolder.textAction.setText("Tap to View Detail / Delete\n(Create Proof Offer must be done from connection page)");
            try {
                JSONObject jsonObject = new JSONObject(activity.messageList.get(position));
                String senderDid = jsonObject.getString("sender_did");
                int index = -1;
                for(int i = 0; i < activity.nameList.size(); i++)
                {
                    if(senderDid.equals(activity.theirDIDList.get(i)))
                    {
                        index = i;
                        break;
                    }
                }
                if(index > -1) {
                    String userName = activity.firstNameList.get(index) + " " + activity.lastNameList.get(index);
                    String companyName = activity.companyNameList.get(index);
                    String walletName = activity.walletNameList.get(index);
                    userName = userName.trim().equals("")?"Unknown":userName;
                    companyName = companyName.equals("")?"Unknown":companyName;
                    walletName = walletName.equals("")?"Unknown":walletName;
                    viewHolder.textId.setText("Proof Request From:\n" + userName + " from " + companyName + "\n" + "Wallet Name: " + walletName);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            viewHolder.textType.setText("New Proof Request");
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_PROOF_OFFER"))
        {
            viewHolder.textAction.setText("Tap to View Detail / Delete\n(Accept Proof Offer must be done from connection page)");
            try {
                JSONObject jsonObject = new JSONObject(activity.messageList.get(position));
                String senderDid = jsonObject.getString("sender_did");
                int index = -1;
                for(int i = 0; i < activity.nameList.size(); i++)
                {
                    if(senderDid.equals(activity.theirDIDList.get(i)))
                    {
                        index = i;
                        break;
                    }
                }
                if(index > -1) {
                    String userName = activity.firstNameList.get(index) + " " + activity.lastNameList.get(index);
                    String companyName = activity.companyNameList.get(index);
                    String walletName = activity.walletNameList.get(index);
                    userName = userName.trim().equals("")?"Unknown":userName;
                    companyName = companyName.equals("")?"Unknown":companyName;
                    walletName = walletName.equals("")?"Unknown":walletName;
                    viewHolder.textId.setText("Proof Offer From:\n" + userName + " from " + companyName + "\n" + "Wallet Name: " + walletName);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            viewHolder.textType.setText("New Proof Offer");
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_CREDENTIAL_ACCEPT_REQUEST"))
        {
            viewHolder.textAction.setText("View Detail / Delete\n(Receive Credential must be done from connection page)");
            try {
                JSONObject jsonObject = new JSONObject(activity.messageList.get(position));
                String senderDid = jsonObject.getString("sender_did");
                int index = -1;
                for(int i = 0; i < activity.nameList.size(); i++)
                {
                    if(senderDid.equals(activity.theirDIDList.get(i)))
                    {
                        index = i;
                        break;
                    }
                }
                if(index > -1) {
                    String userName = activity.firstNameList.get(index) + " " + activity.lastNameList.get(index);
                    String companyName = activity.companyNameList.get(index);
                    String walletName = activity.walletNameList.get(index);
                    userName = userName.trim().equals("")?"Unknown":userName;
                    companyName = companyName.equals("")?"Unknown":companyName;
                    walletName = walletName.equals("")?"Unknown":walletName;
                    viewHolder.textId.setText("Credential Issued From:\n" + userName + " from " + companyName + "\n" + "Wallet Name: " + walletName);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            viewHolder.textType.setText("New Credential Issuance");
        }
    }

    @Override
    public int getItemCount()
    {
        return activity.messageidList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DATA;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout layout;
        public TextView textType;
        public TextView textId;
        public TextView textAction;

        public ItemViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_region);
            textType = view.findViewById(R.id.text_type);
            textId = view.findViewById(R.id.text_id);
            textAction = view.findViewById(R.id.text_action);
        }
    }
}