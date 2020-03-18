package kr.fatos.tnavi.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.Nullable;

import biz.fatossdk.newanavi.ANaviApplication;
import biz.fatossdk.newanavi.base.AMapBaseActivity;
import kr.fatos.tnavi.R;

/**
 * Created by kyungilwoo on 2017. 2. 9..
 */

public class TermOfUseActivity extends AMapBaseActivity {
    public static final String TAG = "AMAP";
    private ANaviApplication m_gApp;
    private Context m_Context = null;
    private Button m_btnBack, m_btnHome;
    private WebView m_WebView;
    public TermOfUseActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termofuse);
        m_Context = this;
        m_gApp = (ANaviApplication) m_Context.getApplicationContext();
//        WebView wb = new WebView(this);
//        wb.loadUrl("file:///android_asset/setting/termsofuse_fatos.html");
//        setContentView(wb);


        m_WebView = (WebView) findViewById(R.id.webview);
        if(m_gApp.getAppSettingInfo().m_nDefaultLanguage == 0)
            m_WebView.loadUrl("file:///android_asset/setting_maphi/termsofuse_fatos.html");
        else
            m_WebView.loadUrl("file:///android_asset/setting_maphi/termsofuse_fatos_eng.html");

        m_btnBack = (Button) findViewById(R.id.setting_search_back_btn);
        m_btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*m_btnHome = (Button) findViewById(R.id.setting_search_btn_cancel);
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
}
