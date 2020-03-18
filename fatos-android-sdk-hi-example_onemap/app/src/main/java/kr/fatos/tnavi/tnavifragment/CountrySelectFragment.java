package kr.fatos.tnavi.tnavifragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import biz.fatossdk.newanavi.ANaviApplication;
import biz.fatossdk.newanavi.base.AMapBaseFragment;
import kr.fatos.tnavi.Activity.SearchSettingActivity;
import kr.fatos.tnavi.Adapter.CountryItemListAdapter;
import kr.fatos.tnavi.Code.SettingsCode;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.Unit.CountryItemList;

import static android.content.Context.MODE_PRIVATE;


public class CountrySelectFragment extends AMapBaseFragment {
    public static final String TAG = "FATOSMapHI::"+CountrySelectFragment.class.getSimpleName();
    private Context m_Context = null;
    private Button m_btnBack, m_btnHome;
    private ANaviApplication m_gApp;
    private  SharedPreferences prefs;
    private ListView m_languageListView;
    private CountryItemListAdapter versionAdapter = null;
    private TextView m_txtTitle;


    public CountrySelectFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_country_select, container, false);
        m_Context = view.getContext();
        m_gApp = (ANaviApplication) m_Context.getApplicationContext();

        prefs = m_Context.getSharedPreferences(getResources().getString(R.string.app_registerId), MODE_PRIVATE);

        final IntentFilter filter = new IntentFilter();
        filter.addAction("RELOAD_ACTIVITY");
        fatosCountryList();

        if(countryList != null)
        {
            versionAdapter = new CountryItemListAdapter(m_Context,countryList);
        }
        m_languageListView = (ListView)view.findViewById(R.id.list_languageinfo);
        m_languageListView.setAdapter(versionAdapter);
        m_languageListView.setItemsCanFocus(false);
        m_languageListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        versionAdapter.setSavedItem(prefs.getInt(SettingsCode.getKeyIndex(),0));

        m_languageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if(prefs.getInt(SettingsCode.getKeyIndex(),0) != position)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(m_Context);
                    builder.setTitle("Language");
                    builder.setMessage(getResources().getString(R.string.string_wecountry_change));
                    builder.setPositiveButton(getString(R.string.string_btn_popup_positive), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            language_change(position);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(SettingsCode.getKeyCountry(),countrynames[position]);
                            editor.putInt(SettingsCode.getKeyIndex(),position);
                            SettingsCode.setValueCountry(countrynames[position]);
                            SettingsCode.setValueIndex(position);
                            editor.apply();

                            Intent intent = new Intent();
                            intent.setAction("RELOAD_COUNTRY"); // Action name
                            m_Context.sendBroadcast(intent);

                            ((SearchSettingActivity)getActivity()).onBackPressed();
                            //overridePendingTransition(R.anim.right_in, R.anim.right_out);
                            //finish();


                        }
                    });
                    builder.setNegativeButton(getString(R.string.string_btn_popup_negative),new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.show();


                }
            }
        });
        return view;
    }

    public void language_change(int index){

        //한국-> 10번째로 수정, language는 0이 한국어, 1이 영어이므로
        if(index < 9){
            index = 1;
        }else{
            index = 0;
        }


        m_gApp.getAppSettingInfo().m_nDefaultLanguage = index;
        m_gApp.getRoutePathInfo().m_nDefaultLanguage = index;
        m_gApp.updateLanguage();
        updateMenuLanguage();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public static String RESULT_CONTRYCODE = "countrycode";
    public String[] countrynames, countrycodes;
    private TypedArray imgs;
    private ArrayList<CountryItemList> countryList;

    private void fatosCountryList() {
        countryList = new ArrayList<CountryItemList>();

        countrynames = getResources().getStringArray(R.array.onemap_country_names);
        countrycodes = getResources().getStringArray(R.array.onemap_country_codes);

        if(countryList.size() > 0)
            countryList.clear();

        for(int i = 0; i < countrycodes.length; i++){
            countryList.add(new CountryItemList(countrynames[i], countrycodes[i]));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateMenuLanguage();
    }

    public void updateMenuLanguage() {
        m_gApp.updateLanguage();

        countrynames = getResources().getStringArray(R.array.onemap_country_names);
        countrycodes = getResources().getStringArray(R.array.onemap_country_codes);

        if(countryList.size() > 0)
            countryList.clear();
        for(int i = 0; i < countrycodes.length; i++){
            countryList.add(new CountryItemList(countrynames[i], countrycodes[i]));
        }
    }
}
