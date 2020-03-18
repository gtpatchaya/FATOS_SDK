package kr.fatos.tnavi.tnavifragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.AddTrace;
import com.google.firebase.perf.metrics.Trace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import biz.fatossdk.fminterface.FMBaseActivity;
import biz.fatossdk.fminterface.FMInterface;
import biz.fatossdk.newanavi.ANaviApplication;
import biz.fatossdk.newanavi.manager.AMapLog;
import biz.fatossdk.newanavi.manager.AMapPositionManager;
import kr.fatos.tnavi.Activity.TNaviPickerActivity;
import kr.fatos.tnavi.Code.TNaviActionCode;
import kr.fatos.tnavi.Interface.FragmentCommunicator;
import kr.fatos.tnavi.Lib.GoLib;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.TNaviMainActivity;
import kr.fatos.tnavi.WidgetUnit.MovableFloatingActionButton;

public class SearchMainFragment extends Fragment implements View.OnClickListener{
    TextView title_text_view, textView_DriveInfo_Distance, textView_Day, textView_Daytext, textView_Time, textView_Eta;
    static TextView textView_MainAddress;
    ImageButton imageButton_MainMenu, imageButton_PlayStop, imageButton_Close, imageButton_SpeedControl, imageButton_ReRoute;
    RelativeLayout container_button, relativeLayout_MenuItem;
    LinearLayout linearLayout_driveInfo, linearLayout_PlayStop, linearLayout_Time, ll_bottom;
    LinearLayout linearLayout_BottomOnemap, linearLayout_AddressOnemap, linearLayout_TimeDistOnemap;
    ImageButton imageButton_MainMenuOnemap, imageButton_SearchOnemap;
    TextView textView_GoalTimeOnemap, textView_DayEtaOnemap, textView_DayOnemap, textView_TimeEtaOnemap, textView_EtaOnemap, textView_DistOnemap;
    TextView textView_TimehOnemap, textView_TimeEtaMinOnemap;
    MovableFloatingActionButton floatingActionButton_Reroute;
    static TextView textView_MainAddressOnemap;
    View view_shadow;

    private static Context m_Context;
    Animation animation_in, animation_out;
    Animation animation_left, animation_right;
    boolean isSimul = true;
    Button pop_button1;
    Button pop_button2;
    Button pop_button3;
    Button pop_button4;
    private static FMInterface m_FMInterface;
    double m_dGetX, m_dGetY;
    TimerHandler timerhandler = null;
    RelativeLayout container_searchBar;
    static SearchTask searchTask;
    public FragmentCommunicator fComm;
    private final static int HANDLER_WHAT_TIMER = 1;
    String m_strAppMode = "";
    Trace myTrace;
    private static ANaviApplication m_gApp;
    private static final String TAG = SearchMainFragment.class.getSimpleName();

    private String strAddr;
    private static Activity mActivity;
    int m_nDefaultSpeed = 0;
    //==============================================================================================
    public static SearchMainFragment newInstance(){
        SearchMainFragment fragment = new SearchMainFragment();

        return fragment;
    }
    //==============================================================================================
    @Override
    @AddTrace(name = "onCreateView(SearchMain)", enabled = true)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try{
            m_Context = container.getContext();
        }catch(NullPointerException e){
            Crashlytics.log(Log.ERROR, "SearchMainFragment", "oncreateView onCreate Error:: " + e.toString());
        }

        mActivity = (TNaviMainActivity)getActivity();

        myTrace = FirebasePerformance.getInstance().newTrace("SearchMainTrace");
        myTrace.start();

        try{
            m_FMInterface = ((TNaviMainActivity)getActivity()).m_FMInterface;
            myTrace.incrementMetric("m_FMInterfae not null", 1);
        }catch (NullPointerException e){
            myTrace.incrementMetric("m_FMInterfae is null", 1);
            FMInterface.CreateInstance(m_Context);
            m_FMInterface = FMInterface.GetInstance();
        }

        View view = null;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = inflater.inflate(R.layout.fragment_searchmain, container, false);
        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            view = inflater.inflate(R.layout.fragment_searchmain_land, container, false);
        }

        final IntentFilter filter = new IntentFilter();
        filter.addAction("RELOAD_ACTIVITY");
        filter.addAction("SET_START_TEXT_FROM_SHOWMAP");
        m_Context.registerReceiver(quickMenuFinishReceiver, filter);

        if(getArguments() != null)
        {
            m_strAppMode = getArguments().getString(TNaviActionCode.APP_MODE);
        }

        m_gApp = (ANaviApplication) view.getContext().getApplicationContext();

        return view;
    }
    //==============================================================================================
    @Override
    public void onResume() {
        changeLanguage();
        final IntentFilter filter = new IntentFilter();
        filter.addAction("RELOAD_ACTIVITY");
        filter.addAction("SET_START_TEXT_FROM_SHOWMAP");
        m_Context.registerReceiver(quickMenuFinishReceiver, filter);

        super.onResume();
    }
    //==============================================================================================
    @Override
    public void onStop() {
        super.onStop();

        myTrace.stop();

        if(m_Context != null){
            try {
                m_Context.unregisterReceiver(quickMenuFinishReceiver);
            } catch (IllegalArgumentException e){

            } catch (Exception e) {

            }finally {

            }
        }
    }
    //==============================================================================================
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title_text_view = view.findViewById(R.id.title_text_view);
        imageButton_MainMenu = view.findViewById(R.id.imageButton_MainMenu);
        textView_MainAddress = view.findViewById(R.id.textView_MainAddress);
        linearLayout_driveInfo = view.findViewById(R.id.linearLayout_driveInfo);
        textView_Day = view.findViewById(R.id.textView_Day);
        textView_Daytext = view.findViewById(R.id.textView_Daytext);
        textView_Time = view.findViewById(R.id.textView_Time);
        textView_Eta = view.findViewById(R.id.textView_Eta);
        textView_DriveInfo_Distance = view.findViewById(R.id.textView_DriveInfo_Distance);
        container_searchBar = view.findViewById(R.id.place_search);
        imageButton_PlayStop = view.findViewById(R.id.imageButton_PlayStop);
        imageButton_Close = view.findViewById(R.id.imageButton_Close);
        linearLayout_PlayStop = view.findViewById(R.id.linearLayout_PlayStop);
        linearLayout_Time = view.findViewById(R.id.linearLayout_Time);
        imageButton_SpeedControl = view.findViewById(R.id.imageButton_SpeedControl);
        relativeLayout_MenuItem = view.findViewById(R.id.relativeLayout_MenuItem);
        ll_bottom = view.findViewById(R.id.drive_bottom_layout);
        imageButton_ReRoute = view.findViewById(R.id.imageButton_ReRoute);
        floatingActionButton_Reroute = view.findViewById(R.id.floatingActionButton_Reroute);
        linearLayout_BottomOnemap = view.findViewById(R.id.linearLayout_BottomOnemap);
        view_shadow = view.findViewById(R.id.view_shadow);
        imageButton_MainMenuOnemap = view.findViewById(R.id.imageButton_MainMenuOnemap);
        imageButton_SearchOnemap = view.findViewById(R.id.imageButton_SearchOnemap);
        textView_MainAddressOnemap = view.findViewById(R.id.textView_MainAddressOnemap);
        linearLayout_TimeDistOnemap = view.findViewById(R.id.linearLayout_TimeDistOnemap);
        textView_GoalTimeOnemap = view.findViewById(R.id.textView_GoalTimeOnemap);
        textView_DayEtaOnemap = view.findViewById(R.id.textView_DayEtaOnemap);
        textView_DayOnemap = view.findViewById(R.id.textView_DayOnemap);
        textView_TimeEtaOnemap = view.findViewById(R.id.textView_TimeEtaOnemap);
        textView_EtaOnemap = view.findViewById(R.id.textView_EtaOnemap);
        textView_DistOnemap = view.findViewById(R.id.textView_DistOnemap);
        linearLayout_AddressOnemap = view.findViewById(R.id.linearLayout_AddressOnemap);
        textView_TimehOnemap = view.findViewById(R.id.textView_TimehOnemap);
        textView_TimeEtaMinOnemap = view.findViewById(R.id.textView_TimeEtaMinOnemap);
        animation_in = AnimationUtils.loadAnimation(m_Context, R.anim.pull_in);
        animation_out = AnimationUtils.loadAnimation(m_Context, R.anim.pull_out);
        animation_left = AnimationUtils.loadAnimation(m_Context, R.anim.pull_left);
        animation_right = AnimationUtils.loadAnimation(m_Context, R.anim.pull_right);

        linearLayout_BottomOnemap.setVisibility(View.VISIBLE);
        view_shadow.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)relativeLayout_MenuItem.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ABOVE, R.id.linearLayout_BottomOnemap);
        relativeLayout_MenuItem.setLayoutParams(layoutParams);

        ll_bottom.setVisibility(View.GONE);

        RelativeLayout.LayoutParams linearLayout_PlayStopLayoutParams = (RelativeLayout.LayoutParams)linearLayout_PlayStop.getLayoutParams();
        linearLayout_PlayStopLayoutParams.addRule(RelativeLayout.ABOVE, R.id.linearLayout_BottomOnemap);
        linearLayout_PlayStop.setLayoutParams(linearLayout_PlayStopLayoutParams);

        RelativeLayout.LayoutParams floatingActionButton_RerouteLayoutParams = (RelativeLayout.LayoutParams)floatingActionButton_Reroute.getLayoutParams();
        floatingActionButton_RerouteLayoutParams.addRule(RelativeLayout.ABOVE, R.id.linearLayout_BottomOnemap);
        floatingActionButton_Reroute.setLayoutParams(floatingActionButton_RerouteLayoutParams);

        if(m_FMInterface.nativeIsRoute())
        {
            linearLayout_AddressOnemap.setVisibility(View.GONE);
            linearLayout_TimeDistOnemap.setVisibility(View.VISIBLE);
        }

        timerhandler = new TimerHandler();

        if(getArguments() != null)
        {
            m_strAppMode = getArguments().getString(TNaviActionCode.APP_MODE);
        }

        title_text_view.setOnClickListener(this);
        imageButton_MainMenu.setOnClickListener(this);
        imageButton_PlayStop.setOnClickListener(this);
        imageButton_Close.setOnClickListener(this);
        linearLayout_Time.setOnClickListener(this);
        imageButton_SpeedControl.setOnClickListener(this);
        ll_bottom.setOnClickListener(this);
        imageButton_ReRoute.setOnClickListener(this);
        floatingActionButton_Reroute.setOnClickListener(this);
        imageButton_MainMenuOnemap.setOnClickListener(this);
        imageButton_SearchOnemap.setOnClickListener(this);

        init_ButtonView(view);

        if(m_strAppMode == TNaviActionCode.APP_MODE_ROUTE)
        {
            RouteMode();
        }
        else if(m_strAppMode == TNaviActionCode.APP_MODE_SIMULATE)
        {
            SimulMode();
        }
        else if(m_strAppMode == TNaviActionCode.APP_MODE_DEFAULT)
        {
            DefaultMode();
        }
        
        TNaviActionCode.CUR_SIMUL_SPEED = TNaviActionCode.SIMUL_SPEED_1;
        m_nDefaultSpeed = ((TNaviMainActivity)getActivity()).m_FMInterface.FM_GetSimulationSpeed();
    }
    //==============================================================================================
    private void init_ButtonView(View view){
        ImageButton btn_goPopMenu;

        btn_goPopMenu = view.findViewById(R.id.imageButton_MainMenuOnemap);

        pop_button1 = view.findViewById(R.id.pop_button1);
        pop_button2 = view.findViewById(R.id.pop_button2);
        pop_button3 = view.findViewById(R.id.pop_button3);
        pop_button4 = view.findViewById(R.id.pop_button4);

        btn_goPopMenu.setOnClickListener(this);
        pop_button1.setOnClickListener(this);
        pop_button2.setOnClickListener(this);
        pop_button3.setOnClickListener(this);
        pop_button4.setOnClickListener(this);

        container_button = view.findViewById(R.id.container_button);
    }
    //==============================================================================================
    private void closePopupMenu() {
        if(timerhandler!=null){
            try{
                timerhandler.removeMessages(HANDLER_WHAT_TIMER);
            }catch (Exception e){

            }
        }

        container_button.setVisibility(View.GONE);
        container_button.setAnimation(animation_out);
        animation_out.start();
    }
    //==============================================================================================
    private void goPopupMenu(){
        String appMode = ((TNaviMainActivity)getActivity()).APP_MODE;
        if(container_button != null)
        {
            if(container_button.getVisibility() == View.GONE) {
                //design적용전까지 일단 메인화면에서는 막아둠
                if(appMode.equals(TNaviActionCode.APP_MODE_DEFAULT)){
                    container_button.setVisibility(View.VISIBLE);
                    LinearLayout ll = (LinearLayout)container_button.getChildAt(0);
                    ll.getChildAt(0).setVisibility(View.INVISIBLE);
                    ll.getChildAt(1).setVisibility(View.INVISIBLE);
                    ll.getChildAt(2).setVisibility(View.INVISIBLE);

                    container_button.setAnimation(animation_in);
                    animation_in.start();
                }else if(appMode.equals(TNaviActionCode.APP_MODE_ROUTE)){
                    container_button.setVisibility(View.VISIBLE);
                    LinearLayout ll = (LinearLayout)container_button.getChildAt(0);
                    ll.getChildAt(0).setVisibility(View.INVISIBLE);
                    container_button.setAnimation(animation_in);
                    animation_in.start();

                }else{
                    container_button.setVisibility(View.VISIBLE);
                    container_button.setAnimation(animation_in);
                    animation_in.start();

                    LinearLayout ll = (LinearLayout)container_button.getChildAt(0);

                    ll.getChildAt(0).setVisibility(View.VISIBLE);
                    ll.getChildAt(1).setVisibility(View.VISIBLE);
                    ll.getChildAt(2).setVisibility(View.VISIBLE);
                }

            }
            else if (container_button.getVisibility() == View.VISIBLE)
            {
               closePopupMenu();
            }

        }
    }
    //==============================================================================================
    @Override
    public void onClick(View v) {
        fComm.fragmentContactActivity(v);
        switch(v.getId()) {
            case R.id.imageButton_MainMenu :
            case R.id.imageButton_MainMenuOnemap :
                ActivityManager activityManager = (ActivityManager) m_Context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);

                ActivityManager.RunningTaskInfo running = info.get(0);
                ComponentName componentName = running.topActivity;

                if (TNaviPickerActivity.class.getName().equals(componentName.getClassName()))
                {
                    Intent intent = new Intent();
                    intent.setAction("CLOSE_POPUP");
                    m_Context.sendBroadcast(intent);
                }

                //DEFAULT일때는 팝업메뉴 안띄움
                goPopupMenu();

                Message msg = new Message();
                msg.what = HANDLER_WHAT_TIMER;
                Bundle goDelay = new Bundle();
                goDelay.putString("word", "STOP");
                msg.setData(goDelay);
                timerhandler.sendMessageDelayed(msg, 6000);
                break;

            case R.id.imageButton_PlayStop : //모의주행 일시정지, 시작
                if(m_gApp.IsSimulateMode()) {
                    if(isSimul) {
                        isSimul = false;

                        imageButton_PlayStop.setBackgroundResource(R.drawable.selector_play);
                        ((TNaviMainActivity) getActivity()).m_FMInterface.FM_PauseSimulation(); //모의 주행 일시정지
                    }
                    else
                    {
                        isSimul = true;

                        imageButton_PlayStop.setBackgroundResource(R.drawable.selector_stop);
                        ((TNaviMainActivity) getActivity()).m_FMInterface.FM_ResumeSimulation(); //모의 주행 재시작
                        ((TNaviMainActivity) getActivity()).mapMoveCurrnetPostion();
                    }
                }
                break;

            case R.id.imageButton_Close ://경로 취소
                hide_search(true);
                setPlayStopButtonVisible(false);
                ((TNaviMainActivity)getActivity()).simulCancel();

                break;

            case R.id.imageButton_SpeedControl :
                switch(TNaviActionCode.CUR_SIMUL_SPEED)
                {
                    case TNaviActionCode.SIMUL_SPEED_1 :
                        TNaviActionCode.CUR_SIMUL_SPEED = TNaviActionCode.SIMUL_SPEED_2;
                        imageButton_SpeedControl.setBackgroundResource(R.drawable.selector_speed_control_2);
                        ((TNaviMainActivity)getActivity()).m_FMInterface.FM_SetSimulationSpeed(m_nDefaultSpeed * TNaviActionCode.CUR_SIMUL_SPEED);
                        break;

                    case TNaviActionCode.SIMUL_SPEED_2 :
                        TNaviActionCode.CUR_SIMUL_SPEED = TNaviActionCode.SIMUL_SPEED_4;
                        imageButton_SpeedControl.setBackgroundResource(R.drawable.selector_speed_control_4);
                        ((TNaviMainActivity)getActivity()).m_FMInterface.FM_SetSimulationSpeed(m_nDefaultSpeed * TNaviActionCode.CUR_SIMUL_SPEED);
                        break;

                    case TNaviActionCode.SIMUL_SPEED_4 :
                        TNaviActionCode.CUR_SIMUL_SPEED = TNaviActionCode.SIMUL_SPEED_8;
                        imageButton_SpeedControl.setBackgroundResource(R.drawable.selector_speed_control_8);
                        ((TNaviMainActivity)getActivity()).m_FMInterface.FM_SetSimulationSpeed(m_nDefaultSpeed * TNaviActionCode.CUR_SIMUL_SPEED);
                        break;

                    case TNaviActionCode.SIMUL_SPEED_8 :
                        TNaviActionCode.CUR_SIMUL_SPEED = TNaviActionCode.SIMUL_SPEED_1;
                        imageButton_SpeedControl.setBackgroundResource(R.drawable.selector_speed_control_1);
                        ((TNaviMainActivity)getActivity()).m_FMInterface.FM_SetSimulationSpeed(m_nDefaultSpeed * TNaviActionCode.CUR_SIMUL_SPEED);
                        break;
                }
                break;

            case R.id.imageButton_ReRoute ://재탐색 버튼
                ((TNaviMainActivity)getActivity()).ReRoute();
                break;

            case R.id.floatingActionButton_Reroute : //재탐색 플로팅 버튼
                ((TNaviMainActivity)getActivity()).ReRoute();
                break;

            case R.id.pop_button1: // Go
                ((TNaviMainActivity)getActivity()).m_FMInterface.FM_StartRGService(FMBaseActivity.onFatosMapListener);  //경로 안내 시작
                ((TNaviMainActivity)getActivity()).showTbtLayout(true);
                Bundle bundle = new Bundle();
                bundle.putString(TNaviActionCode.APP_MODE,TNaviActionCode.APP_MODE_ROUTE);
                GoLib.getInstance().goTNaviMainActivity(m_Context, bundle);
                closePopupMenu();
                break;

            case R.id.pop_button2: // Cancel Route
                ((TNaviMainActivity)getActivity()).popUpDialogShow(getString(R.string.string_yes),getString(R.string.string_title_route_cancel),
                        getString(R.string.string_content_route_cancel), true);

                closePopupMenu();
                break;

            case R.id.pop_button3: //RouteInformation
                if(m_gApp.IsSimulateMode()){
                    ((TNaviMainActivity)getActivity()).simulCancel();
                }else{
                    ((TNaviMainActivity)getActivity()).goRouteInfo();
                }

                closePopupMenu();
                break;

            case R.id.pop_button4: //Settings
                Bundle garbage_bundle = new Bundle();
                GoLib.getInstance().goSettingActivity(m_Context,garbage_bundle);
                closePopupMenu();
                break;
        }
    }
    //==============================================================================================
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(getActivity() != null && getActivity() instanceof TNaviMainActivity) {
            strAddr = ((TNaviMainActivity)getActivity()).getAddrText();
            fComm = (FragmentCommunicator) context;
        }
    }
    //==============================================================================================
    public void setAddrText(double[] i_dCoord)
    {
        m_dGetX = i_dCoord[0];
        m_dGetY = i_dCoord[1];

        double[] params = new double[2];

        params[0] = m_dGetY;
        params[1] = m_dGetX;

        try{
            searchTask = new SearchTask();
            searchTask.execute(params);

        }catch (Exception e){

        }
    }
    //==============================================================================================
    public void setAddrText(String i_strAddr)
    {
        textView_MainAddressOnemap.setText(i_strAddr);
    }
    //==============================================================================================
    public void setRemainDistance(String i_strRemainDistance)
    {
        textView_DistOnemap.setText(i_strRemainDistance);
    }
    //==============================================================================================
    public void setRemainTime(String i_strRemainTime, boolean bArrTime)
    {
        if(isAdded()) {
            String strDay = "";
            String strTime = "";
            String strEta = "";

            if (bArrTime) {
                if (i_strRemainTime.contains(" ")) {
                    strTime = i_strRemainTime.substring(0, i_strRemainTime.indexOf(" "));

                    strEta = i_strRemainTime.substring(i_strRemainTime.length() - 2);

                    String strTimeEta = strTime + " " + strEta;

                    textView_GoalTimeOnemap.setText(strTimeEta);
                }
            } else {
                if (i_strRemainTime.contains(",")) {
                    strDay = i_strRemainTime.substring(0, i_strRemainTime.indexOf(","));
                    strTime = i_strRemainTime.substring(i_strRemainTime.indexOf(",") + 1);
                } else {
                    strTime = i_strRemainTime;
                }

                String strTimeH = strTime.substring(0, strTime.indexOf(":"));
                String strTimeM = strTime.substring(strTime.indexOf(":") + 1);

                if (!strDay.equals("")) {
                    textView_DayEtaOnemap.setVisibility(View.VISIBLE);
                    textView_DayOnemap.setVisibility(View.VISIBLE);

                    textView_DayEtaOnemap.setText(strDay);

                    if (Integer.valueOf(strDay) > 1) {
                        textView_DayOnemap.setText(getResources().getString(R.string.days_small) + " ");
                    } else {
                        textView_DayOnemap.setText(getResources().getString(R.string.day_small) + " ");
                    }

                    if (strTimeH.equals("00")) {
                        textView_TimeEtaOnemap.setVisibility(View.GONE);
                        textView_TimehOnemap.setVisibility(View.GONE);
                    } else {
                        textView_TimeEtaOnemap.setVisibility(View.VISIBLE);
                        textView_TimehOnemap.setVisibility(View.VISIBLE);

                        textView_TimeEtaOnemap.setText(strTimeH);
                        textView_TimehOnemap.setText(" " + getString(R.string.h) + " ");
                    }

                    if (strTimeM.equals("00")) {
                        textView_TimeEtaMinOnemap.setVisibility(View.GONE);
                        textView_EtaOnemap.setVisibility(View.GONE);
                    } else {
                        textView_TimeEtaMinOnemap.setVisibility(View.VISIBLE);
                        textView_EtaOnemap.setVisibility(View.VISIBLE);

                        textView_TimeEtaMinOnemap.setText(strTimeM);
                        textView_EtaOnemap.setText(" " + getString(R.string.min));
                    }
                } else if (!strTimeH.equals("00")) {
                    textView_DayEtaOnemap.setVisibility(View.GONE);
                    textView_DayOnemap.setVisibility(View.GONE);

                    textView_TimeEtaOnemap.setVisibility(View.VISIBLE);
                    textView_TimehOnemap.setVisibility(View.VISIBLE);

                    textView_TimeEtaOnemap.setText(strTimeH);
                    textView_TimehOnemap.setText(" " + getString(R.string.h) + " ");

                    if (strTimeM.equals("00")) {
                        textView_TimeEtaMinOnemap.setVisibility(View.GONE);
                        textView_EtaOnemap.setVisibility(View.GONE);
                    } else {
                        textView_TimeEtaMinOnemap.setVisibility(View.VISIBLE);
                        textView_EtaOnemap.setVisibility(View.VISIBLE);

                        textView_TimeEtaMinOnemap.setText(strTimeM);
                        textView_EtaOnemap.setText(" " + getString(R.string.min));
                    }
                } else {
                    textView_DayEtaOnemap.setVisibility(View.GONE);
                    textView_DayOnemap.setVisibility(View.GONE);

                    textView_TimeEtaOnemap.setVisibility(View.GONE);
                    textView_TimehOnemap.setVisibility(View.GONE);

                    textView_TimeEtaMinOnemap.setVisibility(View.VISIBLE);
                    textView_EtaOnemap.setVisibility(View.VISIBLE);

                    textView_TimeEtaMinOnemap.setText(strTimeM);

                    if (isAdded()) {
                        textView_EtaOnemap.setText(" " + getResources().getString(R.string.min));
                    }
                }
            }
        }
    }
    //==============================================================================================
    private void RouteMode()
    {
        hide_search(false);

        setPlayStopButtonVisible(false);

        setReRouteButtonVisible(true);

        setAddrSearchButtonVisible(true);
    }
    //==============================================================================================
    private void SimulMode()
    {
        hide_search(false);

        setPlayStopButtonVisible(true);

        setReRouteButtonVisible(false);

        setAddrSearchButtonVisible(false);
    }
    //==============================================================================================
    private void DefaultMode()
    {
        hide_search(true);

        setPlayStopButtonVisible(false);

        setReRouteButtonVisible(false);

        setAddrSearchButtonVisible(true);
    }
    //==============================================================================================
    public void hide_search(boolean Show_Or_Hide){
        if(container_searchBar != null)
        {
            if(Show_Or_Hide)
            {
                container_searchBar.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(m_Context, R.anim.top_in);
                animation.setDuration(1200);

                linearLayout_AddressOnemap.setVisibility(View.VISIBLE);
                linearLayout_TimeDistOnemap.setVisibility(View.GONE);

                container_searchBar.setAnimation(animation);
                container_searchBar.animate();

                animation.start();
            }
            else
            {
                container_searchBar.setVisibility(View.GONE);

                linearLayout_AddressOnemap.setVisibility(View.GONE);
                linearLayout_TimeDistOnemap.setVisibility(View.VISIBLE);
            }
        }
    }
    //==============================================================================================
    public void setPlayStopButtonVisible(boolean i_bFlag)
    {
        if(i_bFlag)
        {
            linearLayout_PlayStop.setVisibility(View.VISIBLE);
            linearLayout_PlayStop.setAnimation(animation_left);

            animation_left.start();
        }
        else
        {
            linearLayout_PlayStop.setVisibility(View.GONE);
            linearLayout_PlayStop.setAnimation(animation_right);

            animation_right.start();
        }
    }
    //==============================================================================================
    @SuppressLint("RestrictedApi")
    public void setReRouteButtonVisible(boolean i_bFlag)
    {
        if(i_bFlag)
        {
            floatingActionButton_Reroute.setVisibility(View.VISIBLE);
        }
        else
        {
            floatingActionButton_Reroute.setVisibility(View.GONE);
        }
    }
    //==============================================================================================
    public void setAddrSearchButtonVisible(boolean i_bFlag)
    {
        if(i_bFlag)
        {
            imageButton_SearchOnemap.setVisibility(View.VISIBLE);
        }
        else
        {
            imageButton_SearchOnemap.setVisibility(View.INVISIBLE);
        }
    }
    //==============================================================================================
    private void changeLanguage()
    {
        Configuration conf = getResources().getConfiguration();
        conf.locale = m_gApp.getFatosLocale();
        DisplayMetrics metrics = new DisplayMetrics();
        ((TNaviMainActivity)getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Resources resources = new Resources(((TNaviMainActivity)getActivity()).getAssets(), metrics, conf);

        title_text_view.setHint(resources.getString(R.string.string_wesearch));
        pop_button4.setText(resources.getString(R.string.string_wesettings));
        pop_button3.setText(resources.getString(R.string.string_routeinfomation));
        pop_button2.setText(resources.getString(R.string.string_cancleroute));
        pop_button1.setText(resources.getString(R.string.string_go_route_from_simul));
    }
    //==============================================================================================
    BroadcastReceiver quickMenuFinishReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();

            if (action.equals("RELOAD_COUNTRY")) {
                changeLanguage();
            }
        }
    };
    //==============================================================================================
    public class TimerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (imageButton_MainMenuOnemap != null) {
                //주행중일 때만 사라지도록 하던 방식에서 모든 모드에서  사라지도록
                closePopupMenu();
            }
        }
    }
    //==============================================================================================
    @AddTrace(name = "curDongNameTask[main]", enabled = true)
    public static class SearchTask extends AsyncTask<double[], Void, String> {
        @Override
        protected String doInBackground(double[]... geoPoints)
        {
            double[] latYlonX = geoPoints[0];
            double longitude = latYlonX[0];
            double latitude = latYlonX[1];

            if(isCancelled()){
                return null;
            }

            if(m_FMInterface != null && m_gApp.getAppSettingInfo().m_nDefaultLanguage == 0){
                //국내의 경우 native 사용
                String address = m_FMInterface.FM_GetAddressVol2(latitude,longitude);
                if(address!=null){
                    if(!address.equals("")) return address;
                }
            }
//            try
//            {
//                Geocoder geoCoder = new Geocoder(m_Context,m_gApp.getFatosLocale());
//                List<Address> addresses = geoCoder.getFromLocation(longitude,latitude, 1);
//
//                if (addresses.size() > 0) {
//                    AMapLog.e(TAG,"@@@@@ addr1 : " + gettAddresString(addresses.get(0)));
//
//                }
//                }
//            catch (IOException ex)
//            {
//
//            }

//            AMapLog.e(TAG,"@@@@@ addr2 : " + AMapPositionManager.getCurPosName());
            return AMapPositionManager.getCurPosName();
 /*
            try
            {
                double[] latYlonX = geoPoints[0];
                double longitude = latYlonX[0];
                double latitude = latYlonX[1];

                if(isCancelled()){
                    return null;
                }

                if(m_FMInterface != null && m_gApp.getAppSettingInfo().m_nDefaultLanguage == 0){
                    //국내의 경우 native 사용
                    String address = m_FMInterface.FM_GetAddressVol2(latitude,longitude);
                    if(address!=null){
                        if(!address.equals("")) return address;
                    }
                }


                //해외인 경우 구글 사용
                Geocoder geoCoder = new Geocoder(m_Context,m_gApp.getFatosLocale());
                List<Address> addresses = geoCoder.getFromLocation(longitude,latitude, 1);

                if (addresses.size() > 0) {
                    return gettAddresString(addresses.get(0));
                }


            }
            catch (IOException ex)
            {

            }

            return null;

  */
        }

        @Override
        protected void onPostExecute(String address)
        {
            if(address != null){
                final String address_str = address;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(mActivity!=null) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView_MainAddressOnemap.setText(address_str);
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }
    //==============================================================================================
    public static String gettAddresString(Address address){
        boolean bAppended = false;

        if (null != address ) {
            StringBuilder sb = new StringBuilder();
            if(address.getAdminArea() != null && address.getAdminArea().equals("") == false){
                sb.append(address.getAdminArea());
                bAppended = true;
            }

            if(address.getLocality() != null && address.getLocality().equals("") == false){
                //AdminArea와 Locality가 같은경우 덮어쓰지 않음 (19/01/31 for FATOSMapHi)
                if(!sb.toString().equals(address.getLocality())){
                    if(bAppended == true)
                        sb.append(" ");//sb.append("/");
                    sb.append(address.getLocality());
                    bAppended = true;
                }
            }

            if(address.getSubLocality() != null && address.getSubLocality().equals("") == false){

                if(bAppended == true)
                    sb.append(" ");//sb.append("/");

                sb.append(address.getSubLocality());
                bAppended = true;
            }
            if(address.getThoroughfare() != null && address.getThoroughfare().equals("") == false){

                if(bAppended == true)
                    sb.append(" ");//sb.append("/");

                sb.append(address.getThoroughfare());
                bAppended = true;
            }
            return sb.toString();
        }
        return "";
    }
    //==============================================================================================
}
