package kr.fatos.tnavi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


import biz.fatossdk.newanavi.ANaviApplication;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.Unit.CountryItemList;

/**
 * fatos country manager
 */
public class CountryItemListAdapter extends BaseAdapter {
    private ArrayList<CountryItemList> CountryItemList;
    private LayoutInflater l_Inflater;

    private Context m_Context = null;
    private ANaviApplication m_gApp;

    private int m_nSavedIdx = -1;
    public CountryItemListAdapter(Context context, ArrayList<CountryItemList> results) {
        m_Context = context;
        CountryItemList = results;
        l_Inflater = LayoutInflater.from(context);
        m_gApp = (ANaviApplication) m_Context.getApplicationContext();
    }

    public void setSavedItem(int nItemIdx)
    {
        m_nSavedIdx =nItemIdx;
    }


    public int getCount() {
        return CountryItemList.size();
    }

    public Object getItem(int position) {
        return CountryItemList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.country_row, null);
            holder = new ViewHolder();
            holder.txt_counturyName = (TextView) convertView.findViewById(R.id.name);
            //holder.img_counturyFlag = (ImageView) convertView.findViewById(R.id.flag);
            holder.img_checkItem = (ImageView) convertView.findViewById(R.id.rb_Choice);
            holder.img_checkItem.setVisibility(View.INVISIBLE);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt_counturyName.setText(CountryItemList.get(position).getName());
        //holder.img_counturyFlag.setImageDrawable(CountryItemList.get(position).getFlag());

        if(m_nSavedIdx == position)
        {
            holder.img_checkItem.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.img_checkItem.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    /**
     * 향후 작업 중 필요한 항목 추가 한다.
     */
    static class ViewHolder {
        TextView txt_counturyName;
        //ImageView img_counturyFlag;
        ImageView img_checkItem;
    }

}
