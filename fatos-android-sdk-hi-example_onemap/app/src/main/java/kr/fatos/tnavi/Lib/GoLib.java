package kr.fatos.tnavi.Lib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import kr.fatos.tnavi.Activity.CategoryActivity;

import kr.fatos.tnavi.Activity.SearchSettingActivity;
import kr.fatos.tnavi.Activity.SettingActivity;
import kr.fatos.tnavi.tnavifragment.SearchFragment;
import kr.fatos.tnavi.TNaviMainActivity;

public class GoLib {
    public final String TAG = GoLib.class.getSimpleName();

    private volatile static GoLib instance;

    public static GoLib getInstance()
    {
        if(instance == null)
        {
            synchronized (GoLib.class)
            {
                if(instance == null)
                {
                    instance = new GoLib();
                }
            }
        }

        return instance;
    }

    //프레그먼트 이동
    public void goFragment(FragmentManager fragmentManager, int containerViewId, Fragment fragment, @Nullable Bundle bundle)
    {
        if(bundle != null)
        {
            fragment.setArguments(bundle);
        }

        fragmentManager.beginTransaction().replace(containerViewId, fragment).commit();
    }

    public void goFragment(FragmentManager fragmentManager, int containerViewId, Fragment fragment, String tag, @Nullable Bundle bundle)
    {
        if(bundle != null)
        {
            fragment.setArguments(bundle);
        }

        fragmentManager.beginTransaction().replace(containerViewId, fragment, tag).commit();
    }

    //프레그먼트 add
    public void goFragmentReplaceToBackStack(FragmentManager fragmentManager, int containerViewId, Fragment fragment, String tag, @Nullable Bundle bundle)
    {
        if(bundle != null)
        {
            fragment.setArguments(bundle);
        }

        fragmentManager.beginTransaction().replace(containerViewId, fragment, tag).addToBackStack(null).commit();
    }

    //이전 프래그먼트
    public void goBackFragment(FragmentManager fragmentManager)
    {
        fragmentManager.popBackStack();
    }

    //검색 Activity로 이동
    public void goSearchActivity(Context context, Bundle bundle)
    {
        Intent intent = new Intent(context, SearchFragment.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    //카테고리 Activity로 이동
    public Intent goCategoryActivity(Context context)
    {
        Intent intent = new Intent(context, CategoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return intent;
    }

    //메인 화면으로 이동
    public void goTNaviMainActivity(Context context, Bundle bundle)
    {
        Intent intent = new Intent(context, TNaviMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if(bundle != null) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
    }
    public void goSettingActivity(Context context, Bundle bundle)
    {
        Intent intent = new Intent(context, SettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if(bundle != null) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
    }
    public void goSearchSettingActivity(Context context, Bundle bundle)
    {
        Intent intent = new Intent(context, SearchSettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if(bundle != null) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
    }
}