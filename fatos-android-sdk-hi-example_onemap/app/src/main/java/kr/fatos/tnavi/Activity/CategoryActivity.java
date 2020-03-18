package kr.fatos.tnavi.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.fatos.tnavi.Adapter.CategoryAdapter;
import kr.fatos.tnavi.Code.SettingsCode;
import kr.fatos.tnavi.Lib.GUnitLib;
import kr.fatos.tnavi.R;

public class CategoryActivity extends AppCompatActivity {
    Button button_back;
    RecyclerView recyclerView_CateList;
    RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferences prefs;
    CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        prefs = getSharedPreferences(getResources().getString(R.string.app_registerId), MODE_PRIVATE);
        button_back = findViewById(R.id.button_back);
        recyclerView_CateList = findViewById(R.id.recyclerView_CateList);

        setRecyclerView();

        NTCategoryService();

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setRecyclerView()
    {
        recyclerView_CateList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView_CateList.setLayoutManager(mLayoutManager);

        categoryAdapter = new CategoryAdapter(this, R.layout.recyclerview_category, new ArrayList<String>());
        recyclerView_CateList.setAdapter(categoryAdapter);
    }

    //카테고리 목록 반환
    public void NTCategoryService()
    {
        categoryAdapter.addItemList(GUnitLib.getInstance().getG_ALCate());
    }

    public void returnIntentResult(String i_str, int index)
    {

        Intent toSearch = new Intent();
        toSearch.setAction("RELOAD_CATEGORY"); // Action name
        sendBroadcast(toSearch);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SettingsCode.getKeyCategory(), i_str);
        editor.putInt(SettingsCode.getKeyCategoryindex(),index);

        SettingsCode.setValueCategory(i_str);
        SettingsCode.setValueCategoryIndex(index);

        editor.apply();

        Intent brod_intent = new Intent();
        brod_intent.setAction("RELOAD_ACTIVITY"); // Action name
        sendBroadcast(brod_intent);

        finish();
    }


}
