package kr.fatos.tnavi.Lib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import kr.fatos.tnavi.R;

public class AppNetwork extends BroadcastReceiver {
    private Activity activity;

    public AppNetwork()
    {

    }

    public AppNetwork(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action= intent.getAction();

        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
        {
            try
            {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
                NetworkInfo wifi_network = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if(wifi_network != null)
                {
                    // wifi, 3g 둘 중 하나라도 있을 경우
                    if(wifi_network != null && activeNetInfo != null)
                    {
                    }
                    // wifi, 3g 둘 다 없을 경우
                    else
                    {
                        new AlertDialog.Builder(activity)
                                .setTitle((activity.getApplicationContext()).getString(R.string.app_name))
                                .setMessage((activity.getApplicationContext()).getString(R.string.string_network_error))
                                .setPositiveButton((activity.getApplicationContext()).getString(R.string.string_popupTitle_btn_Ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    }
                }
            }
            catch (Exception e)
            {
            }
        }
    }
}