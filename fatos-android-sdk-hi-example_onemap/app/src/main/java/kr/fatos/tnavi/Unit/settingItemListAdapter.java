package kr.fatos.tnavi.Unit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import biz.fatossdk.anavi.R;
import biz.fatossdk.newanavi.ANaviApplication;


/**
 * 설정 정보 리스트 갱신 Adapter
 *
 * @author p0576
 */
public class settingItemListAdapter extends BaseAdapter {
    public static final String TAG = "AMAP";

    private ArrayList<settingItemDetailList> settingItemList;
    private LayoutInflater l_Inflater;

    private Context m_Context = null;
    private ANaviApplication m_gApp;
    public settingItemListAdapter(Context context, ArrayList<settingItemDetailList> results) {
        m_Context = context;
        settingItemList = results;
        l_Inflater = LayoutInflater.from(context);
        m_gApp = (ANaviApplication) m_Context.getApplicationContext();
    }


    public int getCount() {
        return settingItemList.size();
    }

    public Object getItem(int position) {
        return settingItemList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.list_setting, null);
            holder = new ViewHolder();
            holder.txt_settingTitleName = (TextView) convertView.findViewById(R.id.setting_title);
            holder.txt_settingDataName = (TextView) convertView.findViewById(R.id.setting_data);
            holder.btn_RouteLineColor = (Button) convertView.findViewById(R.id.route_line_color);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt_settingTitleName.setText(settingItemList.get(position).m_strSettingName);
        holder.txt_settingDataName.setText(settingItemList.get(position).m_strSettingDataName);

        switch (settingItemList.get(position).m_nType) {
            case settingItemDetailList.SETTING_TYPE_TEXT: {
                holder.txt_settingDataName.setVisibility(View.VISIBLE);
                holder.btn_RouteLineColor.setVisibility(View.GONE);
                holder.txt_settingTitleName.setTextColor(m_Context.getResources().getColor(R.color.black));
                holder.txt_settingDataName.setTextColor(m_Context.getResources().getColor(R.color.black));

            }
            break;
            case settingItemDetailList.SETTING_TYPE_ROUTELINE: {
                holder.txt_settingDataName.setVisibility(View.GONE);
                holder.btn_RouteLineColor.setVisibility(View.VISIBLE);
                holder.txt_settingTitleName.setTextColor(m_Context.getResources().getColor(R.color.black));
                holder.btn_RouteLineColor.setBackgroundColor(m_Context.getResources().getColor(R.color.red));
                //holder.btn_RouteLineColor.setBackgroundColor(m_gApp.getAppSettingInfo().m_nRouteColor);

            }
            break;
            case settingItemDetailList.SETTING_TYPE_IMG: {
                holder.txt_settingDataName.setVisibility(View.GONE);
                holder.btn_RouteLineColor.setVisibility(View.GONE);
            }
            break;
            case settingItemDetailList.SETTING_TYPE_NEXTPAGE: {
                holder.txt_settingDataName.setVisibility(View.VISIBLE);
                holder.btn_RouteLineColor.setVisibility(View.GONE);
                if (settingItemList.get(position).m_bEnable) {
                    holder.txt_settingTitleName.setTextColor(m_Context.getResources().getColor(R.color.black));
                    holder.txt_settingDataName.setTextColor(m_Context.getResources().getColor(R.color.black));
                }
            }break;
        }

        if (settingItemList.get(position).m_bEnable == false) {
            holder.txt_settingTitleName.setTextColor(m_Context.getResources().getColor(R.color.lightGray));
            holder.txt_settingDataName.setTextColor(m_Context.getResources().getColor(R.color.lightGray));
        }

        return convertView;
    }

    /**
     * 향후 작업 중 필요한 항목 추가 한다.
     */
    static class ViewHolder {
        TextView txt_settingTitleName;
        TextView txt_settingDataName;
        Button btn_RouteLineColor;
    }

}
