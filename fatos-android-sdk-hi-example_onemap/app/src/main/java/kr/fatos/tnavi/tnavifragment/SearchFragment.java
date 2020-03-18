package kr.fatos.tnavi.tnavifragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

import biz.fatossdk.config.ErrorMessage;
import biz.fatossdk.config.FatosBuildConfig;
import biz.fatossdk.fminterface.FMError;
import biz.fatossdk.newanavi.ANaviApplication;
import biz.fatossdk.openapi.common.POIItem;
import kr.fatos.tnavi.Adapter.SearchAdapter;
import kr.fatos.tnavi.Code.SettingsCode;
import kr.fatos.tnavi.Code.TNaviActionCode;
import kr.fatos.tnavi.Lib.GoLib;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.TNaviMainActivity;
import kr.fatos.tnavi.Unit.NPoiItem;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment {
    private final static int HANDLER_WHAT_TIMER = 1;

    Button button_back;
    EditText editText_search, editText_start, editText_goal;
    ImageButton imageButton_cate, imageButton_clear, imageButton_Search, imageButton_sClear, imageButton_gClear, imageButton_SSearch, imageButton_GSearch;
    RecyclerView recyclerView_List;
    TextView textView_cate, textView_Address, textView_country, txt_nosearchresult;
    LinearLayout linearLayout_progressBar, linearLayout_SettingInfo, linearLayout_Head;
    String args;
    String app_mode;
    String strWord;
    RecyclerView.LayoutManager mLayoutManager;
    SearchAdapter listAdapter;
    RelativeLayout relativeLayout_SearchNoData, relativeLayout_Background, relativeLayout_search;
    private static SharedPreferences prefs;
    ArrayList<NPoiItem> arrayList;

    String m_strPackageName;
    Context mContext;

    TimerHandler timerHandler = null;

    InputMethodManager mImm;
    String m_strText = "";
    ArrayList<NPoiItem> arrayListMap = new ArrayList<>();
    boolean m_bFlag = false;
    private ANaviApplication m_gApp;
    public NPoiItem m_nPoiItem = new NPoiItem();
    public int m_nSearchType = 1; //0: 시작 설정, 1: 목적지 설정, 2: 일반 설정
    //==============================================================================================
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }
    //==============================================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    //==============================================================================================
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        if (getArguments() != null) {
            args = getArguments().getString(TNaviActionCode.ROUTE_VIA_OR_GOAL);
            app_mode = getArguments().getString(TNaviActionCode.APP_MODE);
            strWord = getArguments().getString(TNaviActionCode.SEARCH_MODE_WORD);
            arrayListMap = Parcels.unwrap(getArguments().getParcelable(TNaviActionCode.ARRAYLIST_MAP_TO_SEARCH));
        }

        mContext = view.getContext();
        m_gApp = (ANaviApplication) mContext.getApplicationContext();
        m_strPackageName = mContext.getPackageName();
        prefs = mContext.getSharedPreferences(getResources().getString(R.string.app_registerId), MODE_PRIVATE);
        final IntentFilter filter = new IntentFilter();
        filter.addAction("RELOAD_CATEGORY");
        filter.addAction("RELOAD_COUNTRY");
        filter.addAction("SET_START_TEXT_FROM_SHOWMAP");
        mContext.registerReceiver(quickMenuFinishReceiver, filter);

        return view;
    }
    //==============================================================================================
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        button_back = view.findViewById(R.id.button_back);
        editText_search = view.findViewById(R.id.editText_search);
        imageButton_cate = view.findViewById(R.id.imageButton_cate);
        imageButton_clear = view.findViewById(R.id.imageButton_clear);
        imageButton_Search = view.findViewById(R.id.imageButton_Search);
        recyclerView_List = view.findViewById(R.id.recyclerView_List);
        textView_cate = view.findViewById(R.id.textView_cate);
        textView_country = view.findViewById(R.id.textView_country);
        linearLayout_progressBar = view.findViewById(R.id.linearLayout_progressBar);
        linearLayout_SettingInfo = view.findViewById(R.id.linearLayout_SettingInfo);
        textView_Address = view.findViewById(R.id.textView_Address);
        relativeLayout_SearchNoData = view.findViewById(R.id.relativeLayout_SearchNoData);
        relativeLayout_Background = view.findViewById(R.id.relativeLayout_Background);
        linearLayout_Head = view.findViewById(R.id.linearLayout_Head);
        relativeLayout_search = view.findViewById(R.id.relativeLayout_search);
        editText_start = view.findViewById(R.id.editText_start);
        editText_goal = view.findViewById(R.id.editText_goal);
        imageButton_sClear = view.findViewById(R.id.imageButton_sClear);
        imageButton_gClear = view.findViewById(R.id.imageButton_gClear);
        imageButton_SSearch = view.findViewById(R.id.imageButton_SSearch);
        imageButton_GSearch = view.findViewById(R.id.imageButton_GSearch);

        textView_cate.setText(SettingsCode.getValueCategory());
        textView_country.setText(SettingsCode.getValueCountry());
        editText_search.addTextChangedListener(textWatcher);
        editText_start.addTextChangedListener(textWatcherS);
        editText_goal.addTextChangedListener(textWatcherG);

        txt_nosearchresult = view.findViewById(R.id.txt_nosearchresult);

        ((Activity) getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageButton_cate.setVisibility(View.GONE);
                linearLayout_SettingInfo.setVisibility(View.GONE);
            }
        });

        setRecyclerView();

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        imageButton_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText_search.getText().toString().isEmpty()) {
                    showEventKeyboard();
                }

                editText_search.setText("");

                setSearchVisible(true);
            }
        });

        imageButton_sClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText_start.getText().toString().isEmpty()) {
                    showEventKeyboard();
                }

                ((TNaviMainActivity) getActivity()).set_strAddr("", 0);
                ((TNaviMainActivity) getActivity()).setStartCoord(0, 0);

                editText_start.setText("");
                setSearchVisible(true);

                changeFocus();
            }
        });

        imageButton_gClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText_goal.getText().toString().isEmpty()) {
                    showEventKeyboard();
                }

                editText_goal.setText("");

                setSearchVisible(true);
            }
        });

        imageButton_cate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                GoLib.getInstance().goSearchSettingActivity(mContext, bundle); // 수정중
            }
        });

        imageButton_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(m_strText.isEmpty() || m_strText.equals(""))
                {
                    return;
                }

                showProgress("\'" + m_strText + "\'\n" + getString(R.string.string_wesearchtext));

                ((TNaviMainActivity)getActivity()).getSearchPoiItem(m_strText);
            }
        });

        imageButton_SSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(m_strText.isEmpty() || m_strText.equals(""))
                {
                    return;
                }

                showProgress("\'" + m_strText + "\'\n" + getString(R.string.string_wesearchtext));

                ((TNaviMainActivity)getActivity()).getSearchPoiItem(m_strText);
            }
        });

        imageButton_GSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(m_strText.isEmpty() || m_strText.equals(""))
                {
                    return;
                }

                showProgress("\'" + m_strText + "\'\n" + getString(R.string.string_wesearchtext));

                ((TNaviMainActivity)getActivity()).getSearchPoiItem(m_strText);
            }
        });

        editText_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(EditorInfo.IME_ACTION_SEARCH == i)
                {
                    if(m_strText.isEmpty() || m_strText.equals(""))
                    {
                        return false;
                    }

                    showProgress("\'" + m_strText + "\'\n" + getString(R.string.string_wesearchtext));

                    ((TNaviMainActivity)getActivity()).getSearchPoiItem(m_strText);
                }

                return true;
            }
        });

        editText_start.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(EditorInfo.IME_ACTION_SEARCH == i)
                {
                    if(m_strText.isEmpty() || m_strText.equals(""))
                    {
                        return false;
                    }

                    showProgress("\'" + m_strText + "\'\n" + getString(R.string.string_wesearchtext));

                    ((TNaviMainActivity)getActivity()).getSearchPoiItem(m_strText);
                }

                return true;
            }
        });

        editText_goal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(EditorInfo.IME_ACTION_SEARCH == i)
                {
                    if(m_strText.isEmpty() || m_strText.equals(""))
                    {
                        return false;
                    }

                    showProgress("\'" + m_strText + "\'\n" + getString(R.string.string_wesearchtext));

                    ((TNaviMainActivity)getActivity()).getSearchPoiItem(m_strText);
                }

                return true;
            }
        });

        editText_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                m_nSearchType = 2;
                return false;
            }
        });

        editText_start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                m_nSearchType = 0;
                return false;
            }
        });

        editText_goal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                m_nSearchType = 1;
                return false;
            }
        });

        linearLayout_SettingInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        relativeLayout_Background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        timerHandler = new TimerHandler();

        if (getArguments() != null) {
            args = getArguments().getString(TNaviActionCode.ROUTE_VIA_OR_GOAL);
            app_mode = getArguments().getString(TNaviActionCode.APP_MODE);
            strWord = getArguments().getString(TNaviActionCode.SEARCH_MODE_WORD);

            arrayListMap = Parcels.unwrap(getArguments().getParcelable(TNaviActionCode.ARRAYLIST_MAP_TO_SEARCH));

            if(args.equals(TNaviActionCode.JUST_GOAL))
            {
                if(!TextUtils.isEmpty(strWord)) {
                    editText_start.setText(strWord);
                }

                editText_goal.requestFocus();

                relativeLayout_search.setVisibility(View.GONE);
                linearLayout_Head.setVisibility(View.VISIBLE);
            }
            else if(args.equals(TNaviActionCode.CHANGE_VIA_GO_ROUTE) || args.equals(TNaviActionCode.CHANGE_GOAL_GO_ROUTE))
            {
                linearLayout_Head.setVisibility(View.GONE);
                relativeLayout_search.setVisibility(View.VISIBLE);
            }
        }

        if (arrayListMap != null) {
            if (arrayListMap.size() > 0) {
                setDistVisible();
                listAdapter.addItemList(arrayListMap);

                m_bFlag = true;

                setSearchVisible(false);
            }
        } else {
            setSearchVisible(true);
        }

        if (strWord != null) {
            editText_search.setText(strWord);
        }

        m_bFlag = false;

        super.onViewCreated(view, savedInstanceState);
    }
    //==============================================================================================
    public void getRecentPOI() {
        if (arrayList.size() > 0) {
            arrayList.clear();
        }

        listAdapter.clearItemList();

        arrayList = ((TNaviMainActivity) mContext).LoadRecentPOI();

        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }

        Collections.reverse(arrayList);

        listAdapter.setDistVisible(false);
        listAdapter.addItemList(arrayList);
    }
    //==============================================================================================
    public void setDistVisible()
    {
        listAdapter.setDistVisible(true);
    }
    //==============================================================================================
    public void showEventKeyboard() {
        mImm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    //==============================================================================================
    private void setRecyclerView() {
        recyclerView_List.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView_List.setLayoutManager(mLayoutManager);
        //만약 args가 있어서, 목적지인지 출발지인지 구분해야 하는 경우

        if (args != null) {
            listAdapter = new SearchAdapter(mContext, R.layout.recyclerview_search, new ArrayList<NPoiItem>(), args, app_mode, this);
        } else {
            listAdapter = new SearchAdapter(mContext, R.layout.recyclerview_search, new ArrayList<NPoiItem>(),
                    TNaviActionCode.JUST_GOAL, TNaviActionCode.APP_MODE_JUST_GOAL_SEARCH, this);
        }

        recyclerView_List.setAdapter(listAdapter);

        arrayList = new ArrayList<>();
    }
    //==============================================================================================
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (m_bFlag) {
                return;
            }

            timerHandler.removeMessages(HANDLER_WHAT_TIMER);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (m_bFlag) {
                return;
            }

            m_strText = s.toString();

            ((TNaviMainActivity) getActivity()).setSearchWord(m_strText);
        }

        @Override
        public void afterTextChanged(Editable s) {
            final String keyword = s.toString();

            ((Activity) getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (keyword.length() > 0) {
                        imageButton_clear.setVisibility(View.VISIBLE);
                    } else {
                        imageButton_clear.setVisibility(View.GONE);
                        setSearchVisible(true);
                        return;
                    }
                }
            });

            if (m_bFlag) {
                return;
            }
        }
    };
    //==============================================================================================
    TextWatcher textWatcherS = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (m_bFlag) {
                return;
            }

            timerHandler.removeMessages(HANDLER_WHAT_TIMER);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (m_bFlag) {
                return;
            }

            m_strText = s.toString();

            ((TNaviMainActivity) getActivity()).setSearchWord(m_strText);
        }

        @Override
        public void afterTextChanged(Editable s) {
            final String keyword = s.toString();

            ((Activity) getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (keyword.length() > 0) {
                        imageButton_sClear.setVisibility(View.VISIBLE);
                    } else {
                        imageButton_sClear.setVisibility(View.GONE);
                        setSearchVisible(true);
                        return;
                    }
                }
            });

            if (m_bFlag) {
                return;
            }
        }
    };
    //==============================================================================================
    TextWatcher textWatcherG = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (m_bFlag) {
                return;
            }

            timerHandler.removeMessages(HANDLER_WHAT_TIMER);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (m_bFlag) {
                return;
            }

            m_strText = s.toString();

            ((TNaviMainActivity) getActivity()).setSearchWord(m_strText);
        }

        @Override
        public void afterTextChanged(Editable s) {
            final String keyword = s.toString();

            ((Activity) getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (keyword.length() > 0) {
                        imageButton_gClear.setVisibility(View.VISIBLE);
                    } else {
                        imageButton_gClear.setVisibility(View.GONE);
                        setSearchVisible(true);
                        return;
                    }
                }
            });

            if (m_bFlag) {
                return;
            }
        }
    };
    //==============================================================================================
    private class EditChangeThread extends Thread {
        private String strText;

        public EditChangeThread(String strText) {
            this.strText = strText;
        }

        @Override
        public void run() {
            super.run();

            Message msg = new Message();
            msg.what = HANDLER_WHAT_TIMER;
            Bundle bundle = new Bundle();
            bundle.putString("word", strText);
            msg.setData(bundle);
            timerHandler.sendMessageDelayed(msg, 500);
        }
    }
    //==============================================================================================
    public class TimerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String strMsg = msg.getData().getString("word");

            showProgress("\'" + strMsg + "\'\n" + getString(R.string.string_wesearchtext));

            ((TNaviMainActivity)getActivity()).getSearchPoiItem(strMsg);
        }
    }
    //==============================================================================================
    public void showProgress(String i_msg) {
        final String msg = i_msg;

        ((Activity) getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView_Address.setText(msg);
                linearLayout_progressBar.setVisibility(View.VISIBLE);
            }
        });
    }
    //==============================================================================================
    public void hideProgress() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                linearLayout_progressBar.setVisibility(View.GONE);
            }
        });
    }
    //==============================================================================================
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                textView_cate.setText(SettingsCode.getValueCategory());
            }
        }
    }
    //==============================================================================================
    public void setKeyboardVisible(boolean bVisible) {
        if (bVisible) {

        } else {
            mImm.hideSoftInputFromWindow(editText_search.getWindowToken(), 0);
            mImm.hideSoftInputFromWindow(editText_start.getWindowToken(), 0);
            mImm.hideSoftInputFromWindow(editText_goal.getWindowToken(), 0);
        }
    }
    //==============================================================================================
    public void setSearchVisible(boolean i_bFlag)   //true면 최근 데이터 뿌려주고 false 일때는 검색결과 없을때 자동차 모양
    {
        if (i_bFlag) {
            getRecentPOI();
        }

        ((Activity) getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listAdapter.getItemCount() <= 0) {
                    relativeLayout_SearchNoData.setVisibility(View.VISIBLE);
                } else {
                    relativeLayout_SearchNoData.setVisibility(View.INVISIBLE);

                }
            }
        });

        hideProgress();
    }
    //==============================================================================================
    BroadcastReceiver quickMenuFinishReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String temp = ((TNaviMainActivity) getActivity()).getSearchWord();

            if (temp == null) {
                temp = "";
            }

            m_strText = temp;

            if (action.equals("RELOAD_CATEGORY")) {
                if (textView_cate != null) {
                    textView_cate.setText(SettingsCode.getValueCategory());

                    showProgress("\'" + temp + "\'\n" + getString(R.string.string_wesearchtext));

                    if (SettingsCode.getValueCountry().equals(getString(R.string.string_korea))) {
                        ((TNaviMainActivity)getActivity()).getSearchPoiItem(temp);
                    }
                }
            } else if (action.equals("RELOAD_COUNTRY")) {
                if (textView_country != null) {
                    textView_country.setText(SettingsCode.getValueCountry());

                    showProgress("\'" + temp + "\'\n" + getString(R.string.string_wesearchtext));

                    editText_search.setHint(getString(R.string.string_wesearch));
                    txt_nosearchresult.setText(getString(R.string.string_wenodata));

                    ((TNaviMainActivity)getActivity()).getSearchPoiItem(temp);
                }
            }
            else if(action.equals("SET_START_TEXT_FROM_SHOWMAP"))
            {
                String strText = intent.getStringExtra("data");

                if(m_nSearchType == 0)
                {
                    editText_start.setText(strText);

                    changeFocus();
                }
            }
        }
    };
    //==============================================================================================
    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(quickMenuFinishReceiver);
        super.onDestroy();
    }
    //==============================================================================================
    public void MergePOIItem(ArrayList<POIItem> i_ALPoiItem, String i_strResult)
    {
        arrayList.clear();
        listAdapter.clearItemList();

        ArrayList<NPoiItem> itemlist = new ArrayList<>();

        if(i_ALPoiItem.size() <= 0)
        {
            if(i_strResult == ErrorMessage.SUCCESS_NAVER_RESULT || i_strResult == FMError.FME_SUCCESS_SEARCH_SUCCESS) {
                setSearchVisible(true);
            }
            else if(i_strResult == ErrorMessage.ERROR_NAVER_RESULT || i_strResult == FMError.FME_MESSAGE_SEARCH_ERROR ||
                    i_strResult == ErrorMessage.TIMEOUT_RESULT || i_strResult.equals(ErrorMessage.ERROR_NOSTRA_RESULT))
            {
                setSearchVisible(false);
            }

            return;
        }

        for(int i = 0; i < SettingsCode.getKeyResultCount(); i++)
        {
            if(i >= i_ALPoiItem.size())
            {
                break;
            }

            NPoiItem nPoiItem = new NPoiItem();
            nPoiItem.setNostraId(i_ALPoiItem.get(i).id);
            nPoiItem.setEnglishName(i_ALPoiItem.get(i).name);
            nPoiItem.setTelephoneNumber(i_ALPoiItem.get(i).telNo);

            if(i_ALPoiItem.get(i).frontLon == null || i_ALPoiItem.get(i).frontLon.isEmpty())
            {
                nPoiItem.setLocationPointX(0);
            }
            else
            {
                nPoiItem.setLocationPointX(Double.valueOf(i_ALPoiItem.get(i).frontLon));
            }

            if(i_ALPoiItem.get(i).frontLat == null || i_ALPoiItem.get(i).frontLat.isEmpty())
            {
                nPoiItem.setLocationPointY(0);
            }
            else
            {
                nPoiItem.setLocationPointY(Double.valueOf(i_ALPoiItem.get(i).frontLat));
            }

            nPoiItem.setAdminLevel1EnglishName(i_ALPoiItem.get(i).upperAddrName);
            nPoiItem.setAdminLevel2EnglishName(i_ALPoiItem.get(i).middleAddrName);
            nPoiItem.setAdminLevel3EnglishName(i_ALPoiItem.get(i).lowerAddrName);
            nPoiItem.setAdminLevel4Englishname(i_ALPoiItem.get(i).detailAddrName);
            nPoiItem.setNoorLat(i_ALPoiItem.get(i).noorLat);
            nPoiItem.setNoorLon(i_ALPoiItem.get(i).noorLon);
            nPoiItem.setFirstNo(i_ALPoiItem.get(i).firstNo);
            nPoiItem.setSecondNo(i_ALPoiItem.get(i).secondNo);
            nPoiItem.setRoadName(i_ALPoiItem.get(i).roadName);
            nPoiItem.setFirstBuildNo(i_ALPoiItem.get(i).firstBuildNo);
            nPoiItem.setSecondBuildNo(i_ALPoiItem.get(i).secondBuildNo);
            nPoiItem.setRadius(i_ALPoiItem.get(i).radius);
            nPoiItem.setBizName(i_ALPoiItem.get(i).bizName);
            nPoiItem.setUpperBizName(i_ALPoiItem.get(i).upperBizName);
            nPoiItem.setMiddleBizName(i_ALPoiItem.get(i).middleBizName);
            nPoiItem.setLowerBizName(i_ALPoiItem.get(i).lowerBizName);
            nPoiItem.setDetailBizName(i_ALPoiItem.get(i).detailBizName);
            nPoiItem.setRpFlag(i_ALPoiItem.get(i).rpFlag);
            nPoiItem.setParkFlag(i_ALPoiItem.get(i).parkFlag);
            nPoiItem.setDetailInfoFlag(i_ALPoiItem.get(i).detailInfoFlag);
            nPoiItem.setDesc(i_ALPoiItem.get(i).desc);
            nPoiItem.setDistance(i_ALPoiItem.get(i).nFromCurPosDist);

            itemlist.add(nPoiItem);
        }

        arrayList = itemlist;
        setDistVisible();
        listAdapter.addItemList(arrayList);

        ((TNaviMainActivity) getActivity()).setSearchList(arrayList);

        setSearchVisible(false);
    }
    //==============================================================================================
    public void changeFocus()
    {
        editText_goal.requestFocus();
        m_nSearchType = 1;
    }
    //==============================================================================================
    public void setStartText(NPoiItem i_nPoiItem)
    {
        editText_start.setText(i_nPoiItem.getEnglishName());

        ((TNaviMainActivity) getActivity()).set_strAddr(i_nPoiItem.getEnglishName(), 0);
        ((TNaviMainActivity) getActivity()).setStartCoord(i_nPoiItem.getLocationPointX(), i_nPoiItem.getLocationPointY());
    }
    //==============================================================================================
    public NPoiItem getPOIItem()
    {
        return m_nPoiItem;
    }
    //==============================================================================================
}