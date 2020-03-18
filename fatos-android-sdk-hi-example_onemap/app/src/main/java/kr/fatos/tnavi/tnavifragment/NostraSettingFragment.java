package kr.fatos.tnavi.tnavifragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.UUID;

import biz.fatossdk.config.FatosBuildConfig;
import biz.fatossdk.newanavi.ANaviApplication;
import biz.fatossdk.newanavi.manager.AMapDataTransferCode;
import kr.fatos.tnavi.Activity.CategoryActivity;
import kr.fatos.tnavi.Activity.CopyrightActivity;
import kr.fatos.tnavi.Activity.CountrySelectActivity;
import kr.fatos.tnavi.Activity.HiddenSettingActivity;
import kr.fatos.tnavi.Activity.TermOfUseActivity;
import kr.fatos.tnavi.Activity.VersionInfoActivity;
import kr.fatos.tnavi.Code.SettingsCode;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.Unit.settingItemDetailList;
import kr.fatos.tnavi.Unit.settingItemListAdapter;

import static android.content.Context.MODE_PRIVATE;

public class NostraSettingFragment extends Fragment
{
    public static final String TAG = "FATOSMapHI" + NostraSettingFragment.class.getSimpleName();

    private PopupWindow m_PopupWindow;

    // 리스트 종류
    public static final int SETTING_COUNTRY = 0;
    public static final int SETTING_CATEGORY = 1;
    //    public static final int SETTING_SPEEDUNIT = 2; //스피드단위 주석
    //    public static final int SETTING_DISTANCEUNIT = 3; //거리단위 주석
    public static final int SETTING_TERMOFUSE = 2;
    public static final int SETTING_VERSIONINFO = 3;
    public static final int SETTING_COPYRIGHT = 4;
    public static final int SETTING_UUID = 5;

    private int default_language = 0;
    private static SharedPreferences prefs;
    private ListView m_SettingListView;
    private ArrayList<settingItemDetailList> arSettingDessert = new ArrayList<settingItemDetailList>();
    private settingItemListAdapter settingAdapter;

    static int[] SETTING_MENU_NAME = new int[]{
            R.string.string_nostrasetting_country,
            R.string.string_nostrasetting_category,
            //카테고리
            //            R.string.string_wespeed_unit, //스피드단위 주석
            //            R.string.string_wedistance_unit, //거리단위 주석
            R.string.string_weterm_of_use,
            R.string.string_weversion_info,
            R.string.string_wecopyright,
            R.string.string_weuuid
    };

    static boolean[] SETTING_MENU_NAME_ENABLE = new boolean[]{
            true, true,
            //            false, false,
            true, true, true, false
    };

    static int[] SETTING_MENU_NAME_TYPE = new int[]{
            settingItemDetailList.SETTING_TYPE_TEXT,
            settingItemDetailList.SETTING_TYPE_TEXT,
            //카테고리
            //            settingItemDetailList.SETTING_TYPE_TEXT, //스피드단위 주석
            //            settingItemDetailList.SETTING_TYPE_NEXTPAGE, //거리단위 주석
            settingItemDetailList.SETTING_TYPE_NEXTPAGE,
            settingItemDetailList.SETTING_TYPE_NEXTPAGE,
            settingItemDetailList.SETTING_TYPE_NEXTPAGE,
            settingItemDetailList.SETTING_TYPE_TEXT
    };

    static final int[] SETTING_SPEED_UNIT_NAME = new int[]{
            biz.fatossdk.anavi.R.string.string_speed_km,
            biz.fatossdk.anavi.R.string.string_speed_mi
    };
    static final int[] SETTING_DISTANCE_UNIT_NAME = new int[]{
            biz.fatossdk.anavi.R.string.string_km,
            biz.fatossdk.anavi.R.string.string_mi
    };

    public String[] countrynames;

    private Context m_Context = null;
    private ANaviApplication m_gApp;
    private boolean m_bPortrait = false;

    private boolean m_bIsLongPress = false;
    final Handler someHandler = new Handler();
    final int duration = 2000;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_nostra_setting, container, false);

        m_Context = getActivity();
        prefs = m_Context.getSharedPreferences(getResources().getString(R.string.app_registerId), MODE_PRIVATE);
        final IntentFilter filter = new IntentFilter();
        filter.addAction("RELOAD_ACTIVITY");
        m_Context.registerReceiver(quickMenuFinishReceiver, filter);

        m_gApp = (ANaviApplication)m_Context.getApplicationContext();

        SETTING_MENU_NAME[0] = R.string.string_nostrasetting_language;
        countrynames = getResources().getStringArray(R.array.onemap_country_names);

        int ot = getResources().getConfiguration().orientation;
        switch(ot)
        {
            case Configuration.ORIENTATION_LANDSCAPE:
                m_bPortrait = false;
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                m_bPortrait = true;
                break;
        }

        arSettingDessert.clear();

        int nIndex = 0;

        for(int i = 0; i < SETTING_MENU_NAME.length; i++)
        {
            if(i == 1)
            {
                continue;
            }

            settingItemDetailList settingList = new settingItemDetailList();
            settingList.m_strSettingName = getResources().getString(SETTING_MENU_NAME[i]);
            settingList.m_bEnable = SETTING_MENU_NAME_ENABLE[i];
            settingList.m_nType = SETTING_MENU_NAME_TYPE[i];

            switch(i)
            {
                case SETTING_COUNTRY:
                {
                    String country = countrynames[SettingsCode.getValueIndex()];
                    settingList.m_strSettingDataName = country + " >";
                }
                break;

                case SETTING_CATEGORY:
                {
                    String category = SettingsCode.getValueCategory();
                    settingList.m_strSettingDataName = category;
                }
                break;

                case SETTING_TERMOFUSE:
                {
                    settingList.m_strSettingDataName = " >";
                }
                break;

                case SETTING_VERSIONINFO:
                {
                    settingList.m_strSettingDataName = " >";
                }
                break;

                case SETTING_COPYRIGHT:
                {
                    settingList.m_strSettingDataName = " >";
                }
                break;

                case SETTING_UUID:
                {
                    if(!TextUtils.isEmpty(m_gApp.m_strUUID))
                    {
                        settingList.m_strSettingDataName = m_gApp.m_strUUID;
                    }

                    break;
                }
            }

            arSettingDessert.add(nIndex, settingList);

            nIndex++;
        }

        if(arSettingDessert != null)
        {
            settingAdapter = new settingItemListAdapter(getActivity(), arSettingDessert);
        }

        m_SettingListView = v.findViewById(R.id.list_common_setting);
        m_SettingListView.setAdapter(settingAdapter);
        m_SettingListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        m_SettingListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ANaviApplication.MultiClickPreventer.preventMultiClick(view);

                if(!arSettingDessert.get(position).m_bEnable)
                {
                    return;
                }

                boolean bSettingName = false;

                bSettingName = arSettingDessert.get(position).m_strSettingName.equals(getResources().getString(R.string.string_nostrasetting_language));

                if(bSettingName)
                {
                    Intent intent = new Intent(getActivity(), CountrySelectActivity.class);
                    startActivity(intent);
                    settingAdapter.notifyDataSetChanged();
                }
                else if(arSettingDessert.get(position).m_strSettingName.equals(getResources().getString(R.string.string_nostrasetting_category)))
                {
                    Intent intentSearchkeyword = new Intent(getActivity(), CategoryActivity.class);
                    startActivity(intentSearchkeyword);
                    settingAdapter.notifyDataSetChanged();
                }
                else if(arSettingDessert.get(position).m_strSettingName.equals(getResources().getString(R.string.string_weterm_of_use)))
                {
                    Intent intentSearchkeyword = new Intent(getActivity(), TermOfUseActivity.class);
                    startActivity(intentSearchkeyword);
                }
                else if(arSettingDessert.get(position).m_strSettingName.equals(getResources().getString(R.string.string_weversion_info)))
                {
                    Intent intentSearchkeyword = new Intent(getActivity(), VersionInfoActivity.class);
                    startActivity(intentSearchkeyword);
                }
                else if(arSettingDessert.get(position).m_strSettingName.equals(getResources().getString(R.string.string_wecopyright)))
                {
                    Intent intentSearchkeyword = new Intent(getActivity(), CopyrightActivity.class);
                    startActivity(intentSearchkeyword);
                }
            }
        });

        m_SettingListView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int eventAction = event.getAction();

                if(eventAction == MotionEvent.ACTION_DOWN)
                {
                    m_bIsLongPress = true;
                    someHandler.postDelayed(someCall, duration);
                }
                else if(eventAction == MotionEvent.ACTION_UP)
                {
                    m_bIsLongPress = false;
                    someHandler.removeCallbacks(someCall);
                }

                return false;
            }
        });

        return v;
    }

    final Runnable someCall = new Runnable()
    {
        @Override
        public void run()
        {
            if(m_bIsLongPress)
            {
                Intent intentSearchkeyword = new Intent(getActivity(), HiddenSettingActivity.class);
                startActivity(intentSearchkeyword);
            }
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

    }

    private Button[] m_btnCarInfo;
    private Button[] m_btnOilInfo;
    private Button m_btnClose;


    @Override
    public void onResume()
    {
        super.onResume();
        //updateViewPage();
        updateMenuLanguage();
    }

    public void updateMenuLanguage()
    {
        m_gApp.updateLanguage();
        arSettingDessert.get(SETTING_COUNTRY).m_strSettingDataName = countrynames[SettingsCode.getValueIndex()] + " >";
        settingAdapter.notifyDataSetChanged();

        for(int i = 0; i < arSettingDessert.size(); i++)
        {
            settingItemDetailList settingList = arSettingDessert.get(i);
            settingList.m_strSettingName = arSettingDessert.get(i).m_strSettingName;
            settingList.m_bEnable = arSettingDessert.get(i).m_bEnable;
            settingList.m_nType = arSettingDessert.get(i).m_nType;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        switch(newConfig.orientation)
        {
            case Configuration.ORIENTATION_PORTRAIT:
                Log.d(TAG, "세로");
                m_bPortrait = true;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                Log.d(TAG, "가로");
                m_bPortrait = false;
                break;
        }
    }

    public void reloadActivity()
    {
        updateMenuLanguage();
    }

    private boolean bFinish = false;
    BroadcastReceiver quickMenuFinishReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if(action.equals("RELOAD_ACTIVITY"))
            {
                reloadActivity();
            }
        }
    };

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        m_Context.unregisterReceiver(quickMenuFinishReceiver);
    }
}
