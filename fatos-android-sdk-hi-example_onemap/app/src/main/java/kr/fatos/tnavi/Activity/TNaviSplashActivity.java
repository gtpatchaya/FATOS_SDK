package kr.fatos.tnavi.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.UUID;

import biz.fatossdk.newanavi.ANaviApplication;
import kr.fatos.tnavi.Code.TNaviActionCode;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.TNaviMainActivity;

public class TNaviSplashActivity extends Activity {
    private Handler m_Handler = new Handler();
    private ANaviApplication m_gApp;
    private Context m_Context = null;
    private static final String TAG = TNaviSplashActivity.class.getSimpleName();
    private Activity m_MainActivity;

    private static boolean m_bExitApp = false;
    //==============================================================================================
    public TNaviSplashActivity() {
    }
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_MainActivity = this;
        m_Context = this;
        m_gApp = (ANaviApplication) m_Context.getApplicationContext();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(TNaviActionCode.READY_MAIN_MAP);
        registerReceiver(quickMenuFinishReceiver, filter);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_splash);

        ImageView fatoslogo = findViewById(R.id.fatoslogo);
        ImageView fatostitle = findViewById(R.id.fatostitle);
        LinearLayout linearLayout_Main = findViewById(R.id.linearLayout_Main);
        ImageView we_loading = findViewById(R.id.we_loading);

        linearLayout_Main.setBackgroundResource(R.color.hiSplashBackgroundColor);
        fatoslogo.setImageResource(R.drawable.splash_title);
        fatostitle.setImageResource(R.drawable.splash_logo);
        we_loading.setImageResource(R.drawable.hi_splash_sdk);

        m_gApp.setM_nExternalCallMode(-1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResultWRITE_EXTERNAL_STORAGE = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionResultACCESS_FINE_LOCATION = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionResultACCESS_READ_PHONE= checkSelfPermission(Manifest.permission.READ_PHONE_STATE);

            if (permissionResultWRITE_EXTERNAL_STORAGE == PackageManager.PERMISSION_DENIED
                    || permissionResultACCESS_FINE_LOCATION == PackageManager.PERMISSION_DENIED
                    || permissionResultACCESS_READ_PHONE == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 1000);
            }
            else {
                final TelephonyManager tm = (TelephonyManager) m_Context.getSystemService(Context.TELEPHONY_SERVICE);
                final String tmDevice, tmSerial, androidId;

                tmDevice = "" + tm.getDeviceId();
                tmSerial = "" + tm.getSimSerialNumber();
                androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
                String deviceId = deviceUuid.toString();

                m_gApp.m_strUUID = deviceId;

                m_Handler.postDelayed(startMainActivity, 10);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        else {
            m_Handler.postDelayed(startMainActivity, 10);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
    //==============================================================================================
    /**
     * 사용자가 권한을 허용했는지 거부했는지 체크
     * @param requestCode   1000번
     * @param permissions   개발자가 요청한 권한들
     * @param grantResults  권한에 대한 응답들
     *                    permissions와 grantResults는 인덱스 별로 매칭된다.     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean bAllGranted = true;

        if (requestCode == 1000) {
            for(int i = 0; i < grantResults.length ; i++)
            {
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(TNaviSplashActivity.this, "App 실행에 필요한 권한이 설정 되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    bAllGranted = false;
                    break;
                }

            }

            m_Handler.postDelayed(startMainActivity, 10);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
    //==============================================================================================
    BroadcastReceiver quickMenuFinishReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();

            if (action.equals(TNaviActionCode.READY_MAIN_MAP)){
                Log.d(TAG,"mainActivity is ready!");
                m_MainActivity.finish();
            }
        }
    };
    //==============================================================================================
    private final Runnable startMainActivity = new Runnable() {
        @Override
        public void run() {
            if(m_bExitApp) {
                return;
            }

            NTCategoryService();

            Intent intentStartMain = new Intent(m_MainActivity, TNaviMainActivity.class);
            intentStartMain.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intentStartMain);
        }
    };
    //==============================================================================================
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(quickMenuFinishReceiver);
    }
    //==============================================================================================
    //카테고리 목록 반환 초반에 한번만 불러오자
    public void NTCategoryService()
    {
//        NTCategoryService.executeAsync(new ServiceRequestListener<NTCategoryResultSet>() {
//            @Override
//            public void onResponse(NTCategoryResultSet result) {
//                NTCategoryResult[] items = result.getResults();
//
//                if(items.length > 0)
//                {
//                    GUnitLib.getInstance().getG_ALCate().add(getString(R.string.string_weallcate));
//
//                    for(NTCategoryResult item : items)
//                    {
//                        String strTemp = item.getCategoryCode();
//
//                        GUnitLib.getInstance().getG_ALCate().add(strTemp);
//                    }
//                }
//            }
//
//            @Override
//            public void onError(String errorMessage, int statusCode) {
//            }
//        });
    }
    //==============================================================================================
}
