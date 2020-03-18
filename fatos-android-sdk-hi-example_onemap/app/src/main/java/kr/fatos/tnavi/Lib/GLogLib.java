package kr.fatos.tnavi.Lib;

import android.util.Log;

import kr.fatos.tnavi.BuildConfig;

//디버그 모드일때만 로그 표출하기 위한 글로벌 로그 클래스
public class GLogLib {
    private static boolean enabled = BuildConfig.DEBUG;

    public static void d(String tag, String text)
    {
        if(!enabled)
        {
            return;
        }

        Log.d(tag, text);
    }

    public static void d(String text)
    {
        if(!enabled)
        {
            return;
        }

        Log.d("Log", text);
    }

    public static void d(String tag, Class<?> cls, String text)
    {
        if(!enabled)
        {
            return;
        }

        Log.d(tag, cls.getName() + "." + text);
    }

    public static void e(String tag, String text)
    {
        if(!enabled)
        {
            return;
        }

        Log.e(tag, text);
    }
}
