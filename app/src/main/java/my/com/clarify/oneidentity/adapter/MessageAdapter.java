package my.com.clarify.oneidentity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.activity.MessageActivity;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public int VIEW_TYPE_DATA = 0;
    public MessageActivity activity;
    int scrollCount = 0;
    public MessageAdapter(MessageActivity activity)
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

        viewHolder.textId.setVisibility(View.VISIBLE);
        viewHolder.textSender.setVisibility(View.VISIBLE);
        if(activity.typeList.get(position).equals("MSG_TYPE_CONN_REQUEST"))
        {
            viewHolder.textType.setText(activity.getString(R.string.new_connection_request));
            viewHolder.textAction.setText(activity.getString(R.string.message_action_new_connection_request));
            viewHolder.textDatetime.setText(activity.createdList.get(position));
            viewHolder.textId.setVisibility(View.GONE);
            viewHolder.textSender.setVisibility(View.GONE);
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_CONN_ACCEPT"))
        {
            viewHolder.textType.setText(activity.getString(R.string.connection_request_accepted));
            viewHolder.textAction.setText(activity.getString(R.string.message_action_acknowledge_connection));
            viewHolder.textDatetime.setText(activity.createdList.get(position));
            viewHolder.textId.setVisibility(View.GONE);
            viewHolder.textSender.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject(activity.messageList.get(position));
                String userName = "";
                String companyName = "";
                String walletName = "";
                userName = userName.trim().equals("")?"Unknown":userName;
                companyName = companyName.equals("")?"Unknown":companyName;
                walletName = walletName.equals("")?"Unknown":walletName;
                viewHolder.textId.setText(userName + " from " + companyName + "\n" + "Wallet Name: " + walletName);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_SECURE_SEND"))
        {
            viewHolder.textType.setText(activity.getString(R.string.secure_message));
            viewHolder.textAction.setText(activity.getString(R.string.message_action_secure_message));
            viewHolder.textDatetime.setText(activity.createdList.get(position));

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
                    viewHolder.textSender.setText(activity.nameList.get(index));
                    String userName = activity.usernameList.get(index);
                    String walletName = activity.walletNameList.get(index);
                    userName = userName.trim().equals("")?"Unknown":userName;
                    walletName = walletName.equals("")?"Unknown":walletName;
                    //viewHolder.textId.setText("Message From:\n" + userName + "\n" + "Wallet Name: " + walletName + "\n" + "Message : " + jsonObject.getString("message"));
                }
                viewHolder.textId.setText(jsonObject.getString("message"));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_CREDENTIAL_OFFER"))
        {
            viewHolder.textType.setText(activity.getString(R.string.new_credential_offer));
            viewHolder.textDatetime.setText(activity.createdList.get(position));
            viewHolder.textAction.setText(activity.getString(R.string.message_action_new_credential_offer));
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
                    viewHolder.textSender.setText(activity.nameList.get(index));
                    String userName = activity.usernameList.get(index);
                    String walletName = activity.walletNameList.get(index);
                    userName = userName.trim().equals("")?"Unknown":userName;
                    walletName = walletName.equals("")?"Unknown":walletName;
                    viewHolder.textId.setVisibility(View.GONE);
                    //viewHolder.textId.setText("Credential Offer From:\n" + userName + "\n" + "Wallet Name: " + walletName);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_CREDENTIAL_REQUEST"))
        {
            viewHolder.textType.setText(activity.getString(R.string.new_credential_request));
            viewHolder.textAction.setText(activity.getString(R.string.message_action_new_credential_request));
            viewHolder.textDatetime.setText(activity.createdList.get(position));
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
                    viewHolder.textSender.setText(activity.nameList.get(index));
                    String userName = activity.usernameList.get(index);
                    String walletName = activity.walletNameList.get(index);
                    userName = userName.trim().equals("")?"Unknown":userName;
                    walletName = walletName.equals("")?"Unknown":walletName;
                    viewHolder.textId.setVisibility(View.GONE);
                    //viewHolder.textId.setText("Credential Request From:\n" + userName + "\n" + "Wallet Name: " + walletName);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_PROOF_REQUEST"))
        {
            viewHolder.textType.setText(activity.getString(R.string.proof_request));
            viewHolder.textAction.setText(activity.getString(R.string.message_action_new_proof_offer));
            viewHolder.textDatetime.setText(activity.createdList.get(position));
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
                    viewHolder.textSender.setText(activity.nameList.get(index));
                    String userName = activity.usernameList.get(index);
                    String walletName = activity.walletNameList.get(index);
                    userName = userName.trim().equals("")?"Unknown":userName;
                    walletName = walletName.equals("")?"Unknown":walletName;
                    viewHolder.textId.setVisibility(View.GONE);
                    //viewHolder.textId.setText("Proof Request From:\n" + userName + "\n" + "Wallet Name: " + walletName);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_PROOF_OFFER"))
        {
            viewHolder.textType.setText(activity.getString(R.string.new_proof_offer));
            viewHolder.textAction.setText(activity.getString(R.string.message_action_new_proof_offer));
            viewHolder.textDatetime.setText(activity.createdList.get(position));
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
                    viewHolder.textSender.setText(activity.nameList.get(index));
                    String userName = activity.usernameList.get(index);
                    String walletName = activity.walletNameList.get(index);
                    userName = userName.trim().equals("")?"Unknown":userName;
                    walletName = walletName.equals("")?"Unknown":walletName;
                    viewHolder.textId.setVisibility(View.GONE);
                    //viewHolder.textId.setText("Proof Offer From:\n" + userName + "\n" + "Wallet Name: " + walletName);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else if(activity.typeList.get(position).equals("MSG_TYPE_CREDENTIAL_ACCEPT_REQUEST"))
        {
            viewHolder.textType.setText(activity.getString(R.string.new_credential_issuance));
            viewHolder.textAction.setText(activity.getString(R.string.message_action_new_credential_issuance));
            viewHolder.textDatetime.setText(activity.createdList.get(position));
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
                    viewHolder.textSender.setText(activity.nameList.get(index));
                    String userName = activity.usernameList.get(index);
                    String walletName = activity.walletNameList.get(index);
                    userName = userName.trim().equals("")?"Unknown":userName;
                    walletName = walletName.equals("")?"Unknown":walletName;
                    viewHolder.textId.setVisibility(View.GONE);
                    //viewHolder.textId.setText("Credential Issued From:\n" + userName + "\n" + "Wallet Name: " + walletName);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
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
        public TextView textDatetime;
        public TextView textSender;

        public ItemViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_region);
            textType = view.findViewById(R.id.text_type);
            textId = view.findViewById(R.id.text_detail);
            textAction = view.findViewById(R.id.text_action);
            textDatetime = view.findViewById(R.id.text_datetime);
            textSender = view.findViewById(R.id.text_sender);
        }
    }
}