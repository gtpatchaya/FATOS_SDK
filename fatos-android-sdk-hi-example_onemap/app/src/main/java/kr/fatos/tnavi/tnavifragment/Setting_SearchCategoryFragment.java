package kr.fatos.tnavi.tnavifragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import biz.fatossdk.newanavi.base.AMapBaseFragment;
import kr.fatos.tnavi.Activity.SearchSettingActivity;
import kr.fatos.tnavi.Adapter.CategoryAdapterForSetting;
import kr.fatos.tnavi.Code.SettingsCode;
import kr.fatos.tnavi.Lib.GUnitLib;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.Unit.settingItemDetailList;
import kr.fatos.tnavi.Unit.settingItemListAdapter;

import static android.content.Context.MODE_PRIVATE;

public class Setting_SearchCategoryFragment extends AMapBaseFragment {



    static final int[] SETTING_MENU_NAME = new int[]{R.string.string_nostrasetting_country,R.string.string_nostrasetting_category};


    static boolean[] SETTING_MENU_NAME_ENABLE = new boolean[]{true, true};

    static int[] SETTING_MENU_NAME_TYPE = new int[]{settingItemDetailList.SETTING_TYPE_TEXT, settingItemDetailList.SETTING_TYPE_NEXTPAGE};

    Button button_back;
    RecyclerView recyclerView_CateList;
    RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferences prefs;
    CategoryAdapterForSetting categoryAdapter;
    ArrayList<String> arrayList;
    private Context m_Context = null;
    int nCount = 0;

    public static final int SETTING_COUNTRY = 0;
    public static final int SETTING_LANGUAGE = 1;
    public static final int SETTING_CATEGORY = 2;
    private int default_language = 0;

    private ListView m_SettingListView;
    private ArrayList<settingItemDetailList> arSettingDessert = new ArrayList<settingItemDetailList>();
    private settingItemListAdapter settingAdapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, container, false);

        m_Context = getActivity();
        prefs = m_Context.getSharedPreferences(getResources().getString(R.string.app_registerId), MODE_PRIVATE);

        final IntentFilter filter = new IntentFilter();
        filter.addAction("RELOAD_ACTIVITY");
        recyclerView_CateList = v.findViewById(R.id.recyclerView_CateList);
        progressBar = v.findViewById(R.id.category_progress);
        setRecyclerView();

        NTCategoryService();
        return v;
    }




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



    private void setRecyclerView()
    {
        recyclerView_CateList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(m_Context);
        recyclerView_CateList.setLayoutManager(mLayoutManager);

        categoryAdapter = new CategoryAdapterForSetting(this,R.layout.recyclerview_category, new ArrayList<String>());
        recyclerView_CateList.setAdapter(categoryAdapter);

        arrayList = new ArrayList<String>();
    }

    //카테고리 목록 반환
    public void NTCategoryService()
    {
        nCount = 0;

        categoryAdapter.clearItemList();

        categoryAdapter.addItemList(GUnitLib.getInstance().getG_ALCate());
        progressBar.setVisibility(View.GONE);

    }

    public void returnIntentResult(String i_str, int index)
    {

        Intent toSearch = new Intent();
        toSearch.setAction("RELOAD_CATEGORY"); // Action name

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SettingsCode.getKeyCategory(), i_str);
        editor.putInt(SettingsCode.getKeyCategoryindex(),index);

        SettingsCode.setValueCategory(i_str);
        SettingsCode.setValueCategoryIndex(index);

        editor.apply();

        m_Context.sendBroadcast(toSearch);
        ((SearchSettingActivity)getActivity()).onBackPressed();



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
