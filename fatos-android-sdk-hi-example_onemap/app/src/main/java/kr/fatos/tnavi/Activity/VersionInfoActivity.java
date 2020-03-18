package kr.fatos.tnavi.Activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import biz.fatossdk.config.FatosBuildConfig;
import biz.fatossdk.fminterface.FMInterface;
import biz.fatossdk.navi.NaviDto.DtoGetVersionRes;
import biz.fatossdk.newanavi.ANaviApplication;
import biz.fatossdk.newanavi.base.AMapBaseActivity;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.Unit.settingItemDetailList;
import kr.fatos.tnavi.Unit.settingItemListAdapter;

/**
 * Created by kyungilwoo on 2017. 2. 9..
 */

public class VersionInfoActivity extends AMapBaseActivity {
    public static final String TAG = "AMAP";
    private Context m_Context = null;
    private Button m_btnBack, m_btnHome;

    private ANaviApplication m_gApp;

    private ListView m_VersionListView;
    private ArrayList<settingItemDetailList> arVersionDessert = new ArrayList<settingItemDetailList>();
    private settingItemListAdapter versionAdapter = null;
    private TextView m_txtTitle;

    static final int[] VERSION_MENU_NAME = new int[]{R.string.string_app, R.string.histring_buildnumber,
            R.string.string_basemapdb, R.string.histring_searchdb,R.string.histring_road,R.string.string_sdi};
        static final String[] VERSION_MENU_DATA_NAME = new String[]{"Appversion", "buildnumber", "basemapdb" ,"searchdb",
                "roaddata", "sdidata"};

    public VersionInfoActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_versioninfo);

        m_Context = this;
        m_gApp = (ANaviApplication) m_Context.getApplicationContext();

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String strRouteServerType = " - dev RP";
        switch (FatosBuildConfig.buildRouteServerType) {
            case Commercial:
                strRouteServerType = "";
                break;
            case Inside:
            case Develop:
                strRouteServerType = " - dev RP";
                break;
        }

        switch (FatosBuildConfig.CommercialBuild) {
            case Commercial:
                VERSION_MENU_DATA_NAME[0] = "v" + pInfo.versionName;
                break;
            case Develop:
                VERSION_MENU_DATA_NAME[0] = "v" + pInfo.versionName + " Dev mode" + strRouteServerType;
                break;
            case Inside:
                VERSION_MENU_DATA_NAME[0] = "v" + pInfo.versionName + " Inside mode" + strRouteServerType;
                break;
        }

        VERSION_MENU_DATA_NAME[1] = Integer.toString(pInfo.versionCode);
        DtoGetVersionRes version = FMInterface.GetInstance().FM_GetNaviversion();

        if(version != null)
        {

            if(version.nMapDate == 0)
            {
                VERSION_MENU_DATA_NAME[2] = (getResources().getString(R.string.string_setting_mapupdate_non_info));
            }
            else
            {
                VERSION_MENU_DATA_NAME[2] = ""+version.nMapDate;
            }

            if(version.nSearchDate == 0)
            {
                VERSION_MENU_DATA_NAME[3] = (getResources().getString(R.string.string_setting_mapupdate_non_info));
            }
            else
            {
                VERSION_MENU_DATA_NAME[3] = (""+version.nSearchDate);
            }

            if(version.nNetworkDate == 0)
            {
                VERSION_MENU_DATA_NAME[4] = (getResources().getString(R.string.string_setting_mapupdate_non_info));
            }
            else
            {
                VERSION_MENU_DATA_NAME[4] = (""+version.nNetworkDate);
            }


            VERSION_MENU_DATA_NAME[5] = (getResources().getString(R.string.string_setting_mapupdate_non_info));

            //// TODO: 2019. 3. 15. etc 리스트에서 0,1 두개 넘어오는데 name은 null임 확인 필요
            for(int i =0 ; i < version.listEtc.length; i++)
            {
                if(version.listEtc[i].strName.indexOf("SDI") != -1
                        || version.listEtc[i].strName.indexOf("sdi") != -1)
                {
                    if(version.listEtc[i].nCurDate == 0)
                    {
                        VERSION_MENU_DATA_NAME[5] = (getResources().getString(R.string.string_setting_mapupdate_non_info));
                    }
                    else
                    {
                        VERSION_MENU_DATA_NAME[5] = (""+version.listEtc[i].nCurDate);
                    }
                    break;
                }
            }
        }

        arVersionDessert.clear();

        for (int i = 0; i < VERSION_MENU_NAME.length; i++) {
            settingItemDetailList versionList = new settingItemDetailList();
            versionList.m_strSettingName = getResources().getString(VERSION_MENU_NAME[i]);
            versionList.m_strSettingDataName = VERSION_MENU_DATA_NAME[i];
            arVersionDessert.add(i,versionList);
        }

        if(arVersionDessert != null)
        {
            versionAdapter = new settingItemListAdapter(m_Context, arVersionDessert);
        }
        m_VersionListView = (ListView)findViewById(R.id.list_versioninfo);
        m_VersionListView.setAdapter(versionAdapter);
        m_VersionListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        m_VersionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        m_txtTitle = (TextView)findViewById(R.id.poi_search_text_view);

        m_btnBack = (Button) findViewById(R.id.setting_search_back_btn);
        m_btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       /* m_btnHome = (Button) findViewById(R.id.setting_search_btn_cancel);
        m_btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });*/


    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMenuLanguage();
    }

    public void updateMenuLanguage() {
        m_gApp.updateLanguage();

        m_txtTitle.setText(m_Context.getResources().getString(R.string.string_versioninfo));
        for (int i = 0; i < VERSION_MENU_NAME.length; i++) {
            settingItemDetailList versionList = arVersionDessert.get(i);
            versionList.m_strSettingName = getResources().getString(VERSION_MENU_NAME[i]);
            versionList.m_strSettingDataName = VERSION_MENU_DATA_NAME[i];
        }
    }
}
