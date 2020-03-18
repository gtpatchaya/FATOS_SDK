package kr.fatos.tnavi.Lib;

import android.content.Context;
import android.widget.Toast;

//글로벌 토스트 클래스
public class GToastLib {
    public static void s(Context context, int id)
    {
        Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
    }

    public static void s(Context context, String text)
    {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void l(Context context, int id)
    {
        Toast.makeText(context, id, Toast.LENGTH_LONG).show();
    }

    public static void l(Context context, String text)
    {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
