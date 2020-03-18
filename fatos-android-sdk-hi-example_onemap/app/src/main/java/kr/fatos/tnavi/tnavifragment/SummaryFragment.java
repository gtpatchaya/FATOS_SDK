package kr.fatos.tnavi.tnavifragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import biz.fatossdk.fminterface.FMBaseActivity;
import biz.fatossdk.map.FMPMapConst;
import biz.fatossdk.nativeMap.MapAnimation;
import biz.fatossdk.navi.RouteParam;
import biz.fatossdk.navi.rgdata.RouteData;
import biz.fatossdk.navi.rgdata.SERVICE_LINK;
import biz.fatossdk.navi.rgdata.SERVICE_LINK_INFO;
import biz.fatossdk.newanavi.AMapMainActivity;
import biz.fatossdk.newanavi.ANaviApplication;
import kr.fatos.tnavi.Adapter.RouteDetailListAdapter;
import kr.fatos.tnavi.Adapter.RouteListAdapter;
import kr.fatos.tnavi.Code.TNaviActionCode;
import kr.fatos.tnavi.Interface.FragmentCommunicator;
import kr.fatos.tnavi.Lib.GoLib;
import kr.fatos.tnavi.List.RouteDetailList;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.TNaviMainActivity;
import kr.fatos.tnavi.Unit.RouteCardData;
import kr.fatos.tnavi.Unit.RouteType;
import kr.fatos.tnavi.exlib.BlockSnapHelper;

import static biz.fatossdk.nativeMap.MapAnimation.MAP_ANI_TYPE_DCCEL;
import static biz.fatossdk.nativeMap.MapAnimation.MAP_ANI_TYPE_DIRECT;
import static biz.fatossdk.newanavi.ANaviApplication.m_MapHandle;

public class SummaryFragment extends Fragment implements RouteListAdapter.OnItemClicked, View.OnClickListener
{
    //==============================================================================================
    @Override
    public void onClick(View v)
    {
        int index = 0;
        Bundle args;

        switch(v.getId())
        {
            case R.id.btn_recomm:
                index = lm.indexOfChild(v) + 1;
                break;

            case R.id.btn_exp:
                index = lm.indexOfChild(v) + 1;
                break;

            case R.id.btn_general:
                index = lm.indexOfChild(v) + 1;
                break;

            case (R.id.btn_short):
                index = lm.indexOfChild(v) + 1;
                break;

            case (R.id.btn_free):
                index = lm.indexOfChild(v) + 1;
                break;

            case (R.id.ll_btn_start):
                args = new Bundle();
                args.putString(TNaviActionCode.ROUTE_VIA_OR_GOAL, TNaviActionCode.CHANGE_VIA_GO_ROUTE);
                fComm.rerouteFromSummary(args);
                break;

            case (R.id.ll_btn_goal):
                args = new Bundle();
                args.putString(TNaviActionCode.ROUTE_VIA_OR_GOAL, TNaviActionCode.CHANGE_GOAL_GO_ROUTE);
                fComm.rerouteFromSummary(args);
                break;

            case (R.id.btn_route_cancel):
                ((TNaviMainActivity)getActivity()).m_FMInterface.FM_CancelRoute();
                ((TNaviMainActivity)getActivity()).onBackPressed();

                Bundle bundle = new Bundle();
                bundle.putString(TNaviActionCode.APP_MODE, TNaviActionCode.APP_MODE_DEFAULT);
                GoLib.getInstance().goTNaviMainActivity(mContext, bundle);
                break;

            case (R.id.btn_change_via_and_goal):
                String temp;

                if(tv_start.getText() == "")
                {
                    ((TNaviMainActivity)getActivity()).set_strAddr(getString(R.string.string_via_hint), 1);
                }
                else
                {
                    ((TNaviMainActivity)getActivity()).set_strAddr((String)tv_start.getText(), 1);
                }

                if(tv_goal.getText() == "")
                {
                    ((TNaviMainActivity)getActivity()).set_strAddr(getString(R.string.string_via_hint), 0);
                }
                else
                {
                    ((TNaviMainActivity)getActivity()).set_strAddr((String)tv_goal.getText(), 0);
                }

                ((TNaviMainActivity)getActivity()).routeTovia(0.0, 0.0, TNaviActionCode.CHANGE_VIA_AND_GOAL, null);
                break;

            default:
                break;
        }

        //최대값, 최소값이 아닐때만 경로 선택 update
        if(index > 0 && index < routeItemMaxValue)
        {
            select_buttonResource(index);
            updateRouteListPageIndicator(index);
            routeListAdapter.setSelected(index - 1);
        }
    }

    //==============================================================================================
    private FragmentCommunicator fComm;
    private RouteListAdapter routeListAdapter;
    private static ArrayList<RouteCardData> routeList;
    private static final String TAG = SummaryFragment.class.getSimpleName();
    private Button.OnClickListener btn_onClickListener;
    private Context mContext;
    private static int current_item = 1;
    private LinearLayout lm;
    private int routeItemMaxValue = 4;
    private int routeItemMinValue = 1;
    private View mView;
    private RelativeLayout btn_routeCancel;
    private RelativeLayout btn_routeChange;
    private TextView tv_start;
    private TextView tv_goal;
    private ANaviApplication m_gApp;
    private LinearLayout linearLayout_Head;
    private LinearLayout bottom_summary;
    private LinearLayout linearLayout_RouteDetail, linearLayout_RouteDetailClose;
    private ListView listView_RouteDetail;
    private ImageView imageView_RouteDetailClose;

    private int itemIndex = 0;
    private int[] itemColor = new int[5];

    private RouteDetailListAdapter m_NextListAdapter = null;
    private ArrayList<RouteDetailList> m_NextTbtList = new ArrayList<>();
    private int previousDistanceFromFirstCellToTop;
    private MapAnimation m_MapAnimation = null;
    private float[] m_fLevel = new float[1];
    private double[] m_dCenterXY = new double[2];
    private int m_nLastSelPosition = -1;
    private float[] scaleScreen = new float[2];

    private final int MAP_OBJ_FLAG_PIN = 7;

    //==============================================================================================
    @Override
    public void onRouteListItemClick(int position)
    {
    }

    //==============================================================================================
    @Override
    public void onRouteListItemStartBtnClick(int position, boolean bIsGoDrive, int nRouteType)
    {
        if(bIsGoDrive)
        {

            m_gApp.setM_nSelRouteOption(nRouteType);

            ((TNaviMainActivity)getActivity()).m_FMInterface.FM_StartRGService(FMBaseActivity.onFatosMapListener);  //경로 안내 시작
            ((TNaviMainActivity)getActivity()).SaveLastRouteData(); //주행중인 정보 저장
            ((TNaviMainActivity)getActivity()).showTbtLayout(true);
            ((ANaviApplication)m_gApp).setMapSummaryOption(false);

            if(m_gApp.getAppSettingInfo().m_nRPType == 2)
            {
                ((TNaviMainActivity)getActivity()).setAutoReRouteforOnemap(RouteParam.SS_FATOS_SERVER);
            }

            Bundle bundle = new Bundle();
            bundle.putString(TNaviActionCode.APP_MODE, TNaviActionCode.APP_MODE_ROUTE);
            GoLib.getInstance().goTNaviMainActivity(this.mContext, bundle);
        }
        else
        {
            //FM_StartSimulation
            m_gApp.setM_nSelRouteOption(nRouteType);

            ((TNaviMainActivity)getActivity()).m_FMInterface.FM_StartSimulation(FMBaseActivity.onFatosMapListener); //모의 주행 시작
            ((TNaviMainActivity)getActivity()).showTbtLayout(true);
            ((ANaviApplication)m_gApp).setMapSummaryOption(false);
            Bundle bundle = new Bundle();
            bundle.putString(TNaviActionCode.APP_MODE, TNaviActionCode.APP_MODE_SIMULATE);
            GoLib.getInstance().goTNaviMainActivity(this.mContext, bundle);
        }
    }

    //==============================================================================================
    public static SummaryFragment newInstance()
    {
        SummaryFragment fragment = new SummaryFragment();
        return fragment;
    }

    //==============================================================================================
    @Override
    public void onPause()
    {
        super.onPause();
    }

    //==============================================================================================
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        if(!((TNaviMainActivity)mContext).APP_MODE.equals(TNaviActionCode.SEARCH_MODE) && !((TNaviMainActivity)mContext).APP_MODE.equals(TNaviActionCode.APP_MODE_SIMULATE) && !((TNaviMainActivity)mContext).APP_MODE.equals(TNaviActionCode.APP_MODE_ROUTE) && !((TNaviMainActivity)mContext).APP_MODE.equals(TNaviActionCode.APP_MODE_SHOWING_SUMMARY))
        {

            ((TNaviMainActivity)mContext).set_strAddr("", 0);
            ((TNaviMainActivity)mContext).setStartCoord(0, 0);
        }
    }

    //==============================================================================================
    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    //==============================================================================================
    @Override
    public void onResume()
    {
        super.onResume();
    }

    //==============================================================================================
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        m_MapAnimation = new MapAnimation();
        m_MapAnimation.Reset();
        FMBaseActivity.onFatosMapListener.onMapAnimation(m_MapAnimation);
    }

    //==============================================================================================
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //        Log.e(TAG,"OnCreateView");

        scaleScreen[0] = 0.5f;
        scaleScreen[1] = 0.5f;

        ((TNaviMainActivity)getActivity()).m_FMInterface.nativeMapRouteLineFitLevelPosEx(m_MapHandle, scaleScreen, m_fLevel, m_dCenterXY, true);

        View view = null;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            view = inflater.inflate(R.layout.fragment_summary, container, false);
        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            view = inflater.inflate(R.layout.fragment_summary_land, container, false);
        }

        mView = view;
        mContext = view.getContext();

        m_gApp = (ANaviApplication)mContext.getApplicationContext();

        m_gApp.ChangeMapViewMode(1, false);


        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("openRouteDetail");
        intentFilter.addAction("SET_START_TEXT_FROM_SHOWMAP");
        mContext.registerReceiver(broadcastReceiver, intentFilter);

        linearLayout_Head = view.findViewById(R.id.linearLayout_Head);
        LinearLayout ll_btn_start = view.findViewById(R.id.ll_btn_start);
        LinearLayout ll_btn_goal = view.findViewById(R.id.ll_btn_goal);
        bottom_summary = view.findViewById(R.id.bottom_summary);

        lm = view.findViewById(R.id.btn_layout);

        linearLayout_RouteDetail = view.findViewById(R.id.linearLayout_RouteDetail);
        listView_RouteDetail = view.findViewById(R.id.listView_RouteDetail);
        imageView_RouteDetailClose = view.findViewById(R.id.imageView_RouteDetailClose);
        linearLayout_RouteDetailClose = view.findViewById(R.id.linearLayout_RouteDetailClose);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)linearLayout_Head.getLayoutParams();
        layoutParams.topMargin = 0;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            layoutParams.leftMargin = 0;
            layoutParams.rightMargin = 0;
        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            layoutParams.leftMargin = 200;
            layoutParams.rightMargin = 200;
        }

        linearLayout_Head.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams bottom_summarylayoutParams = (RelativeLayout.LayoutParams)bottom_summary.getLayoutParams();
        bottom_summarylayoutParams.leftMargin = 0;
        bottom_summarylayoutParams.rightMargin = 0;
        bottom_summarylayoutParams.bottomMargin = 0;
        bottom_summary.setLayoutParams(bottom_summarylayoutParams);

        lm.setPadding(260, 0, 260, 0);
        lm.setBackgroundResource(R.drawable.tap_bg);

        m_NextTbtList.clear();
        m_NextListAdapter = new RouteDetailListAdapter(getActivity(), m_NextTbtList);

        if(m_NextTbtList != null)
        {
            listView_RouteDetail.setAdapter(m_NextListAdapter);

            ArrayList<RouteDetailList> m_updateTBTList;
            m_updateTBTList = GetRpResults(0);
            m_NextTbtList.addAll(m_updateTBTList);
            m_NextListAdapter.notifyDataSetChanged();
        }

        listView_RouteDetail.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(m_NextTbtList == null)
                {
                    return;
                }

                setSelectedListColor(position);
                showMapGuidePoint(position, true);
            }
        });

        listView_RouteDetail.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                View firstCell = listView_RouteDetail.getChildAt(0);

                if(firstCell == null)
                {
                    return;
                }

                int distanceFromFirstCellToTop = listView_RouteDetail.getFirstVisiblePosition() * firstCell.getHeight() - firstCell.getTop();

                previousDistanceFromFirstCellToTop = distanceFromFirstCellToTop;
            }
        });
        imageView_RouteDetailClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                linearLayout_RouteDetail.setVisibility(View.GONE);

                RouteDetailClose();
            }
        });

        linearLayout_RouteDetailClose.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        String[] text = ((TNaviMainActivity)getActivity()).get_strAddr();

        tv_start = view.findViewById(R.id.tv_start);
        tv_goal = view.findViewById(R.id.tv_goal);

        if(text[0] == null)
        {
            text[0] = "";
        }
        else if(text[1] == null)
        {
            text[1] = "";
        }

        if(text[0].equals(getResources().getString(R.string.string_via_hint)))
        {
            tv_start.setText("");
            tv_start.setHint(R.string.string_via_hint);
        }
        else
        {
            tv_start.setText(text[0]);
        }

        if(text[1].equals(getResources().getString(R.string.string_via_hint)))
        {
            tv_goal.setText("");
            tv_goal.setHint(R.string.string_via_hint);
        }
        else
        {
            tv_goal.setText(text[1]);
        }

        //주소,이름 둘다 없는 경우 좌표를 뿌려준다.
        if(text[0].equals(""))
        {
            String x = String.format("%.5f", TNaviMainActivity.saved_data.viaX);
            String y = String.format("%.5f", TNaviMainActivity.saved_data.viaY);

            String XYStr = "" + x + "\n" + y;
            tv_start.setText(XYStr);
        }

        if(text[1].equals(""))
        {
            String x = String.format("%.5f", TNaviMainActivity.saved_data.goalX);
            String y = String.format("%.5f", TNaviMainActivity.saved_data.goalY);
            String XYStr = "" + x + "\n" + y;
            tv_goal.setText(XYStr);
        }

        ll_btn_start.setOnClickListener(this);
        ll_btn_goal.setOnClickListener(this);

        btn_routeCancel = view.findViewById(R.id.btn_route_cancel);
        btn_routeCancel.setOnClickListener(this);

        btn_routeChange = view.findViewById(R.id.btn_change_via_and_goal);
        btn_routeChange.setOnClickListener(this);

        RecyclerView routeListRecyclerView = view.findViewById(R.id.list_routelist);

        routeListRecyclerView.setHasFixedSize(true);

        routeListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        //DATA를 감쌀 List 초기화 및 어댑터에 등록
        routeList = new ArrayList<>();
        routeListAdapter = new RouteListAdapter(routeList);
        routeListAdapter.setOnClick(this);
        ArrayList<Integer> routeOptionList = new ArrayList<Integer>();

        routeListRecyclerView.setAdapter(routeListAdapter);

        if(getArguments() != null)
        {
            ArrayList<RouteCardData> routeData = getArguments().getParcelableArrayList(TNaviActionCode.LONGTOUCH_ROUTESUMMARY);

            //경탐 결과가 넘어옴
            if(routeData != null)
            {
                for(int i = 0; i < routeData.size(); i++)
                {
                    RouteCardData data = routeData.get(i);
                    routeList.add(data);
                    //// TODO: 2019. 2. 28. 경탐->요약 2번 반복하면 nType이 1(추천)으로 넘어옴, 확인필요
                    routeOptionList.add(data.nType);
                }

                routeItemMaxValue = routeData.size() + 1;
            }
            else
            {
            }
        }
        else
        {
            RouteCardData routeCardData = new RouteCardData();

            routeList.add(routeCardData);
            routeList.add(routeCardData);
            routeList.add(routeCardData);
        }

        BlockSnapHelper snapHelper = new BlockSnapHelper(4);
        snapHelper.attachToRecyclerView(routeListRecyclerView);
        snapHelper.setSnapBlockCallback(new BlockSnapHelper.SnapBlockCallback()
        {
            @Override
            public void onBlockSnap(int snapPosition)
            {
            }

            @Override
            public void onBlockSnapped(int snapPosition)
            {
                routeListAdapter.setSelected(snapPosition);
                routeListAdapter.notifyDataSetChanged();
                select_buttonResource(snapPosition + 1);
                updateRouteListPageIndicator(snapPosition + 1);
            }
        });

        int selectIndex = getArguments().getInt(TNaviActionCode.SELECT_ROUTE_INDEX, 0);

        if(routeList.size() == 1)
        {
            selectIndex = 0;
        }

        //arg에 넘어온 값(m_gApp.getM_nSelRouteIdx)이 있으면 넣어주고 없으면 디폴트값 0
        routeListAdapter.setSelected(selectIndex);

        //cardView select 인덱스를 넘겨준다, 카드뷰는 리스트 인덱스 +1
        ((RecyclerView)view.findViewById(R.id.list_routelist)).scrollToPosition(selectIndex);
        routeListAdapter.notifyDataSetChanged();

        //버튼 동적 생성(추천,일반,무료 등등)
        create_buttonResource(routeList.size(), routeOptionList);

        //카드뷰 내부 아이템 선택/비선택 여부를 위해 인덱스 전달
        select_buttonResource(selectIndex + 1);

        ((TNaviMainActivity)getActivity()).setSearchWord("");

        ((TNaviMainActivity)getActivity()).showTbtLayout(false);

        //        RouteDetailClose();

        return view;
    }

    private final Runnable StartSummaryMapAnimation = new Runnable()
    {
        @Override
        public void run()
        {
            float fCenterX = 0, fCenterY = 0;
            fCenterX = 0.45f;
            fCenterY = 0.5f;

            float viatmp = 0;

            //                    m_MapAnimation.Reset();

            viatmp = m_fLevel[0] * (float)1.5;

            if(viatmp - m_fLevel[0] > 1)
            {
                viatmp = m_fLevel[0] + 1;
            }

            if(viatmp > FMPMapConst.MAPVIEW_MAX_LEVEL)
            {
                viatmp = FMPMapConst.MAPVIEW_MAX_LEVEL;
            }
            m_MapAnimation.setTilt(0, MAP_ANI_TYPE_DCCEL);
            m_MapAnimation.setAngle(0, MAP_ANI_TYPE_DIRECT);
            m_MapAnimation.setCenter(scaleScreen[0], scaleScreen[1]);
            m_MapAnimation.setLevel(viatmp, m_fLevel[0] + 0.5f, MAP_ANI_TYPE_DCCEL);
            m_MapAnimation.setMapWGS84(m_dCenterXY[0], m_dCenterXY[1], MAP_ANI_TYPE_DIRECT);

            ((FMBaseActivity)getActivity()).onMapLevelInOut(m_fLevel[0] + 0.5f);
            ((FMBaseActivity)getActivity()).onUpdateMapMode(3);

            FMBaseActivity.onFatosMapListener.onMapAnimation(m_MapAnimation);
        }
    };


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        new Handler(Looper.getMainLooper()).postDelayed(StartSummaryMapAnimation, 10);

    }

    //==============================================================================================
    private void select_buttonResource(int index)
    {
        LinearLayout ll_summary_btn = mView.findViewById(R.id.btn_layout);
        ArrayList<ImageView> imageList_select = new ArrayList<ImageView>();
        ArrayList<ImageView> imageList_deselect = new ArrayList<ImageView>();
        ArrayList<ImageView> imageList_bar = new ArrayList<ImageView>();
        ArrayList<TextView> tvList_text = new ArrayList<TextView>();

        for(int i = 0; i < routeItemMaxValue - 1; i++)
        {
            LinearLayout ll_summary_btn2 = (LinearLayout)ll_summary_btn.getChildAt(i);
            RelativeLayout rl_summary_btn = (RelativeLayout)ll_summary_btn2.getChildAt(0);
            imageList_bar.add((ImageView)rl_summary_btn.getChildAt(0));
            imageList_select.add((ImageView)rl_summary_btn.getChildAt(1));
            imageList_deselect.add((ImageView)rl_summary_btn.getChildAt(2));
            tvList_text.add((TextView)rl_summary_btn.getChildAt(3));
        }

        for(int i = 0; i < routeItemMaxValue - 1; i++)
        {
            if(i == index - 1)
            {
                imageList_bar.get(i).setVisibility(View.VISIBLE);
                imageList_select.get(i).setVisibility(View.VISIBLE);
                imageList_deselect.get(i).setVisibility(View.GONE);
                tvList_text.get(i).setTextColor(itemColor[i]);
            }
            else
            {
                imageList_bar.get(i).setVisibility(View.GONE);
                imageList_select.get(i).setVisibility(View.GONE);
                imageList_deselect.get(i).setVisibility(View.VISIBLE);
                tvList_text.get(i).setTextColor(Color.DKGRAY);
                tvList_text.get(i).getWidth();
            }
        }

        ((TNaviMainActivity)getActivity()).m_FMInterface.FM_SelectRouteVol2(index - 1);
    }

    //==============================================================================================
    private void create_buttonResource(int itemSize, ArrayList<Integer> RouteTypeList)
    {
        //최종 레이아웃 영역 정의
        LinearLayout.LayoutParams area_params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.3f);
        //감쌀 레이아웃 정의
        RelativeLayout.LayoutParams Rl_params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //이미지와 텍스트뷰 크기 정의
        RelativeLayout.LayoutParams Imageview_params_2 = new RelativeLayout.LayoutParams((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics()), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics()));

        Imageview_params_2.addRule(RelativeLayout.CENTER_IN_PARENT);

        RelativeLayout.LayoutParams Tv_params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Tv_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        Tv_params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        for(int i = 0; i < itemSize; i++)
        {
            switch(RouteTypeList.get(i))
            {
                case (RouteType.ROUTE_RECOMM):
                    ImageView imageView = new ImageView(mView.getContext());
                    ImageView imageView2 = new ImageView(mView.getContext());
                    ImageView imageView3 = new ImageView(mView.getContext());

                    TextView textView = new TextView(mView.getContext());
                    textView.setLayoutParams(Tv_params);
                    textView.setGravity(Gravity.CENTER);

                    textView.setId(R.id.summary_tv_text);
                    textView.setTextColor((getResources().getColor(R.color.cardview_onemap_textcolor)));
                    textView.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    LinearLayout area = new LinearLayout(mView.getContext());
                    area.setOrientation(LinearLayout.VERTICAL);
                    area.setLayoutParams(area_params);

                    RelativeLayout area2 = new RelativeLayout(mView.getContext());
                    area2.setLayoutParams(Rl_params);

                    imageView.setBackground(getResources().getDrawable(R.drawable.tab_btn_bar_one));

                    itemColor[itemIndex] = (getResources().getColor(R.color.cardview_onemap_textcolor));
                    itemIndex += 1;

                    imageView2.setLayoutParams(Imageview_params_2);
                    imageView3.setLayoutParams(Imageview_params_2);

                    imageView2.setImageResource(R.drawable.tab_btn_icon_recom_1_one_s);
                    imageView2.setTag(Integer.valueOf(R.drawable.tab_btn_icon_recom_1_one_s));

                    imageView3.setImageResource(R.drawable.tab_btn_icon_recom_1_one_n);
                    imageView3.setTag(Integer.valueOf(R.drawable.tab_btn_icon_recom_1_one_n));

                    imageView3.setVisibility(View.GONE);

                    imageView.setId(R.id.summary_btn_icon);
                    imageView2.setId(R.id.btn_recomm_icon);

                    //상단바 크기 정의r
                    RelativeLayout.LayoutParams Imageview_params_i1 = new RelativeLayout.LayoutParams((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics()), (int)getResources().getDimension(R.dimen.bottom_iv_1_height));

                    Imageview_params_i1.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.CENTER_HORIZONTAL);
                    imageView.setLayoutParams(Imageview_params_i1);

                    area2.addView(imageView);
                    area2.addView(imageView2);
                    area2.addView(imageView3);
                    area2.addView(textView);

                    area.addView(area2);
                    area.setId(R.id.btn_recomm);
                    lm.addView(area);

                    area.setOnClickListener(this);
                    break;

                case (RouteType.ROUTE_EXP): //exp
                    ImageView imageView_2 = new ImageView(mView.getContext());
                    ImageView imageView2_2 = new ImageView(mView.getContext());
                    ImageView imageView2_2_1 = new ImageView(mView.getContext());


                    TextView textView_2 = new TextView(mView.getContext());
                    textView_2.setLayoutParams(Tv_params);
                    textView_2.setGravity(Gravity.CENTER);

                    textView_2.setTextColor((getResources().getColor(R.color.cardview_onemap_textcolor)));
                    textView_2.setId(R.id.summary_tv_text);
                    textView_2.measure(Tv_params.width, Tv_params.height);

                    itemColor[itemIndex] = (getResources().getColor(R.color.cardview_onemap_textcolor));
                    itemIndex += 1;
                    LinearLayout area_2 = new LinearLayout(mView.getContext());
                    area_2.setOrientation(LinearLayout.VERTICAL);
                    area_2.setLayoutParams(area_params);


                    RelativeLayout area2_2 = new RelativeLayout(mView.getContext());
                    area2_2.setLayoutParams(Rl_params);

                    imageView_2.setBackground(getResources().getDrawable(R.drawable.tab_btn_bar_one));

                    imageView2_2.setLayoutParams(Imageview_params_2);
                    imageView2_2_1.setLayoutParams(Imageview_params_2);

                    imageView2_2.setImageResource(R.drawable.tab_btn_icon_recom_2_one_s);
                    imageView2_2.setTag(Integer.valueOf(R.drawable.tab_btn_icon_recom_2_one_s));


                    imageView2_2_1.setImageResource(R.drawable.tab_btn_icon_recom_2_one_n);
                    imageView2_2_1.setTag(Integer.valueOf(R.drawable.tab_btn_icon_recom_2_one_n));
                    imageView2_2_1.setVisibility(View.GONE);

                    imageView_2.setId(R.id.btn_exp_bar);
                    imageView2_2.setId(R.id.btn_exp_icon);

                    //상단바 크기 정의
                    RelativeLayout.LayoutParams Imageview_params_i2 = new RelativeLayout.LayoutParams((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics()), (int)getResources().getDimension(R.dimen.bottom_iv_1_height));

                    Imageview_params_i2.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.CENTER_HORIZONTAL);
                    imageView_2.setLayoutParams(Imageview_params_i2);

                    area2_2.addView(imageView_2);
                    area2_2.addView(imageView2_2);
                    area2_2.addView(imageView2_2_1);
                    area2_2.addView(textView_2);
                    area_2.addView(area2_2);
                    area_2.setId(R.id.btn_exp);
                    lm.addView(area_2);
                    area_2.setOnClickListener(this);

                    break;

                case (RouteType.ROUTE_SHORT):
                    ImageView imageView_3 = new ImageView(mView.getContext());
                    ImageView imageView2_3 = new ImageView(mView.getContext());
                    ImageView imageView2_3_1 = new ImageView(mView.getContext());

                    TextView textView_3 = new TextView(mView.getContext());
                    textView_3.setLayoutParams(Tv_params);
                    textView_3.setGravity(Gravity.CENTER);

                    textView_3.setTextColor((getResources().getColor(R.color.cardview_onemap_textcolor)));
                    textView_3.setId(R.id.summary_tv_text);
                    textView_3.measure(Tv_params.width, Tv_params.height);

                    itemColor[itemIndex] = (getResources().getColor(R.color.cardview_onemap_textcolor));
                    itemIndex += 1;

                    LinearLayout area_3 = new LinearLayout(mView.getContext());
                    area_3.setOrientation(LinearLayout.VERTICAL);
                    area_3.setLayoutParams(area_params);


                    RelativeLayout area2_3 = new RelativeLayout(mView.getContext());
                    area2_3.setLayoutParams(Rl_params);

                    imageView_3.setBackground(getResources().getDrawable(R.drawable.tab_btn_bar_one));

                    imageView2_3.setLayoutParams(Imageview_params_2);
                    imageView2_3_1.setLayoutParams(Imageview_params_2);

                    imageView2_3.setImageResource(R.drawable.tab_btn_icon_short_one_s);
                    imageView2_3.setTag(Integer.valueOf(R.drawable.tab_btn_icon_short_one_s));
                    imageView2_3_1.setImageResource(R.drawable.tab_btn_icon_short_one_n);
                    imageView2_3_1.setTag(Integer.valueOf(R.drawable.tab_btn_icon_short_one_n));

                    imageView2_3_1.setVisibility(View.GONE);


                    imageView_3.setId(R.id.btn_short_icon);
                    imageView2_3.setId(R.id.btn_short_bar);

                    //상단바 크기 정의
                    RelativeLayout.LayoutParams Imageview_params_i3 = new RelativeLayout.LayoutParams((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics()), (int)getResources().getDimension(R.dimen.bottom_iv_1_height));

                    Imageview_params_i3.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.CENTER_HORIZONTAL);
                    imageView_3.setLayoutParams(Imageview_params_i3);

                    area2_3.addView(imageView_3);
                    area2_3.addView(imageView2_3);
                    area2_3.addView(imageView2_3_1);
                    area2_3.addView(textView_3);
                    area_3.addView(area2_3);
                    area_3.setId(R.id.btn_short);
                    lm.addView(area_3);

                    area_3.setOnClickListener(this);
                    break;

                case (RouteType.ROUTE_GENERAL):
                    ImageView imageView_4 = new ImageView(mView.getContext());
                    ImageView imageView2_4 = new ImageView(mView.getContext());
                    ImageView imageView2_4_1 = new ImageView(mView.getContext());

                    TextView textView_4 = new TextView(mView.getContext());
                    textView_4.setLayoutParams(Tv_params);
                    textView_4.setGravity(Gravity.CENTER);

                    LinearLayout area_4 = new LinearLayout(mView.getContext());
                    area_4.setOrientation(LinearLayout.VERTICAL);
                    area_4.setLayoutParams(area_params);

                    RelativeLayout area2_4 = new RelativeLayout(mView.getContext());
                    area2_4.setLayoutParams(Rl_params);

                    textView_4.setTextColor((getResources().getColor(R.color.cardview_onemap_textcolor)));
                    textView_4.setId(R.id.summary_tv_text);
                    textView_4.measure(Tv_params.width, Tv_params.height);

                    itemColor[itemIndex] = (getResources().getColor(R.color.cardview_onemap_textcolor));
                    itemIndex += 1;

                    imageView_4.setBackground(getResources().getDrawable(R.drawable.tab_btn_bar_one));
                    imageView_4.setId(R.id.btn_general_bar);

                    imageView2_4.setId(R.id.btn_general_icon);
                    imageView2_4.setLayoutParams(Imageview_params_2);

                    imageView2_4.setImageResource(R.drawable.tab_btn_icon_recom_2_one_s);
                    imageView2_4.setTag(Integer.valueOf(R.drawable.tab_btn_icon_recom_2_one_s));

                    imageView2_4_1.setLayoutParams(Imageview_params_2);
                    imageView2_4_1.setImageResource(R.drawable.tab_btn_icon_recom_2_one_n);
                    imageView2_4_1.setTag(Integer.valueOf(R.drawable.tab_btn_icon_recom_2_one_n));

                    //상단바 크기 정의
                    RelativeLayout.LayoutParams Imageview_params_i4 = new RelativeLayout.LayoutParams((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics()), (int)getResources().getDimension(R.dimen.bottom_iv_1_height));

                    Imageview_params_i4.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.CENTER_HORIZONTAL);
                    imageView_4.setLayoutParams(Imageview_params_i4);

                    area2_4.addView(imageView_4);
                    area2_4.addView(imageView2_4);
                    area2_4.addView(imageView2_4_1);
                    area2_4.addView(textView_4);
                    area_4.addView(area2_4);
                    area_4.setId(R.id.btn_general);
                    lm.addView(area_4);

                    area_4.setOnClickListener(this);
                    break;

                case (RouteType.ROUTE_FREE):
                    ImageView imageView_5 = new ImageView(mView.getContext());
                    ImageView imageView2_5 = new ImageView(mView.getContext());
                    ImageView imageView2_5_1 = new ImageView(mView.getContext());

                    TextView textView_5 = new TextView(mView.getContext());
                    textView_5.setLayoutParams(Tv_params);
                    textView_5.setGravity(Gravity.CENTER);

                    textView_5.setTextColor((getResources().getColor(R.color.cardview_onemap_textcolor)));
                    textView_5.setId(R.id.summary_tv_text);
                    textView_5.measure(Tv_params.width, Tv_params.height);

                    itemColor[itemIndex] = (getResources().getColor(R.color.cardview_onemap_textcolor));
                    itemIndex += 1;

                    LinearLayout area_5 = new LinearLayout(mView.getContext());
                    area_5.setOrientation(LinearLayout.VERTICAL);
                    area_5.setLayoutParams(area_params);

                    RelativeLayout area2_5 = new RelativeLayout(mView.getContext());
                    area2_5.setLayoutParams(Rl_params);

                    imageView_5.setBackground(getResources().getDrawable(R.drawable.tab_btn_bar_one));
                    imageView_5.setId(R.id.btn_free_bar);

                    //imageView2_5.setId(R.id.btn_general_icon);
                    imageView2_5.setLayoutParams(Imageview_params_2);

                    imageView2_5.setImageResource(R.drawable.tab_btn_icon_free_s);
                    imageView2_5.setTag(Integer.valueOf(R.drawable.tab_btn_icon_free_s));

                    imageView2_5_1.setLayoutParams(Imageview_params_2);
                    imageView2_5_1.setImageResource(R.drawable.tab_btn_icon_free_n);
                    imageView2_5_1.setTag(Integer.valueOf(R.drawable.tab_btn_icon_free_n));

                    //상단바 크기 정의
                    RelativeLayout.LayoutParams Imageview_params_i5 = new RelativeLayout.LayoutParams((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics()), (int)getResources().getDimension(R.dimen.bottom_iv_1_height));

                    Imageview_params_i5.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.CENTER_HORIZONTAL);
                    imageView_5.setLayoutParams(Imageview_params_i5);

                    area2_5.addView(imageView_5);
                    area2_5.addView(imageView2_5);
                    area2_5.addView(imageView2_5_1);
                    area2_5.addView(textView_5);
                    area_5.addView(area2_5);
                    area_5.setId(R.id.btn_free);
                    lm.addView(area_5);

                    area_5.setOnClickListener(this);
                    break;
            }
        }

        for(int i = 0; i < routeItemMaxValue; i++)
        {
            m_gApp.setMapRouteLineColor(i, itemColor[i]);
        }
    }

    //==============================================================================================
    //snap에 따라서 item position을 전달받는다.
    private void updateRouteListPageIndicator(int itemPosition)
    {
        switch(itemPosition)
        {
            case 1:
                current_item = 1;
                break;

            case 2:
                current_item = 2;
                break;

            case 3:
                current_item = 3;
                break;

            default:
            {
            }
        }

        ((RecyclerView)mView.findViewById(R.id.list_routelist)).scrollToPosition(itemPosition - 1);
    }

    //==============================================================================================
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if(getActivity() != null && getActivity() instanceof TNaviMainActivity)
        {
            fComm = (FragmentCommunicator)context;
        }
    }

    //==============================================================================================
    private ArrayList<RouteDetailList> GetRpResults(int idx)
    {
        if(m_gApp == null)
        {
            return null;
        }

        if(m_gApp.rgData() == null)
        {
            return null;
        }

        if(m_gApp.rgData().m_pContext == null)
        {
            return null;
        }

        if(m_gApp.rgData().m_pContext[idx] == null)
        {
            return null;
        }

        m_gApp.m_nGoalRemainLinkLength = 0;
        RouteData rgData = m_gApp.rgData().m_pContext[idx];

        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.SECOND, rgData.nTime);
        now = cal.getTime();

        ArrayList<RouteDetailList> results = new ArrayList<>();
        RouteDetailList item_details = null;

        if(rgData.nServiceLinkCount < 1)
        {
            // messageBox 관리 생략
            results = null;
            return null;
        }

        if(rgData.nServiceLinkCount > 2)
        {
            int nPreGp = rgData.nServiceLinkCount - 2;
            int nGoalIdx = rgData.nServiceLinkCount - 1;

            if(rgData.pServiceLink[nPreGp].slinkInfo.nLength > 50 && rgData.pServiceLink[nGoalIdx].slinkInfo.nType == 50 && rgData.pServiceLink[nGoalIdx].slinkInfo.nLength < 50)
            {
                m_gApp.m_nGoalRemainLinkLength = rgData.pServiceLink[nGoalIdx].slinkInfo.nLength;
                int nCurLinkLength = rgData.pServiceLink[nPreGp].slinkInfo.nLength;
                rgData.pServiceLink[nPreGp] = rgData.pServiceLink[nGoalIdx];
                rgData.pServiceLink[nPreGp].slinkInfo.nLength += nCurLinkLength;
                rgData.nServiceLinkCount--;
            }
        }

        String strReplace;
        int nSumTime = 0;

        for(int nArrIdx = 0; nArrIdx < rgData.nServiceLinkCount; nArrIdx++)
        {
            item_details = new RouteDetailList();

            double fLatY, fLonX;
            SERVICE_LINK_INFO item_servicelink = rgData.pServiceLink[nArrIdx].slinkInfo;
            SERVICE_LINK nextServiceLink = null;

            strReplace = m_gApp.RemoveHTMLTag(item_servicelink.szStringText);

            if(strReplace != null)
            {
                // [Yeon-2019.01.30] 방면 명칭 분리
                String strDivision = "|";

                if(strReplace.contains(strDivision))
                {
                    strReplace = strReplace.substring(0, strReplace.indexOf(strDivision));
                }
            }

            item_details.setTxtInfoName(strReplace);
            item_details.setTbtCode(item_servicelink.nType);

            item_details.setSubInfo(String.format("Dist: %s | Time: %s", getTotalRemainDistStr(item_servicelink.nLength), getTotalTimeStr(item_servicelink.nTime)));
            nSumTime += item_servicelink.nTime;
            item_details.setTimeDistInfo(getTotalTimeStr(item_servicelink.nTime), getTotalRemainDistStr(item_servicelink.nLength), nSumTime);

            fLatY = rgData.pServiceLink[nArrIdx].wpGP.y;
            fLonX = rgData.pServiceLink[nArrIdx].wpGP.x;

            String strX, strY;
            strX = Double.toString(fLonX);
            strY = Double.toString(fLatY);
            item_details.setCoordX(strX);
            item_details.setCoordY(strY);

            results.add(item_details);
            item_details = null;
        }

        return results;
    }

    //==============================================================================================
    private String getTotalRemainDistStr(int distance)
    {
        String strResultDist = "0";
        if(m_gApp.getAppSettingInfo().m_nDistUnit == 0) // km
        {
            if(distance >= 1000)
            {
                float km = (float)distance / 1000.f;

                if(km >= 100)
                {
                    strResultDist = String.format("%d %s", (int)km, "km");
                }
                else if(km >= 10)
                {
                    strResultDist = String.format("%d %s", (int)km, "km");
                }
                else
                {
                    strResultDist = String.format("%.1f %s", (float)distance / 1000, "km");
                }
            }
            else
            {
                if(distance <= 0)
                {
                    distance = 0;
                }
                if(distance >= 10)
                {
                    distance = distance - (distance % 10);
                    strResultDist = String.format("%d %s", distance, "m");
                }
                else
                {
                    strResultDist = String.format("%d %s", distance, "m");
                }

            }
        }
        else
        {
            if(distance >= 300)
            {
                float km = (float)distance / 1000.f;

                if(km >= 100)
                {
                    strResultDist = String.format("%d %s", (int)(km * ANaviApplication.MITOKILOMETER), "mile");
                }
                else if(km >= 10)
                {
                    strResultDist = String.format("%.1f %s", (float)(km * ANaviApplication.MITOKILOMETER), "mile");
                }
                else
                {
                    strResultDist = String.format("%.2f %s", (float)(km * ANaviApplication.MITOKILOMETER), "mile");
                }
            }
            else
            {
                if(distance <= 0)
                {
                    distance = 0;
                }
                if(distance >= 10)
                {
                    distance = distance - (distance % 10);
                    strResultDist = String.format("%d ft", (int)(distance * ANaviApplication.MITOFIT), "ft");
                }
                else
                {
                    strResultDist = String.format("%d ft", (int)(distance * ANaviApplication.MITOFIT), "ft");
                }

            }
        }

        return strResultDist;
    }

    //==============================================================================================
    private String getTotalTimeStr(int secTime)
    {
        int minutes = 0;
        String strResultTime;
        strResultTime = String.format("%d s", secTime);

        if(secTime > 59)
        {
            minutes = (int)(secTime / 60);
            strResultTime = String.format("%d min", minutes);
        }

        return strResultTime;
    }

    //==============================================================================================
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String strAction = intent.getAction();

            if(strAction.equals("openRouteDetail"))
            {
                linearLayout_RouteDetail.setVisibility(View.VISIBLE);

                RouteDetailOpen();
            }
            else if(strAction.equals("SET_START_TEXT_FROM_SHOWMAP"))
            {
                String strText = intent.getStringExtra("data");

                tv_start.setText(strText);
            }
        }
    };

    //==============================================================================================
    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(broadcastReceiver != null)
        {
            mContext.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }

    }

    //==============================================================================================
    private void RouteDetailOpen()
    {
        m_NextTbtList.clear();
        ArrayList<RouteDetailList> arrTbtList = null;
        arrTbtList = GetRpResults(m_gApp.getM_nSelRouteIdx());
        m_NextTbtList.addAll(arrTbtList);
        m_NextListAdapter.notifyDataSetChanged();
        listView_RouteDetail.smoothScrollToPosition(0);

        setSelectedListColor(0);
        showMapGuidePoint(0, true);

        m_gApp.setMainMapOption(true, true, true, false);
    }

    //==============================================================================================
    private void RouteDetailClose()
    {
        m_gApp.setMapSummaryOption(false);

        float fCenterX = 0, fCenterY = 0;
        fCenterX = 0.45f;
        fCenterY = 0.5f;

        float viatmp = 0;
        m_MapAnimation.Reset();

        viatmp = m_fLevel[0] * (float)1.5;

        if(viatmp - m_fLevel[0] > 1)
        {
            viatmp = m_fLevel[0] + 1;
        }

        if(viatmp > FMPMapConst.MAPVIEW_MAX_LEVEL)
        {
            viatmp = FMPMapConst.MAPVIEW_MAX_LEVEL;
        }

        m_MapAnimation.setCenter(fCenterX, fCenterY);
        m_MapAnimation.setLevel(viatmp, m_fLevel[0] + 0.5f, MAP_ANI_TYPE_DCCEL);
        m_MapAnimation.setMapWGS84(m_dCenterXY[0], m_dCenterXY[1], MAP_ANI_TYPE_DIRECT);

        FMBaseActivity.onFatosMapListener.onMapAnimation(m_MapAnimation);

        m_gApp.setMainMapOption(true, true, false, false);
    }

    //==============================================================================================
    private void setSelectedListColor(int nSelPos)
    {
        if(listView_RouteDetail.isShown())
        {
            if(nSelPos != -1)
            {
                m_NextListAdapter.setSelectedItem(nSelPos);
            }
        }
    }

    //==============================================================================================
    public void showMapGuidePoint(int position, boolean bAnimation)
    {
        if(position == -1 || m_NextTbtList == null)
        {
            return;
        }

        int anyType = MAP_ANI_TYPE_DIRECT;

        if(bAnimation)
        {
            anyType = MAP_ANI_TYPE_DCCEL;
        }

        m_nLastSelPosition = position;
        float fCenterX = 0, fCenterY = 0;
        fCenterX = 0.45f;
        fCenterY = 0.5f;

        m_MapAnimation.Reset();
        m_MapAnimation.setCenter(fCenterX, fCenterY);
        m_MapAnimation.setLevel(FMPMapConst.MAPVIEW_LEVEL_VIA_AUTO_MODE, FMPMapConst.MAPVIEW_LEVEL_DEFAULT_AUTO_MODE, MAP_ANI_TYPE_DCCEL);
        m_MapAnimation.setMapWGS84(Double.parseDouble(m_NextTbtList.get(position).getCoordX()), Double.parseDouble(m_NextTbtList.get(position).getCoordY()), anyType);
        FMBaseActivity.onFatosMapListener.onMapAnimation(m_MapAnimation);

        FMBaseActivity.onFatosMapListener.onMapDrawPinImg(Double.parseDouble(m_NextTbtList.get(position).getCoordX()), Double.parseDouble(m_NextTbtList.get(position).getCoordY()), MAP_OBJ_FLAG_PIN);

        m_gApp.setMapSummaryOption(true);
        m_gApp.setMapTripOption(false);
    }
    //==============================================================================================
}
