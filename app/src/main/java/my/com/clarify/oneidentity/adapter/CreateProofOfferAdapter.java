package my.com.clarify.oneidentity.adapter;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.activity.CreateProofOfferActivity;

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
            viewHolder.inputValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    activity.attributeValueList.set(position, charSequence + "");
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
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