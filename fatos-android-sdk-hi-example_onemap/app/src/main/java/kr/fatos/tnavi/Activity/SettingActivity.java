package kr.fatos.tnavi.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.Locale;

import biz.fatossdk.exlib.smarttablayout.SmartTabLayout;
import biz.fatossdk.exlib.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import biz.fatossdk.exlib.smarttablayout.utils.v4.FragmentPagerItems;
import biz.fatossdk.fminterface.FMBaseActivity;
import biz.fatossdk.newanavi.ANaviApplication;
import biz.fatossdk.tts.TTSEngine;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.tnavifragment.NaviSettingFragment;
import kr.fatos.tnavi.tnavifragment.NostraSettingFragment;

public class SettingActivity extends FMBaseActivity
{

    public static final String TAG = "FATOSMapHI" + SettingActivity.class.getSimpleName();
    private ANaviApplication m_gApp;
    private Button m_btnBack, m_btnHome;
    private Button[] m_btnCarInfo;
    private Button[] m_btnOilInfo;
    private Context m_Context = null;
    private TextView m_txtTitle, m_txtVehicletype, m_txtCartype, m_txtRouteLineTitle;
    private boolean m_bPortrait = false;

    /*static final int[] SETTING_CARINFO_NAME = new int[]{R.string.string_compact, R.string.string_standard,
            R.string.string_midvan, R.string.string_lvan, R.string.string_truck, R.string.string_spurpose};
    static final int[] SETTING_OIL_NAME = new int[]{R.string.string_gasoline, R.string.string_premium,
            R.string.string_diesel, R.string.string_lpg, R.string.string_electricity};
*/
    private FragmentPagerItemAdapter adapter;
    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;
    private LinearLayout m_CarInfoLayout, m_RouteLineLayout;

    private static int activeInstances = 0;
    private Button m_btnRouteColor0, m_btnRouteColor1, m_btnRouteColor2, m_btnRouteColor3, m_btnRouteColor4, m_btnRouteColor5, m_btnRouteColor6;
    private Button m_btnRouteColor7, m_btnRouteColor8;
    static int[] SETTING_NAVI_MENU_NAME_ROUTECOLOR = new int[]{
            Color.argb(255, 255, 0, 0),
            Color.argb(255, 0, 255, 0),
            Color.argb(255, 0, 0, 255),
            Color.argb(255, 255, 0, 255),
            Color.argb(255, 255, 255, 0),
            Color.argb(255, 117, 157, 249),
            Color.argb(255, 127, 127, 127),
            Color.argb(255, 127, 63, 0),
            Color.argb(255, 0, 63, 127)
    };

    private int[] selectRouteLineColor = new int[2];
    private int[] nonSelectRouteLineColor = new int[2];

    public SettingActivity()
    {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        m_Context = this;
        m_gApp = (ANaviApplication)m_Context.getApplicationContext();
        updateLocale();
        setContentView(R.layout.activity_setting);

        final IntentFilter filter = new IntentFilter();
        filter.addAction("RELOAD_ACTIVITY");
        m_Context.registerReceiver(quickMenuFinishReceiver, filter);

        //updateMenuLanguage();

        adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this).add(getString(R.string.string_wegeneral), NostraSettingFragment.class).add(getString(R.string.string_wenavigation), NaviSettingFragment.class).create());
        //int title, float width, Class<? extends Fragment> clazz,
        //        Bundle args)
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        viewPagerTab = (SmartTabLayout)findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        m_btnBack = (Button)findViewById(R.id.setting_search_back_btn);
        m_btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        m_btnHome = (Button)findViewById(R.id.setting_search_btn_cancel);
        m_btnHome.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //                Intent intent = new Intent(m_Context, AMapMainActivity.class);
                //                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //                setResult(RESULT_OK,intent);
                //                startActivity(intent);
                //                finish();
                Intent intent = new Intent();
                intent.setAction("RESULT_FINISH"); // Action name
                sendBroadcast(intent);

                finish();
            }
        });

        m_txtTitle = (TextView)findViewById(R.id.poi_search_text_view);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        m_Context.unregisterReceiver(quickMenuFinishReceiver);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void finishActivity(int requestCode)
    {
        super.finishActivity(requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume()
    {
        updateMenuLanguage();
        super.onResume();
    }

    public void updateLocale()
    {
        //Locale locale = m_gApp.getFatosLocale();
        Locale locale = new Locale(LOCALE_NAME_LIST[m_gApp.getAppSettingInfo().m_nDefaultLanguage]);

        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, null);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        if(TTSEngine.getInstance() != null)
        {
            TTSEngine.getInstance().setLocale(locale);
        }
        Locale.setDefault(locale);
    }

    public void updateMenuLanguage()
    {
        Locale locale = m_gApp.getFatosLocale();
        locale = new Locale(LOCALE_NAME_LIST[m_gApp.getAppSettingInfo().m_nDefaultLanguage]);

        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, null);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        if(TTSEngine.getInstance() != null)
        {
            TTSEngine.getInstance().setLocale(locale);
        }

        Locale.setDefault(locale);

        m_txtTitle.setText(m_Context.getResources().getString(R.string.string_wesettings));

        adapter.startUpdate(viewPager);
        adapter.setPageTitle(0, getString(R.string.string_wegeneral));
        adapter.setPageTitle(1, getString(R.string.string_wenavigation));
        adapter.finishUpdate(viewPager);
        adapter.notifyDataSetChanged();
        viewPager.invalidate();
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

    public void reloadActivity()
    {
        if(Build.VERSION.SDK_INT >= 11)
        {
            recreate();
            //updateMenuLanguage();

        }
        else
        {
            Intent i = new Intent(this, SettingActivity.class);  //your class

            updateMenuLanguage();
            //// TODO: 2019. 2. 7. 연타하면 액티비티가 쌓이는 문제가 있음
            startActivity(i);
            overridePendingTransition(R.anim.hold, R.anim.hold);
            finish();
        }
    }

    private void updateRouteColorInfo(int nSelIdx)
    {
        m_gApp.getAppSettingInfo().m_nRouteColor = SETTING_NAVI_MENU_NAME_ROUTECOLOR[nSelIdx];
        m_gApp.saveSettingInfo(m_Context, m_gApp.getAppSettingInfo());

        selectRouteLineColor[0] = SETTING_NAVI_MENU_NAME_ROUTECOLOR[nSelIdx];
        selectRouteLineColor[1] = Color.argb(255, 0, 0, 0);
        nonSelectRouteLineColor[0] = SETTING_NAVI_MENU_NAME_ROUTECOLOR[nSelIdx];
        nonSelectRouteLineColor[1] = Color.argb(255, 0, 0, 0);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        switch(newConfig.orientation)
        {
            case Configuration.ORIENTATION_PORTRAIT:
                Log.d(TAG, "세로");
                //setContentView(R.layout.activity_settings);
                m_bPortrait = true;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                Log.d(TAG, "가로");
                //setContentView(R.layout.activity_settings);
                m_bPortrait = false;
                break;
        }
    }
}
