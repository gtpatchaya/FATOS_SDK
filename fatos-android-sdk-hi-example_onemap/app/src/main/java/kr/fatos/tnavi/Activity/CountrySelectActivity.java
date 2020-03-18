package kr.fatos.tnavi.Activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.content.res.Configuration;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;

import biz.fatossdk.fminterface.FMBaseActivity;
import biz.fatossdk.navi.NaviInterface;
import biz.fatossdk.newanavi.ANaviApplication;
import biz.fatossdk.tts.TTSEngine;
import kr.fatos.tnavi.Adapter.CountryItemListAdapter;
import kr.fatos.tnavi.Code.SettingsCode;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.Unit.CountryItemList;

/**
 * Created by kyungilwoo on 2017. 2. 9..
 */

public class CountrySelectActivity extends FMBaseActivity
{
    public static final String TAG = "FATOSMapHI::" + CountrySelectActivity.class.getSimpleName();
    private Context m_Context = null;
    private Button m_btnBack, m_btnHome;
    private ANaviApplication m_gApp;
    private SharedPreferences prefs;
    private ListView m_languageListView;
    private CountryItemListAdapter versionAdapter = null;
    private TextView m_txtTitle;

    public CountrySelectActivity()
    {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_select);

        prefs = getSharedPreferences(getResources().getString(R.string.app_registerId), MODE_PRIVATE);
        m_Context = this;
        m_gApp = (ANaviApplication)m_Context.getApplicationContext();

        final IntentFilter filter = new IntentFilter();
        filter.addAction("RELOAD_ACTIVITY");
        m_Context.registerReceiver(quickMenuFinishReceiver, filter);

        fatosCountryList();

        if(countryList != null)
        {
            versionAdapter = new CountryItemListAdapter(m_Context, countryList);
        }

        m_languageListView = (ListView)findViewById(R.id.list_languageinfo);
        m_languageListView.setAdapter(versionAdapter);
        m_languageListView.setItemsCanFocus(false);
        m_languageListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        versionAdapter.setSavedItem(prefs.getInt(SettingsCode.getKeyIndex(), 0));

        m_languageListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                if(prefs.getInt(SettingsCode.getKeyIndex(), 0) != position)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(m_Context);
                    builder.setTitle("Language");
                    builder.setMessage(getResources().getString(R.string.string_wecountry_change));
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                            language_change(position);

                            SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.app_registerId), MODE_PRIVATE).edit();
                            editor.putString(SettingsCode.getKeyCountry(), countrynames[position]);
                            editor.putInt(SettingsCode.getKeyIndex(), position);
                            SettingsCode.setValueCountry(countrynames[position]);
                            SettingsCode.setValueIndex(position);
                            editor.apply();

                            Intent toSearch = new Intent();
                            toSearch.setAction("RELOAD_COUNTRY"); // searchmain에서 접근하는 검색옵션 메뉴쪽에도 알려줘야함
                            sendBroadcast(toSearch);

                            Intent intent = new Intent();
                            intent.setAction("RELOAD_ACTIVITY"); //
                            sendBroadcast(intent);
                            finish();

                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Log.d(TAG, "click no");
                        }
                    });

                    builder.show();
                }
            }
        });

        m_txtTitle = (TextView)findViewById(R.id.poi_search_text_view);

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
                Intent intent = new Intent();
                intent.setAction("RESULT_FINISH"); // Action name
                sendBroadcast(intent);

                finish();
            }
        });
    }

    private boolean bFinish = false;
    BroadcastReceiver quickMenuFinishReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if(action.equals("RELOAD_ACTIVITY"))
            {
                //reloadActivity();
            }

        }
    };

    public void reloadActivity()
    {
        finish();
        Intent i = new Intent(m_Context, CountrySelectActivity.class);  //your class
        startActivity(i);
    }

    public static String RESULT_CONTRYCODE = "countrycode";
    public String[] countrynames, countrycodes;
    private TypedArray imgs;
    private ArrayList<CountryItemList> countryList;

    private void fatosCountryList()
    {
        countryList = new ArrayList<CountryItemList>();

        countrynames = getResources().getStringArray(R.array.onemap_country_names);
        countrycodes = getResources().getStringArray(R.array.onemap_country_codes);

        if(countryList.size() > 0)
        {
            countryList.clear();
        }

        for(int i = 0; i < countrycodes.length; i++)
        {
            countryList.add(new CountryItemList(countrynames[i], countrycodes[i]));
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        m_Context.unregisterReceiver(quickMenuFinishReceiver);
        super.onDestroy();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateMenuLanguage();
    }

    public void language_change(int index)
    {
        if(index == 0)
        {
            index = 1;
        }
        else if(index == 1)
        {
            index = 5;
        }
        else if(index == 2)
        {
            index = 8;
        }
        else
        {
            index = 0;
        }

        m_gApp.getAppSettingInfo().m_nDefaultLanguage = index;
        m_gApp.getRoutePathInfo().m_nDefaultLanguage = index;
        m_gApp.saveSettingInfo(m_Context, m_gApp.getAppSettingInfo());

        m_gApp.updateLanguage();

        updateMenuLanguage();
    }

    public void updateMenuLanguage()
    {
        m_gApp.updateLanguage();

        m_txtTitle.setText(m_Context.getResources().getString(R.string.string_nostrasetting_country));

        countrynames = getResources().getStringArray(R.array.onemap_country_names);
        countrycodes = getResources().getStringArray(R.array.onemap_country_codes);

        if(countryList.size() > 0)
        {
            countryList.clear();
        }

        for(int i = 0; i < countrycodes.length; i++)
        {
            countryList.add(new CountryItemList(countrynames[i], countrycodes[i]));
        }

        NaviInterface.SetLanguage(m_gApp.getAppSettingInfo().m_nDefaultLanguage);
        Locale locale = new Locale(LOCALE_NAME_LIST[m_gApp.getAppSettingInfo().m_nDefaultLanguage]);
        m_gApp.setFatosLocale(locale);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        TTSEngine.getInstance().setLocale(locale);
    }
}
