package kr.fatos.tnavi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.fatos.tnavi.Activity.CategoryActivity;
import kr.fatos.tnavi.Code.SettingsCode;
import kr.fatos.tnavi.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private int resource;
    private ArrayList<String> list;
    private CategoryActivity categoryActivity;

    public CategoryAdapter(CategoryActivity activity, int resource, ArrayList<String> list) {
        this.categoryActivity = activity;
        this.resource = resource;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final String str = list.get(i);
        final int index = list.indexOf(str);

        viewHolder.textView_Name.setText(str);

        if(i == SettingsCode.getValueCategoryIndex()){
            viewHolder.image_cheked.setVisibility(View.VISIBLE);
        }else{
            viewHolder.image_cheked.setVisibility(View.INVISIBLE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index != SettingsCode.getValueCategoryIndex()) {
                    categoryActivity.returnIntentResult(str, index);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItemList(ArrayList<String> itemList)
    {
        this.list.addAll(itemList);

        notifyDataSetChanged();
    }

    public void clearItemList()
    {
        if(this.list.size() > 0)
        {
            this.list.clear();

            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView_Name;
        ImageView image_cheked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_Name = itemView.findViewById(R.id.textView_Name);
            image_cheked = itemView.findViewById(R.id.rb_Choice);

        }
    }
}
