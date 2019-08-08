package my.com.clarify.oneidentity.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.activity.PaymentWalletListActivity;

public class PaymentWalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public int VIEW_TYPE_DATA = 0;
    public PaymentWalletListActivity activity;
    int scrollCount = 0;
    public PaymentWalletAdapter(PaymentWalletListActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.list_item_payment_wallet, viewGroup, false);
        ItemViewHolder listHolder = new ItemViewHolder(mainGroup);
        return listHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final ItemViewHolder viewHolder = (ItemViewHolder)holder;
        viewHolder.textCurrency.setText(activity.currencyList.get(position));
        viewHolder.textAddress.setText("Address: " + activity.addressList.get(position));
        viewHolder.textCreated.setText("Created: " + activity.createdList.get(position));
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
        return activity.addressList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DATA;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout layout;
        public TextView textCurrency;
        public TextView textAddress;
        public TextView textCreated;

        public ItemViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_region);
            textCurrency = view.findViewById(R.id.text_currency);
            textAddress = view.findViewById(R.id.text_address);
            textCreated = view.findViewById(R.id.text_created);
        }
    }
}