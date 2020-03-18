package kr.fatos.tnavi.Lib;

import java.util.ArrayList;

public class GUnitLib {
    private ArrayList<String> g_ALCate = new ArrayList<>();

    private volatile static GUnitLib instance;

    public static GUnitLib getInstance()
    {
        if(instance == null)
        {
            synchronized (GUnitLib.class)
            {
                if(instance == null)
                {
                    instance = new GUnitLib();
                }
            }
        }

        return instance;
    }

    public ArrayList<String> getG_ALCate() {
        return g_ALCate;
    }
}
