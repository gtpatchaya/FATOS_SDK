package kr.fatos.tnavi.Lib;

import android.content.Context;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import kr.fatos.tnavi.R;

public class GUtilLib {
    private volatile static GUtilLib instance;
    private static Context m_Context;

    public static GUtilLib getInstance(Context i_context)
    {
        if(instance == null)
        {
            synchronized (GUtilLib.class)
            {
                if(instance == null)
                {
                    instance = new GUtilLib();
                }

                m_Context = i_context;
            }
        }

        return instance;
    }

    //남은거리 계산
    public String updateTotalRemainDist(int distance) {

        String strResult = "";

        if (distance >= 1000)
        {
            float km = (float) distance / 1000.f;

            if (km >= 100)
            {
                DecimalFormat format = new DecimalFormat("###,###");
                String strTempKm = format.format(km);

                strResult = String.format("%s %s", strTempKm, m_Context.getResources().getString(R.string.km));
            }
            else if (km >= 10)
            {
                strResult = String.format("%.1f %s", (float) distance / 1000, m_Context.getResources().getString(R.string.km));
            }
            else
            {
                strResult = String.format("%.1f %s", (float) distance / 1000, m_Context.getResources().getString(R.string.km));
            }
        }
        else
        {
            if (distance <= 0)
            {
                distance = 0;
            }

            if (distance >= 10)
            {
                distance = distance - (distance % 10);
                strResult = String.format("%d %s", distance, m_Context.getResources().getString(R.string.m));
            }
            else
            {
                strResult = String.format("%d %s", distance, m_Context.getResources().getString(R.string.m));
            }
        }

        return strResult;
    }

    public String updateTotalRemainDistForMile(int distance) {
        String strResult = "0";
        if (distance >= 350) {
            float km = (float) distance / 1000.f;

            if (km >= 100) {
                DecimalFormat format = new DecimalFormat("###,###");
                String strTempKm = format.format((int) (km * 0.6214f));

                strResult = String.format("%s mile", strTempKm);
            } else if (km >= 10) {
                strResult = String.format("%.1f mile", (float) (km * 0.6214f));
            } else {
                strResult = String.format("%.2f mile", (float) (km *  0.6214f));
            }
        } else {
            if (distance <= 0) {
                distance = 0;
            }
            if (distance >= 10) {
                distance = distance - (distance % 10);
                strResult = String.format("%d ft", (int) (distance * 3.28084f));
            } else {
                DecimalFormat format = new DecimalFormat("###,###");
                String strTempKm = format.format((int) (distance * 3.28084f));

                strResult = String.format("%s ft", strTempKm);
            }

        }

        return strResult;
    }

    /**
     # m_gApp.getAppSettingInfo().m_bArriveTime
     m_bArriveTime = true : 도착 예정시간
                    false: 남은 시간
     # 요약화면 시간계산 함수
    */
    public String updateTotalRemainTime(int secTime, boolean i_bArriveTime, Locale i_lLocale) {

        String strResult = "", strTemp;
        Calendar now = Calendar.getInstance();

        if(secTime < 10){
            secTime = 0;
        }else if(secTime < 60) {
            secTime = 60;
        }
        if(i_bArriveTime)
        {
            Calendar tmp = (Calendar) now.clone();
            tmp.add(Calendar.SECOND, secTime);
            Calendar arriveCal = tmp;

//            if(i_lLocale.getLanguage() != "ko") {
                DateFormat outputFormat = new SimpleDateFormat("hh:mm a", i_lLocale);
                strTemp = outputFormat.format(arriveCal.getTime());

                strResult = strTemp.replace("AM", m_Context.getResources().getString(R.string.AM)).replace("PM", m_Context.getResources().getString(R.string.PM));
        }
        else
        {
            int minutes = 0;
            minutes = (int) (secTime / 60);

            String strDay;
            String strHour;

            strHour = m_Context.getString(R.string.h);

            if (60 > minutes)
            {
                strResult = String.format("%02d " + m_Context.getString(R.string.min), minutes);
            }
            else
            {
                int hour = (int) (minutes / 60);
                int extraMin = minutes % 60;

                if(hour < 24)
                {
                    if (extraMin == 0)
                    {
                        strResult = String.format("%02d " + strHour, hour);
                    }
                    else
                    {
                        strResult = String.format("%02d " + strHour + " %02d " + m_Context.getString(R.string.min), hour, extraMin);
                    }
                }
                else
                {
                    int nDay = hour / 24;
                    int nExtraHour = hour - (nDay * 24);

                    if (nDay > 1) {
                        strDay = m_Context.getString(R.string.days_small);
                    } else {
                        strDay = m_Context.getString(R.string.day_small);
                    }

                    if(extraMin == 0 && nExtraHour == 0)
                    {
                        strResult = String.format("%02d " + strDay, nDay);
                    }
                    else if(extraMin == 0 && nExtraHour != 0)
                    {
                        strResult = String.format("%02d " + strDay + " %02d " + strHour, nDay, nExtraHour);
                    }
                    else
                    {
                        strResult = String.format("%02d " + strDay + " %02d " + strHour + " %02d " + m_Context.getString(R.string.min), nDay, nExtraHour, extraMin);
                    }
                }
            }
        }

        return strResult;
    }

    //일자 제거된 남은시간
    public String updateTotalRemainTimeWithoutDay(int secTime, boolean i_bArriveTime, Locale i_lLocale) {
        String strResult = "", strTemp;
        Calendar now = Calendar.getInstance();

        if(secTime < 10){
            secTime = 0;
        }else if(secTime < 60) {
            secTime = 60;
        }

        if(i_bArriveTime)
        {
            Calendar tmp = (Calendar) now.clone();
            tmp.add(Calendar.SECOND, secTime);
            Calendar arriveCal = tmp;

            DateFormat outputFormat = new SimpleDateFormat("hh:mm a", i_lLocale);
            strTemp = outputFormat.format(arriveCal.getTime());

            strResult =  strTemp.replace("AM", " " + m_Context.getResources().getString(R.string.AM)).replace("PM", m_Context.getResources().getString(R.string.PM));
        }
        else
        {
            int minutes = 0;
            minutes = (int) (secTime / 60);

            String strHour;

            strHour = m_Context.getString(R.string.h);

            if (60 > minutes)
            {
                strResult = String.format("%02d " + m_Context.getString(R.string.min), minutes);
            }
            else
            {
                int hour = (int) (minutes / 60);
                int extraMin = minutes % 60;

                if (extraMin == 0)
                {
                    strResult = String.format("%02d " + strHour, hour);
                }
                else
                {
                    strResult = String.format("%02d " + strHour + " %02d " + m_Context.getString(R.string.min), hour, extraMin);
                }
            }
        }

        return strResult;
    }

    //주행이나 모의 주행중 시간 계산
    public String updateTotalRemainTimeRoute(int secTime, boolean i_bArriveTime, Locale i_lLocale) {

        String strResult = "", strTemp;
        Calendar now = Calendar.getInstance();

        if(secTime < 10){
            secTime = 0;
        }else if(secTime < 60) {
            secTime = 60;
        }

        if(i_bArriveTime)
        {
            Calendar tmp = (Calendar) now.clone();
            tmp.add(Calendar.SECOND, secTime);
            Calendar arriveCal = tmp;

            DateFormat outputFormat = new SimpleDateFormat("hh:mm a", i_lLocale);
            strTemp = outputFormat.format(arriveCal.getTime());

            strResult = " " + strTemp.replace("AM", m_Context.getResources().getString(R.string.AM)).replace("PM", m_Context.getResources().getString(R.string.PM));
        }
        else
        {
            int minutes = 0;
            minutes = (int) (secTime / 60);

            if (60 > minutes)
            {
                strResult = String.format("00:%02d", minutes);
            }
            else
            {
                int hour = (int) (minutes / 60);
                int extraMin = minutes % 60;

                if(hour < 24)
                {
//                    if (extraMin == 0)
//                    {
//                        strResult = String.format("%02d", hour);
//                    }
//                    else
                    {
                        strResult = String.format("%02d:%02d", hour, extraMin);
                    }
                }
                else
                {
                    int nDay = hour / 24;
                    int nExtraHour = hour - (nDay * 24);

//                    if(extraMin == 0 && nExtraHour == 0)
//                    {
//                        strResult = String.format("%d", nDay);
//                    }
//                    else if(extraMin == 0 && nExtraHour != 0)
//                    {
//                        strResult = String.format("%d,%02d", nDay, nExtraHour);
//                    }
//                    else
                    {
                        strResult = String.format("%02d,%02d:%02d", nDay, nExtraHour, extraMin);
                    }
                }
            }
        }

        return strResult;
    }

    //일자 제거된 남은시간
    public String updateTotalRemainTimeRouteWithoutDay(int secTime, boolean i_bArriveTime, Locale i_lLocale) {
        String strResult = "", strTemp;
        Calendar now = Calendar.getInstance();

        if(secTime < 10){
            secTime = 0;
        }else if(secTime < 60) {
            secTime = 60;
        }

        if(i_bArriveTime)
        {
            Calendar tmp = (Calendar) now.clone();
            tmp.add(Calendar.SECOND, secTime);
            Calendar arriveCal = tmp;

            DateFormat outputFormat = new SimpleDateFormat("hh:mm a", i_lLocale);
            strTemp = outputFormat.format(arriveCal.getTime());

            strResult = strTemp.replace("AM", m_Context.getResources().getString(R.string.AM)).replace("PM", m_Context.getResources().getString(R.string.PM));
        }
        else
        {
            int minutes = 0;
            minutes = (int) (secTime / 60);

            if (60 > minutes)
            {
                strResult = String.format("00:%02d", minutes);
            }
            else
            {
                int hour = (int) (minutes / 60);
                int extraMin = minutes % 60;

                strResult = String.format("%02d:%02d", hour, extraMin);
            }
        }

        return strResult;
    }
}
