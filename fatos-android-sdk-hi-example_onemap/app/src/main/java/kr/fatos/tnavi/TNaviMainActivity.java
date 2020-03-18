package kr.fatos.tnavi;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.parceler.Parcels;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import biz.fatossdk.config.ErrorMessage;
import biz.fatossdk.config.FatosBuildConfig;
import biz.fatossdk.fminterface.FMBaseActivity;
import biz.fatossdk.fminterface.FMDriveInfo;
import biz.fatossdk.fminterface.FMError;
import biz.fatossdk.fminterface.FMInterface;
import biz.fatossdk.fminterface.FMSortOption;
import biz.fatossdk.fminterface.RouteSummaryData;
import biz.fatossdk.map.FMPMapConst;
import biz.fatossdk.nativeMap.MapAnimation;
import biz.fatossdk.navi.RoutePosition;
import biz.fatossdk.navi.rgdata.RouteData;
import biz.fatossdk.newanavi.ANaviApplication;
import biz.fatossdk.newanavi.list.lastRouteDataItem;
import biz.fatossdk.newanavi.manager.AMapGoogleSearchUtil;
import biz.fatossdk.newanavi.manager.AMapPositionManager;
import biz.fatossdk.newanavi.manager.AMapUtil;
import biz.fatossdk.newanavi.setting.saveSettingInfoList;
import biz.fatossdk.newanavi.splash.FatosToast;
import biz.fatossdk.newanavi.struct.PoiPositionInfo;
import biz.fatossdk.openapi.Route;
import biz.fatossdk.openapi.common.POIItem;
import biz.fatossdk.openapi.common.PathPointInfo;
import kr.fatos.tnavi.Activity.TNaviPickerActivity;
import kr.fatos.tnavi.Code.SettingsCode;
import kr.fatos.tnavi.Code.TNaviActionCode;
import kr.fatos.tnavi.Interface.FragmentCommunicator;
import kr.fatos.tnavi.Lib.AppNetwork;
import kr.fatos.tnavi.Lib.GUtilLib;
import kr.fatos.tnavi.Lib.GoLib;
import kr.fatos.tnavi.Unit.NPoiItem;
import kr.fatos.tnavi.Unit.RouteCardData;
import kr.fatos.tnavi.Unit.RouteCardDataVol2;
import kr.fatos.tnavi.tnavifragment.SearchFragment;
import kr.fatos.tnavi.tnavifragment.SearchMainFragment;
import kr.fatos.tnavi.tnavifragment.SearchShowMapFragment;
import kr.fatos.tnavi.tnavifragment.SummaryFragment;

public class TNaviMainActivity extends FMBaseActivity implements FragmentCommunicator , SharedPreferences.OnSharedPreferenceChangeListener{
    public static SharedPreferences prefs = null;
    private Handler m_Handler = new Handler();

    //검색화면에서 온건지, 처음 로직인지를 가지는 액션코드..
    public String APP_MODE = TNaviActionCode.APP_MODE_DEFAULT;
    public static String[] strAddr = {"Start","Goal"};
    public static double[] m_dStartCoord = {0, 0};
    private ANaviApplication m_gApp;
    public static Context m_Context = null;
    private FirebaseAnalytics mFirebaseAnalytics;

    private Route m_route;
    public boolean RouteFlag = false; // 주행중 판단 flag, native의 isroute는 경로 유무 여부를 리턴하기 때문에..

    //첫실행인지 판단
    public boolean bFirstRun = false;

    private FragmentManager fragmentManager;

    private final String TAG = TNaviMainActivity.class.getSimpleName();

    private final static String tag_summary_fragment = "tag_summary_fragment";
    private final static String tag_search_fragment = "tag_search_fragment";
    private final static String tag_showmap_fragment = "tag_showmap_fragment";
    private final static String tag_searchmain_fragment = "tag_searchmain_fragment";

    private ProgressDialog mProgressdialog;
    private MapAnimation m_MapAnimation = new MapAnimation();
    public FMInterface m_FMInterface;
    private int m_iEngineInit = 0;
    private boolean isChangeViaWithGoalFlag = false;

    public static savedData saved_data;

    private double[] m_dMapTouchScreenWGS84 = new double[2];

    SearchMainFragment searchMainFragment;
    SearchShowMapFragment searchShowMapFragment;

    private ArrayList<NPoiItem> m_TempPoiItem;//검색 최종 리스트
    private String m_SearchWord;//검색 최종 단어

    private boolean bThreadFlag = true;
    private boolean m_bPaused = false; //행정동명 쓰레드 실행 중지 플레그

    private ArrayList<RouteCardData> saved_summaryCardData;

    private NPoiItem m_POIItem;
    private NPoiItem m_RecentPOIItem;
    private ArrayList<NPoiItem> m_ALPoiItem;

    ImageView imageView_marker, imageView_MapCenterPoint;

    //출발지, 목적지 탐색 여부.. e.g 출발지만, 목적지만, 출발지 변경, 목적지 변경, 출/목 두개를 변경 등..
    private String m_strMapViewRouteViaOrGoal = "";

    //경로안내중인 목적지
    private lastRouteDataItem m_lastRouteDataItem;
    private boolean bLastRouteFlag = false;

    AppNetwork receiver;
    //==============================================================================================
    public class savedData {
        public double viaX;
        public double viaY;
        public double goalX;
        public double goalY;

        public savedData(){
            viaX = 0;
            viaY = 0;
            goalX = 0;
            goalY = 0;
        }

        public savedData(double viaX, double viaY, double goalX, double goalY){
            this.viaX = viaX;
            this.viaY = viaY;
            this.goalX = goalX;
            this.goalY = goalY;
        }

        public savedData getData(){
            return this;
        }

        public void clearData(){
            this.viaX = 0;
            this.viaY = 0;
            this.goalX = 0;
            this.goalY = 0;
        }
    }
    //==============================================================================================
    public void setAPP_MODE(String APP_MODE){

        Log.e(TAG,"setApp_Mode : " + APP_MODE);
//        clearTouchInfo();

        if(APP_MODE != null && !APP_MODE.equals("")) {
            this.APP_MODE = APP_MODE;
            changeMapAerialMode(m_gApp.getAppSettingInfo().m_nAirLocalBuild);
        }else{
            return;
        }

        //기본모드
        if(APP_MODE.equals(TNaviActionCode.APP_MODE_DEFAULT)){
            setGpsAppMode(TNaviActionCode.GPS_APP_MODE_DEFAULT);
            setUIMode(TNaviActionCode.UI_NORMAL_DRIVE_MODE);

            if(m_FMInterface.FM_GetDriveInfo().isM_bIsRoute()){
                routeCancel();
            }

            this.RouteFlag = false;

//            mapMoveDirectCurPos();
        }

        //시뮬레이션 모드
        if(APP_MODE.equals(TNaviActionCode.APP_MODE_SIMULATE)){
            setGpsAppMode(TNaviActionCode.GPS_APP_MODE_DEFAULT);
            setUIMode(TNaviActionCode.UI_SIMUL_MODE);
        }

        //주행 모드
        if(APP_MODE.equals(TNaviActionCode.APP_MODE_ROUTE)){
            setGpsAppMode(TNaviActionCode.GPS_APP_MODE_DEFAULT);
            setUIMode(TNaviActionCode.UI_DRIVE_MODE);
            this.RouteFlag = true;
        }

        //경로요약 모드
        if(APP_MODE.equals(TNaviActionCode.APP_MODE_SHOWING_SUMMARY)){
            setUIMode(TNaviActionCode.UI_SUMMARY_MODE);
            changeMapAerialMode(saveSettingInfoList.MAPMODE_AIR_OFF_BUILDING_ON);
//            m_gApp.ChangeMapViewMode(1, false); //지도보기, 경로요약 모드일때는 맵모드 고정(2D)
        }

        //지도보기 모드
        if(APP_MODE.equals(TNaviActionCode.APP_MODE_SHOW_MAP))
        {
            setUIMode(TNaviActionCode.UI_SHOW_MAP_MODE);
            setGpsAppMode(TNaviActionCode.GPS_APP_MODE_SHOW_MAP);
//            m_gApp.ChangeMapViewMode(1, false); //지도보기, 경로요약 모드일때는 맵모드 고정(2D)
        }

        if(APP_MODE.equals(TNaviActionCode.APP_MODE_DEFAULT) ||
                APP_MODE.equals(TNaviActionCode.APP_MODE_ROUTE) ||
                APP_MODE.equals(TNaviActionCode.APP_MODE_SIMULATE) ||
                APP_MODE.equals(TNaviActionCode.APP_MODE_JUST_GOAL_SEARCH)
            )
        {               //메인지도 보여질떄는 기존모드 적용되도록
            m_gApp.ChangeMapViewMode(m_gApp.m_nCurMapMode, true);
//            mapMoveDirectCurPos();

        }else{          //그 외 모드일때는 맵모드 2D로 고정
            m_gApp.ChangeMapViewMode(1, false);
        }

    }
    //==============================================================================================
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(SettingsCode.getKeyCountry())) {

        }
    }
    //==============================================================================================
    @Override
    public void fragmentContactActivity(View v) {
        switch (v.getId()) {
            //검색 프래그먼트 진입점
            case R.id.title_text_view :
            case R.id.imageButton_SearchOnemap :
                if(m_gApp.m_bNaviCallbackFlag  && RouteFlag){
                    routeCancel();
                }
                ActivityManager activityManager = (ActivityManager) m_Context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);

                ActivityManager.RunningTaskInfo running = info.get(0);
                ComponentName componentName = running.topActivity;

                if (TNaviPickerActivity.class.getName().equals(componentName.getClassName()))
                {
                    Intent intent = new Intent();
                    intent.setAction("CLOSE_POPUP");
                    sendBroadcast(intent);
                }

                setDriveInfoThreadFlag(false);

                Bundle args = new Bundle();


                args.putString(TNaviActionCode.ROUTE_VIA_OR_GOAL, TNaviActionCode.JUST_GOAL);
                args.putString(TNaviActionCode.APP_MODE, TNaviActionCode.APP_MODE_JUST_GOAL_SEARCH);
                GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchFragment.newInstance(),tag_search_fragment, args);
                setAPP_MODE(TNaviActionCode.APP_MODE_JUST_GOAL_SEARCH);

                break;

            case R.id.linearLayout_Time :
                m_gApp.getAppSettingInfo().m_bArriveTime = !m_gApp.getAppSettingInfo().m_bArriveTime;
                break;
        }
    }
    //==============================================================================================
    @Override
    public void rerouteFromSummary(Bundle bundle) {
        String args = bundle.getString(TNaviActionCode.ROUTE_VIA_OR_GOAL);
        bundle.putString(TNaviActionCode.APP_MODE,TNaviActionCode.APP_MODE_SEARCH_TO_ROUTE);

        switch(args){
            case TNaviActionCode.CHANGE_VIA_GO_ROUTE:
                setAPP_MODE(TNaviActionCode.SEARCH_MODE);
                GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchFragment.newInstance(), tag_search_fragment, bundle);
                break;
            case TNaviActionCode.CHANGE_GOAL_GO_ROUTE:
                setAPP_MODE(TNaviActionCode.SEARCH_MODE);
                GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchFragment.newInstance(), tag_search_fragment, bundle);
                break;
        }
    }
    //==============================================================================================
    public void updatelaunguage()
    {
        final String DefaultCountry = prefs.getString(SettingsCode.getKeyCountry(),"");

        Resources res = getResources();
        String[] strCountry = res.getStringArray(R.array.onemap_country_names);

        if(DefaultCountry.equals(res.getString(R.string.string_welanguage_english)))
        {
            m_gApp.getAppSettingInfo().m_nDefaultLanguage = 1;
        }
        else if(DefaultCountry.equals(res.getString(R.string.string_welanguage_zh)))
        {
            m_gApp.getAppSettingInfo().m_nDefaultLanguage = 5;
        }
        else if(DefaultCountry.equals(res.getString(R.string.string_welanguage_th)))
        {
            m_gApp.getAppSettingInfo().m_nDefaultLanguage = 8;
        }
        else
        {
            m_gApp.getAppSettingInfo().m_nDefaultLanguage = 0;
        }

        if(m_gApp.getRoutePathInfo().m_nServiceType == FatosBuildConfig.FATOS_SITE_IS_OceanEnergy){
            m_gApp.getAppSettingInfo().m_nDefaultLanguage = 0;
        }

        m_gApp.saveSettingInfo(m_Context,m_gApp.getAppSettingInfo());

        m_gApp.getRoutePathInfo().m_nDefaultLanguage = m_gApp.getAppSettingInfo().m_nDefaultLanguage;
        m_gApp.updateLanguage();

        showTbtLayout(false);
        showTbtLayout(true);//현재 TBT정보와 다음 TBT정보를 표출 false일때 표출 해제
    }
    //==============================================================================================
    private final Runnable MapInit = new Runnable() {
        @Override
        public void run() {
            try {
                InitProcess();
                setContentView(R.layout.activity_tnavi_main);

                if(bFirstRun)
                {
                    bFirstRun = false;
                }

                //속도와 거리 단위를 Km로 초기화
                m_gApp.getAppSettingInfo().m_nSpeedUnit = 0;
                m_gApp.getAppSettingInfo().m_nDistUnit = 0;
                SettingsCode.setDistanceUnit(m_gApp.getAppSettingInfo().m_nDistUnit);

                imageView_marker = findViewById(R.id.imageView_marker);
                imageView_MapCenterPoint = findViewById(R.id.imageView_MapCenterPoint);

                saved_data = new savedData();

                CheckLastRoute();
            }catch (IOException e){

            }
        }
    };
    //==============================================================================================
    //오류유발 함수
    private void overFlow(){
        this.overFlow();
    }
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        m_Context = this;

        setContentView(R.layout.activity_splash);

        ImageView fatoslogo = findViewById(R.id.fatoslogo);
        ImageView fatostitle = findViewById(R.id.fatostitle);
        LinearLayout linearLayout_Main = findViewById(R.id.linearLayout_Main);
        ImageView we_loading = findViewById(R.id.we_loading);

        linearLayout_Main.setBackgroundResource(R.color.hiSplashBackgroundColor);
        fatoslogo.setImageResource(R.drawable.splash_title);
        fatostitle.setImageResource(R.drawable.splash_logo);
        we_loading.setImageResource(R.drawable.hi_splash_sdk);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "testMapHi_id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "testMapHi_Name");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new AppNetwork(this);
        registerReceiver(receiver, filter);

        this.strAddr[0] = getResources().getString(R.string.string_via_hint);

        /**
         *   SharedPreferences 초기화 및 기본값 세팅
         *
         * */
        prefs = getSharedPreferences(getResources().getString(R.string.app_registerId), MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(this);

        // 초기 실행인 경우
        if (prefs.getBoolean("firstrun", true)) {
            bFirstRun = true;

            SharedPreferences.Editor editor = prefs.edit();
            //국가 기본값 태국으로 설정
            editor.putString(SettingsCode.getKeyCountry(), "English");
            editor.putInt(SettingsCode.getKeyIndex(), 0);
            //카테고리 기본값 모든 카테고리로 설정
            editor.putString(SettingsCode.getKeyCategory(), getResources().getString(R.string.string_weallcate));
            editor.putInt(SettingsCode.getKeyCategoryindex(),0);
            //언어 기본값 영어로 설정
            editor.putString(SettingsCode.getKeyLanguage(), getResources().getString(R.string.string_welanguage_english));

            //첫 실행 flag 전환
            editor.putBoolean("firstrun", false);
            editor.apply();
        }

        //카테고리 인덱스
        SettingsCode.setValueCategoryIndex(prefs.getInt(SettingsCode.getKeyCategoryindex(),0));
        //카테고리
        SettingsCode.setValueCategory(prefs.getString(SettingsCode.getKeyCategory(),getResources().getString(R.string.string_weallcate)));
        //국가 인덱스
        SettingsCode.setValueIndex(prefs.getInt(SettingsCode.getKeyIndex(), 0));
        //국가
        SettingsCode.setValueCountry(prefs.getString(SettingsCode.getKeyCountry(), getResources().getString(R.string.string_welanguage_english)));

        String strCountry = SettingsCode.getValueCountry();

        if(!strCountry.equals(getString(R.string.string_welanguage_english)) && !strCountry.equals(getString(R.string.string_welanguage_zh)) &&
                !strCountry.equals(getString(R.string.string_welanguage_th)) && !strCountry.equals(getString(R.string.string_welanguage_korean)))
        {
            SettingsCode.setValueCountry(getString(R.string.string_welanguage_english));
            SettingsCode.setValueIndex(0);
        }

        Intent intent = new Intent();
        intent.setAction("RELOAD_COUNTRY"); // Action name
        m_Context.sendBroadcast(intent);

        //FATOS Auto는 버전 체크를 하므로.. 버전체크 우회용
        FatosBuildConfig.buildFATOSauto = false;
        FatosBuildConfig.setBuildSearchResultMode(ANaviApplication.TMAP_SEARCH_MODE);

        try {
            m_Handler.postDelayed(MapInit, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setM_ALPoiItem();
        m_ALPoiItem = LoadRecentPOI();
    }
    //==============================================================================================
    public void setM_ALPoiItem()
    {
        if(m_ALPoiItem == null)
        {
            m_ALPoiItem = new ArrayList<>();
        }
    }
    //==============================================================================================
    //Activity recreate되는 경우, fragment에서 GoActivity를 사용하는 경우 onCreate가 아닌 onNewIntent로 온다
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            if(bundle.getString(TNaviActionCode.ROUTE_VIA_OR_GOAL) != null)
            {
                m_strMapViewRouteViaOrGoal = bundle.getString(TNaviActionCode.ROUTE_VIA_OR_GOAL);
            }

            if(bundle.getString(TNaviActionCode.APP_MODE) != null)
            {
                setAPP_MODE(bundle.getString(TNaviActionCode.APP_MODE));
            }
        }

        modeProcess(intent);
    }
    //==============================================================================================
    @Override
    protected void onResume() {
        super.onResume();
        //행정동명 스레드 시작
        setDriveInfoThreadFlag(true);
    }
    //==============================================================================================
    @Override
    protected void onPause() {
        super.onPause();
        hide_ProgressDialog();
        //행정동명 스레드 중지
        setDriveInfoThreadFlag(false);
    }
    //==============================================================================================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String addr = "";
        switch (requestCode)
        {
            case TNaviActionCode.LONGTOUCH_CLICK :
                setMarkerVisible(false);

                if(resultCode == RESULT_OK)
                {
                    //터치 초기화(오토스케일링 off)
                    onUpdateMapMode(3);


                    if(data.getExtras() != null )
                    {
                        addr = data.getExtras().getString("strAddr");
                        set_strAddr(getResources().getString(R.string.string_via_hint),0);
                        set_strAddr(addr,1);

                        //도착지만 업데이트 해준다.
                    }

                    routeTovia(m_dMapTouchScreenWGS84[0],m_dMapTouchScreenWGS84[1],TNaviActionCode.JUST_GOAL, m_POIItem);
                }
                else
                {
                    //Log.e(TAG,"RESULT_CANCELED");
                    if(RouteFlag) { //현재 주행중일 때
                        showTbtLayout(true);
                        m_FMInterface.FM_StartRGService(FMBaseActivity.onFatosMapListener);
                    }
                    m_POIItem = null;
                }

                mapMoveCurrnetPostion();

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    //==============================================================================================
    //메인화면 최초 행정동명 가져오기
    public String getAddrText()
    {
        double[] latlon = new double[2];
        String strAddr = "";

        //prevent nxe
        if(m_FMInterface != null){
            m_FMInterface.FM_GetMapCenterPos(latlon);
            strAddr = m_FMInterface.FM_GetAddressVol2(latlon[0], latlon[1]);
        }

        return strAddr;
    }
    //==============================================================================================
    //지도보기에서 좌표이동
    public void MovePoint(double dX, double dY, String i_strAddr)
    {
        fragmentManager = getSupportFragmentManager();
        searchShowMapFragment = (SearchShowMapFragment)fragmentManager.findFragmentById(R.id.container);

        m_FMInterface.FM_SetMapPosition(0, dX, dY, 16.f);
    }
    //==============================================================================================
    public void onUpdateMapMode(final int nStatus) {
        if(nStatus == 3 && searchShowMapFragment != null){
            searchShowMapFragment.isTouch = true;
        }

        super.onUpdateMapMode(nStatus);
    }
    //==============================================================================================
    //모드에 따라 하는일
    private void modeProcess(Intent i_intent) {
        //search에서 검색결과 클릭해서 넘어 온 경우
        String EnglishName = "";
        String AddrFull = "";

//        Log.e(TAG,"modeProcess : " + APP_MODE);
        if (Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_SEARCH_TO_ROUTE)) {
            NPoiItem poiItem = Parcels.unwrap(i_intent.getParcelableExtra(TNaviActionCode.POI_ITEM));

            if(poiItem.getEnglishName() != null){
                EnglishName = poiItem.getEnglishName();
            }
            if(poiItem.getAddressFull()!= null){
                AddrFull = poiItem.getAddressFull();
            }

            Bundle bundle = i_intent.getExtras();

            if (bundle != null) {
                if (bundle.getString(TNaviActionCode.ROUTE_VIA_OR_GOAL) != null) {
                    String args = bundle.getString(TNaviActionCode.ROUTE_VIA_OR_GOAL);

                    if (args != null) {
                        //args 가 있는 경우, summary 에서 넘어온 경우이므로 경로 취소 및 재탐색
                        if (Objects.equals(args, TNaviActionCode.CHANGE_VIA_GO_ROUTE)) {
                            if(!AddrFull.equals("")){
                                set_strAddr(AddrFull,0);
                            }

                            if(!EnglishName.equals("")){
                                set_strAddr(EnglishName,0);
                            }

                            //출발지만 업데이트 해준다.
                            routeTovia(poiItem.getLocationPointX(), poiItem.getLocationPointY(), args, poiItem); //출발지 업데이트
                        } else if (Objects.equals(args, TNaviActionCode.CHANGE_GOAL_GO_ROUTE)) {
                            if(!AddrFull.equals("")){
                                set_strAddr(AddrFull,1);
                            }

                            if(!EnglishName.equals("")){
                                set_strAddr(EnglishName,1);
                            }
                            routeTovia(poiItem.getLocationPointX(), poiItem.getLocationPointY(), args, poiItem); //목적지 업데이트
                        }
                    }
                }
            }
        } else if (Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_SHOW_MAP)) {
            String args = i_intent.getExtras().getString(TNaviActionCode.ROUTE_VIA_OR_GOAL);
            NPoiItem poiItem = Parcels.unwrap(i_intent.getParcelableExtra(TNaviActionCode.POI_ITEM));
            int nSearchKind = i_intent.getExtras().getInt(TNaviActionCode.SEARCH_KIND);

            Bundle bundle = new Bundle();
            bundle.putParcelable(TNaviActionCode.POI_ITEM, Parcels.wrap(poiItem));
            bundle.putString(TNaviActionCode.ROUTE_VIA_OR_GOAL, args);
            bundle.putInt(TNaviActionCode.SEARCH_KIND, nSearchKind);

            GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchShowMapFragment.newInstance(), tag_showmap_fragment, bundle);

            View view = this.getCurrentFocus();

            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        } else if(Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_DEFAULT)) {
            //현재 좌표로 지도 이동
            mapMoveCurrnetPostion();

            setDriveInfoThreadFlag(true);
            GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchMainFragment.newInstance(), null);

        } else if(Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_SEARCH)) {
            //터치 초기화(오토스케일링 방지)
            onUpdateMapMode(3);
            mapMoveCurrnetPostion();

            GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchFragment.newInstance(), tag_search_fragment, null);
        } else if (Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_JUST_GOAL_SEARCH)) {

            //터치 초기화(오토스케일링 방지)
            onUpdateMapMode(3);
            mapMoveCurrnetPostion();

            NPoiItem poiItem = Parcels.unwrap(i_intent.getParcelableExtra(TNaviActionCode.POI_ITEM));

            if(poiItem.getEnglishName() != null){
                EnglishName = poiItem.getEnglishName();
            }

            if(poiItem.getAddressFull()!= null){
                AddrFull = poiItem.getAddressFull();
            }

            if(!AddrFull.equals("")){
                if(TextUtils.isEmpty(strAddr[0]) || strAddr[0].equals(getResources().getString(R.string.string_via_hint))) {
                    set_strAddr(getResources().getString(R.string.string_via_hint), 0);
                }

                set_strAddr(AddrFull,1);
            }

            if(!EnglishName.equals("")){
                if(TextUtils.isEmpty(strAddr[0]) || strAddr[0].equals(getResources().getString(R.string.string_via_hint))) {
                    set_strAddr(getResources().getString(R.string.string_via_hint), 0);
                }

                set_strAddr(EnglishName,1);
            }

            if(AddrFull.equals("") && EnglishName.equals("")){
                if(TextUtils.isEmpty(strAddr[0]) || strAddr[0].equals(getResources().getString(R.string.string_via_hint))) {
                    set_strAddr(getResources().getString(R.string.string_via_hint), 0);
                }

                set_strAddr(poiItem.getLocationPointX() + ", " + poiItem.getLocationPointY(),1);
            }

            routeTovia(poiItem.getLocationPointX(), poiItem.getLocationPointY(), TNaviActionCode.JUST_GOAL, poiItem); //목적지만 탐색(변경 X)
        }
        else if(Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_ROUTE))
        {
            setDriveInfoThreadFlag(true);

            Bundle bundle = new Bundle();
            bundle.putString(TNaviActionCode.APP_MODE, TNaviActionCode.APP_MODE_ROUTE);
            mapMoveCurrnetPostion();

            GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchMainFragment.newInstance(), bundle);
        }
        else if(Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_SIMULATE))
        {
            setDriveInfoThreadFlag(true);

            Bundle bundle = new Bundle();
            bundle.putString(TNaviActionCode.APP_MODE, TNaviActionCode.APP_MODE_SIMULATE);

            mapMoveCurrnetPostion();
            GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchMainFragment.newInstance(), tag_searchmain_fragment, bundle);
        }
    }
    //==============================================================================================
    private void InitProcess() throws IOException {
        m_Context = this;
        m_gApp = (ANaviApplication) m_Context.getApplicationContext();
        m_route = m_gApp.getRouteApiInstance();
        FMInterface.CreateInstance(m_Context);
        m_FMInterface = FMInterface.GetInstance();
        m_iEngineInit = initFatosNaviEngine();

        if(m_iEngineInit != 1)
        {
            Intent intent = new Intent();
            intent.setAction(TNaviActionCode.READY_MAIN_MAP); // Action name
            sendBroadcast(intent);

            return;
        }

        m_FMInterface.FM_Init(m_Context, m_gApp.getRoutePathInfo().m_strAPIKEY, new ANaviApplication.MapStatusListener() {
            @Override
            public void onRouteFinish() {
                // 출<->목 변경시에는 fragment 전환 하지 않음
                if(isChangeViaWithGoalFlag){
                    isChangeViaWithGoalFlag = false;
                    return;
                }

                if(!APP_MODE.equals(TNaviActionCode.APP_MODE_DEFAULT))
                {
                    setAPP_MODE(TNaviActionCode.APP_MODE_DEFAULT);
                }

                m_gApp.ArriveGoalVol2(m_Context);

                Bundle bundle = new Bundle();
                bundle.putString(TNaviActionCode.APP_MODE,TNaviActionCode.APP_MODE_DEFAULT);
                GoLib.getInstance().goFragment(getSupportFragmentManager(),R.id.container, SearchMainFragment.newInstance(), tag_searchmain_fragment, bundle);
            }

            //bMapMove : 원위치 버튼 눌렀을때 false, 터치로 움직였을때 true
            @Override
            public void onMapMove(boolean bMapMove) {
                setDriveInfoThreadFlag(true);

                if(bMapMove) {
                    setMapCenterVisible(true);
                    setDriveInfoThreadFlag(true);
                }
                else
                {
                    setMapCenterVisible(false);
                }
            }

            @Override
            public void onReRouting() {

            }

            @Override
            public void onCycleReRouting() {

            }

            @Override
            public void onInControlLineArea() {

            }

            @Override
            public void mapReady() {
                //1초에 한번씩 행정동명 가져오는 쓰레드 시작
                DriveInfoThread.start();

                if(Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_DEFAULT))
                {
                    //검색화면 추가
                    GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchMainFragment.newInstance(), null);
                }

                //경로 색상 초기화
                m_gApp.setMapRouteLineColor(0, getResources().getColor(R.color.cardview_onemap_textcolor));
            }

            @Override
            public void updateObjPickerInfo(int nType, String strKey, String strName, double nLong, double nLat) {

            }

            @Override
            public void updateMapTouch(float fX, float fY) {

            }

            @Override
            public void updateMapLongTouch(float fX, float fY) {

                if(APP_MODE.equals(TNaviActionCode.APP_MODE_SHOWING_SUMMARY)){
                    return;
                }

                //지도보기 시에는 롱클릭을 막는다.
                if(!Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_SHOW_MAP))
                {
                    /**
                     * 화면 좌표를 wgs84 좌표로 변환
                     */
                    m_FMInterface.FM_GetWGS84ToScreen(fX, fY, m_dMapTouchScreenWGS84);
                    /**
                     * wgs84 좌표 기준 행정동명 가져오기
                     * Thread 처리
                     */

                    final FMDriveInfo fmDriveInfo = GetDriveInfo();

                    String strAddr;
                    strAddr = m_FMInterface.FM_GetAddressVol2(m_dMapTouchScreenWGS84[0], m_dMapTouchScreenWGS84[1]);

                    /**
                     * 지도 이동
                     */
                    m_FMInterface.FM_SetMapPosition(0, m_dMapTouchScreenWGS84[0], m_dMapTouchScreenWGS84[1], 16.f);

                    setMarkerVisible(true);

                    Intent intentSearchkeyword = new Intent(m_Context, TNaviPickerActivity.class);
                    intentSearchkeyword.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle bundle = new Bundle();
                    bundle.putString(TNaviActionCode.APP_MODE, APP_MODE);
                    bundle.putDouble("XCoord", m_dMapTouchScreenWGS84[0]);
                    bundle.putDouble("YCoord", m_dMapTouchScreenWGS84[1]);
                    bundle.putString("Address", fmDriveInfo.getM_strCurPosName());
                    intentSearchkeyword.putExtras(bundle);

                    if(m_POIItem == null)
                    {
                        m_POIItem = new NPoiItem();
                    }

                    m_POIItem.init();
                    m_POIItem.setLocationPointX(m_dMapTouchScreenWGS84[0]);
                    m_POIItem.setLocationPointY(m_dMapTouchScreenWGS84[1]);
                    m_POIItem.setEnglishName(strAddr);
                    m_POIItem.setStrName(strAddr);

                    setGpsAppMode(TNaviActionCode.GPS_APP_MODE_LONG_TOUCH);

                    startActivityForResult(intentSearchkeyword, TNaviActionCode.LONGTOUCH_CLICK);
                }
            }

            @Override
            public void updateMapAngle(float nAngle) {

            }
        });

        Intent intent = new Intent();
        intent.setAction(TNaviActionCode.READY_MAIN_MAP); // Action name
        sendBroadcast(intent);
        m_gApp.getAppSettingInfo().updateSDIInfo();
        updatelaunguage();
    }
    //==============================================================================================
    public void setMarkerVisible(boolean i_bFlag)
    {
        if(i_bFlag) {
            imageView_marker.setVisibility(View.VISIBLE);
        }
        else
        {
            imageView_marker.setVisibility(View.GONE);
        }
    }
    //==============================================================================================
    public void movePoiPos(double x, double y) {
        PoiPositionInfo poiInfo = m_gApp.getPoiPositionInfo();

        int nAniType = MapAnimation.MAP_ANI_TYPE_DIRECT;

        float viatmp = 0;
        float goaltmp = 0;
        m_MapAnimation.Reset();

        m_MapAnimation.setCenter(0.5f,0.5f,nAniType);

        m_MapAnimation.setMapWGS84(x,y,nAniType);

        m_MapAnimation.setLevel(FMPMapConst.SELECT_MAP_MOVE_LEVE, FMPMapConst.SELECT_MAP_MOVE_LEVE, nAniType);

        viatmp = 5.58f;
        goaltmp = 0.28f;

        if(viatmp - goaltmp > 1) {
            viatmp = goaltmp + 1;
        }

        if(viatmp > FMPMapConst.MAPVIEW_MAX_LEVEL) {
            viatmp = FMPMapConst.MAPVIEW_MAX_LEVEL;
        }

        m_gApp.m_fScreenX = 0.5f;

        onFatosMapListener.onMapDrawPinImg(x,y, m_gApp.m_MainMapObjType[3]);
        onFatosMapListener.onMapAnimation(m_MapAnimation);

        m_gApp.setMainMapOption(false,false,true,false);
    }
    //==============================================================================================
    public void routeCancel(){
        bLastRouteFlag = false;
        showTbtLayout(false);
        m_gApp.ArriveGoalVol2(m_Context);

        m_FMInterface.FM_CancelRoute();
//        m_FMInterface.FM_StartRGService(FMBaseActivity.onFatosMapListener);
        updateCancelRoute();

        if(!APP_MODE.equals(TNaviActionCode.APP_MODE_DEFAULT))
        {
            setAPP_MODE(TNaviActionCode.APP_MODE_DEFAULT);
        }

        set_strAddr("", 0);
        setStartCoord(0, 0);

        Bundle bundle = new Bundle();
        bundle.putString(TNaviActionCode.APP_MODE,TNaviActionCode.APP_MODE_DEFAULT);
        GoLib.getInstance().goFragment(getSupportFragmentManager(),R.id.container, SearchMainFragment.newInstance(), tag_searchmain_fragment, bundle);
    }
    //==============================================================================================
    public void goRouteInfo(){
        if(saved_summaryCardData!=null) {
            fragmentManager = getSupportFragmentManager();

            Bundle args = new Bundle();
            SummaryFragment summaryFragment = new SummaryFragment();

            //기존 방식
            ArrayList<RouteCardData> choice = new ArrayList<>();

            if(saved_summaryCardData.size() > 1){
                choice.add(saved_summaryCardData.get(m_gApp.getM_nSelRouteIdx()));
            }else{
                choice.add(saved_summaryCardData.get(0));
            }

            m_FMInterface.m_RgDataContext = m_gApp.rgData();
            args.putParcelableArrayList(TNaviActionCode.LONGTOUCH_ROUTESUMMARY, choice);
            args.putInt(TNaviActionCode.SELECT_ROUTE_INDEX, m_gApp.getM_nSelRouteIdx());
            summaryFragment.setArguments(args);

            m_FMInterface.FM_RouteSummary(FMBaseActivity.onFatosMapListener);

            setAPP_MODE(TNaviActionCode.APP_MODE_SHOWING_SUMMARY);
            GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, summaryFragment, tag_summary_fragment, args);
        }

        showTbtLayout(false);
    }
    //==============================================================================================
    public void simulCancel(){
        if(saved_summaryCardData!=null) {

            m_FMInterface.FM_StopSimulation(); //모의 주행 중지
            fragmentManager = getSupportFragmentManager();

            Bundle args = new Bundle();
            args.putParcelableArrayList(TNaviActionCode.LONGTOUCH_ROUTESUMMARY, saved_summaryCardData);
            args.putInt(TNaviActionCode.SELECT_ROUTE_INDEX, m_gApp.getM_nSelRouteIdx());
            SummaryFragment summaryFragment = new SummaryFragment();
            summaryFragment.setArguments(args);
            m_FMInterface.FM_RE_RouteSummary(FMBaseActivity.onFatosMapListener);
            //모의주행 종료시에는 요약화면으로 가야 함.
            setAPP_MODE(TNaviActionCode.APP_MODE_SHOWING_SUMMARY);
            GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, summaryFragment, tag_summary_fragment, args);
        }

        showTbtLayout(false);
    }
    //==============================================================================================
    //경로 재탐색
    public void ReRoute()
    {
        super.manualReRouteMenu();

        FatosToast.ShowFatosYellow(getResources().getString(R.string.string_hireroute_status));
    }
    //==============================================================================================
    @Override
    public void onBackPressed() {
        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        fragmentManager = getSupportFragmentManager();

        //경로요약->백키
        if(null != fragmentManager.findFragmentByTag(tag_summary_fragment)) {
            if(RouteFlag){ //현재 주행중일 때
                showTbtLayout(true);
                m_FMInterface.FM_StartRGService(FMBaseActivity.onFatosMapListener);  //경로 안내 시작
                setAPP_MODE(TNaviActionCode.APP_MODE_ROUTE);
                modeProcess(new Intent());
            }else{
                GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchMainFragment.newInstance(), null);
                m_FMInterface.FM_CancelRoute();
                routeCancel();
            }
        }else if(null != fragmentManager.findFragmentByTag(tag_search_fragment)){ //검색화면이 떠있다면
            if(Objects.equals(APP_MODE, TNaviActionCode.SEARCH_MODE)) // 검색화면에서 요약화면으로 다시 돌아가야할 때,
            {
                routeSuccess();
            }
            else
            {

                setDriveInfoThreadFlag(true);

                setSearchWord("");
                GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchMainFragment.newInstance(), null);
                setAPP_MODE(TNaviActionCode.APP_MODE_DEFAULT);
                mapMoveDirectCurPos();
            }
        }
        else if(fragmentManager.findFragmentByTag(tag_showmap_fragment) != null)
        {
            setDriveInfoThreadFlag(false);

            if(m_strMapViewRouteViaOrGoal.equals(TNaviActionCode.JUST_GOAL))
            {
                Bundle bundle = new Bundle();
                bundle.putString(TNaviActionCode.APP_MODE, TNaviActionCode.APP_MODE_JUST_GOAL_SEARCH);
                bundle.putString(TNaviActionCode.ROUTE_VIA_OR_GOAL, TNaviActionCode.JUST_GOAL);
                bundle.putParcelable(TNaviActionCode.ARRAYLIST_MAP_TO_SEARCH, Parcels.wrap(m_TempPoiItem));
                bundle.putString(TNaviActionCode.SEARCH_MODE_WORD, m_SearchWord);

                GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchFragment.newInstance(), tag_search_fragment, bundle);

                setAPP_MODE(TNaviActionCode.APP_MODE_SEARCH);
            }
            else if(m_strMapViewRouteViaOrGoal.equals(TNaviActionCode.CHANGE_VIA_GO_ROUTE) || m_strMapViewRouteViaOrGoal.equals(TNaviActionCode.CHANGE_GOAL_GO_ROUTE))
            {
                Bundle bundle = new Bundle();
                bundle.putString(TNaviActionCode.APP_MODE,TNaviActionCode.APP_MODE_SEARCH_TO_ROUTE);
                bundle.putString(TNaviActionCode.ROUTE_VIA_OR_GOAL, m_strMapViewRouteViaOrGoal);
                bundle.putParcelable(TNaviActionCode.ARRAYLIST_MAP_TO_SEARCH, Parcels.wrap(m_TempPoiItem));
                bundle.putString(TNaviActionCode.SEARCH_MODE_WORD, m_SearchWord);

                GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchFragment.newInstance(), tag_search_fragment, bundle);

                setAPP_MODE(TNaviActionCode.SEARCH_MODE);
            }

            m_strMapViewRouteViaOrGoal = "";
        } else if(APP_MODE.equals(TNaviActionCode.APP_MODE_ROUTE)){
            popUpDialogShow(getString(R.string.string_yes),getString(R.string.string_title_route_cancel),
                    getString(R.string.string_content_route_cancel), true);
        } else if(APP_MODE.equals(TNaviActionCode.APP_MODE_SIMULATE)){
            simulCancel();
        }
        else
        {
            //연타시 딜레이가 있는 디바이스의 경우 다른 화면에서 종료 팝업이 안내되는 경우가 있어서, 앱모드 한번 더 검증
            if(APP_MODE.equals(TNaviActionCode.APP_MODE_DEFAULT)){
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.string_hi_close))
                        .setPositiveButton(getString(R.string.string_btn_popup_positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                {
                                    finishAndRemoveTask();
                                }
                                else
                                {
                                    finish();
                                }
                                //앱 종료시 gps서비스 남아있는 문제 때문에 super.onDestroy 호출 후 process kill 하도록 수정
                                TNaviMainActivity.super.onDestroy();

                                if(bThreadFlag)
                                {
                                    bThreadFlag = false;
                                }

                                unregisterReceiver(receiver);
                                releaseFatosAuto();

                                Process.killProcess(Process.myPid());
                                System.exit(0);
                            }
                        })
                        .setNegativeButton(getString(R.string.string_btn_popup_negative), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                m_gApp.startAutoRouteGudiance();
                            }
                        })
                        .show();
            }
        }
    }
    //==============================================================================================
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(bThreadFlag)
        {
            bThreadFlag = false;
        }

        unregisterReceiver(receiver);

        releaseFatosAuto();

        Process.killProcess(Process.myPid());
        System.exit(0);
    }
    //==============================================================================================
    /**
     * 주행 정보
     */
    private FMDriveInfo GetDriveInfo(){
        FMDriveInfo driveinfo = m_FMInterface.FM_GetDriveInfo();

        if(driveinfo == null) {
            return null;
        }

        return driveinfo;
    }
    //==============================================================================================
    static class HttpResultHandler extends Handler {
        private final WeakReference<TNaviMainActivity> mActivity;

        HttpResultHandler(TNaviMainActivity activity) {
            mActivity = new WeakReference<TNaviMainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            TNaviMainActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
    //==============================================================================================
    public void getSearchPoiItem(String strMsg)
    {
        ArrayList<String> arInfo = new ArrayList<>();
        arInfo.add(strMsg);

        int index = prefs.getInt(SettingsCode.getKeyIndex(), 0);
        String[] countrycodes;

        countrycodes = getResources().getStringArray(R.array.wecountry_codes);

        arInfo.add(countrycodes[0]);

        arInfo.add(String.valueOf(AMapPositionManager.getCurrentLonX()));
        arInfo.add(String.valueOf(AMapPositionManager.getCurrentLatY()));
        //fatostest - 출발지 임시로 고정
        // 태국
//        arInfo.add(String.valueOf(100.577896));
//        arInfo.add(String.valueOf(13.767313));

        // 싱가포르
//        arInfo.add(String.valueOf(103.867790));
//        arInfo.add(String.valueOf(1.355378));

//        String strTemp = getString(R.string.string_weallcate);
//
//        if (!SettingsCode.getValueCategory().equals(strTemp))
//        {
//            arInfo.add(SettingsCode.getValueCategory());
//        }

        m_FMInterface.FM_SearchPOIForTNavi(new HttpResultHandler(TNaviMainActivity.this), arInfo, false);
    }
    //==============================================================================================
    public String[] get_strAddr(){
        return strAddr;
    }
    //==============================================================================================
    public void set_strAddr(String strAddr, int index){
        this.strAddr[index] = strAddr;
    }
    //==============================================================================================
    public void setStartCoord(double i_dX, double i_dY)
    {
        if(i_dX <= 0 || i_dY <= 0)
        {
            this.m_dStartCoord[0] = m_FMInterface.FM_GetDriveInfo().getM_fCurLonX();
            this.m_dStartCoord[1] = m_FMInterface.FM_GetDriveInfo().getM_fCurLatY();
        }
        else {
            this.m_dStartCoord[0] = i_dX;
            this.m_dStartCoord[1] = i_dY;
        }
    }
    //==============================================================================================
    //목적지로 경로 탐색
    public void routeTovia(double x, double y, String flag, @Nullable NPoiItem i_PoiItem){
//        Log.e(TAG,"routeToVia!");
        clearTouchInfo();
        show_ProgressDialog(R.string.string_higetting_search_direction , false);

        if(i_PoiItem != null) {
            m_RecentPOIItem = i_PoiItem;

            if (i_PoiItem.getAddressFull() == null){
                i_PoiItem.setAdminLevel1LocalName("");
            }

            if(i_PoiItem.getEnglishName() == null){
                i_PoiItem.setEnglishName("");
            }

            if(i_PoiItem.getAddressFull().equals("") || i_PoiItem.getEnglishName().equals("")){
                String address_wgs = String.format("%.5f",i_PoiItem.getLocationPointX()) + ", " +
                        String.format("%.5f",i_PoiItem.getLocationPointY());
                m_RecentPOIItem.setAdminLevel1EnglishName("No Address Name");
                m_RecentPOIItem.setEnglishName(address_wgs);
            }
        }

        m_FMInterface.FM_GetViaPOIList().clear();
        List<RoutePosition> positionList = m_FMInterface.FM_GetViaPOIList();
        RoutePosition positionList0 = new RoutePosition();
        RoutePosition positionList1 = new RoutePosition();

        switch(flag){
            case TNaviActionCode.JUST_GOAL :
                positionList.clear();

                positionList0.x = this.m_dStartCoord[0];
                positionList0.y = this.m_dStartCoord[1];

                //fatostest - 임시로 시작 위치 변경
                // 태국
//                positionList0.x = 100.577896;
//                positionList0.y = 13.767313;

                // 싱가포르
//                positionList0.x = 103.867790;
//                positionList0.y = 1.355378;

                positionList0.name = this.strAddr[0];
                positionList.add(positionList0);

                positionList1.x = x;
                positionList1.y = y;

                positionList1.name = this.strAddr[1];

                if(this.strAddr[1].equals("")){
                    positionList1.name = "Goal";
                }

                positionList1.bPassingPoint = false;

                positionList.add(positionList1);

                saved_data.clearData();

                saved_data.viaX = positionList0.x;
                saved_data.viaY = positionList0.y;
                saved_data.goalX = x;
                saved_data.goalY = y;

                break;

            case TNaviActionCode.CHANGE_VIA_GO_ROUTE:
                m_FMInterface.FM_CancelRoute();
                positionList0.x = x;
                positionList0.y = y;
                positionList0.name = this.strAddr[0];

                positionList1.x = saved_data.goalX;
                positionList1.y = saved_data.goalY;

                positionList1.name = this.strAddr[1];

                if(this.strAddr[1].equals("")){
                    positionList1.name = "Goal";
                }

                positionList.add(positionList0);
                positionList.add(positionList1);

                saved_data.viaX = x;
                saved_data.viaY = y;

                break;

            case TNaviActionCode.CHANGE_GOAL_GO_ROUTE:
                m_FMInterface.FM_CancelRoute();
                positionList0.x = saved_data.viaX;
                positionList0.y = saved_data.viaY;
                positionList0.name = this.strAddr[0];

                positionList1.x = x;
                positionList1.y = y;

                positionList1.name = this.strAddr[1];

                if(this.strAddr[1].equals("")){
                    positionList1.name = "Goal";
                }

                positionList.add(positionList0);
                positionList.add(positionList1);

                saved_data.goalX = x;
                saved_data.goalY = y;

                break;

            case TNaviActionCode.CHANGE_VIA_GOAL_GO_ROUTE: // 출발지, 목적지 둘 다 재탐

                break;

            case TNaviActionCode.CHANGE_VIA_AND_GOAL:
                isChangeViaWithGoalFlag = true;
                m_FMInterface.FM_CancelRoute();

                positionList0.x = saved_data.goalX;
                positionList0.y = saved_data.goalY;
                positionList0.name = this.strAddr[0];

                positionList1.x = saved_data.viaX;
                positionList1.y = saved_data.viaY;
                positionList1.name = this.strAddr[1];

                positionList.add(positionList0);
                positionList.add(positionList1);

                saved_data.viaX = positionList0.x;
                saved_data.viaY = positionList0.y;

                saved_data.goalX = positionList1.x;
                saved_data.goalY = positionList1.y;

                break;
        }

        m_FMInterface.FM_RouteVol2_Via(new HttpResultHandler(TNaviMainActivity.this), positionList);
    }
    //==============================================================================================
    private PathPointInfo m_RoutePathInfo;
    //==============================================================================================
    private PathPointInfo setPathPointInfo(List<RoutePosition> m_PoiList)
    {
        m_RoutePathInfo = m_gApp.getRoutePathInfo();
        m_RoutePathInfo.setRpType(AMapPositionManager.GetInstance().getRpType());

        m_RoutePathInfo.reqType = 0; //탐색
        int searchOption = 0;

        if(m_PoiList.get(0).x == 0f || m_PoiList.get(0).y == 0f)
        {
            m_RoutePathInfo.startX = m_PoiList.get(0).x = AMapPositionManager.getCurrentLonX();
            m_RoutePathInfo.startY = m_PoiList.get(0).y = AMapPositionManager.getCurrentLatY();

            m_RoutePathInfo.startName = "Start";
        }
        else
        {
            m_RoutePathInfo.startX = m_PoiList.get(0).x;
            m_RoutePathInfo.startY = m_PoiList.get(0).y;
            m_RoutePathInfo.startName =  m_PoiList.get(0).name;
            AMapPositionManager.setStartName(m_RoutePathInfo.startName);
        }

        m_RoutePathInfo.endX = m_PoiList.get(m_PoiList.size() - 1).x;
        m_RoutePathInfo.endY = m_PoiList.get(m_PoiList.size() - 1).y;
        m_RoutePathInfo.angle = (int)AMapPositionManager.getCurAngle();

        AMapPositionManager.setStartFlagYX(Double.toString(m_RoutePathInfo.startX), Double.toString(m_RoutePathInfo.startY));
        AMapPositionManager.setGoalYX(Double.toString(m_RoutePathInfo.endX), Double.toString(m_RoutePathInfo.endY));

        m_RoutePathInfo.endName = m_PoiList.get(m_PoiList.size() - 1).name;//mPoiItem.getPOIName();
        AMapPositionManager.setGoalName(m_RoutePathInfo.endName);
        AMapPositionManager.setGoalAddrName(m_PoiList.get(m_PoiList.size() - 1).addr);
        m_RoutePathInfo.searchOption = searchOption;
        m_RoutePathInfo.directionOption = 1;

        m_RoutePathInfo.setViaPoint(AMapPositionManager.getReRouteViaPointX(), AMapPositionManager.getReRouteViaPointY());

        AMapPositionManager.setRoutePosition(m_RoutePathInfo.startY, m_RoutePathInfo.startX, m_RoutePathInfo.endY, m_RoutePathInfo.endX, m_RoutePathInfo.endY, m_RoutePathInfo.endX);

        return m_RoutePathInfo;
    }
    //==============================================================================================
    //경탐 완료시 실행,
    private void routeSuccess(){

        ArrayList<RouteCardData> summaryCardData = new ArrayList<RouteCardData>();
        ArrayList<RouteSummaryData> summaryData = m_FMInterface.FM_RouteSummary(FMBaseActivity.onFatosMapListener);

        for(int i=0; i<summaryData.size(); i++ ){
            RouteSummaryData summarydata = summaryData.get(i);
            //String strTypeName, int nType, int nLength, int nTime, int nFee, int nAvgSpeed, int nTurnCongestion, int nOptionColor
            RouteCardData data = new RouteCardData(summarydata.strTypeName,summarydata.nType,summarydata.nLength,summarydata.nTime,summarydata.nFee,
                    summarydata.nAvgSpeed,
                    summarydata.nTurnCongestion,m_Context);
            summaryCardData.add(data);
        }
        saved_summaryCardData = new ArrayList<RouteCardData>();
        saved_summaryCardData = summaryCardData;

        if(!bLastRouteFlag) {
            setAPP_MODE(TNaviActionCode.APP_MODE_SHOWING_SUMMARY);

            fragmentManager = getSupportFragmentManager();

            Bundle args = new Bundle();
            args.putParcelableArrayList(TNaviActionCode.LONGTOUCH_ROUTESUMMARY, summaryCardData);

            SummaryFragment summaryFragment = new SummaryFragment();
            summaryFragment.setArguments(args);

            GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container,summaryFragment,tag_summary_fragment,args);
        }
    }
    //==============================================================================================
    private void show_ProgressDialog(int message, boolean cancelable){

        mProgressdialog = ProgressDialog.show(this, "", getResources().getString(message), true, cancelable);
        mProgressdialog.show();

    }
    //==============================================================================================
    private void hide_ProgressDialog(){
        if(mProgressdialog != null && mProgressdialog.isShowing())
        {
            mProgressdialog.dismiss();
        }
    }
    //==============================================================================================
    private ArrayList<POIItem> searchResultPOI;
    //==============================================================================================
    private void handleMessage(Message msg) {
        String result = msg.getData().getString(AMapGoogleSearchUtil.RESULT);
        ArrayList<String> searchList = new ArrayList<String>();

        if (result.equals(FMError.FME_SUCCESS_SEARCH_SUCCESS)) {
            searchResultPOI = m_FMInterface.FM_GetSearchResult(FMSortOption.FM_SORT_BY_DIST);

            if(searchResultPOI != null)
            {
                SearchFragment sf = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                sf.MergePOIItem(searchResultPOI, result);
            }
        }
        else if (result.equals(ErrorMessage.SUCCESS_NAVER_RESULT)) {
            searchResultPOI = m_gApp.getPoiSearchDataNaver();

            if(searchResultPOI != null)
            {
                Collections.sort(searchResultPOI, new AMapUtil.PoiCurPosDistDescCompareForPOIItem());
                SearchFragment sf = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                sf.MergePOIItem(searchResultPOI, result);

            }
        }
        else if (result.equals(ErrorMessage.ERROR_NAVER_RESULT)) {
            searchResultPOI = m_gApp.getPoiSearchDataNaver();

            if(searchResultPOI != null)
            {
                searchResultPOI.clear();

                SearchFragment sf = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                sf.MergePOIItem(searchResultPOI, result);
            }
        }
        else if(result.equals(FMError.FME_MESSAGE_STATE_POI_AUTO))
        {
            ArrayList<String> arDessert = m_FMInterface.FM_GetRecommentWordResult();

            if(arDessert != null) {
                FatosToast.ShowFatosYellow("FME_MESSAGE_STATE_POI_AUTO Size : " + arDessert.size());
            }
            else{
                String btn_text = getResources().getString(R.string.string_popupTitle_btn_Ok);
                String title = getResources().getString(R.string.string_popupTitle_error);
                popUpDialogShow(btn_text,title,"arDessert is null",false);
            }

        }
        else if(result.equals(FMError.FME_MESSAGE_SEARCH_ERROR) || result.equals(ErrorMessage.TIMEOUT_RESULT) || result.equals(ErrorMessage.ERROR_NOSTRA_RESULT))
        {
            String btn_text = getResources().getString(R.string.string_popupTitle_btn_Ok);
            String title = getResources().getString(R.string.string_popupTitle_error);
            popUpDialogShow(btn_text, title, result, false);

            searchResultPOI = m_FMInterface.FM_GetSearchResult(FMSortOption.FM_SORT_BY_DIST);

            if(searchResultPOI != null)
            {
                searchResultPOI.clear();

                SearchFragment sf = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                sf.MergePOIItem(searchResultPOI, result);
            }
        }
        else if(result.equals(FMError.FME_SUCCESS_ROUTE_SUCCESS))
        {
//            mapMoveCurrnetPostion();
            mapMoveAniReset();

            hide_ProgressDialog();

            routeSuccess();

            //최근목적지로 저장
            addRecentPOI();
            SaveRecentPOI();
        }
        else if(result.equals(FMError.FME_ERROR_ROUTE))
        {
            hide_ProgressDialog();
            String btn_text = getResources().getString(R.string.string_popupTitle_btn_Ok);
            String title = getResources().getString(R.string.string_popupTitle_error);
            popUpDialogShow(btn_text,title,"FME_ERROR_ROUTE",false);

            //경로 탐색 에러나면 POIItem 초기화
            m_POIItem = null;
            m_RecentPOIItem = null;
        }
        else if(result.equals(FMError.FME_MESSAGE_FIX_SEARCH_SUCCESS))
        {
            String strAddr = msg.getData().getString(ErrorMessage.ADDR_SEARCH_SUCCESS_RESULT);
            FatosToast.ShowFatosYellow(strAddr);
        }
        else{
            hide_ProgressDialog();

            FatosToast.ShowFatosYellow(result);
        }
    }
    //==============================================================================================
    public void addRecentPOI()
    {
        setM_ALPoiItem();

        for(int i = 0; i < m_ALPoiItem.size(); i++) {
            if (m_ALPoiItem.size() >= SettingsCode.getKeyRecentPoiCount()) {
                m_ALPoiItem.remove(0);
            }
        }

        if(m_RecentPOIItem == null){
            return;
        }

        for(int i = 0; i < m_ALPoiItem.size(); i++)
        {
            NPoiItem tempPoiItem = m_ALPoiItem.get(i);

            if(tempPoiItem.getLocationPointX() == m_RecentPOIItem.getLocationPointX()
            && tempPoiItem.getLocationPointY() == m_RecentPOIItem.getLocationPointY())
            {
                m_POIItem = null;
                m_RecentPOIItem = null;

                return;
            }
        }

        if(m_RecentPOIItem != null){
            m_ALPoiItem.add(m_RecentPOIItem);
        }

        m_POIItem = null;
        m_RecentPOIItem = null;
    }
    //==============================================================================================
    public void SaveRecentPOI()
    {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        sharedPreferences = getSharedPreferences(TNaviActionCode.RECENT_ROUTE_KEY, MODE_PRIVATE);

        editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String jsonUsers = gson.toJson(m_ALPoiItem);

        editor.putString(TNaviActionCode.RECENT_ROUTE_SUB_KEY, jsonUsers);
        editor.commit();
    }
    //==============================================================================================
    public ArrayList<NPoiItem> LoadRecentPOI()
    {
        SharedPreferences sharedPreferences;
        List<NPoiItem> listItems;

        sharedPreferences = getSharedPreferences(TNaviActionCode.RECENT_ROUTE_KEY, MODE_PRIVATE);

        if (sharedPreferences.contains(TNaviActionCode.RECENT_ROUTE_SUB_KEY))
        {
            String jsonItems = sharedPreferences.getString(TNaviActionCode.RECENT_ROUTE_SUB_KEY, "");

            Gson gson = new Gson();

            NPoiItem[] Item = gson.fromJson(jsonItems, NPoiItem[].class);

            listItems = Arrays.asList(Item);
            listItems = new ArrayList<>(listItems);
        }
        else
        {
            return null;
        }

        return (ArrayList<NPoiItem>) listItems;
    }
    //==============================================================================================
    public void updateCancelRoute() {
        updateCancelRouteLayout();
    }
    //==============================================================================================
    //검색 최종단어를 저장했다 필요한곳에 쓰자
    public void setSearchWord(String i_strWord)
    {
        m_SearchWord = i_strWord;
    }
    //==============================================================================================
    public String getSearchWord()
    {
        return m_SearchWord;
    }
    //==============================================================================================
    public void setSearchList(ArrayList<NPoiItem> i_PoiItem)
    {
        m_TempPoiItem = i_PoiItem;
    }
    //==============================================================================================
    public void clearSearchList()
    {
        if(m_TempPoiItem != null) {
            m_TempPoiItem.clear();
        }
    }
    //==============================================================================================
    //1초에 한번씩 행정동명 가져오는 쓰레드
    Thread DriveInfoThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(bThreadFlag)
            {
                try
                {
                    synchronized (DriveInfoThread)
                    {
                        while(m_bPaused)
                        {
                            try
                            {
                                DriveInfoThread.wait();
                            }
                            catch(InterruptedException e)
                            {

                            }
                        }
                    }

                    Thread.sleep(1000);

                    final FMDriveInfo fmDriveInfo = GetDriveInfo();

                    //기본 모드 일때
                    if(Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_DEFAULT))
                    {
                        fragmentManager = getSupportFragmentManager();
                        searchMainFragment = (SearchMainFragment)fragmentManager.findFragmentById(R.id.container);
                        double[] latlon = new double[2];
                        m_FMInterface.FM_GetMapCenterPos(latlon);

                        Message msg = new Message();
                        msg.what = TNaviActionCode.HANDLER_MAP_MOVE_DEFAULT;
                        Bundle data = new Bundle();
                        data.putString("Address", fmDriveInfo.getM_strCurPosName());
                        data.putDoubleArray("Coords", latlon);
                        msg.setData(data);
                        mapUseHandler.sendMessage(msg);
                    }
                    //지도보기 모드 일 때
                    else if(Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_SHOW_MAP))
                    {
                        fragmentManager = getSupportFragmentManager();
                        searchShowMapFragment = (SearchShowMapFragment) fragmentManager.findFragmentById(R.id.container);

                        double[] latlon = new double[2];

                        m_FMInterface.FM_GetMapCenterPos(latlon);

                        Message msg = new Message();
                        msg.what = TNaviActionCode.HANDLER_MAP_MOVE_SHOW_MAP;
                        Bundle data = new Bundle();
                        data.putString("Address", fmDriveInfo.getM_strCurPosName());
                        data.putDoubleArray("Coords", latlon);
                        msg.setData(data);
                        mapUseHandler.sendMessage(msg);
                    }
                    //모의 주행 모드일때
                    else if(Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_SIMULATE))
                    {
                        fragmentManager = getSupportFragmentManager();
                        searchMainFragment = (SearchMainFragment)fragmentManager.findFragmentById(R.id.container);

                        Message msg = new Message();
                        msg.what = TNaviActionCode.HANDLER_MAP_MOVE_SIMULATE;
                        Bundle data = new Bundle();

                        double[] latlon = new double[2];
                        m_FMInterface.FM_GetMapCenterPos(latlon);
                        data.putDoubleArray("Coords", latlon);
                        data.putString("Address", fmDriveInfo.getM_strCurPosName());

                        if(SettingsCode.getDistanceUnit() == 0)
                        {
                            data.putString("RemainDistance", GUtilLib.getInstance(m_Context).updateTotalRemainDist(fmDriveInfo.getM_nTotalRemainderDist()));
                        }
                        else
                        {
                            data.putString("RemainDistance", GUtilLib.getInstance(m_Context).updateTotalRemainDistForMile(fmDriveInfo.getM_nTotalRemainderDist()));
                        }

                        //fatostest - 남은시간 일자 제거
                        data.putString("RemainTime", GUtilLib.getInstance(m_Context).updateTotalRemainTimeRouteWithoutDay(fmDriveInfo.getM_nServiceLinkRemainderTime(), true, m_gApp.getFatosLocale()));
                        data.putString("RemainTimeTwo", GUtilLib.getInstance(m_Context).updateTotalRemainTimeRouteWithoutDay(fmDriveInfo.getM_nServiceLinkRemainderTime(), false, m_gApp.getFatosLocale()));

                        //fatostest - 남은시간 일자 포함
//                        data.putString("RemainTime", GUtilLib.getInstance(m_Context).updateTotalRemainTimeRoute(fmDriveInfo.getM_nServiceLinkRemainderTime(), true, m_gApp.getFatosLocale()));
//                        data.putString("RemainTimeTwo", GUtilLib.getInstance(m_Context).updateTotalRemainTimeRoute(fmDriveInfo.getM_nServiceLinkRemainderTime(), false, m_gApp.getFatosLocale()));

                        msg.setData(data);
                        mapUseHandler.sendMessage(msg);
                    }
                    //경로 주행 모드일때
                    else if(Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_ROUTE))
                    {
                        fragmentManager = getSupportFragmentManager();
                        searchMainFragment = (SearchMainFragment)fragmentManager.findFragmentById(R.id.container);

                        Message msg = new Message();
                        msg.what = TNaviActionCode.HANDLER_MAP_MOVE_ROUTE;
                        Bundle data = new Bundle();
                        data.putString("Address", fmDriveInfo.getM_strCurPosName());

                        double[] latlon = new double[2];
                        m_FMInterface.FM_GetMapCenterPos(latlon);
                        data.putDoubleArray("Coords", latlon);

                        if(SettingsCode.getDistanceUnit() == 0)
                        {
                            data.putString("RemainDistance", GUtilLib.getInstance(m_Context).updateTotalRemainDist(fmDriveInfo.getM_nTotalRemainderDist()));
                        }
                        else
                        {
                            data.putString("RemainDistance", GUtilLib.getInstance(m_Context).updateTotalRemainDistForMile(fmDriveInfo.getM_nTotalRemainderDist()));
                        }

                        //fatostest - 남은시간 일자 제거
                        data.putString("RemainTime", GUtilLib.getInstance(m_Context).updateTotalRemainTimeRouteWithoutDay(fmDriveInfo.getM_nServiceLinkRemainderTime(), true, m_gApp.getFatosLocale()));
                        data.putString("RemainTimeTwo", GUtilLib.getInstance(m_Context).updateTotalRemainTimeRouteWithoutDay(fmDriveInfo.getM_nServiceLinkRemainderTime(), false, m_gApp.getFatosLocale()));

                        //fatostest - 남은시간 일자 포함
//                        data.putString("RemainTime", GUtilLib.getInstance(m_Context).updateTotalRemainTimeRoute(fmDriveInfo.getM_nServiceLinkRemainderTime(), true, m_gApp.getFatosLocale()));
//                        data.putString("RemainTimeTwo", GUtilLib.getInstance(m_Context).updateTotalRemainTimeRoute(fmDriveInfo.getM_nServiceLinkRemainderTime(), false, m_gApp.getFatosLocale()));

                        msg.setData(data);
                        mapUseHandler.sendMessage(msg);
                    }
                    else if(Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_SHOWING_SUMMARY))
                    {

                    }
                }
                catch (Exception e)
                {

                }
            }
        }
    });
    //==============================================================================================
    public void setDriveInfoThreadFlag(boolean i_bFlag)
    {
        if(!i_bFlag) {
            synchronized (DriveInfoThread) {
                m_bPaused = true;
            }
        }
        else
        {
            synchronized (DriveInfoThread)
            {
                m_bPaused = false;

                DriveInfoThread.notify();
            }
        }
    }
    //==============================================================================================
    //메인화면 지도이동할때마다 행정동명 가져오기
    Handler mapUseHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String strAddr;
            double[] dCoords = new double[2];
            String strRemainDistance;
            String strRemainTime;
            String strRemainTimeTwo;

            switch(msg.what)
            {
                case TNaviActionCode.HANDLER_MAP_MOVE_DEFAULT :
                    strAddr = msg.getData().getString("Address");
                    dCoords = msg.getData().getDoubleArray("Coords");

                    if(searchMainFragment != null && dCoords != null)
                    {
                        searchMainFragment.setAddrText(dCoords);
                    }

                    if(imageView_marker.getVisibility() == View.VISIBLE)
                    {
                        ActivityManager activityManager = (ActivityManager) m_Context.getSystemService(Context.ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);

                        ActivityManager.RunningTaskInfo running = info.get(0);
                        ComponentName componentName = running.topActivity;

                        if (TNaviPickerActivity.class.getName().equals(componentName.getClassName()))
                        {
                            if(m_dMapTouchScreenWGS84 != null)
                            {
                                m_dMapTouchScreenWGS84[0] = dCoords[0];
                                m_dMapTouchScreenWGS84[1] = dCoords[1];
                            }

                            if(m_POIItem == null)
                            {
                                m_POIItem = new NPoiItem();
                            }

                            m_POIItem.init();
                            m_POIItem.setLocationPointX(dCoords[0]);
                            m_POIItem.setLocationPointY(dCoords[1]);
                            m_POIItem.setEnglishName(strAddr);
                            m_POIItem.setStrName(strAddr);

                            Intent intent = new Intent();
                            intent.setAction("SET_TEXT");
                            intent.putExtra("strX", String.format("%.4f", dCoords[0]));
                            intent.putExtra("strY", String.format("%.4f", dCoords[1]));
                            intent.putExtra("strAddress", strAddr);
                            sendBroadcast(intent);
                        }
                    }

                    break;

                case TNaviActionCode.HANDLER_MAP_MOVE_SHOW_MAP :
                    strAddr = msg.getData().getString("Address");
                    dCoords = msg.getData().getDoubleArray("Coords");

                    if(searchShowMapFragment != null)
                    {
                        if(!m_bPaused) {
                            if (dCoords.length > 0) {
                                searchShowMapFragment.setName(dCoords);
                            }
                            searchShowMapFragment.startSearchAddress();

                        }
                    }
                    break;

                case TNaviActionCode.HANDLER_MAP_MOVE_SIMULATE :
                    strAddr = msg.getData().getString("Address");
                    dCoords = msg.getData().getDoubleArray("Coords");
                    strRemainDistance = msg.getData().getString("RemainDistance");
                    strRemainTime = msg.getData().getString("RemainTime");
                    strRemainTimeTwo = msg.getData().getString("RemainTimeTwo");

                    if(searchMainFragment != null || dCoords!=null)
                    {
                        searchMainFragment.setAddrText(dCoords);
                        searchMainFragment.setRemainDistance(strRemainDistance);

                        searchMainFragment.setRemainTime(strRemainTime, true);
                        searchMainFragment.setRemainTime(strRemainTimeTwo, false);
                    }
                    break;

                case TNaviActionCode.HANDLER_MAP_MOVE_ROUTE :
                    strAddr = msg.getData().getString("Address");
                    dCoords = msg.getData().getDoubleArray("Coords");
                    strRemainDistance = msg.getData().getString("RemainDistance");
                    strRemainTime = msg.getData().getString("RemainTime");
                    strRemainTimeTwo = msg.getData().getString("RemainTimeTwo");

                    if(searchMainFragment != null || dCoords!=null || searchMainFragment.isAdded())
                    {
                        searchMainFragment.setAddrText(dCoords);
                        searchMainFragment.setRemainDistance(strRemainDistance);

                        searchMainFragment.setRemainTime(strRemainTime, true);
                        searchMainFragment.setRemainTime(strRemainTimeTwo, false);
                    }
                    break;
            }

        }
    };
    //==============================================================================================
    Handler popUpHandler = new Handler(){
        public void handleMessage(Message msg){
            String title = msg.getData().getString("title");
            String content = msg.getData().getString("content");
            String btn_text = msg.getData().getString("btn_text");
            boolean cancelable = msg.getData().getBoolean("cancelable");

            AlertDialog.Builder builder = new AlertDialog.Builder(m_Context);
            builder.setTitle(title);
            builder.setMessage(content);

            //경로 취소 여부
            if(cancelable){
                builder.setPositiveButton(getString(R.string.string_btn_popup_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(m_gApp.m_bNaviCallbackFlag) {
                            routeCancel();
                        }
                        else
                        {
                            FatosToast.ShowFatosYellow(getResources().getString(R.string.route_cancel_msg));
                        }
                    }
                });

                builder.setNegativeButton(getString(R.string.string_btn_popup_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }else{
                //// TODO: 2019. 2. 25. 오류안내 메시지는 confirm, 선택 가능한 메시지는 OK, Cancel 로 수정?
                //일반 알림은 neutral 버튼만 사용.
                builder.setNeutralButton(btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
            builder.show();
        }
    };
    //==============================================================================================
    public void popUpDialogShow(String btn_text, String title, String content, boolean cancelable) {
        Bundle bundle = new Bundle();
        bundle.putString("btn_text", btn_text);
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putBoolean("cancelable", cancelable);
        Message msg = new Message();
        msg.setData(bundle);
        popUpHandler.handleMessage(msg);
    }
    //==============================================================================================
    public void setMapCenterVisible(boolean i_bFlag)
    {
        Message msg = new Message();
        msg.what = 10;
        Bundle data = new Bundle();
        data.putBoolean("Flag", i_bFlag);
        msg.setData(data);
        hCenterPoint.sendMessage(msg);
    }
    //==============================================================================================
    Handler hCenterPoint = new Handler()
    {
        @Override
        public void handleMessage(Message message) {
            boolean bFlag = message.getData().getBoolean("Flag");

            if((Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_DEFAULT) ||
                    Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_SIMULATE) ||
                    Objects.equals(APP_MODE, TNaviActionCode.APP_MODE_ROUTE))
                    && bFlag)
            {
                imageView_MapCenterPoint.setVisibility(View.VISIBLE);
            }
            else
            {
                imageView_MapCenterPoint.setVisibility(View.GONE);
            }
        }
    };
    //==============================================================================================
    //주행중인 정보 저장
    public void SaveLastRouteData()
    {
        lastRouteDataItem item_details = new lastRouteDataItem();

        double dlonX = AMapPositionManager.getCurrentLonX();
        double dlatY = AMapPositionManager.getCurrentLatY();
        double glatY = AMapPositionManager.getDestLatY();
        double glonX = AMapPositionManager.getDestLongX();

        String strCurX = "" + dlonX;
        String strCurY = "" + dlatY;
        String strGoalX = "" + glonX;
        String strGoalY = "" + glatY;

        RouteData rgData;
        rgData = m_gApp.rgData().m_pContext[m_gApp.getM_nSelRouteIdx()];

        item_details.setSearchOption(rgData.nType);
        item_details.setFeeOption(rgData.nFee);
        item_details.setPlusCount();
        item_details.setCoordX(strCurX);
        item_details.setCoordY(strCurY);
        item_details.setFrontCoordX(strGoalX);
        item_details.setFrontCoordY(strGoalY);
        item_details.setGoalPosName(AMapPositionManager.getGoalName());
        item_details.setAddrName(AMapPositionManager.getGoalAddrName());
        item_details.setLowerBizName(AMapPositionManager.getLowerBizName());
        item_details.setUpperBizName(AMapPositionManager.getUpperBizName());
        item_details.setTelNo(AMapPositionManager.getTelNo());
        m_gApp.saveLastRouteInfoVol2(m_Context, item_details);
        m_gApp.saveRoutePositionListVol2(m_Context);
        item_details = null;

        //route card data 추가
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = this.getSharedPreferences("LastRoutCardData", Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();

        ArrayList<RouteCardDataVol2> choice = new ArrayList<>();

        RouteCardData data = saved_summaryCardData.get(m_gApp.getM_nSelRouteIdx());

        choice.add(new RouteCardDataVol2(data.strTypeName,data.nType,data.nLength,data.nTime,data.nFee,data.nAvgSpeed,data.nTurnCongestion));
        String jsonUsers = gson.toJson(choice);
        editor.putString("LastRoutCardData", jsonUsers);
        editor.putInt(TNaviActionCode.SELECT_ROUTE_INDEX, m_gApp.getM_nSelRouteIdx());
        editor.commit();
    }
    //==============================================================================================
    public void CheckLastRoute()
    {
        m_lastRouteDataItem = m_gApp.getLastRouteInfoVol2(m_Context);

        if(m_lastRouteDataItem != null)
        {
            if(m_lastRouteDataItem.IsArriveGoal() == false)
            {
                Configuration conf = getResources().getConfiguration();
                conf.locale = m_gApp.getFatosLocale();
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                Resources resources = new Resources(getAssets(), metrics, conf);

                new AlertDialog.Builder(this)
                        .setTitle(resources.getString(R.string.string_hi_routerecovery))
                        .setMessage(resources.getString(R.string.string_hi_routebefore))
                        .setPositiveButton(resources.getString(R.string.string_btn_popup_positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bLastRouteFlag = true;

                                m_gApp.loadRoutePositionListVol2(m_Context);

                                routeVol2(m_lastRouteDataItem);

                                SharedPreferences settings;

                                set_strAddr(getResources().getString(R.string.string_via_hint),0);
                                set_strAddr(m_lastRouteDataItem.getGoalPosName(),1);
                                settings = m_Context.getSharedPreferences("LastRoutCardData", Context.MODE_PRIVATE);

                                if (settings.contains("LastRoutCardData")) {
                                    String jsonString = settings.getString("LastRoutCardData", null);
                                    Gson gson = new Gson();
                                    ArrayList<RouteCardDataVol2> arraydata = gson.fromJson(jsonString, new TypeToken<ArrayList<RouteCardDataVol2>>(){}.getType());
                                    RouteCardDataVol2 data;
                                    data = arraydata.get(0);
                                    saved_summaryCardData = new ArrayList<RouteCardData>();
                                    saved_summaryCardData.add(new RouteCardData(data.strTypeName,data.nType,data.nLength,data.nTime,data.nFee,data.nAvgSpeed,data.nTurnCongestion,m_Context));
                                }

                                setDriveInfoThreadFlag(true);

                                Bundle bundle = new Bundle();
                                bundle.putString(TNaviActionCode.APP_MODE, TNaviActionCode.APP_MODE_ROUTE);
                                mapMoveCurrnetPostion();

                                GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.container, SearchMainFragment.newInstance(), bundle);

                                setAPP_MODE(TNaviActionCode.APP_MODE_ROUTE);
                                modeProcess(new Intent());

                                final FMDriveInfo fmDriveInfo = GetDriveInfo();
                                fragmentManager = getSupportFragmentManager();
                                searchMainFragment = (SearchMainFragment)fragmentManager.findFragmentById(R.id.container);

                                if(searchMainFragment != null)
                                {
                                    if(SettingsCode.getDistanceUnit() == 0)
                                    {
                                        searchMainFragment.setRemainDistance(GUtilLib.getInstance(m_Context).updateTotalRemainDist(fmDriveInfo.getM_nTotalRemainderDist()));
                                    }
                                    else
                                    {
                                        searchMainFragment.setRemainDistance(GUtilLib.getInstance(m_Context).updateTotalRemainDistForMile(fmDriveInfo.getM_nTotalRemainderDist()));
                                    }


                                    //fatostest - 남은시간 일자 제거
                                    searchMainFragment.setRemainTime(GUtilLib.getInstance(m_Context).updateTotalRemainTimeRouteWithoutDay(fmDriveInfo.getM_nServiceLinkRemainderTime(), true, m_gApp.getFatosLocale()), true);
                                    searchMainFragment.setRemainTime(GUtilLib.getInstance(m_Context).updateTotalRemainTimeRouteWithoutDay(fmDriveInfo.getM_nServiceLinkRemainderTime(), false, m_gApp.getFatosLocale()), false);

                                    //fatostest - 남은시간 일자 포함
//                                    searchMainFragment.setRemainTime(GUtilLib.getInstance(m_Context).updateTotalRemainTimeRoute(fmDriveInfo.getM_nServiceLinkRemainderTime(), true, m_gApp.getFatosLocale()), true);
//                                    searchMainFragment.setRemainTime(GUtilLib.getInstance(m_Context).updateTotalRemainTimeRoute(fmDriveInfo.getM_nServiceLinkRemainderTime(), false, m_gApp.getFatosLocale()), false);
                                }
                            }
                        })
                        .setNegativeButton(resources.getString(R.string.string_btn_popup_negative), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bLastRouteFlag = false;
                                m_gApp.ArriveGoalVol2(m_Context);
                            }
                        })
                        .setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                if(i == KeyEvent.KEYCODE_BACK)
                                {
                                    bLastRouteFlag = false;
                                    m_gApp.ArriveGoalVol2(m_Context);
                                }

                                return false;
                            }
                        })
                        .show();
            }
        }
    }
    //==============================================================================================
    //재탐색 플로팅 버튼 위치 저장
    public void SaveFloatingButtonXY(float i_fX, float i_fY)
    {
        prefs = getSharedPreferences(getResources().getString(R.string.app_registerId), MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("floatingX", i_fX);
        editor.putFloat("floatingY", i_fY);
        editor.apply();
    }
    //==============================================================================================
    public float[] LoadFloatingButtonXY()
    {
        float [] fXY = new float[2];

        prefs = getSharedPreferences(getResources().getString(R.string.app_registerId), MODE_PRIVATE);

        fXY[0] = prefs.getFloat("floatingX", 0f);
        fXY[1] = prefs.getFloat("floatingY", 0f);

        return fXY;
    }
    //==============================================================================================
    public void setAutoReRouteforOnemap(int nServerType)
    {
        m_FMInterface.setAutoReRouteforOnemap(nServerType);
    }
    //==============================================================================================

    //==============================================================================================
    //오토스케일 방지
    public void clearTouchInfo()
    {
        onUpdateMapMode(3);
        mapMoveCurrnetPostion();
    }
    //==============================================================================================

}