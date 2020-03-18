package kr.fatos.tnavi.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import biz.fatossdk.fminterface.FMBaseActivity;
import biz.fatossdk.newanavi.ANaviApplication;
import biz.fatossdk.newanavi.splash.FatosToast;
import kr.fatos.tnavi.R;

public class RouteOptionPopupActivity extends FMBaseActivity {

    private Context m_Context = null;
    private ANaviApplication m_gApp;

    private Button m_btnHome;

    private Button[] m_btnRouteOption;
 
    static final int[] SETTING_ROUTE_OPTION = new int[]{R.string.string_route_option_recomm, R.string.string_route_option_exp,
            R.string.string_route_option_general, R.string.string_route_option_short, R.string.string_route_option_free};

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
        //overridePendingTransition(R.anim.hold,R.anim.fade_out_fast);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_route_option_popup);

        m_Context = this;
        m_gApp = (ANaviApplication) m_Context.getApplicationContext();

        m_btnHome = (Button) findViewById(R.id.btn_close);
        m_btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        m_btnRouteOption = new Button[SETTING_ROUTE_OPTION.length];

        m_btnRouteOption[0] = (Button) findViewById(R.id.route_option1);

        m_btnRouteOption[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!selectRouteOption(0)) return;
                updateRouteOption();
            }
        });


        m_btnRouteOption[1] = (Button) findViewById(R.id.route_option2);
        m_btnRouteOption[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectRouteOption(2)) return;
                updateRouteOption();

            }
        });

        m_btnRouteOption[2] = (Button) findViewById(R.id.route_option3);
        m_btnRouteOption[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectRouteOption(3)) return;
                updateRouteOption();

            }
        });

        m_btnRouteOption[3] = (Button) findViewById(R.id.route_option4);
        m_btnRouteOption[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectRouteOption(4)) return;
                updateRouteOption();
            }
        });
        
        m_btnRouteOption[4] = (Button) findViewById(R.id.route_option5);
        m_btnRouteOption[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectRouteOption(5)) return;
                updateRouteOption();
            }
        });

        updateRouteOption();
    }

    /*
    true, // 추천1
            false, // 추천2
            false, // 고속도로 우선
            false, // 일반도로 우선
            false, // 최단거리
            true // 무료 도로

            */
    /*
            true, // 추천1
            false, // 추천2
            false, // 고속도로 우선
            false, // 일반도로 우선
            false, // 최단거리
            true // 무료 도로*/


    private boolean selectRouteOption(int i) {
        if (m_gApp.getAppSettingInfo().m_nCurSelectRouteOption < m_gApp.getAppSettingInfo().MAX_ROUTE_OPTION) {
            if (m_gApp.getAppSettingInfo().m_arRouteOptionEnable[i]) {
                if (m_gApp.getAppSettingInfo().m_nCurSelectRouteOption == 2) {
                    FatosToast.ShowFatosYellow( getString(R.string.warning_opt_route_least, m_gApp.getAppSettingInfo().m_nCurSelectRouteOption) );
                    return false;
                }
                m_gApp.getAppSettingInfo().m_arRouteOptionEnable[i] = false;
                m_gApp.getAppSettingInfo().m_nCurSelectRouteOption--;
            }
            else if (!m_gApp.getAppSettingInfo().m_arRouteOptionEnable[i]) {
                m_gApp.getAppSettingInfo().m_arRouteOptionEnable[i] = true;
                m_gApp.getAppSettingInfo().m_nCurSelectRouteOption++;
            }
        } else {
            if (m_gApp.getAppSettingInfo().m_arRouteOptionEnable[i]) {
                m_gApp.getAppSettingInfo().m_arRouteOptionEnable[i] = false;
                m_gApp.getAppSettingInfo().m_nCurSelectRouteOption--;
            }
            else {
                FatosToast.ShowFatosYellow( getString(R.string.warning_opt_route_upto, m_gApp.getAppSettingInfo().MAX_ROUTE_OPTION) );
                return false;
            }
        }

        return true;
    }

    private void updateRouteOption()
    {
        int select = 0;

        for(int i = 0; i < m_gApp.getAppSettingInfo().TOTAL_ROUTE_OPTION_CNT ; i++)
        {
            switch(i)
            {
                case 0: // 추천 1
                    if(m_gApp.getAppSettingInfo().m_arRouteOptionEnable[i])
                    {
                        m_btnRouteOption[0].setBackgroundResource(R.drawable.setting_enable_btn);
                        select++;
                    }
                    else
                    {
                        m_btnRouteOption[0].setBackgroundResource(R.drawable.setting_normal_btn);
                    }
                    break;
                case 2: // 고속도로 우선
                    if(m_gApp.getAppSettingInfo().m_arRouteOptionEnable[i])
                    {
                        m_btnRouteOption[1].setBackgroundResource(R.drawable.setting_enable_btn);
                        select++;
                    }
                    else
                    {
                        m_btnRouteOption[1].setBackgroundResource(R.drawable.setting_normal_btn);
                    }
                    break;
                case 3: // 일반도로 우선
                    if(m_gApp.getAppSettingInfo().m_arRouteOptionEnable[i])
                    {
                        m_btnRouteOption[2].setBackgroundResource(R.drawable.setting_enable_btn);
                        select++;

                    }
                    else
                    {
                        m_btnRouteOption[2].setBackgroundResource(R.drawable.setting_normal_btn);
                    }
                    break;
                case 4: // 최단거리
                    if(m_gApp.getAppSettingInfo().m_arRouteOptionEnable[i])
                    {
                        m_btnRouteOption[3].setBackgroundResource(R.drawable.setting_enable_btn);
                        select++;

                    }
                    else
                    {
                        m_btnRouteOption[3].setBackgroundResource(R.drawable.setting_normal_btn);
                    }
                    break;
                case 5: // 무료 도로
                    if(m_gApp.getAppSettingInfo().m_arRouteOptionEnable[i])
                    {
                        m_btnRouteOption[4].setBackgroundResource(R.drawable.setting_enable_btn);
                        select++;

                    }
                    else
                    {
                        m_btnRouteOption[4].setBackgroundResource(R.drawable.setting_normal_btn);
                    }
                    break;

            }
        }

        m_gApp.getAppSettingInfo().m_nCurSelectRouteOption = select;
        m_gApp.saveSettingInfo(m_Context, m_gApp.getAppSettingInfo());

    }
}
