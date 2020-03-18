package kr.fatos.tnavi.tnavifragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import biz.fatossdk.config.FatosBuildConfig;
import biz.fatossdk.navi.RouteParam;
import biz.fatossdk.newanavi.ANaviApplication;
import biz.fatossdk.newanavi.base.AMapBaseFragment;
import biz.fatossdk.newanavi.setting.AMapRouteLineSelectorActivity;
import kr.fatos.tnavi.Activity.RouteOptionPopupActivity;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.TNaviMainActivity;
import kr.fatos.tnavi.Unit.settingItemDetailList;
import kr.fatos.tnavi.Unit.settingItemListAdapter;

/**
 * Created by kyungilwoo on 2016. 10. 17..
 */

public class NaviSettingFragment extends AMapBaseFragment {
    // 리스트 종류
    public static final int SETTING_NAVI_ROUTELINE_COLOR = 0;
    public static final int SETTING_NAVI_ROUTE_OPTION = 1;
    public static final int SETTING_NAVI_ROUTE_NIGHTMODE= 2;
    public static final int SETTING_NAVI_ROUTE_DYNAMICZOOM = 3;
    public static final int SETTING_NAVI_RP_SERVER = 4;

    private ListView m_SettingListView;
    private ArrayList<settingItemDetailList> arSettingDessert = new ArrayList<settingItemDetailList>();
    private settingItemListAdapter settingAdapter;

    static final int[] SETTING_NAVI_MENU_NAME = new int[]{R.string.string_hiroutelinecolor, R.string.string_hirouteoptions,
            R.string.string_hinightmode, R.string.string_hidynamiczoom, R.string.string_rpserver};
    static final String[] SETTING_NAVI_MENU_DATA_NAME = new String[]{"", "최적", "Off", "On", "FATOS"};
    static boolean[] SETTING_NAVI_MENU_NAME_ENABLE = new boolean[]{false, true, false, true, true};
    static int[] SETTING_NAVI_MENU_NAME_TYPE = new int[]{settingItemDetailList.SETTING_TYPE_ROUTELINE, settingItemDetailList.SETTING_TYPE_TEXT,
            settingItemDetailList.SETTING_TYPE_TEXT,  settingItemDetailList.SETTING_TYPE_TEXT, settingItemDetailList.SETTING_TYPE_TEXT};

    private Context m_Context = null;
    private ANaviApplication m_gApp;

    private boolean m_bPortrait = false;
    //==============================================================================================
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.navisetting, container, false);
        m_Context = getActivity();
        m_gApp = (ANaviApplication) m_Context.getApplicationContext();

        int ot = getResources().getConfiguration().orientation;

        switch (ot) {
            case Configuration.ORIENTATION_LANDSCAPE:
                m_bPortrait = false;
                break;

            case Configuration.ORIENTATION_PORTRAIT:
                m_bPortrait = true;
                break;
        }

        arSettingDessert.clear();

        int nIndex = 0;

        for (int i = 0; i < SETTING_NAVI_MENU_NAME.length; i++) {
            if(i == 0 || i == 1 || i == 2)
            {
                continue;
            }

            settingItemDetailList settingList = new settingItemDetailList();
            settingList.m_strSettingName = getResources().getString(SETTING_NAVI_MENU_NAME[i]);
            settingList.m_bEnable = SETTING_NAVI_MENU_NAME_ENABLE[i];
            settingList.m_nType = SETTING_NAVI_MENU_NAME_TYPE[i];
            settingList.m_strSettingDataName = SETTING_NAVI_MENU_DATA_NAME[i];

            switch (i) {
                case SETTING_NAVI_ROUTELINE_COLOR: {

                }
                break;

                case SETTING_NAVI_ROUTE_OPTION: {
                    settingList.m_strSettingDataName = " >";
                }
                break;

                case SETTING_NAVI_ROUTE_DYNAMICZOOM: {
                    if (m_gApp.getAppSettingInfo().m_bDynamicZoom)
                        settingList.m_strSettingDataName = "On";
                    else
                        settingList.m_strSettingDataName = "Off";
                }
                break;

                case SETTING_NAVI_RP_SERVER :
                {
                    settingList.m_strSettingDataName = m_gApp.getServerText();

                    break;
                }
            }

            arSettingDessert.add(nIndex, settingList);

            nIndex++;

            if (arSettingDessert != null) {
                settingAdapter = new settingItemListAdapter(getActivity(), arSettingDessert);
            }

            m_SettingListView = v.findViewById(R.id.we_list_navi_setting);
            m_SettingListView.setAdapter(settingAdapter);

            m_SettingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (arSettingDessert.get(position).m_bEnable == false) {
                        return;
                    }

                    if(arSettingDessert.get(position).m_strSettingName.equals(getResources().getString(R.string.string_hiroutelinecolor)))
                    {
                        if (m_bPortrait) {
                            Intent intentSearchkeyword = new Intent(getActivity(), AMapRouteLineSelectorActivity.class);
                            startActivity(intentSearchkeyword);
                        }
                    }
                    else if(arSettingDessert.get(position).m_strSettingName.equals(getResources().getString(R.string.string_hirouteoptions)))
                    {
                        Intent intentSearchkeyword = new Intent(m_Context, RouteOptionPopupActivity.class);
                        startActivityForResult(intentSearchkeyword, position);
                    }
                    else if(arSettingDessert.get(position).m_strSettingName.equals(getResources().getString(R.string.string_hinightmode)))
                    {

                    }
                    else if(arSettingDessert.get(position).m_strSettingName.equals(getResources().getString(R.string.string_hidynamiczoom)))
                    {
                        m_gApp.getAppSettingInfo().m_bDynamicZoom = !m_gApp.getAppSettingInfo().m_bDynamicZoom;
                        m_gApp.saveSettingInfo(m_Context, m_gApp.getAppSettingInfo());

                        if (m_gApp.getAppSettingInfo().m_bDynamicZoom) {
                            arSettingDessert.get(position).m_strSettingDataName = "On";
                        }
                        else {
                            arSettingDessert.get(position).m_strSettingDataName = "Off";
                        }

                        settingAdapter.notifyDataSetChanged();
                    }
                    else if(arSettingDessert.get(position).m_strSettingName.equals(getResources().getString(R.string.string_rpserver)))
                    {
                        m_gApp.setRPType();
                        arSettingDessert.get(position).m_strSettingDataName = m_gApp.getServerText();

                        m_gApp.saveSettingInfo(m_Context, m_gApp.getAppSettingInfo());
                        settingAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        return v;
    }
    //==============================================================================================
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    //==============================================================================================
    @Override
    public void onResume() {
        super.onResume();
    }
    //==============================================================================================
}