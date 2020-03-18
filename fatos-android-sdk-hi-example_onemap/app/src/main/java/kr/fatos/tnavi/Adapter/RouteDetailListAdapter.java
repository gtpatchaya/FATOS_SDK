package kr.fatos.tnavi.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import biz.fatossdk.fatosmaphi.FMPConst;
import biz.fatossdk.newanavi.ANaviApplication;
import kr.fatos.tnavi.List.RouteDetailList;
import kr.fatos.tnavi.R;

import static biz.fatossdk.newanavi.manager.AMapResouceManager.m_naNextTbtOnemap;

public class RouteDetailListAdapter extends BaseAdapter {
    private ArrayList<RouteDetailList> rgItemDetailsrrayList;
    private LayoutInflater l_Inflater;

    private Context m_Context = null;
    private ANaviApplication m_gApp;
    private static int mSelectedPosition = -1;

    public RouteDetailListAdapter(Context context, ArrayList<RouteDetailList> results) {
        m_Context = context;
        rgItemDetailsrrayList = results;
        l_Inflater = LayoutInflater.from(context);
        m_gApp = (ANaviApplication) m_Context.getApplicationContext();
    }

    @SuppressLint("DefaultLocale")
    public String updateTotalRemainTime(int secTime) {

        String strResult = "", strTemp;
        Calendar now = Calendar.getInstance();
//		Date curDate = now.getTime();
        if(secTime < 60)
            secTime = 60;

        if(m_gApp.getAppSettingInfo().m_bArriveTime)
        {
            Calendar tmp = (Calendar) now.clone();
            tmp.add(Calendar.SECOND, secTime);

            DateFormat outputFormat = new SimpleDateFormat("hh:mm a",new Locale("en", "us"));
            strTemp = outputFormat.format(tmp.getTime());
            strResult = strTemp.replace("AM", "am").replace("PM","pm");
            //Log.i("WOO", formattedTime);
        }
        else
        {
            int minutes = 0;
            minutes = (int) (secTime / 60);
            if (60 > minutes) {
                strResult = String.format("%02dmin", minutes);
            } else {
                int hour = (int) (minutes / 60);
                int extraMin = minutes % 60;
                if (extraMin == 0) {
                    strResult = String.format("%02dhour", hour);
                } else {
                    strResult = String.format("%02dh %02dm", hour, extraMin);
                }
            }
        }

        return strResult;
    }
    //

    public int getCount() {
        return rgItemDetailsrrayList.size();
    }

    public Object getItem(int position) {
        return rgItemDetailsrrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.route_detail_list, null);
            holder = new ViewHolder();
            holder.txt_itemStrInfoName = (TextView) convertView.findViewById(R.id.crossname);
            holder.txt_itemStrDist = (TextView) convertView.findViewById(R.id.strdist);
            holder.itemTbtImage = (ImageView) convertView.findViewById(R.id.tbtimg);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt_itemStrInfoName.setText(rgItemDetailsrrayList.get(position).getTxtInfoName());
        holder.txt_itemStrDist.setText(rgItemDetailsrrayList.get(position).getDist());

        holder.itemTbtImage.setImageResource(getTbtImageRes(rgItemDetailsrrayList.get(position).getTbtCode()));

        if (position == mSelectedPosition) {
            convertView.setBackgroundColor(ContextCompat.getColor(m_Context, R.color.fmp_list_addr_search_color_sel));
        } else {

            convertView.setBackgroundColor(ContextCompat.getColor(m_Context, R.color.cardview_onemap_background));
        }

        return convertView;
    }

    /**
     * 향후 작업 중 필요한 항목 추가 한다.
     */
    static class ViewHolder {
        String strPosX;
        String strPosY;
        TextView txt_itemStrInfoName;
        TextView txt_itemStrDist;
        ImageView itemTbtImage;
    }

    public int getTbtImageRes(int idx) {
        if(idx >= FMPConst.eTurn_viaGoalS && idx <= FMPConst.eTurn_viaGoalE)
        {
            idx = 55;
        }
        else if(idx <= 0 || idx >= 55) {
            return -1;
        }

        int res = m_naNextTbtOnemap[idx - 1];

        return (res);
    }

    public void setSelectedItem (int itemPosition) {
        mSelectedPosition = itemPosition;
        notifyDataSetChanged();
    }
}
