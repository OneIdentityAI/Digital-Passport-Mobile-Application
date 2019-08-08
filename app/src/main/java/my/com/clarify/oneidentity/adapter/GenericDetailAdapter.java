package my.com.clarify.oneidentity.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import my.com.clarify.oneidentity.R;
import my.com.clarify.oneidentity.activity.GenericDetailActivity;

public class GenericDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public int VIEW_TYPE_DATA = 0;
    public GenericDetailActivity activity;
    int scrollCount = 0;
    public GenericDetailAdapter(GenericDetailActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.list_item_generic_detail, viewGroup, false);
        ItemViewHolder listHolder = new ItemViewHolder(mainGroup);
        return listHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final ItemViewHolder viewHolder = (ItemViewHolder)holder;
        viewHolder.textName.setText(activity.nameList.get(position));
        viewHolder.inputValue.setText(activity.valueList.get(position));
        String value = activity.valueList.get(position).toLowerCase();
//        if(value.endsWith(".jpg") || value.endsWith(".jpeg") || value.endsWith(".png"))
//        {
//            viewHolder.imageUpload.setVisibility(View.VISIBLE);
//            viewHolder.inputValue.setVisibility(View.GONE);
//            Glide.with(activity).load("https://storage.1id.ai/uploads/" + activity.valueList.get(position)).into(viewHolder.imageUpload);
//        }
//        else
//        {
//            viewHolder.imageUpload.setVisibility(View.GONE);
//            viewHolder.inputValue.setVisibility(View.VISIBLE);
//        }

        if(value.endsWith(".jpg") || value.endsWith(".jpeg") || value.endsWith(".png") && !activity.base64DataList.get(position).equals(""))
        {
            viewHolder.imageUpload.setVisibility(View.VISIBLE);
            viewHolder.inputValue.setVisibility(View.GONE);
            byte[] imageByteArray = Base64.decode(activity.base64DataList.get(position), Base64.DEFAULT);
            Glide.with(activity)
                    .load(imageByteArray)
                    .into(viewHolder.imageUpload);
        }
        else
        {
            viewHolder.imageUpload.setVisibility(View.GONE);
            viewHolder.inputValue.setVisibility(View.VISIBLE);
        }
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
        public EditText inputValue;
        public ImageView imageUpload;

        public ItemViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout_region);
            textName = view.findViewById(R.id.text_name);
            inputValue = view.findViewById(R.id.input_value);
            imageUpload = view.findViewById(R.id.image_upload);
        }
    }
}