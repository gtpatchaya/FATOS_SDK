package kr.fatos.tnavi.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.parceler.Parcels;

import java.util.ArrayList;

import kr.fatos.tnavi.Code.SettingsCode;
import kr.fatos.tnavi.Code.TNaviActionCode;
import kr.fatos.tnavi.Lib.GUtilLib;
import kr.fatos.tnavi.Lib.GoLib;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.TNaviMainActivity;
import kr.fatos.tnavi.Unit.NPoiItem;
import kr.fatos.tnavi.tnavifragment.SearchFragment;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private int resource;
    private ArrayList<NPoiItem> itemList;
    private String action_route;
    private static String app_mode;
    SearchFragment searchFragment;
    private boolean m_bVisibleFlag;

    private final static String TAG = SearchAdapter.class.getSimpleName();

    public SearchAdapter(Context context, int resource, ArrayList<NPoiItem> itemList, String action_route, String app_mode, SearchFragment i_searchFragment) {
        this.context = context;
        this.resource = resource;
        this.itemList = itemList;
        this.action_route = action_route;
        this.app_mode = app_mode;
        this.searchFragment = i_searchFragment;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final NPoiItem item = this.itemList.get(i);

        if(item != null) {
            viewHolder.textView_Name.setText(item.getEnglishName());

            viewHolder.textView_Address.setText(item.getAddressFull());

            String strTempDist = "";

            if (SettingsCode.getDistanceUnit() == 0)
            {
                strTempDist = GUtilLib.getInstance(context).updateTotalRemainDist((int) item.getDistance());
            }
            else
            {
                strTempDist = GUtilLib.getInstance(context).updateTotalRemainDistForMile((int) item.getDistance());
            }

            viewHolder.text_dist.setText(String.valueOf(strTempDist));

            if(m_bVisibleFlag)
            {
                viewHolder.text_dist.setVisibility(View.VISIBLE);
            }
            else
            {
                viewHolder.text_dist.setVisibility(View.GONE);
            }
        }

        //지도보기 선택
        viewHolder.button_MapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //bundle.putString(TNaviActionCode.APP_MODE, app_mode);
                bundle.putString(TNaviActionCode.ROUTE_VIA_OR_GOAL, action_route);
                bundle.putString(TNaviActionCode.APP_MODE, TNaviActionCode.APP_MODE_SHOW_MAP);
                bundle.putInt(TNaviActionCode.SEARCH_KIND, searchFragment.m_nSearchType);
                bundle.putParcelable(TNaviActionCode.POI_ITEM, Parcels.wrap(item));
                GoLib.getInstance().goTNaviMainActivity(context, bundle);
            }
        });
        //경로안내 선택
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFragment.setKeyboardVisible(false);

                if(searchFragment.m_nSearchType == 0) {
                    searchFragment.changeFocus();
                    searchFragment.setStartText(item);

                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString(TNaviActionCode.APP_MODE, app_mode);
                bundle.putString(TNaviActionCode.ROUTE_VIA_OR_GOAL, action_route);
                bundle.putParcelable(TNaviActionCode.POI_ITEM, Parcels.wrap(item));
                GoLib.getInstance().goTNaviMainActivity(context, bundle);

                ((TNaviMainActivity)context).setSearchWord("");
                ((TNaviMainActivity)context).clearSearchList();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void addItemList(ArrayList<NPoiItem> itemList)
    {
        clearItemList();

        this.itemList.addAll(itemList);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void clearItemList()
    {
        if(this.itemList.size() > 0)
        {
            this.itemList.clear();

            notifyDataSetChanged();
        }
    }

    public void setDistVisible(boolean i_bFlag)
    {
        m_bVisibleFlag = i_bFlag;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView_Name, textView_Address, text_dist;
        Button button_MapView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_Name = itemView.findViewById(R.id.textView_Name);
            textView_Address = itemView.findViewById(R.id.textView_Address);
            text_dist = itemView.findViewById(R.id.text_dist);
            button_MapView = itemView.findViewById(R.id.button_MapView);
        }
    }
}